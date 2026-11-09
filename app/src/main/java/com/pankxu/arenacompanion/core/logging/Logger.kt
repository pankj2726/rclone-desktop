package com.pankxu.arenacompanion.core.logging

import android.util.Log
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.time.Instant

object Logger {

    private const val TAG = "App"
    private val _logEntries = MutableStateFlow<List<LogEntry>>(emptyList())
    val logEntries: StateFlow<List<LogEntry>> = _logEntries

    enum class Level { DEBUG, INFO, WARN, ERROR }

    data class LogEntry(
        val timestamp: String,
        val level: Level,
        val tag: String,
        val message: String,
    )

    private val redactPatterns = listOf(
        Regex("sk-arena-[A-Za-z0-9]+"),
        Regex("(?i)(token|secret|password|authorization|api[_-]?key)[\"\\s:=]+[\"']?[\\w\\-.]+"),
        Regex("Bearer\\s+[A-Za-z0-9\\-._~+/]+=*"),
    )

    fun d(tag: String, msg: String) = log(Level.DEBUG, tag, msg)
    fun i(tag: String, msg: String) = log(Level.INFO, tag, msg)
    fun w(tag: String, msg: String) = log(Level.WARN, tag, msg)
    fun e(tag: String, msg: String, throwable: Throwable? = null) {
        log(Level.ERROR, tag, msg)
        throwable?.let { Log.e(tag, msg, it) }
    }

    private fun log(level: Level, tag: String, msg: String) {
        val redacted = redact(msg)
        val entry = LogEntry(Instant.now().toString(), level, tag, redacted)
        _logEntries.value = _logEntries.value + entry
        when (level) {
            Level.DEBUG -> Log.d(tag, redacted)
            Level.INFO -> Log.i(tag, redacted)
            Level.WARN -> Log.w(tag, redacted)
            Level.ERROR -> Log.e(tag, redacted)
        }
    }

    private fun redact(msg: String): String {
        var result = msg
        for (pattern in redactPatterns) {
            result = pattern.replaceFirst(result, "[REDACTED]")
        }
        return result
    }

    fun clear() { _logEntries.value = emptyList() }
}
