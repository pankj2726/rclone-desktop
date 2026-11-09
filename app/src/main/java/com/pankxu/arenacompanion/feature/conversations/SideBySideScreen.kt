package com.pankxu.arenacompanion.feature.conversations

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import com.pankxu.arenacompanion.core.logging.Logger
import com.pankxu.arenacompanion.domain.models.ModelInfo
import com.pankxu.arenacompanion.ui.icons.AppIcons
import kotlinx.coroutines.launch

/** Side-by-Side conversation screen — shows model names instead of Option A/B. */
@Composable
fun SideBySideScreen(
    modelA: ModelInfo?,
    modelB: ModelInfo?,
    onSend: (String) -> Unit,
    onStop: () -> Unit,
    isStreaming: Boolean = false,
) {
    var message by remember { mutableStateOf("") }
    var responseA by remember { mutableStateOf("") }
    var responseB by remember { mutableStateOf("") }
    var showInput by remember { mutableStateOf(true) }
    val scope = rememberCoroutineScope()

    Column(Modifier.fillMaxSize().padding(16.dp)) {
        // Model selector row
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            ModelLabel(modelA, "A", modifier = Modifier.weight(1f))
            Text("VS", style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant)
            ModelLabel(modelB, "B", modifier = Modifier.weight(1f))
        }

        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

        // Response panels
        Row(Modifier.weight(1f)) {
            Card(
                Modifier
                    .weight(1f)
                    .padding(end = 4.dp)
                    .clip(RoundedCornerShape(12.dp)),
            ) {
                Column(Modifier.padding(12.dp)) {
                    ModelHeader(modelA, "A")
                    AnimatedVisibility(
                        visible = responseA.isNotEmpty(),
                        enter = slideInVertically { it / 4 } + fadeIn(),
                        exit = slideOutVertically { it / 4 } + fadeOut(),
                    ) {
                        Text(
                            responseA.ifBlank { "Waiting for response..." },
                            style = MaterialTheme.typography.bodyMedium,
                            fontFamily = FontFamily.Monospace,
                        )
                    }
                }
            }

            Card(
                Modifier
                    .weight(1f)
                    .padding(start = 4.dp)
                    .clip(RoundedCornerShape(12.dp)),
            ) {
                Column(Modifier.padding(12.dp)) {
                    ModelHeader(modelB, "B")
                    AnimatedVisibility(
                        visible = responseB.isNotEmpty(),
                        enter = slideInVertically { it / 4 } + fadeIn(),
                        exit = slideOutVertically { it / 4 } + fadeOut(),
                    ) {
                        Text(
                            responseB.ifBlank { "Waiting for response..." },
                            style = MaterialTheme.typography.bodyMedium,
                            fontFamily = FontFamily.Monospace,
                        )
                    }
                }
            }
        }

        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

        // Input area
        AnimatedVisibility(visible = showInput) {
            Row(Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = message,
                    onValueChange = { message = it },
                    placeholder = { Text("Enter message...") },
                    singleLine = true,
                    modifier = Modifier.weight(1f),
                )
                if (isStreaming) {
                    TextButton(onClick = {
                        onStop()
                        showInput = true
                    }) {
                        androidx.compose.material3.Icon(AppIcons.Stop, contentDescription = "Stop")
                        Text("Stop")
                    }
                } else {
                    TextButton(onClick = {
                        if (message.isNotBlank()) {
                            Logger.i("SideBySide", "Sending message: ${message.take(50)}...")
                            onSend(message)
                            showInput = false
                            // Simulate responses (real impl would stream from API)
                            scope.launch {
                                responseA = "Response from ${modelA?.displayName ?: "Model A"}..."
                                responseB = "Response from ${modelB?.displayName ?: "Model B"}..."
                            }
                            message = ""
                        }
                    }) {
                        androidx.compose.material3.Icon(AppIcons.Send, contentDescription = "Send")
                        Text("Send")
                    }
                }
            }
        }
    }
}

@Composable
private fun ModelLabel(model: ModelInfo?, label: String, modifier: Modifier = Modifier) {
    Column(modifier) {
        Text(
            model?.displayName ?: "Model $label",
            style = MaterialTheme.typography.titleSmall,
            maxLines = 1,
        )
        Text(
            model?.ownedBy ?: "",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
private fun ModelHeader(model: ModelInfo?, label: String) {
    Row(
        Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            model?.displayName ?: "Model $label",
            style = MaterialTheme.typography.titleSmall,
        )
        if (model != null) {
            Text(
                model.arenaType,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.primary,
            )
        }
    }
}
