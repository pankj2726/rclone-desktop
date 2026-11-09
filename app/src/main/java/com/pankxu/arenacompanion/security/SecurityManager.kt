package com.pankxu.arenacompanion.security

import android.content.Context
import com.pankxu.arenacompanion.core.logging.Logger

/**
 * Unified security facade. Provides:
 * - Keystore-backed encryption (via EncryptionManager)
 * - Secure file storage (via SecureStorage)
 * - Biometric authentication (via BiometricManager)
 * - Integrity checks (via IntegrityManager)
 */
class SecurityManager(context: Context) {

    val encryptionManager = EncryptionManager(context)
    val secureStorage = SecureStorage(context)
    val biometricManager = BiometricManager(context)
    val privacyManager = PrivacyManager()

    fun secureLog(tag: String, msg: String) {
        Logger.i(tag, msg)
    }

    fun secureLogError(tag: String, msg: String, throwable: Throwable? = null) {
        Logger.e(tag, msg, throwable)
    }
}
