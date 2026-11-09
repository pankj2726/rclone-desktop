package com.pankxu.arenacompanion.navigation

import android.content.Context
import android.content.Intent
import android.net.Uri
import com.pankxu.arenacompanion.core.logging.Logger

/**
 * Deep link handler.
 * Validates that incoming URLs are arena.ai public pages (R1).
 * Does NOT handle arena.ai auth/chat/private paths — those route to Custom Tabs.
 */
class DeepLinkHandler(context: Context) {

    private val tag = "DeepLinkHandler"

    /** Validation logic — no Context/Android dependency, testable in JVM. */
    object Validator {
        val allowedHosts = setOf("arena.ai")
        val forbiddenPaths = setOf(
            "/api/", "/nextjs-api/", "/_next/", "/c/", "/agent/", "/images/", "/history/search"
        )

        fun validate(url: String): Boolean {
            if (!url.startsWith("https://")) return false
            // Extract host
            val afterScheme = url.substringAfter("https://")
            val host = afterScheme.substringBefore("/").substringBefore("?")
            if (!allowedHosts.contains(host)) return false
            // Check forbidden paths
            val path = url.substringAfter("https://${host}")
            val pathOnly = path.substringBefore("?").substringBefore("#")
            return forbiddenPaths.none { pathOnly.startsWith(it) }
        }

        fun shouldOpenInBrowser(url: String): Boolean {
            return url.contains("/auth/") || url.contains("/login") || url.contains("/signup")
        }
    }

    fun handleIntent(intent: Intent): Uri? {
        val data = intent.data ?: return null
        return validateAndExtract(data)
    }

    fun validateAndExtract(uri: Uri): Uri? {
        if (Validator.validate(uri.toString())) {
            Logger.i(tag, "Accepted deep link: $uri")
            return uri
        }
        Logger.w(tag, "Rejected deep link: $uri")
        return null
    }

    fun shouldOpenInBrowser(uri: Uri): Boolean = Validator.shouldOpenInBrowser(uri.toString())
}
