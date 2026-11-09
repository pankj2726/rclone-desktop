package com.pankxu.arenacompanion.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "model_info")
data class ModelInfoEntity(
    @PrimaryKey val id: String,
    val name: String,
    val displayName: String,
    val arenaType: String,
    val ownedBy: String,
    val isAvailable: Boolean = true,
    val capabilities: String = "",  // JSON array
    val lastUpdated: Long,
)
