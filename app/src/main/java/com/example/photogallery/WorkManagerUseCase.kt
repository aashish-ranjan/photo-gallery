package com.example.photogallery

import android.content.Context
import androidx.work.Constraints
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import java.lang.IllegalStateException

class WorkManagerUseCase private constructor(context: Context) {

    private val workManager = WorkManager.getInstance(context)
    fun initiatePollingWorkRequest() {
        val pollingWorkConstraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.METERED)
            .setRequiresCharging(true)
            .build()
        val pollingWorkRequest = OneTimeWorkRequestBuilder<PhotosPollingWorker>()
            .addTag(PhotosPollingWorker.PHOTOS_POLLING_WORK)
            .setConstraints(pollingWorkConstraints)
            .build()
        workManager.enqueueUniqueWork(PhotosPollingWorker.PHOTOS_POLLING_WORK, ExistingWorkPolicy.REPLACE, pollingWorkRequest)
    }

    fun cancelPollingWorkRequest() {
        workManager.cancelUniqueWork(PhotosPollingWorker.PHOTOS_POLLING_WORK)
    }
    companion object {
        private var INSTANCE: WorkManagerUseCase? = null

        fun initialise(context: Context) {
            if (INSTANCE == null) {
                INSTANCE = WorkManagerUseCase(context)
            }
        }

        fun getInstance(): WorkManagerUseCase {
            return INSTANCE ?: throw IllegalStateException("WorkManagerUseCase must be initialised")
        }
    }

}