package com.pankxu.arenacompanion

import com.pankxu.arenacompanion.core.auth.AuthStateManager
import org.junit.Assert.*
import org.junit.Test

class AuthStateManagerTest {

    @Test fun `starts unauthenticated`() {
        val mgr = AuthStateManager()
        assertEquals(AuthStateManager.AuthStatus.UNAUTHENTICATED, mgr.status.value)
        assertFalse(mgr.hasCredentials())
    }

    @Test fun `credential count updates status`() {
        val mgr = AuthStateManager()
        mgr.setCredentialCount(1)
        assertEquals(AuthStateManager.AuthStatus.CREDENTIALS_STORED, mgr.status.value)
        assertTrue(mgr.hasCredentials())
    }

    @Test fun `zero credentials back to unauthenticated`() {
        val mgr = AuthStateManager()
        mgr.setCredentialCount(3)
        mgr.setCredentialCount(0)
        assertEquals(AuthStateManager.AuthStatus.UNAUTHENTICATED, mgr.status.value)
        assertFalse(mgr.hasCredentials())
    }
}
