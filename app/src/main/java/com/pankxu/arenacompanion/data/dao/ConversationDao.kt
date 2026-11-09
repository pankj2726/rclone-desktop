package com.pankxu.arenacompanion.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import androidx.room.Delete
import androidx.room.Transaction
import com.pankxu.arenacompanion.data.entities.ConversationEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ConversationDao {
    @Query("SELECT * FROM conversations ORDER BY updatedAt DESC")
    fun observeAll(): Flow<List<ConversationEntity>>

    @Query("SELECT * FROM conversations WHERE id = :id")
    suspend fun getById(id: String): ConversationEntity?

    @Query("SELECT * FROM conversations WHERE archived = 0 ORDER BY updatedAt DESC")
    suspend fun getActive(): List<ConversationEntity>

    @Query("SELECT * FROM conversations WHERE archived = 1 ORDER BY updatedAt DESC")
    suspend fun getArchived(): List<ConversationEntity>

    @Insert
    suspend fun insert(conversation: ConversationEntity)

    @Update
    suspend fun update(conversation: ConversationEntity)

    @Query("UPDATE conversations SET archived = :archived WHERE id = :id")
    suspend fun setArchived(id: String, archived: Boolean)

    @Query("UPDATE conversations SET lastMessagePreview = :preview, messageCount = :count, updatedAt = :now WHERE id = :id")
    suspend fun updatePreview(id: String, preview: String, count: Int, now: Long)

    @Query("UPDATE conversations SET estimatedSize = :size WHERE id = :id")
    suspend fun updateSize(id: String, size: Long)

    @Delete
    suspend fun delete(conversation: ConversationEntity)

    @Query("DELETE FROM conversations WHERE id = :id")
    suspend fun deleteById(id: String)

    @Transaction
    suspend fun deleteWithMessages(conversation: ConversationEntity) {
        delete(conversation)
    }
}
