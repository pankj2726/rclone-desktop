package com.pankxu.arenacompanion.domain

import com.pankxu.arenacompanion.core.logging.Logger
import com.pankxu.arenacompanion.core.storage.DatabaseManager
import com.pankxu.arenacompanion.core.utils.IdGenerator
import com.pankxu.arenacompanion.data.entities.ConversationEntity
import com.pankxu.arenacompanion.data.entities.MessageEntity
import kotlinx.coroutines.flow.Flow

/** Manages conversation CRUD and ordering. */
class ConversationManager(private val db: DatabaseManager) {

    val conversations: Flow<List<ConversationEntity>> = db.conversationDao.observeAll()

    suspend fun create(title: String, model: String, mode: String): ConversationEntity {
        val now = System.currentTimeMillis()
        val conv = ConversationEntity(
            id = IdGenerator.uuid(),
            title = title,
            model = model,
            mode = mode,
            createdAt = now,
            updatedAt = now,
        )
        db.conversationDao.insert(conv)
        return conv
    }

    suspend fun getById(id: String): ConversationEntity? = db.conversationDao.getById(id)

    suspend fun getActive(): List<ConversationEntity> = db.conversationDao.getActive()

    suspend fun getArchived(): List<ConversationEntity> = db.conversationDao.getArchived()

    suspend fun rename(id: String, newTitle: String) {
        val conv = getById(id) ?: return
        db.conversationDao.update(conv.copy(title = newTitle, updatedAt = System.currentTimeMillis()))
    }

    suspend fun archive(id: String) = db.conversationDao.setArchived(id, true)

    suspend fun unarchive(id: String) = db.conversationDao.setArchived(id, false)

    suspend fun delete(id: String) {
        db.messageDao.deleteByConversation(id)
        db.attachmentDao.deleteByConversation(id)
        db.conversationDao.deleteById(id)
    }

    suspend fun updateMessageCount(id: String, preview: String, count: Int) {
        db.conversationDao.updatePreview(id, preview, count, System.currentTimeMillis())
    }

    suspend fun updateSize(id: String, sizeBytes: Long) {
        db.conversationDao.updateSize(id, sizeBytes)
    }

    suspend fun sendMessage(conversationId: String, content: String) {
        val now = System.currentTimeMillis()
        val msg = MessageEntity(
            id = IdGenerator.uuid(),
            conversationId = conversationId,
            role = "user",
            content = content,
            createdAt = now,
            updatedAt = now,
        )
        db.messageDao.insert(msg)
        updateMessageCount(conversationId, content.take(100), db.messageDao.countByConversation(conversationId))
    }

    fun stop(conversationId: String) {
        Logger.i("ConversationManager", "Conversation $conversationId stopped")
    }
}
