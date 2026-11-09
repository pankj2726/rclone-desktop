package com.pankxu.arenacompanion.core

import android.content.Context
import com.pankxu.arenacompanion.core.config.AppConfig
import com.pankxu.arenacompanion.core.events.EventBus
import com.pankxu.arenacompanion.core.lifecycle.BackgroundTaskManager
import com.pankxu.arenacompanion.core.lifecycle.NotificationChannelManager
import com.pankxu.arenacompanion.core.logging.CrashReporter
import com.pankxu.arenacompanion.core.logging.Logger
import com.pankxu.arenacompanion.core.network.NetworkManager
import com.pankxu.arenacompanion.core.storage.CacheManager
import com.pankxu.arenacompanion.core.storage.DatabaseManager
import com.pankxu.arenacompanion.core.storage.PreferencesManager
import com.pankxu.arenacompanion.core.storage.StorageManager
import com.pankxu.arenacompanion.core.utils.DefaultDateTimeProvider
import com.pankxu.arenacompanion.core.utils.DefaultDispatcherProvider
import com.pankxu.arenacompanion.domain.AgentExecutionEngine
import com.pankxu.arenacompanion.domain.ConnectorManager
import com.pankxu.arenacompanion.domain.ConversationManager
import com.pankxu.arenacompanion.domain.LeaderboardManager
import com.pankxu.arenacompanion.domain.MessageManager
import com.pankxu.arenacompanion.domain.ModelDiscoveryManager
import com.pankxu.arenacompanion.domain.ToolRegistry
import com.pankxu.arenacompanion.domain.VotingManager
import com.pankxu.arenacompanion.feature.notifications.AppNotificationManager
import com.pankxu.arenacompanion.feature.voice.AudioPlayer
import com.pankxu.arenacompanion.feature.voice.AudioPermissionManager
import com.pankxu.arenacompanion.feature.voice.SpeechToTextManager
import com.pankxu.arenacompanion.feature.voice.TextToSpeechManager
import com.pankxu.arenacompanion.feature.voice.VoiceManager
import com.pankxu.arenacompanion.feature.voice.VoiceSessionManager
import com.pankxu.arenacompanion.security.SecurityManager

class AppContainer(context: Context) {

    val appContext = context.applicationContext
    val appConfig = AppConfig
    val logger = Logger
    val crashReporter = CrashReporter
    val eventBus = EventBus()
    val dispatcherProvider = DefaultDispatcherProvider()
    val dateTimeProvider = DefaultDateTimeProvider()

    // Security
    val securityManager = SecurityManager(appContext)

    // Storage
    val storageManager = StorageManager(appContext)
    val cacheManager = CacheManager(appContext)
    val preferencesManager = PreferencesManager(appContext)
    val databaseManager = DatabaseManager(appContext)

    // Networking
    val networkManager = NetworkManager(appContext)

    // Auth
    val credentialVault = com.pankxu.arenacompanion.core.CredentialVault(appContext)
    val sessionStore = com.pankxu.arenacompanion.core.SessionStore(appContext)
    val authStateManager = com.pankxu.arenacompanion.core.auth.AuthStateManager()
    val tokenManager = com.pankxu.arenacompanion.core.auth.TokenManager()

    // Saved links
    val savedLinks = com.pankxu.arenacompanion.core.SavedLinkStore(appContext)

    // Domain
    val conversationManager = ConversationManager(databaseManager)
    val messageManager = MessageManager(databaseManager)
    val modelDiscoveryManager = ModelDiscoveryManager(databaseManager)
    val leaderboardManager = LeaderboardManager()
    val votingManager = VotingManager()
    val connectorManager = ConnectorManager()
    val agentExecutionEngine = AgentExecutionEngine()
    val toolRegistry = ToolRegistry()

    // Upload
    val fileUploadManager = com.pankxu.arenacompanion.core.upload.FileUploadManager(
        appContext, networkManager
    )
    val attachmentManager = com.pankxu.arenacompanion.core.upload.AttachmentManager(appContext)

    // Voice
    val voiceManager = VoiceManager(appContext)
    val audioPermissionManager = AudioPermissionManager(appContext)
    val speechToTextManager = SpeechToTextManager()
    val textToSpeechManager = TextToSpeechManager(appContext)
    val audioPlayer = AudioPlayer()
    val voiceSessionManager = VoiceSessionManager(voiceManager, speechToTextManager, textToSpeechManager, audioPlayer)

    // Background
    val backgroundTaskManager = BackgroundTaskManager(appContext)
    val notificationChannelManager = NotificationChannelManager(appContext)
    val notificationManager = AppNotificationManager(appContext)

    init {
        notificationChannelManager.createChannels()
    }
}

class ArenaCompanionApp : android.app.Application() {
    val container: AppContainer by lazy { AppContainer(this) }
}
