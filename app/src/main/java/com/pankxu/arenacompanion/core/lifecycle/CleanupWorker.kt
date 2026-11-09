package com.pankxu.arenacompanion.core.lifecycle

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.pankxu.arenacompanion.core.logging.Logger
import com.pankxu.arenacompanion.core.storage.StorageManager
import java.io.File

/** Worker for cleaning up old temp files and cache. */
class CleanupWorker(
    context: Context,
    params: WorkerParameters,
) : CoroutineWorker(context, params) {

    private val tag = "CleanupWorker"

    override suspend fun doWork(): Result {
        val olderThan = inputData.getLong("older_than", 7L * 24 * 60 * 60 * 1000)
        val storage = StorageManager(applicationContext)

        val tempDir = storage.folder("temp")
        val now = System.currentTimeMillis()
        var count = 0

        tempDir.listFiles()?.forEach { file ->
            if (now - file.lastModified() > olderThan) {
                file.delete()
                count++
            }
        }

        Logger.i(tag, "Cleaned up $count old temp files")
        return Result.success()
    }
}
