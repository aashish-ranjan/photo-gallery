package com.example.photogallery

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.photogallery.model.Photo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.lang.IllegalArgumentException

class PhotoGalleryViewModel(private val repository: PhotosRepository): ViewModel() {

    private val _galleryItemsStateFlow : MutableStateFlow<List<Photo>> = MutableStateFlow(emptyList())
    val galleryItemsStateFlow = _galleryItemsStateFlow.asStateFlow()

    init {
//        fetchGalleryPhotos()
        fetchPhotosBySearchQuery(DEFAULT_SEARCH_QUERY)
    }

    private fun fetchGalleryPhotos() {
        viewModelScope.launch(Dispatchers.IO) {
            _galleryItemsStateFlow.value = repository.getPhotos()
        }
    }

    fun fetchPhotosBySearchQuery(searchQuery: String) {
        viewModelScope.launch(Dispatchers.IO) {
            _galleryItemsStateFlow.value = repository.getPhotosBySearchQuery(searchQuery)
        }
    }

    companion object {
        const val DEFAULT_SEARCH_QUERY = "trees"
    }
}

class PhotoViewModelFactory(private val repository: PhotosRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PhotoGalleryViewModel::class.java)) {
            return PhotoGalleryViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}