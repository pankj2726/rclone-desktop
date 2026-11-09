package com.pankxu.arenacompanion.core

import android.content.Context
import com.pankxu.arenacompanion.core.config.AppConfig
import com.pankxu.arenacompanion.core.events.EventBus
import com.pankxu.arenacompanion.core.logging.CrashReporter
import com.pankxu.arenacompanion.core.logging.Logger
import com.pankxu.arenacompanion.core.network.NetworkManager
import com.pankxu.arenacompanion.core.storage.CacheManager
import com.pankxu.arenacompanion.core.storage.DatabaseManager
import com.pankxu.arenacompanion.core.storage.PreferencesManager
import com.pankxu.arenacompanion.core.storage.StorageManager
import com.pankxu.arenacompanion.core.utils.DefaultDateTimeProvider
import com.pankxu.arenacompanion.core.utils.DefaultDispatcherProvider
import com.pankxu.arenacompanion.domain.ConversationManager
import com.pankxu.arenacompanion.domain.MessageManager
import com.pankxu.arenacompanion.security.SecurityManager

/** Manual DI container. Provides all app services. */
class AppContainer(context: Context) {

    val appConfig = AppConfig
    val logger = Logger
    val crashReporter = CrashReporter
    val eventBus = EventBus()
    val dispatcherProvider = DefaultDispatcherProvider()
    val dateTimeProvider = DefaultDateTimeProvider()

    // Security
    val securityManager = SecurityManager(context.applicationContext)

    // Storage
    val storageManager = StorageManager(context.applicationContext)
    val cacheManager = CacheManager(context.applicationContext)
    val preferencesManager = PreferencesManager(context.applicationContext)
    val databaseManager = DatabaseManager(context.applicationContext)

    // Networking
    val networkManager = NetworkManager(context.applicationContext)

    // Auth
    val credentialVault = com.pankxu.arenacompanion.core.CredentialVault(context.applicationContext)
    val sessionStore = com.pankxu.arenacompanion.core.SessionStore(context.applicationContext)

    // Saved links
    val savedLinks = com.pankxu.arenacompanion.core.SavedLinkStore(context.applicationContext)

    // Domain
    val conversationManager = ConversationManager(databaseManager)
    val messageManager = MessageManager(databaseManager)

    // Upload
    val fileUploadManager = com.pankxu.arenacompanion.core.upload.FileUploadManager(
        context.applicationContext, networkManager
    )
    val attachmentManager = com.pankxu.arenacompanion.core.upload.AttachmentManager(
        context.applicationContext
    )
}

class ArenaCompanionApp : android.app.Application() {
    val container: AppContainer by lazy { AppContainer(this) }
}
