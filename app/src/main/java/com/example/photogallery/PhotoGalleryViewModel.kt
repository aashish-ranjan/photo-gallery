package com.example.photogallery

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.photogallery.model.Photo
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch
import java.lang.IllegalArgumentException

@OptIn(ExperimentalCoroutinesApi::class)
class PhotoGalleryViewModel(private val repository: PhotosRepository) : ViewModel() {
    private val preferencesRepository = PreferencesRepository.getInstance()
    private val _pollingEnabledFlow = MutableStateFlow(false)
    val pollingEnabledFlow = _pollingEnabledFlow.asStateFlow()

    val searchQueryResultsFlow: Flow<PagingData<Photo>> =
        preferencesRepository.searchQueryFlow.flatMapLatest { searchQuery ->
            repository.getPhotos(searchQuery)
        }.cachedIn(viewModelScope)

    init {
        viewModelScope.launch {
            preferencesRepository.pollingEnabledFlow.collectLatest { pollingEnabled ->
                _pollingEnabledFlow.value = pollingEnabled
            }
        }

    }

    fun processSearchQuery(searchQuery: String) {
        viewModelScope.launch {
            preferencesRepository.saveSearchQuery(searchQuery)
        }
    }

    fun togglePollingStatus() {
        viewModelScope.launch {
            preferencesRepository.savePollingPreference(!preferencesRepository.pollingEnabledFlow.first())
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