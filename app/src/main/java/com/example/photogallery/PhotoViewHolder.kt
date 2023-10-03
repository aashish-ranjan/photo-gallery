package com.example.photogallery

import androidx.recyclerview.widget.RecyclerView.ViewHolder
import coil.load
import com.example.photogallery.databinding.ItemPhotoBinding
import com.example.photogallery.model.Photo

class PhotoViewHolder(private val binding: ItemPhotoBinding): ViewHolder(binding.root) {
    fun bind(photo: Photo) {
        with(binding) {
            rvItemPhoto.load(photo.imageUrl) {
                placeholder(R.drawable.placeholder_image)
            }
            rvItemPhoto.contentDescription= photo.title
        }
    }
}