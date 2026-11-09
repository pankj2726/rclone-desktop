package com.pankxu.arenacompanion.core.utils

import kotlinx.coroutines.CancellationException

/** Standard result type for operations that can fail. */
sealed class AppResult<out T> {
    data class Success<T>(val data: T) : AppResult<T>()
    data class Failure(val error: AppError) : AppResult<Nothing>()

    fun isSuccess(): Boolean = this is Success
    fun isFailure(): Boolean = this is Failure
    fun dataOrNull(): T? = (this as? Success)?.data
    fun errorOrNull(): AppError? = (this as? Failure)?.error

    fun getOrNull(): T? = dataOrNull()

    suspend fun fold(onSuccess: suspend (T) -> Unit, onFailure: suspend (AppError) -> Unit) {
        when (this) {
            is Success -> onSuccess(data)
            is Failure -> onFailure(error)
        }
    }
}

/** Typed application error. */
data class AppError(
    val code: Int,
    val message: String,
    val throwable: Throwable? = null,
) {
    companion object {
        const val UNKNOWN = 0
        const val NETWORK = 1
        const val AUTH = 2
        const val VALIDATION = 3
        const val UPLOAD = 4
        const val OFFLINE = 5
        const val TIMEOUT = 6
        const val BLOCKED = 7
        const val CANCELLED = 8
    }
}

/** Convenience extension for try/catch to AppResult. */
inline fun <T> runSafe(block: () -> T): AppResult<T> = try {
    AppResult.Success(block())
} catch (e: CancellationException) {
    AppResult.Failure(AppError(AppError.CANCELLED, "Operation cancelled", e))
} catch (e: Exception) {
    AppResult.Failure(AppError(AppError.UNKNOWN, e.message ?: "Unknown error", e))
}

inline suspend fun <T> runSafeSuspend(block: suspend () -> T): AppResult<T> = try {
    AppResult.Success(block())
} catch (e: CancellationException) {
    AppResult.Failure(AppError(AppError.CANCELLED, "Operation cancelled", e))
} catch (e: Exception) {
    AppResult.Failure(AppError(AppError.UNKNOWN, e.message ?: "Unknown error", e))
}
