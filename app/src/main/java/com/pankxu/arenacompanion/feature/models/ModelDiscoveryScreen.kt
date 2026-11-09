package com.pankxu.arenacompanion.feature.models

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.pankxu.arenacompanion.domain.ModelDiscoveryManager
import com.pankxu.arenacompanion.domain.models.ModelInfo
import kotlinx.coroutines.launch

/** Model discovery screen. */
@Composable
fun ModelDiscoveryScreen(
    modelManager: ModelDiscoveryManager,
) {
    val scope = rememberCoroutineScope()
    val models by modelManager.models.collectAsState(initial = emptyList())

    Column(Modifier.fillMaxSize().padding(horizontal = 20.dp)) {
        Text("Models", style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(top = 16.dp, bottom = 4.dp))
        Text("${models.size} models available",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(bottom = 12.dp))
        HorizontalDivider()

        if (models.isEmpty()) {
            Text("No models cached. Fetch from Arena API to discover available models.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 24.dp))
        } else {
            LazyColumn(Modifier.fillMaxSize()) {
                items(models, key = { it.id }) { model ->
                    Card(Modifier.fillMaxWidth().padding(vertical = 6.dp)) {
                        Column(Modifier.padding(16.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Column(Modifier.weight(1f)) {
                                    Text(model.displayName,
                                        style = MaterialTheme.typography.titleMedium,
                                        maxLines = 1, overflow = TextOverflow.Ellipsis)
                                    Text(model.ownedBy,
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                                Text(
                                    if (model.isAvailable) "Available" else "Unavailable",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = if (model.isAvailable)
                                        MaterialTheme.colorScheme.primary
                                    else
                                        MaterialTheme.colorScheme.error
                                )
                            }
                            if (model.capabilities.isNotEmpty()) {
                                Text(model.capabilities.joinToString(", "),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.padding(top = 4.dp))
                            }
                        }
                    }
                }
            }
        }
    }
}
