package com.pankxu.arenacompanion.feature.conversations

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.pankxu.arenacompanion.core.storage.StorageManager
import com.pankxu.arenacompanion.data.entities.ConversationEntity
import com.pankxu.arenacompanion.domain.ConversationManager
import com.pankxu.arenacompanion.ui.empty.EmptyState
import com.pankxu.arenacompanion.ui.icons.AppIcons
import kotlinx.coroutines.launch

@Composable
fun ConversationListScreen(
    conversationManager: ConversationManager,
    storage: StorageManager,
    onOpenConversation: (ConversationEntity) -> Unit,
    onNewConversation: () -> Unit = {},
) {
    val scope = rememberCoroutineScope()
    val conversations by conversationManager.conversations.collectAsState(initial = emptyList())

    Column(Modifier.fillMaxSize().padding(horizontal = 20.dp)) {
        Row(
            Modifier.fillMaxWidth().padding(top = 16.dp, bottom = 4.dp),
            horizontalArrangement = androidx.compose.foundation.layout.Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text("Conversations", style = MaterialTheme.typography.headlineMedium)
            TextButton(onClick = onNewConversation) {
                Icon(AppIcons.Plus, contentDescription = null)
                Text("New")
            }
        }
        HorizontalDivider()

        if (conversations.isEmpty()) {
            EmptyState(
                title = "No conversations",
                message = "Start a new conversation to see it here.",
            )
        } else {
            LazyColumn(Modifier.fillMaxSize()) {
                items(conversations, key = { it.id }) { conv ->
                    Row(
                        Modifier.fillMaxWidth().padding(vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Column(Modifier.weight(1f)) {
                            Text(conv.title.ifBlank { "Untitled" },
                                style = MaterialTheme.typography.titleMedium,
                                maxLines = 1, overflow = TextOverflow.Ellipsis)
                            Text(
                                buildString {
                                    append(conv.lastMessagePreview.ifBlank { conv.model })
                                    if (conv.model.isNotBlank()) {
                                        append(" · ${conv.mode}")
                                    }
                                },
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                maxLines = 1, overflow = TextOverflow.Ellipsis
                            )
                        }
                        TextButton(onClick = { onOpenConversation(conv) }) {
                            Icon(AppIcons.Chat, contentDescription = null)
                            Text("Open")
                        }
                        TextButton(onClick = {
                            scope.launch { conversationManager.delete(conv.id) }
                        }) {
                            Icon(AppIcons.Delete, contentDescription = null)
                            Text("Delete")
                        }
                    }
                    HorizontalDivider()
                }
            }
        }
    }
}
