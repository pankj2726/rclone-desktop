package com.pankxu.arenacompanion.feature.voice

import android.media.MediaPlayer
import com.pankxu.arenacompanion.core.logging.Logger
import java.io.File

/** Audio playback manager. */
class AudioPlayer {

    private val tag = "AudioPlayer"
    private var player: MediaPlayer? = null

    fun play(file: File, onComplete: () -> Unit = {}) {
        return try {
            player?.stop()
            player?.release()
            player = MediaPlayer().apply {
                setDataSource(file.absolutePath)
                setOnCompletionListener {
                    Logger.i(tag, "Playback completed")
                    onComplete()
                }
                setOnErrorListener { mp, what, extra ->
                    Logger.e(tag, "Playback error: what=$what extra=$extra")
                    onComplete()
                    false
                }
                prepare()
                start()
            }
            Logger.i(tag, "Playing: ${file.name}")
        } catch (e: Exception) {
            Logger.e(tag, "Playback failed", e)
            onComplete()
        }
    }

    fun pause() {
        player?.pause()
        Logger.i(tag, "Paused")
    }

    fun resume() {
        player?.start()
        Logger.i(tag, "Resumed")
    }

    fun stop() {
        player?.let {
            if (it.isPlaying) it.stop()
            it.release()
        }
        player = null
        Logger.i(tag, "Stopped")
    }

    fun isPlaying(): Boolean = player?.isPlaying ?: false

    fun release() {
        stop()
    }
}
