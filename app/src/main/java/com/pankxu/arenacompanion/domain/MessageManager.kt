package com.pankxu.arenacompanion.domain

import com.pankxu.arenacompanion.core.storage.DatabaseManager
import com.pankxu.arenacompanion.core.utils.IdGenerator
import com.pankxu.arenacompanion.data.entities.MessageEntity
import kotlinx.coroutines.flow.Flow

/** Manages message CRUD and ordering. */
class MessageManager(private val db: DatabaseManager) {

    fun observeMessages(conversationId: String): Flow<List<MessageEntity>> =
        db.messageDao.observeByConversation(conversationId)

    suspend fun getMessages(conversationId: String): List<MessageEntity> =
        db.messageDao.getByConversation(conversationId)

    suspend fun addUserMessage(conversationId: String, content: String, attachments: String = ""): MessageEntity {
        val now = System.currentTimeMillis()
        val msg = MessageEntity(
            id = IdGenerator.uuid(),
            conversationId = conversationId,
            role = "user",
            content = content,
            createdAt = now,
            updatedAt = now,
            attachments = attachments,
        )
        db.messageDao.insert(msg)
        return msg
    }

    suspend fun addAssistantMessage(
        conversationId: String,
        content: String,
        model: String,
    ): MessageEntity {
        val now = System.currentTimeMillis()
        val msg = MessageEntity(
            id = IdGenerator.uuid(),
            conversationId = conversationId,
            role = "assistant",
            content = content,
            createdAt = now,
            updatedAt = now,
            model = model,
        )
        db.messageDao.insert(msg)
        return msg
    }

    suspend fun addPendingMessage(
        conversationId: String,
        content: String,
        model: String,
    ): MessageEntity {
        val now = System.currentTimeMillis()
        val msg = MessageEntity(
            id = IdGenerator.uuid(),
            conversationId = conversationId,
            role = "assistant",
            content = content,
            createdAt = now,
            updatedAt = now,
            model = model,
            pending = true,
        )
        db.messageDao.insert(msg)
        return msg
    }

    suspend fun updateStreamingContent(messageId: String, content: String) {
        db.messageDao.updateContent(messageId, content, true, System.currentTimeMillis())
    }

    suspend fun markComplete(messageId: String, content: String) {
        db.messageDao.updateContent(messageId, content, false, System.currentTimeMillis())
    }

    suspend fun markError(messageId: String, error: String) {
        db.messageDao.setError(messageId, error)
    }

    suspend fun editMessage(messageId: String, newContent: String) {
        db.messageDao.updateContent(messageId, newContent, false, System.currentTimeMillis())
    }

    suspend fun deleteMessage(messageId: String) {
        db.messageDao.deleteById(messageId)
    }

    suspend fun countMessages(conversationId: String): Int =
        db.messageDao.countByConversation(conversationId)
}
