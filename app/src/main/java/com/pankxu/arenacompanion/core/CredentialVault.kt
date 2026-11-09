package com.pankxu.arenacompanion.core

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

/**
 * A credential the user explicitly stores in the app.
 *
 * The app NEVER extracts credentials from the browser, WebView, or any external source.
 * Credentials are stored ONLY when the user types them into the app's UI.
 * Storage uses EncryptedSharedPreferences (AES-256-GCM via AndroidX Security).
 */
data class StoredCredential(
    val id: String,
    val name: String,
    val type: CredentialType,
    val value: String,          // stored encrypted, shown masked
    val createdAt: Long,
    val note: String = "",
)

enum class CredentialType {
    API_KEY,
    ACCESS_TOKEN,
    REFRESH_TOKEN,
    SESSION_ID,
    OTHER,
}

class CredentialVault(context: Context) {

    private val masterKey = MasterKey.Builder(context)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()

    private val prefs: SharedPreferences = EncryptedSharedPreferences.create(
        context,
        "lmarena_credentials",   // LMArenaSettingsPref
        masterKey,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM,
    )

    private val _credentials = MutableStateFlow(emptyList<StoredCredential>())
    val credentials: StateFlow<List<StoredCredential>> = _credentials

    init {
        prefs.registerOnSharedPreferenceChangeListener { _, key ->
            if (key != null && key.startsWith("cred_")) {
                _credentials.value = decodeAll(prefs.all)
            }
        }
        _credentials.value = decodeAll(prefs.all)
    }

    private fun decodeAll(all: Map<String, Any?>): List<StoredCredential> =
        all.keys
            .filter { it.startsWith("cred_") }
            .mapNotNull { key ->
                runCatching {
                    val json = all[key] as? String ?: return@mapNotNull null
                    decode(json)
                }.getOrNull()
            }
            .sortedByDescending { it.createdAt }

    fun get(id: String): StoredCredential? = runCatching {
        val json = prefs.getString("cred_$id", null) ?: return null
        decode(json)
    }.getOrNull()

    fun save(cred: StoredCredential) {
        prefs.edit()
            .putString("cred_${cred.id}", encode(cred))
            .apply()
    }

    fun update(id: String, newValue: String) {
        val existing = get(id) ?: return
        save(existing.copy(value = newValue))
    }

    fun delete(id: String) {
        prefs.edit().remove("cred_$id").apply()
    }

    fun clearAll() {
        prefs.edit().clear().apply()
    }

    private fun encode(c: StoredCredential): String {
        // Simple field-separated encoding; values are encrypted at rest by
        // EncryptedSharedPreferences before this string is ever persisted.
        return listOf(
            c.id, c.name, c.type.name, c.value, c.createdAt.toString(), c.note
        ).joinToString("\u001E")
    }

    private fun decode(json: String): StoredCredential {
        val f = json.split("\u001E")
        if (f.size < 5) throw IllegalArgumentException("bad credential record")
        return StoredCredential(
            id = f[0],
            name = f[1],
            type = CredentialType.valueOf(f[2]),
            value = f[3],
            createdAt = f[4].toLongOrNull() ?: 0L,
            note = if (f.size > 5) f[5] else "",
        )
    }
}
