package com.pankxu.arenacompanion.feature.voice

import android.content.Context
import android.media.MediaRecorder
import com.pankxu.arenacompanion.core.logging.Logger
import java.io.File

class VoiceManager(context: Context) {

    private val tag = "VoiceManager"
    private var recorder: MediaRecorder? = null
    private var isRecording = false
    private var outputFile: File? = null

    fun startRecording(outputFile: File): Boolean {
        return try {
            this.outputFile = outputFile
            recorder = MediaRecorder().apply {
                setAudioSource(MediaRecorder.AudioSource.MIC)
                setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
                setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
                setOutputFile(outputFile.absolutePath)
                prepare()
                start()
            }
            isRecording = true
            Logger.i(tag, "Recording started: ${outputFile.absolutePath}")
            true
        } catch (e: Exception) {
            Logger.e(tag, "Recording failed", e)
            false
        }
    }

    fun stopRecording(): File? {
        return try {
            recorder?.let {
                it.stop()
                it.release()
            }
            recorder = null
            isRecording = false
            val file = outputFile
            Logger.i(tag, "Recording stopped: ${file?.absolutePath}")
            file
        } catch (e: Exception) {
            Logger.e(tag, "Stop recording failed", e)
            null
        }
    }

    fun isRecording(): Boolean = isRecording

    fun cancelRecording() {
        try {
            recorder?.apply {
                stop()
                release()
            }
        } catch (e: Exception) {
            Logger.w(tag, "Cancel recording failed: ${e.message}")
        }
        recorder = null
        isRecording = false
    }
}
