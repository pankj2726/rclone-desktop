package com.pankxu.arenacompanion.feature.voice

import com.pankxu.arenacompanion.core.logging.Logger
import com.pankxu.arenacompanion.core.utils.IdGenerator

/** Manages voice input/output sessions. */
class VoiceSessionManager(
    private val recorder: VoiceManager,
    private val stt: SpeechToTextManager,
    private val tts: TextToSpeechManager,
    private val player: AudioPlayer,
) {

    private val tag = "VoiceSession"
    private var sessionActive = false
    private var currentSessionId = ""

    fun startSession(): String {
        if (sessionActive) {
            Logger.w(tag, "Voice session already active")
            return currentSessionId
        }
        sessionActive = true
        currentSessionId = IdGenerator.uuid()
        Logger.i(tag, "Voice session started: $currentSessionId")
        return currentSessionId
    }

    fun endSession() {
        if (!sessionActive) return
        recorder.cancelRecording()
        player.stop()
        tts.stop()
        sessionActive = false
        Logger.i(tag, "Voice session ended: $currentSessionId")
    }

    fun isSessionActive(): Boolean = sessionActive

    fun getCurrentSessionId(): String = currentSessionId
}
