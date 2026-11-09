package com.pankxu.arenacompanion.feature

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.pankxu.arenacompanion.core.storage.StorageManager
import kotlinx.coroutines.launch

/**
 * Storage dashboard showing the app's internal storage usage
 * and the folder structure. All data is in /data/data/com.pankxu.arenacompanion/.
 */
@Composable
fun StorageDashboardScreen(
    storage: StorageManager,
) {
    val scope = rememberCoroutineScope()
    val folders = StorageManager.FOLDER_NAMES
    val total = storage.totalSize()

    Column(Modifier.fillMaxSize().padding(horizontal = 20.dp)) {
        Text("Storage", style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(top = 16.dp, bottom = 4.dp))
        Text(
            "Total: ${storage.formatSize(total)} in app-private storage\n" +
            "Path: /data/data/com.pankxu.arenacompanion/",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(bottom = 12.dp),
        )
        HorizontalDivider()

        folders.forEach { name ->
            val size = storage.folderSize(name)
            Row(
                Modifier.fillMaxWidth().padding(vertical = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(name, style = MaterialTheme.typography.titleMedium)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(storage.formatSize(size), style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant)
                    if (name == "cache" || name == "temp") {
                        TextButton(onClick = {
                            scope.launch {
                                if (name == "cache") storage.clearCache() else storage.clearTemp()
                            }
                        }) { Text("Clear") }
                    }
                }
            }
            HorizontalDivider()
        }
    }
}
