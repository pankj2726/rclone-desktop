package com.pankxu.arenacompanion.security

import android.content.Context
import java.io.File

/**
 * Secure file storage using Android Keystore encryption.
 * Files are encrypted at rest and only decrypted when explicitly accessed.
 */
class SecureStorage(context: Context) {

    private val secureDir = File(context.filesDir, "secure")
    private val encryptionManager = EncryptionManager(context)

    init {
        secureDir.mkdirs()
    }

    fun saveEncrypted(name: String, data: String): Boolean {
        return runCatching {
            val encrypted = encryptionManager.encrypt(data)
            File(secureDir, "$name.enc").writeText(encrypted)
        }.isSuccess
    }

    fun loadDecrypted(name: String): String? {
        return runCatching {
            val file = File(secureDir, "$name.enc")
            if (!file.exists()) return null
            val encrypted = file.readText()
            encryptionManager.decrypt(encrypted)
        }.getOrNull()
    }

    fun delete(name: String): Boolean {
        return runCatching {
            File(secureDir, "$name.enc").delete()
        }.isSuccess
    }

    fun listSecureFiles(): List<String> =
        secureDir.listFiles()?.map { it.nameWithoutExtension }?.sorted() ?: emptyList()
}
