package com.pankxu.arenacompanion.core

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.json.JSONArray
import org.json.JSONObject

private val Context.sessionDataStore: DataStore<Preferences> by preferencesDataStore(name = "arena_sessions")

/** A saved browsing session — ours, not Arena's. */
data class SessionMeta(
    val id: String,
    val title: String,
    val url: String,
    val startedAt: Long,
    val endedAt: Long,
    val pageCount: Int,
    val estimatedSize: Long,    // bytes
)

/**
 * Session metadata store. Tracks browsing sessions within the app,
 * not Arena's server-side chat sessions (which the app cannot access).
 *
 * Arena limits each chat session to 128 MB. The app tracks its own
 * storage usage per session and alerts when approaching that threshold.
 */
class SessionStore(private val context: Context) {

    companion object {
        const val ARENA_SESSION_LIMIT_BYTES = 128L * 1024 * 1024  // 128 MB
    }

    private val key = stringPreferencesKey("sessions_json")

    val sessions: Flow<List<SessionMeta>> = context.sessionDataStore.data.map { prefs ->
        decode(prefs[key].orEmpty())
    }

    suspend fun save(session: SessionMeta) {
        context.sessionDataStore.edit { prefs ->
            val current = decode(prefs[key].orEmpty()).filterNot { it.id == session.id }
            prefs[key] = encode(listOf(session) + current)
        }
    }

    suspend fun updateSize(id: String, sizeBytes: Long) {
        context.sessionDataStore.edit { prefs ->
            val list = decode(prefs[key].orEmpty()).map { s ->
                if (s.id == id) s.copy(estimatedSize = sizeBytes) else s
            }
            prefs[key] = encode(list)
        }
    }

    suspend fun delete(id: String) {
        context.sessionDataStore.edit { prefs ->
            prefs[key] = encode(decode(prefs[key].orEmpty()).filterNot { it.id == id })
        }
    }

    fun isOverLimit(session: SessionMeta): Boolean =
        session.estimatedSize >= ARENA_SESSION_LIMIT_BYTES

    fun limitPercent(session: SessionMeta): Int =
        if (session.estimatedSize == 0L) 0
        else (session.estimatedSize * 100 / ARENA_SESSION_LIMIT_BYTES).toInt().coerceAtMost(100)

    private fun encode(list: List<SessionMeta>): String {
        val arr = JSONArray()
        list.forEach {
            arr.put(JSONObject()
                .put("id", it.id)
                .put("title", it.title)
                .put("url", it.url)
                .put("startedAt", it.startedAt)
                .put("endedAt", it.endedAt)
                .put("pageCount", it.pageCount)
                .put("estimatedSize", it.estimatedSize))
        }
        return arr.toString()
    }

    private fun decode(json: String): List<SessionMeta> {
        if (json.isBlank()) return emptyList()
        return runCatching {
            val arr = JSONArray(json)
            (0 until arr.length()).map { i ->
                val o = arr.getJSONObject(i)
                SessionMeta(
                    id = o.getString("id"),
                    title = o.getString("title"),
                    url = o.getString("url"),
                    startedAt = o.optLong("startedAt"),
                    endedAt = o.optLong("endedAt"),
                    pageCount = o.optInt("pageCount"),
                    estimatedSize = o.optLong("estimatedSize"),
                )
            }
        }.getOrDefault(emptyList())
    }
}
