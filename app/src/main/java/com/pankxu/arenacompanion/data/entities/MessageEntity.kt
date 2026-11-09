package com.pankxu.arenacompanion.data.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "messages",
    foreignKeys = [ForeignKey(entity = ConversationEntity::class, parentColumns = ["id"], childColumns = ["conversationId"])],
    indices = [Index("conversationId")]
)
data class MessageEntity(
    @PrimaryKey val id: String,
    val conversationId: String,
    val role: String,           // "user", "assistant", "system"
    val content: String,
    val createdAt: Long,
    val updatedAt: Long,
    val attachments: String = "",  // JSON array of attachment refs
    val model: String = "",         // which model generated this
    val error: String = "",         // error message if failed
    val pending: Boolean = false,   // true if still streaming/sending
)
