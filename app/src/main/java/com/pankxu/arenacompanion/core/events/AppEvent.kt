package com.pankxu.arenacompanion.core.events

/** Application-wide events. */
sealed class AppEvent {
    data class NavigationRequested(val route: String) : AppEvent()
    data class ScreenChanged(val screen: String) : AppEvent()
    data class CredentialAdded(val credentialId: String) : AppEvent()
    data class CredentialRemoved(val credentialId: String) : AppEvent()
    data class SessionStarted(val sessionId: String) : AppEvent()
    data class SessionEnded(val sessionId: String) : AppEvent()
    data class UploadProgress(val uploadId: String, val progress: Int, val bytes: Long) : AppEvent()
    data class UploadCompleted(val uploadId: String) : AppEvent()
    data class UploadFailed(val uploadId: String, val error: String) : AppEvent()
    data class StorageWarning(val message: String) : AppEvent()
    object ThemeChanged : AppEvent()
    object AppStarted : AppEvent()
    object AppResumed : AppEvent()
    object AppPaused : AppEvent()
}
