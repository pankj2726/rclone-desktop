package com.pankxu.arenacompanion.core.storage

import android.content.Context
import com.pankxu.arenacompanion.data.database.AppDatabase
import com.pankxu.arenacompanion.data.dao.AttachmentDao
import com.pankxu.arenacompanion.data.dao.ConversationDao
import com.pankxu.arenacompanion.data.dao.MessageDao
import com.pankxu.arenacompanion.data.dao.ModelDao

/** Manages Room database access. */
class DatabaseManager(context: Context) {

    val database: AppDatabase = AppDatabase.getInstance(context.applicationContext)
    val conversationDao: ConversationDao = database.conversationDao()
    val messageDao: MessageDao = database.messageDao()
    val attachmentDao: AttachmentDao = database.attachmentDao()
    val modelDao: ModelDao = database.modelDao()
}
