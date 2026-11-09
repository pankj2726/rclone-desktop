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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.pankxu.arenacompanion.core.SessionStore
import com.pankxu.arenacompanion.core.storage.StorageManager
import kotlinx.coroutines.launch

/**
 * Session manager showing browsing sessions tracked by the app.
 * Shows storage usage per session and alerts when approaching Arena's 128 MB limit.
 *
 * Note: these are app-side browsing sessions, not Arena's server-side chat sessions.
 */
@Composable
fun SessionManagerScreen(
    sessionStore: SessionStore,
    storage: StorageManager,
) {
    val sessions by sessionStore.sessions.collectAsStateWithLifecycle(initialValue = emptyList())
    val scope = rememberCoroutineScope()

    Column(Modifier.fillMaxSize().padding(horizontal = 20.dp)) {
        Text("Sessions", style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(top = 16.dp, bottom = 4.dp))
        Text(
            "Arena limits each chat session to 128 MB. This tracks your app-side storage usage.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(bottom = 12.dp),
        )
        HorizontalDivider()

        if (sessions.isEmpty()) {
            Text("No sessions recorded yet.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 24.dp))
        } else {
            LazyColumn(Modifier.fillMaxSize()) {
                items(sessions, key = { it.id }) { session ->
                    val pct = sessionStore.limitPercent(session)
                    val overLimit = sessionStore.isOverLimit(session)
                    Row(
                        Modifier.fillMaxWidth().padding(vertical = 10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Column(Modifier.weight(1f)) {
                            Text(session.title.ifBlank { session.url },
                                style = MaterialTheme.typography.titleMedium)
                            Text(session.url.removePrefix("https://"),
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text(
                                "${storage.formatSize(session.estimatedSize)} (${pct}%) of 128 MB",
                                style = MaterialTheme.typography.labelSmall,
                                color = if (overLimit) MaterialTheme.colorScheme.error
                                    else MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                        TextButton(onClick = { scope.launch { sessionStore.delete(session.id) } }) {
                            Text("Remove")
                        }
                    }
                    HorizontalDivider()
                }
            }
        }
    }
}
