package com.example.photogallery

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.photogallery.databinding.FragmentPhotoDetailsBinding

class PhotoDetailsFragment : Fragment() {
    private val args: PhotoDetailsFragmentArgs by navArgs()

    private var _binding: FragmentPhotoDetailsBinding? = null
    private val binding
        get() = checkNotNull(_binding) { "Cannot access binding because it is null. Is the view visible?" }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (binding.photoPageWebview.canGoBack()) {
                    binding.photoPageWebview.goBack()
                } else {
                    (requireActivity() as? AppCompatActivity)?.supportActionBar?.subtitle = ""
                    findNavController().popBackStack()
                }
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(callback)
    }

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPhotoDetailsBinding.inflate(inflater, container, false)

        binding.photoPageWebview.apply {
            webViewClient = WebViewClient()
            loadUrl(args.photoPageUrl.toString())
            settings.javaScriptEnabled = true
            webChromeClient = object : WebChromeClient() {
                override fun onProgressChanged(view: WebView?, newProgress: Int) {
                    super.onProgressChanged(view, newProgress)
                    if (isAdded) {
                        binding.webpageProgressBar.apply {
                            isVisible = newProgress != 100
                            progress = newProgress
                        }
                    }
                }

                override fun onReceivedTitle(view: WebView?, title: String?) {
                    super.onReceivedTitle(view, title)
                    if (isAdded) {
                        (requireActivity() as? AppCompatActivity)?.supportActionBar?.subtitle = title
                    }
                }
            }
        }
        return binding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}