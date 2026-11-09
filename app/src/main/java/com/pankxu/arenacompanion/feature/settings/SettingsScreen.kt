package com.pankxu.arenacompanion.feature.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.pankxu.arenacompanion.core.AppContainer
import com.pankxu.arenacompanion.ui.icons.AppIcons
import kotlinx.coroutines.launch

@Composable
fun SettingsScreen(
    container: AppContainer,
    onNavigateToVault: () -> Unit,
    onToggleOperationLog: () -> Unit = {},
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val prefs = container.preferencesManager

    val themeMode by prefs.themeMode.collectAsState(initial = "system")
    val dynamicColor by prefs.dynamicColor.collectAsState(initial = true)
    val biometricEnabled by prefs.biometricEnabled.collectAsState(initial = false)
    val autoSync by prefs.autoSync.collectAsState(initial = true)
    val notificationsEnabled by prefs.notificationEnabled.collectAsState(initial = false)

    Column(Modifier.fillMaxSize().padding(horizontal = 20.dp)) {
        Text("Settings", style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(top = 16.dp, bottom = 8.dp))

        SettingsRow(
            label = "Theme",
            value = themeMode,
            onClick = {
                scope.launch {
                    prefs.setThemeMode(if (themeMode == "system") "dark" else "system")
                }
            }
        )

        SettingsToggleRow(
            label = "Dynamic Color",
            checked = dynamicColor,
            onCheckedChange = {
                scope.launch { prefs.setDynamicColor(it) }
            }
        )

        SettingsToggleRow(
            label = "Biometric Lock",
            checked = biometricEnabled,
            onCheckedChange = {
                scope.launch { prefs.setBiometricEnabled(it) }
            }
        )

        SettingsToggleRow(
            label = "Auto Sync",
            checked = autoSync,
            onCheckedChange = {
                scope.launch { prefs.setAutoSync(it) }
            }
        )

        SettingsToggleRow(
            label = "Notifications",
            checked = notificationsEnabled,
            onCheckedChange = {
                scope.launch { prefs.setNotificationEnabled(it) }
            }
        )

        SettingsRow(
            label = "Operation Log",
            value = "Tap to toggle live log",
            onClick = onToggleOperationLog
        )

        SettingsRow(
            label = "Credential Vault",
            value = "Open vault",
            onClick = onNavigateToVault
        )
    }
}

@Composable
private fun SettingsRow(label: String, value: String, onClick: () -> Unit) {
    Row(
        Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp)
            .clickable(onClick = onClick),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(label, style = MaterialTheme.typography.bodyLarge)
        Text(value, style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
private fun SettingsToggleRow(label: String, checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Row(
        Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(label, style = MaterialTheme.typography.bodyLarge)
        Switch(checked = checked, onCheckedChange = onCheckedChange)
    }
}
