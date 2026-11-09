package com.pankxu.arenacompanion

import android.content.Intent
import android.os.Bundle
import android.webkit.WebView
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.pankxu.arenacompanion.core.ArenaCompanionApp
import com.pankxu.arenacompanion.domain.models.AgentMode
import com.pankxu.arenacompanion.domain.models.ModelInfo
import com.pankxu.arenacompanion.feature.CompareTrayScreen
import com.pankxu.arenacompanion.feature.CredentialVaultScreen
import com.pankxu.arenacompanion.feature.SessionManagerScreen
import com.pankxu.arenacompanion.feature.StorageDashboardScreen
import com.pankxu.arenacompanion.feature.conversations.ConversationDetailScreen
import com.pankxu.arenacompanion.feature.conversations.ConversationListScreen
import com.pankxu.arenacompanion.feature.models.ModelDiscoveryScreen
import com.pankxu.arenacompanion.feature.settings.SettingsScreen
import com.pankxu.arenacompanion.ui.components.OperationLogPanel
import com.pankxu.arenacompanion.ui.icons.AppIcons
import com.pankxu.arenacompanion.ui.theme.ArenaCompanionTheme
import com.pankxu.arenacompanion.web.BrowserLauncher
import com.pankxu.arenacompanion.web.PageFailure
import com.pankxu.arenacompanion.web.PageState
import com.pankxu.arenacompanion.web.WebViewHost
import kotlinx.coroutines.launch

enum class Screen {
    BROWSER, TRAY, VAULT, STORAGE, SESSIONS, SETTINGS, CONVERSATIONS, MODELS
}

/** Holds the currently open conversation detail state. */
data class OpenConversation(
    val id: String,
    val title: String,
    val mode: AgentMode,
)

class MainActivity : ComponentActivity() {

    private lateinit var host: WebViewHost
    private var webView: WebView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        val app = application as ArenaCompanionApp
        val container = app.container
        val store = container.savedLinks
        val vault = container.credentialVault
        val sessions = container.sessionStore
        val storage = container.storageManager
        val modelManager = container.modelDiscoveryManager
        val conversationManager = container.conversationManager
        val activity = this

        setContent {
            ArenaCompanionTheme {
                var page by remember { mutableStateOf(PageState()) }
                var screen by remember { mutableStateOf(Screen.BROWSER) }
                var openConv by remember { mutableStateOf<OpenConversation?>(null) }
                var selectedModels by remember { mutableStateOf<List<ModelInfo>>(emptyList()) }
                var showOperationLog by remember { mutableStateOf(false) }

                host = remember {
                    WebViewHost(
                        activity = activity,
                        onState = { page = it },
                        onNeedBrowser = { url -> BrowserLauncher.launch(activity, url, partial = true) },
                    )
                }

                val links by store.links.collectAsStateWithLifecycle(initialValue = emptyList())
                val models by modelManager.models.collectAsStateWithLifecycle(initialValue = emptyList())
                val scope = rememberCoroutineScope()

                if (intent?.data != null) {
                    val handler = com.pankxu.arenacompanion.navigation.DeepLinkHandler(activity)
                    val validUri = handler.handleIntent(intent)
                    if (validUri != null) {
                        webView?.post { webView?.loadUrl(validUri.toString()) }
                    }
                }

                BackHandler(enabled = screen == Screen.BROWSER && (webView?.canGoBack() == true)) {
                    webView?.goBack()
                }
                BackHandler(enabled = screen != Screen.BROWSER) { screen = Screen.BROWSER }

                Scaffold(
                    bottomBar = {
                        NavigationBar {
                            NavigationBarItem(selected = screen == Screen.BROWSER, onClick = { screen = Screen.BROWSER }, icon = { Icon(AppIcons.Browser, contentDescription = null) }, label = { Text("Browser") })
                            NavigationBarItem(selected = screen == Screen.TRAY, onClick = { screen = Screen.TRAY }, icon = { Icon(AppIcons.Bookmark, contentDescription = null) }, label = { Text("Tray") })
                            NavigationBarItem(selected = screen == Screen.CONVERSATIONS, onClick = { screen = Screen.CONVERSATIONS }, icon = { Icon(AppIcons.Chat, contentDescription = null) }, label = { Text("Chat") })
                            NavigationBarItem(selected = screen == Screen.MODELS, onClick = { screen = Screen.MODELS }, icon = { Icon(AppIcons.Brain, contentDescription = null) }, label = { Text("Models") })
                            NavigationBarItem(selected = screen == Screen.VAULT, onClick = { screen = Screen.VAULT }, icon = { Icon(AppIcons.Key, contentDescription = null) }, label = { Text("Vault") })
                            NavigationBarItem(selected = screen == Screen.STORAGE, onClick = { screen = Screen.STORAGE }, icon = { Icon(AppIcons.Disk, contentDescription = null) }, label = { Text("Storage") })
                            NavigationBarItem(selected = screen == Screen.SESSIONS, onClick = { screen = Screen.SESSIONS }, icon = { Icon(AppIcons.List, contentDescription = null) }, label = { Text("Sessions") })
                            NavigationBarItem(selected = screen == Screen.SETTINGS, onClick = { screen = Screen.SETTINGS }, icon = { Icon(AppIcons.Settings, contentDescription = null) }, label = { Text("Settings") })
                        }
                    }
                ) { insets ->
                    Box(Modifier.fillMaxSize().padding(insets)) {
                        AnimatedVisibility(
                            visible = true,
                            enter = fadeIn(durationMillis = 200),
                            exit = fadeOut(durationMillis = 150),
                        ) {
                            when (screen) {
                                Screen.BROWSER -> BrowserPane(
                                    page = page,
                                    onCreate = { ctx -> host.create(ctx).also { webView = it } },
                                    onRetry = { webView?.reload() },
                                    onOpenBrowser = { BrowserLauncher.launch(activity, page.url, partial = false) },
                                )
                                Screen.TRAY -> CompareTrayScreen(
                                    links = links,
                                    onOpen = { link -> screen = Screen.BROWSER; webView?.loadUrl(link.url) },
                                    onRemove = { link -> scope.launch { store.remove(link.id) } },
                                )
                                Screen.VAULT -> CredentialVaultScreen(vault = vault)
                                Screen.STORAGE -> StorageDashboardScreen(storage = storage)
                                Screen.SESSIONS -> SessionManagerScreen(sessionStore = sessions, storage = storage)
                                Screen.SETTINGS -> SettingsScreen(
                                    container = container,
                                    onNavigateToVault = { screen = Screen.VAULT },
                                    onToggleOperationLog = { showOperationLog = !showOperationLog },
                                )
                                Screen.CONVERSATIONS -> {
                                    if (openConv != null) {
                                        val conv = openConv!!
                                        AnimatedVisibility(
                                            visible = true,
                                            enter = slideInVertically { it / 4 } + fadeIn(),
                                            exit = slideOutVertically { it / 4 } + fadeOut(),
                                        ) {
                                            ConversationDetailScreen(
                                                conversationId = conv.id,
                                                title = conv.title,
                                                mode = conv.mode,
                                                model = selectedModels.getOrNull(0),
                                                modelB = selectedModels.getOrNull(1),
                                                onSend = { msg ->
                                                    scope.launch {
                                                        conversationManager.sendMessage(conv.id, msg)
                                                    }
                                                },
                                                onStop = { conversationManager.stop(conv.id) },
                                            )
                                        }
                                    } else {
                                        Column(Modifier.fillMaxSize()) {
                                            ConversationListScreen(
                                                conversationManager = conversationManager,
                                                storage = storage,
                                                onOpenConversation = { conv ->
                                                    val mode = parseMode(conv.mode)
                                                    selectedModels = models.take(2)
                                                    openConv = OpenConversation(conv.id, conv.title, mode)
                                                },
                                                onNewConversation = {
                                                    val mode = AgentMode.AGENT
                                                    selectedModels = models.take(2)
                                                    openConv = OpenConversation(
                                                        id = System.currentTimeMillis().toString(),
                                                        title = "New Conversation",
                                                        mode = mode,
                                                    )
                                                },
                                            )
                                            AnimatedVisibility(visible = showOperationLog) {
                                                OperationLogPanel(
                                                    modifier = Modifier.fillMaxWidth().padding(16.dp)
                                                )
                                            }
                                        }
                                    }
                                }
                                Screen.MODELS -> ModelDiscoveryScreen(
                                    modelManager = modelManager,
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    private fun parseMode(modeStr: String): AgentMode = when (modeStr.lowercase()) {
        "agent" -> AgentMode.AGENT
        "direct" -> AgentMode.DIRECT
        "battle" -> AgentMode.BATTLE
        "sidebyside", "side_by_side", "side-by-side" -> AgentMode.SIDE_BY_SIDE
        "multimodal" -> AgentMode.MULTIMODAL
        else -> AgentMode.AGENT
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        val handler = com.pankxu.arenacompanion.navigation.DeepLinkHandler(this)
        val validUri = handler.handleIntent(intent)
        if (validUri != null) {
            webView?.post { webView?.loadUrl(validUri.toString()) }
        }
    }

    override fun onDestroy() {
        webView?.destroy()
        webView = null
        super.onDestroy()
    }
}

@Composable
private fun BrowserPane(
    page: PageState,
    onCreate: (android.content.Context) -> WebView,
    onRetry: () -> Unit,
    onOpenBrowser: () -> Unit,
) {
    val failure = page.failure
    if (failure != null) {
        Column(Modifier.fillMaxSize().padding(24.dp), verticalArrangement = Arrangement.Center) {
            Text(
                text = when (failure) {
                    is PageFailure.Offline -> "You appear to be offline."
                    is PageFailure.Server -> "Arena returned an error (${failure.code})."
                    PageFailure.Gated -> "This page isn't available in the app."
                    is PageFailure.Blocked -> "That link is not supported."
                },
                style = MaterialTheme.typography.headlineMedium,
            )
            Text(
                text = when (failure) {
                    is PageFailure.Offline -> "Reconnect and try again."
                    is PageFailure.Server -> "Try again in a moment."
                    PageFailure.Gated -> "Arena serves this page to a signed-in browser. Open it in your browser to continue."
                    is PageFailure.Blocked -> "The app only opens arena.ai's public pages."
                },
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 8.dp, bottom = 20.dp),
            )
            Button(onClick = onOpenBrowser) { Text("Open in browser") }
            TextButton(onClick = onRetry) { Text("Retry") }
        }
    } else {
        AndroidView(modifier = Modifier.fillMaxSize(), factory = onCreate)
    }
}
