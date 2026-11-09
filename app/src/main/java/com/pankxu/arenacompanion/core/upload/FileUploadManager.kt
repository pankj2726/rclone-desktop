package com.pankxu.arenacompanion.core.upload

import android.content.Context
import com.pankxu.arenacompanion.core.config.AppConfig
import com.pankxu.arenacompanion.core.logging.Logger
import com.pankxu.arenacompanion.core.network.NetworkManager
import com.pankxu.arenacompanion.core.storage.StorageManager
import com.pankxu.arenacompanion.core.utils.AppError
import com.pankxu.arenacompanion.core.utils.AppResult
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody
import okhttp3.Response

class FileUploadManager(
    context: Context,
    private val networkManager: NetworkManager,
) {

    private val tag = "FileUploadManager"
    private val uploadQueue = UploadQueue(networkManager)
    private val validator = FileTypeValidator()
    private val storageManager = StorageManager(context.applicationContext)

    val queue: UploadQueue = uploadQueue

    fun validateFile(file: java.io.File): FileTypeValidator.ValidationOutcome {
        return validator.validate(file, AppConfig.MAX_UPLOAD_SIZE)
    }

    fun prepareForUpload(file: java.io.File): java.io.File? {
        return runCatching {
            val validation = validateFile(file)
            if (validation.result != FileTypeValidator.ValidationResult.VALID) {
                Logger.w(tag, "File validation failed: ${validation.message}")
                return null
            }
            val tempDir = storageManager.folder("temp")
            val tempFile = java.io.File(tempDir, "upload_${System.currentTimeMillis()}_${file.name}")
            file.copyTo(tempFile)
            Logger.d(tag, "File prepared for upload: ${tempFile.absolutePath}")
            tempFile
        }.getOrNull()
    }

    suspend fun uploadFile(
        file: java.io.File,
        apiKey: String,
    ): AppResult<Response> {
        val validation = validateFile(file)
        if (validation.result != FileTypeValidator.ValidationResult.VALID) {
            return AppResult.Failure(AppError(AppError.VALIDATION, validation.message))
        }

        val mimeType = MimeTypeResolver.detectMimeType(file)
        val mediaType = mimeType.toMediaType()
        val requestBody = RequestBody.create(mediaType, file)

        Logger.i(tag, "Uploading ${file.name} ($mimeType, ${StorageFormat.format(file.length())})")

        return networkManager.postMultipart(
            "/v1/files",
            requestBody,
            file.name,
            mimeType,
            apiKey,
        )
    }
}
