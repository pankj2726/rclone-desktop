package com.pankxu.arenacompanion.core.logging

/** Crash reporter abstraction. No third-party crash SDK is used (R5: zero analytics SDKs). */
object CrashReporter {
    fun report(tag: String, message: String, throwable: Throwable? = null) {
        Logger.e(tag, message, throwable)
    }

    fun reportError(error: Throwable, context: String) {
        Logger.e(context, "${error.javaClass.simpleName}: ${error.message}", error)
    }
}
