package com.example.photogallery

import android.Manifest
import android.annotation.SuppressLint
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import kotlinx.coroutines.flow.first
import java.lang.Exception

class PhotosPollingWorker(private val context: Context, parameters: WorkerParameters): CoroutineWorker(context, parameters) {
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
                    showNewPhotosAvailableNotification()
                }
            }
            return Result.success()
        } catch (e: Exception) {
            return Result.failure()
        }
    }

    @SuppressLint("MissingPermission")
    private fun showNewPhotosAvailableNotification() {
        val title = context.getString(R.string.new_photos_available_title)
        val description = context.getText(R.string.new_photos_available_description)
        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            MainActivity.createIntent(context),
            PendingIntent.FLAG_IMMUTABLE
        )
        val notification = NotificationCompat.Builder(applicationContext, PHOTOS_POLLING_CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_new_photo_update)
            .setTicker(title)
            .setContentTitle(title)
            .setContentText(description)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        val notificationManager = NotificationManagerCompat.from(context)
        if (areNotificationsEnabled(notificationManager)) {
            notificationManager.notify(0, notification)
        }
    }

    private fun areNotificationsEnabled(notificationManager: NotificationManagerCompat): Boolean {
        var areNotificationsEnabled = notificationManager.areNotificationsEnabled()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            areNotificationsEnabled = areNotificationsEnabled && notificationManager.getNotificationChannel(PHOTOS_POLLING_CHANNEL_ID)?.importance != NotificationManager.IMPORTANCE_NONE
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            areNotificationsEnabled = areNotificationsEnabled && ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED
        }
        return areNotificationsEnabled
    }

    companion object {
        private const val TAG = "PhotosPollingWorker"
        const val PHOTOS_POLLING_CHANNEL_ID = "photos_polling_channel_id"
        const val PHOTOS_POLLING_WORK = "photosPolling"
    }
}

