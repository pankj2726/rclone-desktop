package com.pankxu.arenacompanion.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "attachments")
data class AttachmentEntity(
    @PrimaryKey val id: String,
    val conversationId: String,
    val messageId: String,
    val fileName: String,
    val mimeType: String,
    val sizeBytes: Long,
    val localPath: String,       // path in app-private storage
    val remoteRef: String = "",  // backend reference if uploaded
    val uploadStatus: String = "pending",  // pending, uploading, uploaded, failed
    val createdAt: Long,
)
