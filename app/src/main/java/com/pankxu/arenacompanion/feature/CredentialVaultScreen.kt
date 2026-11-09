package com.pankxu.arenacompanion.feature

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.pankxu.arenacompanion.core.CredentialType
import com.pankxu.arenacompanion.core.StoredCredential
import kotlinx.coroutines.launch

/**
 * Credential Vault screen.
 *
 * Credentials are stored ONLY when the user explicitly types them into this UI.
 * The app never extracts credentials from the browser, WebView, or any external source.
 * All values are stored encrypted via EncryptedSharedPreferences (AES-256-GCM).
 * Values are masked by default; reveal requires explicit user action.
 */
@Composable
fun CredentialVaultScreen(
    vault: com.pankxu.arenacompanion.core.CredentialVault,
) {
    val context = LocalContext.current
    val credentials by vault.credentials.collectAsStateWithLifecycle(initialValue = emptyList())
    val scope = rememberCoroutineScope()

    var showAddDialog by remember { mutableStateOf(false) }
    var revealedIds = remember { mutableSetOf<String>() }

    Column(Modifier.fillMaxSize().padding(horizontal = 20.dp)) {
        Text("Credential vault", style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(top = 16.dp, bottom = 4.dp))
        Text(
            "Credentials you store here are encrypted with AES-256-GCM and remain on this device only. " +
            "The app does not read credentials from your browser.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(bottom = 12.dp),
        )
        HorizontalDivider()

        Button(onClick = { showAddDialog = true },
            modifier = Modifier.padding(top = 12.dp, bottom = 8.dp)) {
            Text("Add credential")
        }

        if (credentials.isEmpty()) {
            Text("No credentials stored. Add one above.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 16.dp))
        } else {
            LazyColumn(Modifier.fillMaxSize()) {
                items(credentials, key = { it.id }) { cred ->
                    CredentialCard(
                        credential = cred,
                        revealed = revealedIds.contains(cred.id),
                        onToggleReveal = {
                            if (revealedIds.contains(cred.id)) revealedIds.remove(cred.id)
                            else revealedIds.add(cred.id)
                        },
                        onCopy = {
                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE)
                                as ClipboardManager
                            clipboard.setPrimaryClip(ClipData.newPlainText(cred.name, cred.value))
                        },
                        onDelete = { scope.launch { vault.delete(cred.id) } },
                    )
                    HorizontalDivider()
                }
            }
        }
    }

    if (showAddDialog) {
        AddCredentialDialog(
            onAdd = { name, type, value, note ->
                scope.launch {
                    vault.save(StoredCredential(
                        id = System.currentTimeMillis().toString(),
                        name = name,
                        type = type,
                        value = value,
                        createdAt = System.currentTimeMillis(),
                        note = note,
                    ))
                }
                showAddDialog = false
            },
            onDismiss = { showAddDialog = false },
        )
    }
}

@Composable
private fun CredentialCard(
    credential: StoredCredential,
    revealed: Boolean,
    onToggleReveal: () -> Unit,
    onCopy: () -> Unit,
    onDelete: () -> Unit,
) {
    Card(Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
        Column(Modifier.padding(16.dp)) {
            Row(horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically) {
                Text(credential.name, style = MaterialTheme.typography.titleMedium)
                Text(credential.type.name, style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Text(
                if (revealed) credential.value else "•".repeat(minOf(credential.value.length, 24)),
                style = MaterialTheme.typography.bodyMedium,
                fontFamily = FontFamily.Monospace,
                modifier = Modifier.padding(top = 4.dp),
            )
            if (credential.note.isNotBlank()) {
                Text(credential.note, style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 4.dp))
            }
            Row(horizontalArrangement = Arrangement.End, modifier = Modifier.padding(top = 8.dp)) {
                TextButton(onClick = onToggleReveal) { Text(if (revealed) "Hide" else "Reveal") }
                TextButton(onClick = onCopy) { Text("Copy") }
                TextButton(onClick = onDelete) { Text("Delete") }
            }
        }
    }
}

@Composable
private fun AddCredentialDialog(
    onAdd: (name: String, type: CredentialType, value: String, note: String) -> Unit,
    onDismiss: () -> Unit,
) {
    var name by remember { mutableStateOf("") }
    var value by remember { mutableStateOf("") }
    var note by remember { mutableStateOf("") }
    var type by remember { mutableStateOf(CredentialType.API_KEY) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add credential") },
        text = {
            Column {
                OutlinedTextField(name, { name = it }, label = { Text("Name") },
                    modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value, { value = it }, label = { Text("Value (stored encrypted)") },
                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp))
                OutlinedTextField(note, { note = it }, label = { Text("Note (optional)") },
                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp))
            }
        },
        confirmButton = {
            Button(onClick = {
                if (name.isNotBlank() && value.isNotBlank()) onAdd(name, type, value, note)
            }) { Text("Store") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } },
    )
}
