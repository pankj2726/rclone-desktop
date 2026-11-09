package com.pankxu.arenacompanion.feature.voice

import android.content.Context
import android.speech.tts.TextToSpeech
import com.pankxu.arenacompanion.core.logging.Logger
import java.util.Locale

class TextToSpeechManager(private val context: Context) {

    private val tag = "TTS"
    private var tts: TextToSpeech? = null
    private var isInitialized = false

    fun initialize(): Boolean {
        return try {
            tts = TextToSpeech(context.applicationContext) { status ->
                if (status == TextToSpeech.SUCCESS) {
                    val result = tts?.setLanguage(Locale.ENGLISH)
                    isInitialized = result != TextToSpeech.LANG_MISSING_DATA &&
                        result != TextToSpeech.LANG_NOT_SUPPORTED
                    if (isInitialized) {
                        Logger.i(tag, "TTS initialized successfully")
                    } else {
                        Logger.w(tag, "TTS language not supported")
                    }
                } else {
                    Logger.e(tag, "TTS initialization failed: status=$status")
                    isInitialized = false
                }
            }
            true
        } catch (e: Exception) {
            Logger.e(tag, "TTS initialization error", e)
            false
        }
    }

    fun speak(text: String, queueMode: Int = TextToSpeech.QUEUE_FLUSH) {
        if (!isInitialized || tts == null) {
            Logger.w(tag, "TTS not initialized, cannot speak")
            return
        }
        tts?.speak(text, queueMode, null, "utteranceId")
        Logger.i(tag, "Speaking: ${text.take(50)}...")
    }

    fun stop() {
        tts?.stop()
        Logger.i(tag, "TTS stopped")
    }

    fun isInitialized(): Boolean = isInitialized

    fun release() {
        tts?.stop()
        tts?.shutdown()
        tts = null
        isInitialized = false
        Logger.i(tag, "TTS released")
    }
}
