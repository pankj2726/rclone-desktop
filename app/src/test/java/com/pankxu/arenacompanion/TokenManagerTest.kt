package com.pankxu.arenacompanion

import com.pankxu.arenacompanion.core.auth.TokenManager
import org.junit.Assert.*
import org.junit.Test

class TokenManagerTest {

    private val tokenManager = TokenManager()

    @Test fun `expired token detected`() {
        assertTrue(tokenManager.isExpired(System.currentTimeMillis() - 1000))
    }

    @Test fun `future token not expired`() {
        assertFalse(tokenManager.isExpired(System.currentTimeMillis() + 100000))
    }

    @Test fun `no expiry token not expired`() {
        assertFalse(tokenManager.isExpired(0))
    }

    @Test fun `format expiry shows remaining time`() {
        val future = System.currentTimeMillis() + 2 * 60 * 60 * 1000  // 2 hours
        val formatted = tokenManager.formatExpiry(future)
        assertTrue(formatted.contains("h remaining"))
    }

    @Test fun `format expiry shows expired`() {
        val past = System.currentTimeMillis() - 1000
        val formatted = tokenManager.formatExpiry(past)
        assertEquals("Expired", formatted)
    }
}
