package com.example.photogallery

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.photogallery.model.Photo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.lang.IllegalArgumentException

class PhotoGalleryViewModel(private val repository: PhotosRepository) : ViewModel() {
    private val preferencesRepository = PreferencesRepository.getInstance()

    private val _galleryItemsStateFlow: MutableStateFlow<List<Photo>> =
        MutableStateFlow(emptyList())
    val galleryItemsStateFlow = _galleryItemsStateFlow.asStateFlow()

    init {
        viewModelScope.launch {
            preferencesRepository.searchQueryFlow.collectLatest { searchQuery ->
                fetchPhotos(searchQuery)
            }
        }
    }

    private fun fetchPhotos(searchQuery: String) {
        viewModelScope.launch(Dispatchers.IO) {
            _galleryItemsStateFlow.value = if (searchQuery.isEmpty()) repository.getPhotos()
            else repository.getPhotosBySearchQuery(searchQuery)
        }
    }

    fun processSearchQuery(searchQuery: String) {
        viewModelScope.launch {
            preferencesRepository.saveSearchQuery(searchQuery)
        }
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