package com.pankxu.arenacompanion

import com.pankxu.arenacompanion.domain.ConnectorManager
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class ConnectorManagerTest {
    private lateinit var manager: ConnectorManager

    @Before fun setup() { manager = ConnectorManager() }

    @Test fun `discover returns empty initially`() {
        assertTrue(manager.discover().isEmpty())
    }

    @Test fun `disconnect on nonexistent is safe`() {
        manager.disconnect("nonexistent")  // should not crash
    }

    @Test fun `get permissions on nonexistent returns empty`() {
        assertTrue(manager.getPermissions("nonexistent").isEmpty())
    }

    @Test fun `clear empties connectors`() {
        manager.clear()
        assertTrue(manager.discover().isEmpty())
    }
}
