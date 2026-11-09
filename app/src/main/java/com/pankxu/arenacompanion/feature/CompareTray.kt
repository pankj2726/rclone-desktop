package com.pankxu.arenacompanion.feature

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.pankxu.arenacompanion.core.SavedLink

/**
 * P4 - the anti-wrapper feature. These are pages the user saved, rendered natively.
 * Arena has no API for this; it exists so the app earns its install on its own terms.
 */
@Composable
fun CompareTrayScreen(
    links: List<SavedLink>,
    onOpen: (SavedLink) -> Unit,
    onRemove: (SavedLink) -> Unit,
) {
    Column(Modifier.fillMaxSize().padding(horizontal = 20.dp)) {
        Text(
            "Compare tray",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(top = 16.dp, bottom = 4.dp),
        )
        Text(
            "Pages you saved while reading. Stored on this device only.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(bottom = 12.dp),
        )
        HorizontalDivider()

        if (links.isEmpty()) {
            Text(
                "Nothing saved yet. Open a page and use Save.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 24.dp),
            )
        } else {
            LazyColumn(Modifier.fillMaxSize()) {
                items(links, key = { it.id }) { link ->
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ) {
                        Column(Modifier.weight(1f)) {
                            Text(
                                link.title.ifBlank { link.url },
                                style = MaterialTheme.typography.titleMedium,
                                maxLines = 2,
                            )
                            Text(
                                link.url.removePrefix("https://"),
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                maxLines = 1,
                            )
                        }
                        TextButton(onClick = { onOpen(link) }) { Text("Open") }
                        TextButton(onClick = { onRemove(link) }) { Text("Remove") }
                    }
                    HorizontalDivider()
                }
            }
        }
    }
}
