package com.pankxu.arenacompanion.domain.models

/** Agent mode types. */
enum class AgentMode {
    AGENT,
    DIRECT,
    BATTLE,
    SIDE_BY_SIDE,
    MULTIMODAL,
}

/** A model available for selection. */
data class ModelInfo(
    val id: String,
    val name: String,
    val displayName: String,
    val arenaType: String,
    val ownedBy: String,
    val isAvailable: Boolean = true,
    val capabilities: List<String> = emptyList(),
    val contextWindow: Int = 0,
    val maxOutputTokens: Int = 0,
)

/** Leaderboard entry. */
data class LeaderboardEntry(
    val rank: Int,
    val modelName: String,
    val arenaScore: Double,
    val organization: String,
    val votes: Int,
    val ciLower: Double,
    val ciUpper: Double,
)

/** Voting result. */
data class VoteResult(
    val modelA: String,
    val modelB: String,
    val winner: String?,
    val tie: Boolean,
)

/** Connector status. */
data class ConnectorStatus(
    val id: String,
    val name: String,
    val type: String,
    val isConnected: Boolean,
    val isAuthenticated: Boolean,
    val permissions: List<String> = emptyList(),
)

/** Conversation mode configuration. */
data class ConversationConfig(
    val mode: AgentMode = AgentMode.AGENT,
    val modelId: String = "",
    val modelIds: List<String> = emptyList(),
    val isAnonymous: Boolean = false,
    val temperature: Double = 0.7,
)
