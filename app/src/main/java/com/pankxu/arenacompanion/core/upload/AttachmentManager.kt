package com.pankxu.arenacompanion.core.upload

import android.content.Context
import com.pankxu.arenacompanion.core.storage.StorageManager
import com.pankxu.arenacompanion.core.utils.IdGenerator

/**
 * Attachment manager. Handles:
 * - Copying selected files to app-private storage
 * - Managing attachment metadata
 * - Cleanup of temporary files
 */
class AttachmentManager(context: Context) {

    private val storageManager = StorageManager(context.applicationContext)

    data class PreparedAttachment(
        val id: String,
        val fileName: String,
        val originalPath: String,
        val localPath: String,
        val mimeType: String,
        val sizeBytes: Long,
    )

    fun prepare(originalPath: String, fileName: String): PreparedAttachment? {
        return runCatching {
            val source = java.io.File(originalPath)
            if (!source.exists()) {
                return null
            }

            val attId = IdGenerator.uuid()
            val tempDir = storageManager.folder("temp")
            val localPath = java.io.File(tempDir, "att_${attId}_$fileName").absolutePath
            source.copyTo(java.io.File(localPath))

            PreparedAttachment(
                id = attId,
                fileName = fileName,
                originalPath = originalPath,
                localPath = localPath,
                mimeType = MimeTypeResolver.detectMimeType(java.io.File(localPath)),
                sizeBytes = java.io.File(localPath).length(),
            )
        }.getOrNull()
    }

    fun cleanupAttachment(localPath: String) {
        runCatching { java.io.File(localPath).delete() }
    }

    fun cleanupStaleTempFiles(olderThanMillis: Long): Int {
        return runCatching {
            val tempDir = storageManager.folder("temp")
            val now = System.currentTimeMillis()
            var count = 0
            tempDir.listFiles()?.forEach { file ->
                if (now - file.lastModified() > olderThanMillis) {
                    file.delete()
                    count++
                }
            }
            count
        }.getOrDefault(0)
    }
}
