package com.pankxu.arenacompanion.core.upload

import java.io.File

/**
 * MIME type resolver with extension and file-signature validation.
 * Separates Android-supported files from backend-supported files.
 */
object MimeTypeResolver {

    /** Supported image types. */
    val IMAGE_TYPES = setOf("image/png", "image/jpeg", "image/jpg", "image/webp", "image/gif", "image/bmp")

    /** Supported document types. */
    val DOCUMENT_TYPES = setOf("application/pdf", "text/plain", "text/markdown", "application/msword",
        "application/vnd.openxmlformats-officedocument.wordprocessingml.document", "application/rtf")

    /** Supported structured data types. */
    val DATA_TYPES = setOf("application/json", "application/xml", "text/xml", "text/csv", "application/yaml", "text/yaml")

    /** Supported development file types. */
    val CODE_TYPES = setOf("text/javascript", "text/jsx", "text/typescript", "text/tsx", "text/html",
        "text/css", "text/x-kotlin", "text/x-java", "text/x-python", "text/x-sql")

    /** All types the app can handle. */
    val ALL_TYPES = IMAGE_TYPES + DOCUMENT_TYPES + DATA_TYPES + CODE_TYPES

    /** Extension to MIME type mapping (pure Kotlin, no Android APIs). */
    private val EXTENSION_MAP = mapOf(
        "png" to "image/png",
        "jpg" to "image/jpeg",
        "jpeg" to "image/jpeg",
        "webp" to "image/webp",
        "gif" to "image/gif",
        "bmp" to "image/bmp",
        "pdf" to "application/pdf",
        "txt" to "text/plain",
        "md" to "text/markdown",
        "doc" to "application/msword",
        "docx" to "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
        "rtf" to "application/rtf",
        "json" to "application/json",
        "xml" to "application/xml",
        "csv" to "text/csv",
        "yaml" to "application/yaml",
        "yml" to "application/yaml",
        "js" to "text/javascript",
        "jsx" to "text/jsx",
        "ts" to "text/typescript",
        "tsx" to "text/tsx",
        "html" to "text/html",
        "css" to "text/css",
        "kt" to "text/x-kotlin",
        "java" to "text/x-java",
        "py" to "text/x-python",
        "sql" to "text/x-sql",
    )

    /** MIME type to extension mapping (first match). */
    private val MIME_TO_EXT = EXTENSION_MAP.entries.groupBy({ it.value }, { it.key }).mapValues { it.value.first() }

    fun detectMimeType(file: File): String {
        val ext = file.extension.lowercase()
        return EXTENSION_MAP[ext] ?: "application/octet-stream"
    }

    fun getMimeTypeForExtension(ext: String): String? {
        return EXTENSION_MAP[ext.lowercase()]
    }

    fun isSupported(mimeType: String): Boolean = ALL_TYPES.contains(mimeType)

    fun isImage(mimeType: String): Boolean = IMAGE_TYPES.contains(mimeType)

    fun isDocument(mimeType: String): Boolean = DOCUMENT_TYPES.contains(mimeType)

    fun isCode(mimeType: String): Boolean = CODE_TYPES.contains(mimeType)

    fun isData(mimeType: String): Boolean = DATA_TYPES.contains(mimeType)

    fun getExtension(mimeType: String): String? = MIME_TO_EXT[mimeType]

    /** Validates file signature for images (PNG, JPEG, WebP, GIF, BMP). */
    fun validateFileSignature(file: File): Boolean {
        return try {
            val bytes = file.inputStream().use { it.readBytes() }
            when {
                bytes.size < 8 -> false
                bytes[0].toInt() and 0xFF == 0x89 && bytes[1].toInt() and 0xFF == 'P'.code && bytes[2].toInt() and 0xFF == 'N'.code && bytes[3].toInt() and 0xFF == 'G'.code -> true
                bytes[0].toInt() and 0xFF == 0xFF && bytes[1].toInt() and 0xFF == 0xD8 -> true  // JPEG
                bytes[0].toInt() and 0xFF == 0x52 && bytes[1].toInt() and 0xFF == 'W'.code && bytes[2].toInt() and 0xFF == 'W'.code && bytes[3].toInt() and 0xFF == 'P'.code -> true  // WebP
                bytes[0].toInt() and 0xFF == 'G'.code && bytes[1].toInt() and 0xFF == 'I'.code && bytes[2].toInt() and 0xFF == 'F'.code -> true  // GIF
                bytes[0].toInt() and 0xFF == 'B'.code && bytes[1].toInt() and 0xFF == 'M'.code -> true  // BMP
                else -> true  // Other types rely on MIME type only
            }
        } catch (e: Exception) {
            false
        }
    }

    /** Returns a clear description of what each category supports. */
    fun supportedFormatsDescription(): String {
        return "Images: PNG, JPG, JPEG, WEBP, GIF, BMP\n" +
            "Documents: PDF, TXT, MD, DOC, DOCX, RTF\n" +
            "Data: JSON, XML, CSV, YAML\n" +
            "Code: JS, JSX, TS, TSX, HTML, CSS, Kotlin, Java, Python, SQL"
    }
}
