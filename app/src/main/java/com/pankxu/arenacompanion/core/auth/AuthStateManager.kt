package com.pankxu.arenacompanion.core.auth

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

/**
 * Tracks authentication state for the app.
 *
 * NOTE: This tracks the app's own auth state (API key availability), NOT Arena session auth.
 * Arena session auth is handled via Custom Tabs in the browser (R6/R7).
 * The app never imports session cookies or tokens from the browser.
 */
class AuthStateManager {

    enum class AuthStatus { UNAUTHENTICATED, CREDENTIALS_STORED, READY }

    private val _status = MutableStateFlow(AuthStatus.UNAUTHENTICATED)
    val status: StateFlow<AuthStatus> = _status

    private val _credentialCount = MutableStateFlow(0)
    val credentialCount: StateFlow<Int> = _credentialCount

    fun setCredentialCount(count: Int) {
        _credentialCount.value = count
        _status.value = if (count > 0) AuthStatus.CREDENTIALS_STORED else AuthStatus.UNAUTHENTICATED
    }

    fun isReady(): Boolean = _status.value == AuthStatus.READY
    fun hasCredentials(): Boolean = _credentialCount.value > 0
}
