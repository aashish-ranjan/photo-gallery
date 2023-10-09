package com.example.photogallery

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import com.example.photogallery.PhotosPollingWorker.Companion.PHOTOS_POLLING_CHANNEL_ID

fun createNotificationChannelIfApplicable(context: Context) {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return
    val channelName = context.resources.getString(R.string.channel_name)
    val channelDescription = context.resources.getString(R.string.channel_description)
    val importance = NotificationManager.IMPORTANCE_HIGH
    val mChannel = NotificationChannel(PHOTOS_POLLING_CHANNEL_ID, channelName, importance).apply {
        description = channelDescription
    }
    val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    notificationManager.createNotificationChannel(mChannel)
}
