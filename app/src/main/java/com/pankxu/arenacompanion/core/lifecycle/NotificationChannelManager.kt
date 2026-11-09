package com.pankxu.arenacompanion.core.lifecycle

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build

/** Creates notification channels for the app. */
class NotificationChannelManager(private val context: Context) {

    companion object {
        const val CHANNEL_UPLOAD = "upload_notifications"
        const val CHANNEL_SYNC = "sync_notifications"
        const val CHANNEL_SESSION = "session_notifications"
    }

    fun createChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val nm = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            val uploadChannel = NotificationChannel(
                CHANNEL_UPLOAD,
                "Uploads",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Upload progress and completion"
            }

            val syncChannel = NotificationChannel(
                CHANNEL_SYNC,
                "Sync",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Background synchronization"
            }

            val sessionChannel = NotificationChannel(
                CHANNEL_SESSION,
                "Sessions",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Session updates and alerts"
            }

            nm.createNotificationChannels(listOf(uploadChannel, syncChannel, sessionChannel))
        }
    }
}
