package com.pankxu.arenacompanion.core.storage

import android.content.Context
import java.io.File

/**
 * Manages the app's internal storage folder structure.
 *
 * Creates and tracks:
 *   skills/      — user skill definitions
 *   sessions/    — per-session data
 *   media/       — downloaded media
 *   cache/       — WebView and app cache
 *   cookies/     — app cookie metadata (never actual cookie values)
 *   .bin/        — binary artifacts
 *   temp/        — temporary files
 *
 * All data lives in /data/data/com.pankxu.arenacompanion/ (Android app-private storage).
 */
class StorageManager(context: Context) {

    private val rootDir = context.filesDir

    companion object {
        val FOLDER_NAMES = listOf(
            "skills", "sessions", "media", "cache", "cookies", ".bin", "temp",
        )
    }

    init {
        FOLDER_NAMES.forEach { name ->
            File(rootDir, name).mkdirs()
        }
    }

    fun rootDir(): File = rootDir

    fun folder(name: String): File {
        require(FOLDER_NAMES.contains(name)) { "unknown folder: $name" }
        return File(rootDir, name)
    }

    fun sessionFolder(sessionId: String): File {
        val dir = File(folder("sessions"), sessionId)
        dir.mkdirs()
        return dir
    }

    fun totalSize(): Long = rootDir.walkTopDown().filter { it.isFile }.sumOf { it.length() }

    fun folderSize(name: String): Long {
        if (!FOLDER_NAMES.contains(name)) return 0
        return File(rootDir, name).walkTopDown()
            .filter { it.isFile }.sumOf { it.length() }
    }

    fun folderSizes(): Map<String, Long> = FOLDER_NAMES.associateWith { folderSize(it) }

    fun clearFolder(name: String) {
        if (!FOLDER_NAMES.contains(name)) return
        File(rootDir, name).deleteRecursively()
        File(rootDir, name).mkdirs()
    }

    fun clearTemp() {
        clearFolder("temp")
    }

    fun clearCache() {
        clearFolder("cache")
    }

    fun formatSize(bytes: Long): String {
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
