package com.pankxu.arenacompanion.domain

import com.pankxu.arenacompanion.core.storage.DatabaseManager
import com.pankxu.arenacompanion.data.entities.ModelInfoEntity
import com.pankxu.arenacompanion.domain.models.ModelInfo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/** Manages model discovery and caching. */
class ModelDiscoveryManager(private val db: DatabaseManager) {

    val models: Flow<List<ModelInfo>> = db.modelDao.observeAll().map { entities ->
        entities.map { it.toDomain() }
    }

    suspend fun getAvailableModels(): List<ModelInfo> {
        return db.modelDao.getAvailable().map { it.toDomain() }
    }

    suspend fun getModel(id: String): ModelInfo? {
        return db.modelDao.getById(id)?.toDomain()
    }

    suspend fun refresh(models: List<ModelInfo>) {
        val entities = models.map { it.toEntity() }
        db.modelDao.insertAll(entities)
    }

    suspend fun search(query: String): List<ModelInfo> {
        val lower = query.lowercase()
        return getAvailableModels().filter {
            it.displayName.lowercase().contains(lower) ||
            it.name.lowercase().contains(lower) ||
            it.ownedBy.lowercase().contains(lower)
        }
    }

    private fun ModelInfoEntity.toDomain(): ModelInfo {
        return ModelInfo(
            id = id,
            name = name,
            displayName = displayName,
            arenaType = arenaType,
            ownedBy = ownedBy,
            isAvailable = isAvailable,
            capabilities = capabilities.split(",").filter { it.isNotBlank() },
        )
    }

    private fun ModelInfo.toEntity(): ModelInfoEntity {
        return ModelInfoEntity(
            id = id,
            name = name,
            displayName = displayName,
            arenaType = arenaType,
            ownedBy = ownedBy,
            isAvailable = isAvailable,
            capabilities = capabilities.joinToString(","),
            lastUpdated = System.currentTimeMillis(),
        )
    }
}
