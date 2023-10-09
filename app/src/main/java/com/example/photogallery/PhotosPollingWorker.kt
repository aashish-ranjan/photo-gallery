package com.example.photogallery

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import kotlinx.coroutines.flow.first
import java.lang.Exception

class PhotosPollingWorker(context: Context, parameters: WorkerParameters): CoroutineWorker(context, parameters) {
    override suspend fun doWork(): Result {
        val preferencesRepository = PreferencesRepository.getInstance()
        val photosRepository = PhotosRepository.getInstance()
        try {
            val savedSearchQuery = preferencesRepository.searchQueryFlow.first()
            if (savedSearchQuery.isEmpty()) {
                Log.i(TAG, "No saved query, finishing early")
                return Result.success()
            }
            val photosList = photosRepository.getPhotos(savedSearchQuery)
            if (photosList.isNotEmpty()) {
                val firstPhotoId = photosList.first().id
                val lastFetchPhotoId = preferencesRepository.lastFetchPhotoId.first()
                if (firstPhotoId == lastFetchPhotoId) {
                    Log.i(TAG, "No new photos uploaded")
                } else {
                    preferencesRepository.saveLastFetchPhotoId(firstPhotoId)
                }
            }
            return Result.success()
        } catch (e: Exception) {
            return Result.failure()
        }
    }

    companion object {
        private const val TAG = "PhotosPollingWorker"
        const val PHOTOS_POLLING_WORK = "photosPolling"
    }
}

