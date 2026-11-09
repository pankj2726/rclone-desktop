package com.pankxu.arenacompanion.feature.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.material3.Card
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.pankxu.arenacompanion.core.AppContainer
import kotlinx.coroutines.launch

/** Unified settings screen. */
@Composable
fun SettingsScreen(
    container: AppContainer,
    onNavigateToVault: () -> Unit,
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val scope = rememberCoroutineScope()
    val prefs = container.preferencesManager

    val themeMode by prefs.themeMode.collectAsState(initial = "system")
    val dynamicColor by prefs.dynamicColor.collectAsState(initial = true)
    val biometricEnabled by prefs.biometricEnabled.collectAsState(initial = false)
    val autoSync by prefs.autoSync.collectAsState(initial = true)
    val notificationsEnabled by prefs.notificationEnabled.collectAsState(initial = false)

    Column(Modifier.fillMaxSize().padding(horizontal = 20.dp)) {
        Text("Settings", style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(top = 16.dp, bottom = 4.dp))
        HorizontalDivider()

        // Account & Credentials
        Card(Modifier.padding(vertical = 8.dp)) {
            Column(Modifier.padding(16.dp)) {
                Text("Account & Credentials", style = MaterialTheme.typography.titleMedium)
                Text(
                    "Credentials are stored encrypted (AES-256-GCM). " +
                    "Manage your API keys and tokens in the vault.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 4.dp),
                )
                Row(Modifier.padding(top = 8.dp), horizontalArrangement = Arrangement.End) {
                    TextButton(onClick = onNavigateToVault) { Text("Manage Credentials") }
                }
            }
        }
        HorizontalDivider()

        // Appearance
        Card(Modifier.padding(vertical = 8.dp)) {
            Column(Modifier.padding(16.dp)) {
                Text("Appearance", style = MaterialTheme.typography.titleMedium)
                Text("Theme: $themeMode", style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(top = 4.dp))
                Text("Dynamic color: $dynamicColor", style = MaterialTheme.typography.bodyMedium)
            }
        }
        HorizontalDivider()

        // Privacy & Security
        Card(Modifier.padding(vertical = 8.dp)) {
            Column(Modifier.padding(16.dp)) {
                Text("Privacy & Security", style = MaterialTheme.typography.titleMedium)
                Text("Biometric protection: ${if (biometricEnabled) "On" else "Off"}",
                    style = MaterialTheme.typography.bodyMedium, modifier = Modifier.padding(top = 4.dp))
                Text("Auto-sync: ${if (autoSync) "On" else "Off"}",
                    style = MaterialTheme.typography.bodyMedium)
                Text("Notifications: ${if (notificationsEnabled) "On" else "Off"}",
                    style = MaterialTheme.typography.bodyMedium)
            }
        }
        HorizontalDivider()

        // Storage
        Card(Modifier.padding(vertical = 8.dp)) {
            Column(Modifier.padding(16.dp)) {
                Text("Storage", style = MaterialTheme.typography.titleMedium)
                Text("Total: ${container.storageManager.formatSize(container.storageManager.totalSize())}",
                    style = MaterialTheme.typography.bodyMedium, modifier = Modifier.padding(top = 4.dp))
                Text("Cache: ${container.cacheManager.formatSize(container.cacheManager.getTotalCacheSize())}",
                    style = MaterialTheme.typography.bodyMedium)
                Row(Modifier.padding(top = 8.dp), horizontalArrangement = Arrangement.End) {
                    TextButton(onClick = {
                        scope.launch {
                            container.cacheManager.clearAll()
                        }
                    }) { Text("Clear Cache") }
                }
            }
        }
        HorizontalDivider()

        // About
        Card(Modifier.padding(vertical = 8.dp)) {
            Column(Modifier.padding(16.dp)) {
                Text("About", style = MaterialTheme.typography.titleMedium)
                Text("App v${com.pankxu.arenacompanion.core.config.AppConfig.APP_VERSION}",
                    style = MaterialTheme.typography.bodyMedium, modifier = Modifier.padding(top = 4.dp))
                Text("An unofficial companion for arena.ai.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}
