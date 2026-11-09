package com.pankxu.arenacompanion

import com.pankxu.arenacompanion.security.PrivacyManager
import org.junit.Assert.*
import org.junit.Test

class SecurityManagerTest {

    @Test fun `privacy manager redacts api keys`() {
        val pm = PrivacyManager()
        val result = pm.redact("key is sk-arena-abc123def456")
        assertFalse(result.contains("sk-arena-abc123def456"))
        assertTrue(result.contains("[REDACTED]"))
    }

    @Test fun `privacy manager redacts bearer tokens`() {
        val pm = PrivacyManager()
        val result = pm.redact("Authorization: Bearer eyJhbGciOiJIUzI1NiJ9")
        assertFalse(result.contains("eyJhbGciOiJIUzI1NiJ9"))
        assertTrue(result.contains("[REDACTED]"))
    }

    @Test fun `privacy manager redacts passwords`() {
        val pm = PrivacyManager()
        val result = pm.redact("password=mysecret123")
        assertFalse(result.contains("mysecret123"))
        assertTrue(result.contains("[REDACTED]"))
    }

    @Test fun `privacy policy mentions on-device storage`() {
        val pm = PrivacyManager()
        val policy = pm.describePrivacyPolicy()
        assertTrue(policy.contains("device"))
        assertTrue(policy.contains("encrypted"))
    }
}
