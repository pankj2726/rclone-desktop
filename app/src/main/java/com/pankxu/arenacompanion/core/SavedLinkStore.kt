package com.pankxu.arenacompanion.core

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "arena_companion")

data class SavedLink(val id: String, val title: String, val url: String, val savedAt: Long)

/**
 * Local-only storage: no analytics, no network, no identifiers (R4).
 * Format is a simple line-per-record encoding, deliberately dependency-free so the
 * same parsing rules can be unit-tested on the JVM.
 */
class SavedLinkStore(private val context: Context) {

    private val key = stringPreferencesKey("saved_links")

    val links: Flow<List<SavedLink>> = context.dataStore.data.map { prefs ->
        parse(prefs[key].orEmpty())
    }

    suspend fun save(link: SavedLink) {
        context.dataStore.edit { prefs ->
            val current = parse(prefs[key].orEmpty()).filterNot { it.url == link.url }
            prefs[key] = encode(listOf(link) + current)
        }
    }

    suspend fun remove(id: String) {
        context.dataStore.edit { prefs ->
            prefs[key] = encode(parse(prefs[key].orEmpty()).filterNot { it.id == id })
        }
    }

    companion object {
        private const val SEP = "\u001F"
        private const val REC = "\u001E"

        fun encode(list: List<SavedLink>): String = list.joinToString(REC) {
            listOf(it.id, it.title.replace(SEP, " "), it.url, it.savedAt.toString()).joinToString(SEP)
        }

        fun parse(raw: String): List<SavedLink> {
            if (raw.isBlank()) return emptyList()
            return raw.split(REC).mapNotNull { rec ->
                val f = rec.split(SEP)
                if (f.size < 4) return@mapNotNull null
                SavedLink(f[0], f[1], f[2], f[3].toLongOrNull() ?: 0L)
            }
        }
    }
}
