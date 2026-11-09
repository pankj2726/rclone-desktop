package com.pankxu.arenacompanion.feature.voice

import com.pankxu.arenacompanion.core.logging.Logger
import com.pankxu.arenacompanion.core.utils.AppError
import com.pankxu.arenacompanion.core.utils.AppResult

/**
 * Speech-to-text manager.
 *
 * NOTE: STT requires backend API support. This provides the abstraction
 * and infrastructure. Actual transcription is performed via the Arena
 * Platform API (requires API key and backend integration).
 */
class SpeechToTextManager {

    private val tag = "STT"

    /** Checks if STT is available (requires backend API + API key). */
    fun isAvailable(): Boolean = false  // TODO: check backend capability

    /**
     * Transcribe audio bytes to text.
     * Requires backend API support.
     */
    suspend fun transcribe(
        audioBytes: ByteArray,
        mimeType: String,
        apiKey: String,
    ): AppResult<String> {
        // TODO: POST to Arena API for transcription
        // Currently returns not available
        Logger.w(tag, "STT not available — requires backend API integration")
        return AppResult.Failure(AppError(AppError.UNKNOWN, "Speech-to-text not available"))
    }
}
