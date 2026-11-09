package com.pankxu.arenacompanion.core.network

import com.pankxu.arenacompanion.core.config.AppConfig
import com.pankxu.arenacompanion.core.utils.AppError
import kotlinx.coroutines.delay

/** Retry policy with exponential backoff. Never retries non-idempotent POST blindly. */
class RetryPolicy(
    private val maxRetries: Int = AppConfig.MAX_RETRIES,
    private val baseDelayMs: Long = AppConfig.RETRY_BASE_DELAY_MS,
) {

    suspend fun retryWithBackoff(
        operation: suspend () -> com.pankxu.arenacompanion.core.utils.AppResult<*>,
        isIdempotent: Boolean = true,
    ): com.pankxu.arenacompanion.core.utils.AppResult<*> {
        var attempt = 0
        var lastResult: com.pankxu.arenacompanion.core.utils.AppResult<*> =
            com.pankxu.arenacompanion.core.utils.AppResult.Failure(AppError(AppError.UNKNOWN, "No result"))

        while (attempt <= maxRetries) {
            lastResult = operation()

            if (lastResult.isSuccess()) return lastResult

            val error = (lastResult as com.pankxu.arenacompanion.core.utils.AppResult.Failure).error

            if (!isIdempotent || error.code == AppError.AUTH || error.code == AppError.VALIDATION) {
                return lastResult  // don't retry auth failures or non-idempotent ops
            }

            if (!NetworkErrorHandler.isRetryable(error)) {
                return lastResult
            }

            attempt++
            if (attempt > maxRetries) break

            val delayMs = baseDelayMs * (1L shl (attempt - 1))  // exponential backoff
            kotlinx.coroutines.delay(delayMs)
        }

        return lastResult
    }
}
