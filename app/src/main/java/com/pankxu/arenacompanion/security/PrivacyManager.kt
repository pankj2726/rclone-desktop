package com.pankxu.arenacompanion.security

class PrivacyManager {

    var dataCollectionEnabled: Boolean = false
    var crashReportingEnabled: Boolean = true
    var biometricProtectionEnabled: Boolean = false

    fun redact(text: String): String {
        val patterns = listOf(
            Regex("sk-arena-[A-Za-z0-9]+"),
            Regex("Bearer\\s+[A-Za-z0-9\\-._~+/]+=*"),
            Regex("(?i)(token|secret|password|authorization|api[_-]?key)[\"\\s:=]+[\"']?[\\w\\-.]+"),
        )
        var result = text
        for (p in patterns) {
            result = p.replaceFirst(result, "[REDACTED]")
        }
        return result
    }

    fun describePrivacyPolicy(): String =
        "Your data stays on this device. No analytics, no third-party tracking. " +
        "Credentials are stored encrypted with Android Keystore."
}
