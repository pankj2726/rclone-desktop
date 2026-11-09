package com.pankxu.arenacompanion.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.pankxu.arenacompanion.data.dao.AttachmentDao
import com.pankxu.arenacompanion.data.dao.ConversationDao
import com.pankxu.arenacompanion.data.dao.MessageDao
import com.pankxu.arenacompanion.data.dao.ModelDao
import com.pankxu.arenacompanion.data.entities.AttachmentEntity
import com.pankxu.arenacompanion.data.entities.ConversationEntity
import com.pankxu.arenacompanion.data.entities.MessageEntity
import com.pankxu.arenacompanion.data.entities.ModelInfoEntity

@Database(
    entities = [
        ConversationEntity::class,
        MessageEntity::class,
        AttachmentEntity::class,
        ModelInfoEntity::class,
    ],
    version = 1,
    exportSchema = false,
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun conversationDao(): ConversationDao
    abstract fun messageDao(): MessageDao
    abstract fun attachmentDao(): AttachmentDao
    abstract fun modelDao(): ModelDao

    companion object {
        @Volatile private var instance: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return instance ?: synchronized(this) {
                instance ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "app_database"
                ).fallbackToDestructiveMigration().build().also { instance = it }
            }
        }
    }
}
