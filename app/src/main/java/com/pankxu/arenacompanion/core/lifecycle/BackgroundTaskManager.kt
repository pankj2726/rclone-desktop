package com.pankxu.arenacompanion.core.lifecycle

import android.content.Context
import androidx.work.Constraints
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.NetworkType
import com.pankxu.arenacompanion.core.logging.Logger

/**
 * Background task manager using WorkManager.
 * Handles:
 * - Upload workers
 * - Synchronization workers
 * - Cleanup workers
 * - Battery-aware scheduling
 */
class BackgroundTaskManager(context: Context) {

    private val tag = "BackgroundTask"
    private val workManager = WorkManager.getInstance(context.applicationContext)

    fun scheduleUpload(uploadId: String, filePath: String) {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val request = OneTimeWorkRequestBuilder<UploadWorker>()
            .setConstraints(constraints)
            .setInputData(androidx.work.Data.Builder()
                .putString("upload_id", uploadId)
                .putString("file_path", filePath)
                .build())
            .build()

        workManager.enqueueUniqueWork(
            "upload_$uploadId",
            ExistingWorkPolicy.REPLACE,
            request
        )
        Logger.i(tag, "Scheduled upload worker: $uploadId")
    }

    fun cancelUpload(uploadId: String) {
        workManager.cancelUniqueWork("upload_$uploadId")
        Logger.w(tag, "Cancelled upload worker: $uploadId")
    }

    fun scheduleCleanup(olderThanMillis: Long) {
        val request = OneTimeWorkRequestBuilder<CleanupWorker>()
            .setInputData(androidx.work.Data.Builder()
                .putLong("older_than", olderThanMillis)
                .build())
            .build()

        workManager.enqueueUniqueWork(
            "cleanup",
            ExistingWorkPolicy.REPLACE,
            request
        )
        Logger.i(tag, "Scheduled cleanup worker")
    }
}
