package com.pankxu.arenacompanion.core.upload

import java.io.File

/**
 * Validates files before upload.
 * Separates:
 * 1. Android-supported files
 * 2. Backend-accepted files (not verified — no backend docs)
 * 3. Files requiring conversion
 * 4. Unsupported files
 */
class FileTypeValidator {

    enum class ValidationResult { VALID, INVALID_TYPE, INVALID_SIZE, INVALID_SIGNATURE, UNSUPPORTED }

    data class ValidationOutcome(
        val result: ValidationResult,
        val message: String,
        val category: FileCategory,
    )

    enum class FileCategory { IMAGE, DOCUMENT, DATA, CODE, OTHER }

    fun validate(file: File, maxSizeBytes: Long): ValidationOutcome {
        if (!file.exists()) {
            return ValidationOutcome(ValidationResult.INVALID_SIGNATURE, "File not found", FileCategory.OTHER)
        }

        if (file.length() > maxSizeBytes) {
            return ValidationOutcome(
                ValidationResult.INVALID_SIZE,
                "File too large: ${StorageFormat.format(file.length())} (max: ${StorageFormat.format(maxSizeBytes)})",
                categorize(file),
            )
        }

        val mimeType = MimeTypeResolver.detectMimeType(file)
        val category = categorizeFile(mimeType)

        if (category == FileCategory.OTHER) {
            return ValidationOutcome(
                ValidationResult.UNSUPPORTED,
                "Unsupported file type: ${file.extension.uppercase()}",
                FileCategory.OTHER,
            )
        }

        // Validate file signature for images
        if (category == FileCategory.IMAGE) {
            if (!MimeTypeResolver.validateFileSignature(file)) {
                return ValidationOutcome(
                    ValidationResult.INVALID_SIGNATURE,
                    "File signature does not match claimed type",
                    FileCategory.IMAGE,
                )
            }
        }

        return ValidationOutcome(ValidationResult.VALID, "File is valid", category)
    }

    private fun categorize(file: File): FileCategory = categorizeFile(MimeTypeResolver.detectMimeType(file))

    private fun categorizeFile(mimeType: String): FileCategory {
        if (MimeTypeResolver.isImage(mimeType)) return FileCategory.IMAGE
        if (MimeTypeResolver.isDocument(mimeType)) return FileCategory.DOCUMENT
        if (MimeTypeResolver.isData(mimeType)) return FileCategory.DATA
        if (MimeTypeResolver.isCode(mimeType)) return FileCategory.CODE
        return FileCategory.OTHER
    }
}

/** Utility for formatting sizes. */
object StorageFormat {
    fun format(bytes: Long): String {
        if (bytes == 0L) return "0 B"
        val units = listOf("B", "KB", "MB", "GB")
        var val_ = bytes.toDouble()
        var idx = 0
        while (val_ >= 1024 && idx < units.lastIndex) {
            val_ /= 1024
            idx++
        }
        return "%.1f ${units[idx]}".format(val_)
    }
}
