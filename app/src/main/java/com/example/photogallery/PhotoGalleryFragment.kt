package com.example.photogallery

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.SearchView.OnQueryTextListener
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.photogallery.databinding.FragmentPhotoGalleryBinding
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class PhotoGalleryFragment: Fragment() {

    private val repository = PhotosRepository.getInstance()
    private val photoGalleryViewModel: PhotoGalleryViewModel by viewModels {
        PhotoViewModelFactory(repository)
    }
    private var togglePollingMenuItem: MenuItem? = null

    private var _binding: FragmentPhotoGalleryBinding? = null
    private val binding
        get() = checkNotNull(_binding) {
            "Cannot access binding because it is null. Is the view visible?"
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPhotoGalleryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val adapter = PhotoAdapter()
        with(binding.photoGridRv) {
            layoutManager = LinearLayoutManager(requireContext())
            setHasFixedSize(true)
            this.adapter = adapter.withLoadStateHeaderAndFooter(
                header = PhotoLoadStateAdapter(adapter::retry),
                footer = PhotoLoadStateAdapter(adapter::retry)
            )
        }
        adapter.addLoadStateListener { loadState ->
            with(binding) {

                photoGridRv.isVisible = loadState.source.refresh is LoadState.NotLoading
                progressBar.isVisible = loadState.source.refresh is LoadState.Loading
                retryButton.isVisible = loadState.source.refresh is LoadState.Error
                errorMessageTv.isVisible = loadState.source.refresh is LoadState.Error

                //empty view
                if (loadState.source.refresh is LoadState.NotLoading
                    && loadState.append.endOfPaginationReached
                    && adapter.itemCount < 1) {

                    errorMessageTv.isVisible = true
                    photoGridRv.isVisible = false
                    errorMessageTv.setText(R.string.no_results_found)
                } else {
                    errorMessageTv.setText(R.string.failed_loading_photos_message)
                }

                retryButton.setOnClickListener {
                    adapter.retry()
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                photoGalleryViewModel.searchQueryResultsFlow.collectLatest {
                    adapter.submitData(it)
                }
            }
        }
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                photoGalleryViewModel.pollingEnabledFlow.collectLatest { pollingEnabled ->
                    setMenuItemPollingStatus(pollingEnabled)
                }
            }
        }
    }

    private fun setMenuItemPollingStatus(pollingEnabled: Boolean) {
        togglePollingMenuItem?.setTitle(
            if (pollingEnabled) R.string.stop_polling
            else R.string.start_polling
        )
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.gallery_menu, menu)
        val searchItem = menu.findItem(R.id.action_search)
        val searchView = searchItem.actionView as? SearchView
        searchView?.setOnQueryTextListener(object : OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                photoGalleryViewModel.processSearchQuery(query ?: "")
                searchView.clearFocus()
                return true
            }
            override fun onQueryTextChange(newText: String?): Boolean {
                return false
            }
        })
        togglePollingMenuItem = menu.findItem(R.id.action_toggle_polling)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId) {
            R.id.menu_item_clear_search -> {
                photoGalleryViewModel.processSearchQuery("")
                true
            }
            R.id.action_toggle_polling -> {
                photoGalleryViewModel.togglePollingStatus()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        togglePollingMenuItem = null
        _binding = null
    }
}