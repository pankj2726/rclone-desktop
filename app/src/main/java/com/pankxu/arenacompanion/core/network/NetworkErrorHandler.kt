package com.pankxu.arenacompanion.core.network

import com.pankxu.arenacompanion.core.logging.Logger
import com.pankxu.arenacompanion.core.utils.AppError
import okhttp3.Response

/** Classifies HTTP errors and maps them to AppError. */
object NetworkErrorHandler {

    private val tag = "NetworkError"

    fun classify(response: Response): AppError {
        val code = response.code
        val message = response.message

        when (code) {
            401 -> { Logger.w(tag, "401 Unauthorized"); return AppError(AppError.AUTH, "Authentication required") }
            403 -> { Logger.w(tag, "403 Forbidden"); return AppError(AppError.AUTH, "Access forbidden") }
            404 -> { Logger.w(tag, "404 Not found"); return AppError(AppError.UNKNOWN, "Resource not found") }
            409 -> { Logger.w(tag, "409 Conflict"); return AppError(AppError.UNKNOWN, "Conflict") }
            429 -> {
                val retryAfter = response.header("Retry-After")?.toLongOrNull() ?: 0L
                Logger.w(tag, "429 Rate limited (retry-after: ${retryAfter}s)")
                return AppError(AppError.UNKNOWN, "Rate limited. Retry after ${retryAfter}s")
            }
            500, 502, 503, 504 -> {
                Logger.e(tag, "Server error $code: $message")
                return AppError(AppError.NETWORK, "Server error ($code)")
            }
            else -> {
                Logger.w(tag, "HTTP $code: $message")
                return AppError(AppError.UNKNOWN, "HTTP $code: $message")
            }
        }
    }

    fun isRetryable(error: AppError): Boolean {
        return error.code == AppError.NETWORK ||
            error.code == AppError.TIMEOUT ||
            error.code == AppError.OFFLINE
    }

    fun isCancelled(error: AppError): Boolean = error.code == AppError.CANCELLED
}
