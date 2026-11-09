package com.pankxu.arenacompanion.core.network

import android.content.Context
import com.pankxu.arenacompanion.core.logging.Logger
import com.pankxu.arenacompanion.core.utils.AppError
import com.pankxu.arenacompanion.core.utils.AppResult
import okhttp3.RequestBody
import okhttp3.Response

/**
 * High-level network manager. Wraps ApiClient with retry logic and connectivity checks.
 */
class NetworkManager(context: Context) {

    private val tag = "NetworkManager"
    private val apiClient = ApiClient(context)
    private val connectivityMonitor = ConnectivityMonitor(context)
    private val retryPolicy = RetryPolicy()

    suspend fun get(path: String, headers: Map<String, String> = emptyMap()): AppResult<Response> {
        if (!connectivityMonitor.isCurrentlyOnline()) {
            return AppResult.Failure(AppError(AppError.OFFLINE, "No network connection"))
        }
        Logger.d(tag, "GET $path")
        return retryPolicy.retryWithBackoff({ apiClient.get(path, headers) }, isIdempotent = true) as AppResult<Response>
    }

    suspend fun postJson(path: String, json: String, apiKey: String): AppResult<Response> {
        if (!connectivityMonitor.isCurrentlyOnline()) {
            return AppResult.Failure(AppError(AppError.OFFLINE, "No network connection"))
        }
        Logger.d(tag, "POST $path")
        return retryPolicy.retryWithBackoff({ apiClient.postJson(path, json, apiKey) }, isIdempotent = false) as AppResult<Response>
    }

    suspend fun postMultipart(
        path: String,
        fileBody: RequestBody,
        filename: String,
        mimeType: String,
        apiKey: String,
    ): AppResult<Response> {
        Logger.d(tag, "POST multipart $path ($filename)")
        return retryPolicy.retryWithBackoff(
            { apiClient.postMultipart(path, fileBody, filename, mimeType, apiKey) },
            isIdempotent = false
        ) as AppResult<Response>
    }

    fun shutdown() {
        apiClient.shutdown()
    }
}
