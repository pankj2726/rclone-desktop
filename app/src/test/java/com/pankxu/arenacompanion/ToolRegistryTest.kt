package com.pankxu.arenacompanion

import com.pankxu.arenacompanion.core.utils.AppResult
import com.pankxu.arenacompanion.domain.ToolDefinition
import com.pankxu.arenacompanion.domain.ToolRegistry
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class ToolRegistryTest {
    private lateinit var registry: ToolRegistry

    @Before fun setup() { registry = ToolRegistry() }

    @Test fun `register and retrieve tool`() {
        registry.register(ToolDefinition("t1", "Test Tool", "A test tool"))
        val tool = registry.getTool("t1")
        assertNotNull(tool)
        assertEquals("Test Tool", tool!!.name)
    }

    @Test fun `unregister removes tool`() {
        registry.register(ToolDefinition("t1", "Test Tool", "A test tool"))
        registry.unregister("t1")
        assertNull(registry.getTool("t1"))
    }

    @Test fun `list tools returns registered`() {
        registry.register(ToolDefinition("t1", "Tool 1", "desc"))
        registry.register(ToolDefinition("t2", "Tool 2", "desc"))
        assertEquals(2, registry.listTools().size)
    }

    @Test fun `execute registered tool succeeds`() {
        registry.register(ToolDefinition("t1", "Test Tool", "A test tool"))
        val result = registry.execute("t1", emptyMap()) { _, _ ->
            AppResult.Success("output")
        }
        assertTrue(result.isSuccess())
        assertEquals("output", (result as AppResult.Success).data.output)
        assertTrue(result.data.success)
    }

    @Test fun `execute unregistered tool fails`() {
        val result = registry.execute("nonexistent", emptyMap()) { _, _ ->
            AppResult.Success("output")
        }
        assertTrue(result.isFailure())
    }

    @Test fun `execution history recorded`() {
        registry.register(ToolDefinition("t1", "Test Tool", "A test tool"))
        registry.execute("t1", emptyMap()) { _, _ -> AppResult.Success("output") }
        assertEquals(1, registry.executionHistory.value.size)
    }

    @Test fun `clear history empties list`() {
        registry.register(ToolDefinition("t1", "Test Tool", "A test tool"))
        registry.execute("t1", emptyMap()) { _, _ -> AppResult.Success("output") }
        registry.clearHistory()
        assertTrue(registry.executionHistory.value.isEmpty())
    }
}
