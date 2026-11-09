package com.pankxu.arenacompanion.security

import android.content.Context
import android.content.pm.PackageManager

class IntegrityManager(private val context: Context) {

    fun verifyPackageIntegrity(): Boolean {
        return try {
            context.packageManager.getPackageInfo(
                context.packageName,
                PackageManager.GET_SIGNING_CERTIFICATES
            )
            true
        } catch (e: Exception) {
            false
        }
    }

    fun getApkHash(): String? {
        return try {
            val apkFile = context.applicationInfo.sourceDir
            java.io.File(apkFile).inputStream().use { input ->
                val digest = java.security.MessageDigest.getInstance("SHA-256")
                val buffer = ByteArray(8192)
                while (input.available() > 0) {
                    val bytes = input.read(buffer)
                    digest.update(buffer, 0, bytes)
                }
                digest.digest().joinToString("") { "%02x".format(it) }
            }
        } catch (e: Exception) {
            null
        }
    }

    fun getVersionName(): String? {
        return try {
            val info = context.packageManager.getPackageInfo(context.packageName, 0)
            info.versionName
        } catch (e: Exception) {
            null
        }
    }
}
