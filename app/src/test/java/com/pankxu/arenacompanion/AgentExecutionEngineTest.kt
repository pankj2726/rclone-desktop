package com.pankxu.arenacompanion

import com.pankxu.arenacompanion.core.utils.AppError
import com.pankxu.arenacompanion.domain.AgentExecutionEngine
import com.pankxu.arenacompanion.domain.ExecutionState
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class AgentExecutionEngineTest {
    private lateinit var engine: AgentExecutionEngine

    @Before fun setup() { engine = AgentExecutionEngine() }

    @Test fun `starts idle`() {
        assertEquals(ExecutionState.IDLE, engine.executionState.value)
    }

    @Test fun `start task transitions to running`() {
        assertTrue(engine.startTask("task1", "Test task"))
        assertEquals(ExecutionState.RUNNING, engine.executionState.value)
        assertEquals("Test task", engine.currentTask.value)
        assertTrue(engine.isTaskActive("task1"))
    }

    @Test fun `duplicate task prevented`() {
        engine.startTask("task1", "Task 1")
        assertFalse(engine.startTask("task1", "Task 1 again"))
    }

    @Test fun `complete task transitions to completed`() {
        engine.startTask("task1", "Test task")
        assertTrue(engine.completeTask("task1"))
        assertEquals(ExecutionState.COMPLETED, engine.executionState.value)
        assertEquals(100, engine.progress.value)
        assertFalse(engine.isTaskActive("task1"))
    }

    @Test fun `fail task transitions to failed`() {
        engine.startTask("task1", "Test task")
        assertTrue(engine.failTask("task1", AppError(AppError.UNKNOWN, "Test error")))
        assertEquals(ExecutionState.FAILED, engine.executionState.value)
    }

    @Test fun `cancel task transitions to cancelled`() {
        engine.startTask("task1", "Test task")
        assertTrue(engine.cancelTask("task1"))
        assertEquals(ExecutionState.CANCELLED, engine.executionState.value)
    }

    @Test fun `complete inactive task fails`() {
        assertFalse(engine.completeTask("nonexistent"))
    }

    @Test fun `update progress only when running`() {
        engine.startTask("task1", "Test task")
        engine.updateProgress("task1", 50)
        assertEquals(50, engine.progress.value)
        engine.completeTask("task1")
        engine.updateProgress("task1", 75)
        assertEquals(100, engine.progress.value)
    }

    @Test fun `reset clears state`() {
        engine.startTask("task1", "Test task")
        engine.reset()
        assertEquals(ExecutionState.IDLE, engine.executionState.value)
        assertFalse(engine.isTaskActive("task1"))
    }
}
