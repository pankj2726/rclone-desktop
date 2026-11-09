package com.pankxu.arenacompanion.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import com.pankxu.arenacompanion.core.logging.Logger
import com.pankxu.arenacompanion.core.logging.Logger.Level

@Composable
fun OperationLogPanel(
    modifier: Modifier = Modifier,
    maxLines: Int = 20,
) {
    val logs by Logger.logEntries.collectAsState()
    val recent = logs.takeLast(maxLines).reversed()

    Card(modifier = modifier.fillMaxWidth()) {
        Column(Modifier.padding(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Operation Log", style = MaterialTheme.typography.labelLarge)
                Text(
                    "${logs.size} entries",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(start = 8.dp)
                )
            }

            LazyColumn(
                Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .padding(top = 8.dp),
            ) {
                items(recent, key = { "${it.timestamp}-${it.tag}-${it.message.hashCode()}" }) { entry ->
                    LogEntry(entry)
                }
            }
        }
    }
}

@Composable
private fun LogEntry(entry: Logger.LogEntry) {
    val levelColor = when (entry.level) {
        Level.DEBUG -> Color(0x66888888)
        Level.INFO -> Color(0xFF4CAF50)
        Level.WARN -> Color(0xFFFF9800)
        Level.ERROR -> Color(0xFFF44336)
    }

    AnimatedVisibility(
        visible = true,
        enter = fadeIn(durationMillis = 150),
        exit = fadeOut(durationMillis = 150),
    ) {
        Row(
            Modifier.fillMaxWidth().padding(vertical = 2.dp),
            verticalAlignment = Alignment.Top,
        ) {
            Text(
                entry.timestamp.substring(11, 19),
                style = MaterialTheme.typography.labelSmall,
                fontFamily = FontFamily.Monospace,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.width(80.dp),
            )
            Text(
                entry.level.name.take(4),
                style = MaterialTheme.typography.labelSmall,
                fontFamily = FontFamily.Monospace,
                color = levelColor,
                modifier = Modifier.width(40.dp),
            )
            Text(
                entry.message,
                style = MaterialTheme.typography.labelSmall,
                fontFamily = FontFamily.Monospace,
                color = MaterialTheme.colorScheme.onSurface,
            )
        }
    }
}
