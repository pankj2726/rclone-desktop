package com.pankxu.arenacompanion.domain

import com.pankxu.arenacompanion.core.logging.Logger
import com.pankxu.arenacompanion.core.utils.AppError
import com.pankxu.arenacompanion.core.utils.AppResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

/** Tool definition. */
data class ToolDefinition(
    val id: String,
    val name: String,
    val description: String,
    val parameters: Map<String, String> = emptyMap(),
    val requiresUserApproval: Boolean = false,
)

/** Tool execution result. */
data class ToolResult(
    val toolId: String,
    val success: Boolean,
    val output: String,
    val error: String = "",
    val executionTimeMs: Long = 0,
)

/** Registry of available tools. */
class ToolRegistry {

    private val tag = "ToolRegistry"
    private val tools = mutableMapOf<String, ToolDefinition>()

    private val _executionHistory = MutableStateFlow<List<ToolResult>>(emptyList())
    val executionHistory: StateFlow<List<ToolResult>> = _executionHistory

    fun register(tool: ToolDefinition) {
        tools[tool.id] = tool
        Logger.i(tag, "Tool registered: ${tool.name} ($tool.id)")
    }

    fun unregister(toolId: String) {
        tools.remove(toolId)
        Logger.i(tag, "Tool unregistered: $toolId")
    }

    fun getTool(id: String): ToolDefinition? = tools[id]

    fun listTools(): List<ToolDefinition> = tools.values.toList()

    fun execute(
        toolId: String,
        parameters: Map<String, String>,
        executor: suspend (ToolDefinition, Map<String, String>) -> AppResult<String>,
    ): AppResult<ToolResult> {
        val tool = tools[toolId]
            ?: return AppResult.Failure(AppError(AppError.UNKNOWN, "Tool not found: $toolId"))

        if (tool.requiresUserApproval) {
            Logger.w(tag, "Tool ${tool.name} requires user approval before execution")
            // In a real implementation, this would prompt the user
        }

        return try {
            val startTime = System.currentTimeMillis()
            val result = executor(tool, parameters)
            val elapsed = System.currentTimeMillis() - startTime

            val toolResult = ToolResult(
                toolId = toolId,
                success = result.isSuccess(),
                output = result.dataOrNull() ?: "",
                error = result.errorOrNull()?.message ?: "",
                executionTimeMs = elapsed,
            )

            _executionHistory.value = _executionHistory.value + toolResult
            AppResult.Success(toolResult)
        } catch (e: Exception) {
            val toolResult = ToolResult(
                toolId = toolId,
                success = false,
                output = "",
                error = e.message ?: "Unknown error",
                executionTimeMs = 0,
            )
            _executionHistory.value = _executionHistory.value + toolResult
            AppResult.Failure(AppError(AppError.UNKNOWN, e.message ?: "Unknown error", e))
        }
    }

    fun clearHistory() {
        _executionHistory.value = emptyList()
    }
}
