package com.pankxu.arenacompanion.core.storage

import android.content.Context
import com.pankxu.arenacompanion.core.logging.Logger
import java.io.File

/** Manages app cache — WebView cache, image cache, temp files. */
class CacheManager(context: Context) {

    private val cacheDir = context.cacheDir
    private val webViewDir = context.getDir("webview", Context.MODE_PRIVATE)
    private val tag = "CacheManager"

    fun getCacheSize(): Long = cacheDir.walkTopDown().filter { it.isFile }.sumOf { it.length() }

    fun getWebViewCacheSize(): Long = webViewDir.walkTopDown().filter { it.isFile }.sumOf { it.length() }

    fun getTotalCacheSize(): Long = getCacheSize() + getWebViewCacheSize()

    fun clearAll() {
        Logger.i(tag, "Clearing all cache (${formatSize(getTotalCacheSize())})")
        cacheDir.deleteRecursively()
        cacheDir.mkdirs()
        webViewDir.deleteRecursively()
        webViewDir.mkdirs()
    }

    fun clearWebViewCache() {
        Logger.i(tag, "Clearing WebView cache")
        webViewDir.deleteRecursively()
        webViewDir.mkdirs()
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
