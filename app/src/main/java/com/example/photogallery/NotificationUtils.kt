package com.example.photogallery

import android.Manifest
import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
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

@SuppressLint("MissingPermission")
fun showNewPhotosAvailableNotification(context: Context) {
    val title = context.getString(R.string.new_photos_available_title)
    val description = context.getText(R.string.new_photos_available_description)
    val pendingIntent = PendingIntent.getActivity(
        context,
        0,
        MainActivity.createIntent(context),
        PendingIntent.FLAG_IMMUTABLE
    )
    val notification = NotificationCompat.Builder(context.applicationContext, PHOTOS_POLLING_CHANNEL_ID)
        .setSmallIcon(R.drawable.ic_new_photo_update)
        .setTicker(title)
        .setContentTitle(title)
        .setContentText(description)
        .setContentIntent(pendingIntent)
        .setAutoCancel(true)
        .build()

    val notificationManager = NotificationManagerCompat.from(context)
    if (areNotificationsEnabled(context)) {
        notificationManager.notify(0, notification)
    }
}

private fun areNotificationsEnabled(context: Context): Boolean {
    val notificationManager = NotificationManagerCompat.from(context)
    var areNotificationsEnabled = notificationManager.areNotificationsEnabled()
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        areNotificationsEnabled = areNotificationsEnabled && notificationManager.getNotificationChannel(PHOTOS_POLLING_CHANNEL_ID)?.importance != NotificationManager.IMPORTANCE_NONE
    }
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        areNotificationsEnabled = areNotificationsEnabled && ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED
    }
    return areNotificationsEnabled
}
