package com.pankxu.arenacompanion.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.pankxu.arenacompanion.data.entities.ModelInfoEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ModelDao {
    @Query("SELECT * FROM model_info ORDER BY displayName ASC")
    fun observeAll(): Flow<List<ModelInfoEntity>>

    @Query("SELECT * FROM model_info WHERE isAvailable = 1 ORDER BY displayName ASC")
    suspend fun getAvailable(): List<ModelInfoEntity>

    @Query("SELECT * FROM model_info WHERE id = :id")
    suspend fun getById(id: String): ModelInfoEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(models: List<ModelInfoEntity>)

    @Query("DELETE FROM model_info")
    suspend fun clear()
}
