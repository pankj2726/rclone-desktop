package com.pankxu.arenacompanion.feature.notifications

import android.app.Notification
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.pankxu.arenacompanion.R
import com.pankxu.arenacompanion.core.lifecycle.NotificationChannelManager
import com.pankxu.arenacompanion.core.logging.Logger

/** Manages app notifications. */
class AppNotificationManager(private val context: Context) {

    private val tag = "Notifications"

    fun sendUploadProgress(uploadId: String, progress: Int, fileName: String) {
        if (!NotificationManagerCompat.from(context).areNotificationsEnabled()) return

        val notification = NotificationCompat.Builder(context, NotificationChannelManager.CHANNEL_UPLOAD)
            .setContentTitle("Uploading $fileName")
            .setContentText("Progress: $progress%")
            .setSmallIcon(android.R.drawable.stat_sys_upload)
            .setProgress(100, progress, false)
            .setOngoing(true)
            .setContentIntent(getPendingIntent())
            .build()

        NotificationManagerCompat.from(context).notify(uploadId.hashCode(), notification)
    }

    fun sendUploadComplete(uploadId: String, fileName: String) {
        if (!NotificationManagerCompat.from(context).areNotificationsEnabled()) return

        val notification = NotificationCompat.Builder(context, NotificationChannelManager.CHANNEL_UPLOAD)
            .setContentTitle("Upload complete")
            .setContentText("$fileName uploaded successfully")
            .setSmallIcon(android.R.drawable.stat_sys_upload_done)
            .setAutoCancel(true)
            .setContentIntent(getPendingIntent())
            .build()

        NotificationManagerCompat.from(context).notify(uploadId.hashCode(), notification)
    }

    fun sendUploadFailed(uploadId: String, fileName: String, error: String) {
        if (!NotificationManagerCompat.from(context).areNotificationsEnabled()) return

        val notification = NotificationCompat.Builder(context, NotificationChannelManager.CHANNEL_UPLOAD)
            .setContentTitle("Upload failed")
            .setContentText("$fileName: $error")
            .setSmallIcon(android.R.drawable.stat_sys_upload)
            .setAutoCancel(true)
            .setContentIntent(getPendingIntent())
            .build()

        NotificationManagerCompat.from(context).notify(uploadId.hashCode(), notification)
    }

    fun sendSessionAlert(message: String) {
        if (!NotificationManagerCompat.from(context).areNotificationsEnabled()) return

        val notification = NotificationCompat.Builder(context, NotificationChannelManager.CHANNEL_SESSION)
            .setContentTitle("Session alert")
            .setContentText(message)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setAutoCancel(true)
            .setContentIntent(getPendingIntent())
            .build()

        NotificationManagerCompat.from(context).notify("session_alert", notification)
    }

    fun cancelNotification(id: String) {
        NotificationManagerCompat.from(context).cancel(id.hashCode())
    }

    private fun getPendingIntent(): PendingIntent {
        val intent = Intent(context, com.pankxu.arenacompanion.MainActivity::class.java)
        return PendingIntent.getActivity(
            context, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }
}
