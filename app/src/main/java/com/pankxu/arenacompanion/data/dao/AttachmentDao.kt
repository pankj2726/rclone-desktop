package com.pankxu.arenacompanion.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import androidx.room.Delete
import com.pankxu.arenacompanion.data.entities.AttachmentEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AttachmentDao {
    @Query("SELECT * FROM attachments WHERE conversationId = :conversationId ORDER BY createdAt ASC")
    suspend fun getByConversation(conversationId: String): List<AttachmentEntity>

    @Query("SELECT * FROM attachments WHERE messageId = :messageId ORDER BY createdAt ASC")
    suspend fun getByMessage(messageId: String): List<AttachmentEntity>

    @Query("SELECT * FROM attachments WHERE id = :id")
    suspend fun getById(id: String): AttachmentEntity?

    @Query("SELECT * FROM attachments WHERE uploadStatus = :status ORDER BY createdAt ASC")
    suspend fun getByUploadStatus(status: String): List<AttachmentEntity>

    @Insert
    suspend fun insert(attachment: AttachmentEntity)

    @Update
    suspend fun update(attachment: AttachmentEntity)

    @Query("UPDATE attachments SET uploadStatus = :status, remoteRef = :remoteRef WHERE id = :id")
    suspend fun updateUploadStatus(id: String, status: String, remoteRef: String)

    @Query("DELETE FROM attachments WHERE conversationId = :conversationId")
    suspend fun deleteByConversation(conversationId: String)

    @Query("DELETE FROM attachments WHERE id = :id")
    suspend fun deleteById(id: String)
}
