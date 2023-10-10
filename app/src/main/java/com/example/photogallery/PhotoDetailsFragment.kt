package com.example.photogallery

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.navArgs
import com.example.photogallery.databinding.FragmentPhotoDetailsBinding

class PhotoDetailsFragment : Fragment() {
    val args: PhotoDetailsFragmentArgs by navArgs()

    private var _binding: FragmentPhotoDetailsBinding? = null
    private val binding
        get() = checkNotNull(_binding) { "Cannot access binding because it is null. Is the view visible?" }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPhotoDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}