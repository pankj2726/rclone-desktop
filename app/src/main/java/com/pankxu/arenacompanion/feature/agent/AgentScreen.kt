package com.pankxu.arenacompanion.feature.agent

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
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
import com.pankxu.arenacompanion.core.logging.Logger
import com.pankxu.arenacompanion.domain.models.ModelInfo
import com.pankxu.arenacompanion.ui.icons.AppIcons
import kotlinx.coroutines.launch

data class ChatMessage(
    val id: String,
    val role: String,       // "user" or "assistant"
    val content: String,
    val modelName: String = "",
    val timestamp: Long = System.currentTimeMillis(),
)

/** Agent mode conversation screen — shows the active model name. */
@Composable
fun AgentScreen(
    model: ModelInfo?,
    onSend: (String) -> Unit,
    onStop: () -> Unit,
    isStreaming: Boolean = false,
) {
    var message by remember { mutableStateOf("") }
    var messages by remember { mutableStateOf<List<ChatMessage>>(emptyList()) }
    var isResponding by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    Column(Modifier.fillMaxSize().padding(16.dp)) {
        // Model header bar
        Card(Modifier.fillMaxWidth()) {
            Row(
                Modifier.fillMaxWidth().padding(12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(AppIcons.Brain, contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary)
                    Column(Modifier.padding(start = 8.dp)) {
                        Text("Agent Mode", style = MaterialTheme.typography.titleMedium)
                        Text(
                            model?.displayName ?: "No model selected",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }
                if (model != null) {
                    Text(
                        model.arenaType,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary,
                    )
                }
            }
        }

        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

        // Message list
        LazyColumn(Modifier.weight(1f)) {
            items(messages, key = { it.id }) { msg ->
                AnimatedVisibility(
                    visible = true,
                    enter = slideInVertically { it } + fadeIn(),
                    exit = slideOutVertically { it } + fadeOut(),
                ) {
                    MessageBubble(message = msg)
                }
            }
        }

        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

        // Input area
        Row(Modifier.fillMaxWidth()) {
            OutlinedTextField(
                value = message,
                onValueChange = { message = it },
                placeholder = { Text("Message ${model?.displayName ?: ""}...") },
                singleLine = true,
                modifier = Modifier.weight(1f),
                enabled = !isResponding,
            )
            if (isResponding) {
                TextButton(onClick = {
                    onStop()
                    isResponding = false
                }) {
                    Icon(AppIcons.Stop, contentDescription = "Stop")
                    Text("Stop")
                }
            } else {
                TextButton(onClick = {
                    if (message.isNotBlank()) {
                        val userMsg = ChatMessage(
                            id = System.currentTimeMillis().toString(),
                            role = "user",
                            content = message,
                        )
                        messages = messages + userMsg
                        Logger.i("Agent", "User message: ${message.take(50)}...")
                        message = ""

                        isResponding = true
                        scope.launch {
                            onSend(userMsg.content)
                            // Simulate response
                            val response = "Response from ${model?.displayName ?: "Agent"}..."
                            messages = messages + ChatMessage(
                                id = (System.currentTimeMillis() + 1).toString(),
                                role = "assistant",
                                content = response,
                                modelName = model?.displayName ?: "",
                            )
                            isResponding = false
                        }
                    }
                }) {
                    Icon(AppIcons.Send, contentDescription = "Send")
                    Text("Send")
                }
            }
        }
    }
}

@Composable
private fun MessageBubble(message: ChatMessage) {
    val isUser = message.role == "user"
    Card(
        Modifier.fillMaxWidth().padding(vertical = 4.dp),
        colors = androidx.compose.material3.CardDefaults.cardColors(
            containerColor = if (isUser)
                MaterialTheme.colorScheme.primaryContainer
            else
                MaterialTheme.colorScheme.surfaceVariant,
        ),
    ) {
        Column(Modifier.padding(12.dp)) {
            if (!isUser && message.modelName.isNotEmpty()) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(AppIcons.Brain, contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(end = 4.dp))
                    Text(
                        message.modelName,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary,
                    )
                }
            }
            Text(
                message.content,
                style = MaterialTheme.typography.bodyMedium,
            )
        }
    }
}
