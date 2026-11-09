package com.pankxu.arenacompanion.core.auth

/**
 * Tracks token metadata (expiration, type, creation time).
 * Does NOT store token values — those are in CredentialVault.
 */
class TokenManager {

    data class TokenMeta(
        val id: String,
        val name: String,
        val type: String,
        val createdAt: Long,
        val expiresAt: Long,
        val isExpired: Boolean,
    )

    fun isExpired(expiresAt: Long, now: Long = System.currentTimeMillis()): Boolean {
        if (expiresAt <= 0) return false  // no expiry
        return now >= expiresAt
    }

    fun formatExpiry(expiresAt: Long): String {
        if (expiresAt <= 0) return "No expiry"
        val now = System.currentTimeMillis()
        if (expiresAt < now) return "Expired"
        val diffMs = expiresAt - now
        val hours = diffMs / (1000 * 60 * 60)
        val days = hours / 24
        return if (days > 0) "${days}d ${hours % 24}h remaining"
        else "${hours}h remaining"
    }
}
