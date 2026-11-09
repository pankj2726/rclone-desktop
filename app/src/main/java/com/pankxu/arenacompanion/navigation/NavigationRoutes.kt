package com.pankxu.arenacompanion.navigation

/** Navigation route definitions. */
object NavigationRoutes {
    const val BROWSER = "browser"
    const val TRAY = "tray"
    const val VAULT = "vault"
    const val STORAGE = "storage"
    const val SESSIONS = "sessions"
    const val CONVERSATIONS = "conversations"
    const val CONVERSATION = "conversation/{id}"
    const val SETTINGS = "settings"
    const val ACCOUNT = "account"
    const val MODELS = "models"

    fun conversation(id: String) = "conversation/$id"
}
