package com.pankxu.arenacompanion

import android.content.Intent
import android.os.Bundle
import android.webkit.WebView
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
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
import com.pankxu.arenacompanion.feature.CompareTrayScreen
import com.pankxu.arenacompanion.feature.CredentialVaultScreen
import com.pankxu.arenacompanion.feature.SessionManagerScreen
import com.pankxu.arenacompanion.feature.StorageDashboardScreen
import com.pankxu.arenacompanion.feature.conversations.ConversationListScreen
import com.pankxu.arenacompanion.feature.models.ModelDiscoveryScreen
import com.pankxu.arenacompanion.feature.settings.SettingsScreen
import com.pankxu.arenacompanion.ui.theme.ArenaCompanionTheme
import com.pankxu.arenacompanion.web.BrowserLauncher
import com.pankxu.arenacompanion.web.PageFailure
import com.pankxu.arenacompanion.web.PageState
import com.pankxu.arenacompanion.web.WebViewHost
import kotlinx.coroutines.launch

enum class Screen {
    BROWSER, TRAY, VAULT, STORAGE, SESSIONS, SETTINGS, CONVERSATIONS, MODELS
}

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
        val activity = this

        setContent {
            ArenaCompanionTheme {
                var page by remember { mutableStateOf(PageState()) }
                var screen by remember { mutableStateOf(Screen.BROWSER) }

                host = remember {
                    WebViewHost(
                        activity = activity,
                        onState = { page = it },
                        onNeedBrowser = { url -> BrowserLauncher.launch(activity, url, partial = true) },
                    )
                }

                val links by store.links.collectAsStateWithLifecycle(initialValue = emptyList())
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
                            NavigationBarItem(selected = screen == Screen.BROWSER, onClick = { screen = Screen.BROWSER }, icon = { Text("Web") }, label = { Text("Browser") })
                            NavigationBarItem(selected = screen == Screen.TRAY, onClick = { screen = Screen.TRAY }, icon = { Text("Tray") }, label = { Text("Tray") })
                            NavigationBarItem(selected = screen == Screen.CONVERSATIONS, onClick = { screen = Screen.CONVERSATIONS }, icon = { Text("Chat") }, label = { Text("Chat") })
                            NavigationBarItem(selected = screen == Screen.MODELS, onClick = { screen = Screen.MODELS }, icon = { Text("Model") }, label = { Text("Models") })
                            NavigationBarItem(selected = screen == Screen.VAULT, onClick = { screen = Screen.VAULT }, icon = { Text("Key") }, label = { Text("Vault") })
                            NavigationBarItem(selected = screen == Screen.STORAGE, onClick = { screen = Screen.STORAGE }, icon = { Text("Disk") }, label = { Text("Storage") })
                            NavigationBarItem(selected = screen == Screen.SESSIONS, onClick = { screen = Screen.SESSIONS }, icon = { Text("List") }, label = { Text("Sessions") })
                            NavigationBarItem(selected = screen == Screen.SETTINGS, onClick = { screen = Screen.SETTINGS }, icon = { Text("Set") }, label = { Text("Settings") })
                        }
                    }
                ) { insets ->
                    Box(Modifier.fillMaxSize().padding(insets)) {
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
                            )
                            Screen.CONVERSATIONS -> ConversationListScreen(
                                conversationManager = container.conversationManager,
                                storage = storage,
                                onOpenConversation = { /* TODO: navigate to conversation detail */ },
                            )
                            Screen.MODELS -> ModelDiscoveryScreen(
                                modelManager = container.modelDiscoveryManager,
                            )
                        }
                    }
                }
            }
        }
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
