package com.pankxu.arenacompanion.security

import android.content.Context
import com.pankxu.arenacompanion.core.logging.Logger

/**
 * Biometric authentication manager.
 * Uses Android framework FingerprintManager (API 26+) for hardware detection
 * and enrollment status. Actual authentication is performed via BiometricPrompt
 * in the Compose layer.
 */
class BiometricManager(context: Context) {

    private val tag = "BiometricManager"
    private val appContext = context.applicationContext

    enum class BiometricStatus { UNAVAILABLE, UNABLE, READY }

    fun getStatus(): BiometricStatus {
        val fpm = appContext.getSystemService(Context.FINGERPRINT_SERVICE)
            as? android.hardware.fingerprint.FingerprintManager

        if (fpm == null || !fpm.isHardwareDetected) {
            return BiometricStatus.UNAVAILABLE
        }
        if (!fpm.hasEnrolledFingerprints()) {
            return BiometricStatus.UNABLE
        }
        return BiometricStatus.READY
    }

    fun isAvailable(): Boolean = getStatus() == BiometricStatus.READY
    fun hasEnrolledBiometrics(): Boolean = getStatus() == BiometricStatus.READY
    fun hasHardware(): Boolean {
        val fpm = appContext.getSystemService(Context.FINGERPRINT_SERVICE)
            as? android.hardware.fingerprint.FingerprintManager
        return fpm != null && fpm.isHardwareDetected
    }
}
