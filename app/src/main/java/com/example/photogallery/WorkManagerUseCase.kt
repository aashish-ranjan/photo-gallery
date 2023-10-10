package com.example.photogallery

import android.content.Context
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import java.lang.IllegalStateException
import java.util.concurrent.TimeUnit

class WorkManagerUseCase private constructor(context: Context) {

    private val workManager = WorkManager.getInstance(context)
    fun initiatePollingWorkRequest() {
        val pollingWorkConstraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.UNMETERED)
            .setRequiresCharging(true)
            .build()
        val pollingWorkRequest =
            PeriodicWorkRequestBuilder<PhotosPollingWorker>(15, TimeUnit.MINUTES)
                .addTag(PhotosPollingWorker.PHOTOS_POLLING_WORK)
                .setConstraints(pollingWorkConstraints)
                .build()
        workManager.enqueueUniquePeriodicWork(
            PhotosPollingWorker.PHOTOS_POLLING_WORK,
            ExistingPeriodicWorkPolicy.CANCEL_AND_REENQUEUE,
            pollingWorkRequest
        )
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