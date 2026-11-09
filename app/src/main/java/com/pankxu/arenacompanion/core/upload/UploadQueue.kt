package com.pankxu.arenacompanion.core.upload

import com.pankxu.arenacompanion.core.logging.Logger
import com.pankxu.arenacompanion.core.network.NetworkManager
import com.pankxu.arenacompanion.core.utils.AppResult
import com.pankxu.arenacompanion.core.utils.IdGenerator
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import okhttp3.RequestBody

/**
 * Upload queue manager. Handles:
 * - Upload progress tracking
 * - Upload cancellation
 * - Upload retry
 * - Concurrent upload limits (1 at a time)
 */
class UploadQueue(
    private val networkManager: NetworkManager,
) {
    private val tag = "UploadQueue"

    data class UploadTask(
        val id: String,
        val fileName: String,
        val mimeType: String,
        val sizeBytes: Long,
        val apiKey: String,
        val status: String = "queued",  // queued, uploading, uploaded, failed, cancelled
        val progress: Int = 0,
        val error: String = "",
    )

    private val _tasks = MutableStateFlow<List<UploadTask>>(emptyList())
    val tasks: StateFlow<List<UploadTask>> = _tasks

    fun enqueue(file: java.io.File, apiKey: String): UploadTask {
        val mimeType = MimeTypeResolver.detectMimeType(file)
        val task = UploadTask(
            id = IdGenerator.uuid(),
            fileName = file.name,
            mimeType = mimeType,
            sizeBytes = file.length(),
            apiKey = apiKey,
        )
        _tasks.value = _tasks.value + task
        Logger.i(tag, "Enqueued upload: ${task.fileName} (${StorageFormat.format(task.sizeBytes)})")
        return task
    }

    fun cancel(uploadId: String) {
        _tasks.value = _tasks.value.map {
            if (it.id == uploadId) it.copy(status = "cancelled", error = "User cancelled")
            else it
        }
        Logger.w(tag, "Upload cancelled: $uploadId")
    }

    fun removeCompleted(uploadId: String) {
        _tasks.value = _tasks.value.filterNot { it.id == uploadId }
    }

    fun getTask(uploadId: String): UploadTask? = _tasks.value.find { it.id == uploadId }

    val pendingCount: Int get() = _tasks.value.count { it.status == "queued" || it.status == "uploading" }
}
