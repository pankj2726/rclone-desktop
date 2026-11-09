package com.pankxu.arenacompanion.feature.voice

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat

class AudioPermissionManager(private val context: Context) {

    fun hasRecordPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            context, Manifest.permission.RECORD_AUDIO
        ) == PackageManager.PERMISSION_GRANTED
    }

    fun requiredPermissions(): List<String> = listOf(Manifest.permission.RECORD_AUDIO)
}
