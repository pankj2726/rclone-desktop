package com.pankxu.arenacompanion.domain

import com.pankxu.arenacompanion.core.logging.Logger
import com.pankxu.arenacompanion.core.utils.AppError
import com.pankxu.arenacompanion.core.utils.AppResult
import com.pankxu.arenacompanion.domain.models.AgentMode
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

/** Execution state for agent tasks. */
enum class ExecutionState { IDLE, RUNNING, COMPLETED, FAILED, CANCELLED }

/** Agent execution engine. Manages multi-step task execution. */
class AgentExecutionEngine {

    private val tag = "AgentEngine"

    private val _executionState = MutableStateFlow(ExecutionState.IDLE)
    val executionState: StateFlow<ExecutionState> = _executionState

    private val _currentTask = MutableStateFlow("")
    val currentTask: StateFlow<String> = _currentTask

    private val _progress = MutableStateFlow(0)
    val progress: StateFlow<Int> = _progress

    private val activeTasks = mutableMapOf<String, ExecutionState>()

    fun startTask(taskId: String, description: String): Boolean {
        if (activeTasks.containsKey(taskId)) {
            Logger.w(tag, "Task already active: $taskId")
            return false
        }
        activeTasks[taskId] = ExecutionState.RUNNING
        _currentTask.value = description
        _executionState.value = ExecutionState.RUNNING
        _progress.value = 0
        Logger.i(tag, "Task started: $taskId ($description)")
        return true
    }

    fun updateProgress(taskId: String, progress: Int) {
        if (activeTasks[taskId] == ExecutionState.RUNNING) {
            _progress.value = progress
        }
    }

    fun completeTask(taskId: String): Boolean {
        if (activeTasks[taskId] != ExecutionState.RUNNING) return false
        activeTasks[taskId] = ExecutionState.COMPLETED
        _executionState.value = ExecutionState.COMPLETED
        _progress.value = 100
        Logger.i(tag, "Task completed: $taskId")
        return true
    }

    fun failTask(taskId: String, error: AppError): Boolean {
        if (activeTasks[taskId] != ExecutionState.RUNNING) return false
        activeTasks[taskId] = ExecutionState.FAILED
        _executionState.value = ExecutionState.FAILED
        Logger.e(tag, "Task failed: $taskId — ${error.message}", error.throwable)
        return true
    }

    fun cancelTask(taskId: String): Boolean {
        if (activeTasks[taskId] != ExecutionState.RUNNING) return false
        activeTasks[taskId] = ExecutionState.CANCELLED
        _executionState.value = ExecutionState.CANCELLED
        Logger.i(tag, "Task cancelled: $taskId")
        return true
    }

    fun getTaskState(taskId: String): ExecutionState? = activeTasks[taskId]

    fun isTaskActive(taskId: String): Boolean =
        activeTasks[taskId] == ExecutionState.RUNNING

    fun reset() {
        activeTasks.clear()
        _executionState.value = ExecutionState.IDLE
        _currentTask.value = ""
        _progress.value = 0
    }
}
