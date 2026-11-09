package com.pankxu.arenacompanion.domain

import com.pankxu.arenacompanion.core.logging.Logger
import com.pankxu.arenacompanion.domain.models.ConnectorStatus

/** Manages connector discovery and authentication state. */
class ConnectorManager {

    private val tag = "ConnectorManager"
    private val connectors = mutableMapOf<String, ConnectorStatus>()

    fun discover(): List<ConnectorStatus> {
        // TODO: Discover connectors from Arena API when available
        return connectors.values.toList()
    }

    fun getConnector(id: String): ConnectorStatus? = connectors[id]

    fun connect(id: String): Boolean {
        // TODO: Implement connector authentication
        Logger.i(tag, "Connector connect requested: $id (requires backend)")
        return false
    }

    fun disconnect(id: String) {
        val connector = connectors[id] ?: return
        connectors[id] = connector.copy(isConnected = false, isAuthenticated = false)
        Logger.i(tag, "Connector disconnected: $id")
    }

    fun getPermissions(id: String): List<String> =
        connectors[id]?.permissions ?: emptyList()

    fun clear() {
        connectors.clear()
    }
}
