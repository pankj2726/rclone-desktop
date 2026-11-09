package com.pankxu.arenacompanion.feature.conversations

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.pankxu.arenacompanion.domain.models.AgentMode
import com.pankxu.arenacompanion.domain.models.ModelInfo
import com.pankxu.arenacompanion.feature.agent.AgentScreen
import com.pankxu.arenacompanion.feature.conversations.SideBySideScreen

/** Conversation detail screen — routes to Agent or Side-by-Side based on mode. */
@Composable
fun ConversationDetailScreen(
    conversationId: String,
    title: String,
    mode: AgentMode,
    model: ModelInfo?,
    modelB: ModelInfo? = null,
    onSend: (String) -> Unit = {},
    onStop: () -> Unit = {},
) {
    Column(Modifier.fillMaxSize().padding(horizontal = 16.dp)) {
        Text(title, style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(top = 16.dp, bottom = 4.dp))
        Text(conversationId.take(8), style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(bottom = 8.dp))
        HorizontalDivider()

        when (mode) {
            AgentMode.AGENT -> AgentScreen(model = model, onSend = onSend, onStop = onStop)
            AgentMode.SIDE_BY_SIDE -> SideBySideScreen(
                modelA = model, modelB = modelB, onSend = onSend, onStop = onStop
            )
            else -> Text(
                "Mode ${mode.name} not yet implemented in this screen.",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(top = 24.dp),
            )
        }
    }
}
