package com.pankxu.arenacompanion.core.config

/** Compile-time application configuration. */
object AppConfig {
    const val APP_NAME = "App"
    const val APP_VERSION = "1.0.0"
    const val APP_VERSION_CODE = 1

    /** Build type: "debug" or "release". */
    val BUILD_TYPE: String = BuildConfigManager.buildType

    val IS_DEBUG: Boolean = BUILD_TYPE == "debug"

    /** Base URL for Arena public site. */
    const val ARENA_BASE_URL = "https://arena.ai"

    /** Platform API base URL (used for diagnostics only, not for session auth). */
    const val PLATFORM_API_BASE = "https://api.arena.ai"

    /** Maximum upload size in bytes (50 MB default). */
    const val MAX_UPLOAD_SIZE = 50L * 1024 * 1024

    /** Arena session storage limit (128 MB). */
    const val ARENA_SESSION_LIMIT = 128L * 1024 * 1024

    /** Network timeout configuration. */
    const val CONNECT_TIMEOUT_MS = 30_000L
    const val READ_TIMEOUT_MS = 60_000L
    const val WRITE_TIMEOUT_MS = 60_000L

    /** Retry configuration. */
    const val MAX_RETRIES = 3
    const val RETRY_BASE_DELAY_MS = 1_000L

    /** Cache configuration. */
    const val CACHE_SIZE_MB = 50L
    const val CACHE_MAX_AGE_HOURS = 24L
}

/** Internal holder for build-type info (avoids BuildConfig access issues under AGP 9). */
object BuildConfigManager {
    val buildType: String = runCatching {
        javaClass.getDeclaredField("BUILD_TYPE")
    }.getOrNull()?.let { field ->
        field.isAccessible = true
        (field.get(null) as? String) ?: "debug"
    } ?: "debug"
}
