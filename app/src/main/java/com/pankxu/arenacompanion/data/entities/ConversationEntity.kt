package com.pankxu.arenacompanion.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "conversations")
data class ConversationEntity(
    @PrimaryKey val id: String,
    val title: String,
    val model: String,
    val mode: String,           // "agent", "direct", "battle", "sidebyside"
    val createdAt: Long,
    val updatedAt: Long,
    val archived: Boolean = false,
    val lastMessagePreview: String = "",
    val messageCount: Int = 0,
    val estimatedSize: Long = 0L,
)
