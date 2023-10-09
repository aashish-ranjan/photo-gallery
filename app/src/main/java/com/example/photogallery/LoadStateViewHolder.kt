package com.example.photogallery

import androidx.core.view.isVisible
import androidx.paging.LoadState
import androidx.recyclerview.widget.RecyclerView
import com.example.photogallery.databinding.ItemLoadStateBinding

class  LoadStateViewHolder(private val binding: ItemLoadStateBinding, private val retry: () -> Unit): RecyclerView.ViewHolder(binding.root) {

    init {
        binding.retryButton.setOnClickListener {
            retry()
        }
    }
    fun bind(loadState: LoadState) {
        with(binding) {
            if (loadState is LoadState.Error) {
                errorMessageTv.text = loadState.error.localizedMessage
            }
            progressBar.isVisible = loadState is LoadState.Loading
            retryButton.isVisible = loadState is LoadState.Error
            errorMessageTv.isVisible = loadState is LoadState.Error
        }
    }
}