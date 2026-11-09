package com.pankxu.arenacompanion

import com.pankxu.arenacompanion.navigation.DeepLinkHandler
import org.junit.Assert.*
import org.junit.Test

class DeepLinkHandlerTest {

    private val validator = DeepLinkHandler.Validator

    @Test fun `arena ai homepage accepted`() {
        assertTrue(validator.validate("https://arena.ai/"))
    }

    @Test fun `arena ai public path accepted`() {
        assertTrue(validator.validate("https://arena.ai/leaderboard"))
    }

    @Test fun `non arena host rejected`() {
        assertFalse(validator.validate("https://google.com/"))
    }

    @Test fun `non https scheme rejected`() {
        assertFalse(validator.validate("http://arena.ai/"))
    }

    @Test fun `forbidden api path rejected`() {
        assertFalse(validator.validate("https://arena.ai/api/test"))
    }

    @Test fun `forbidden nextjs api path rejected`() {
        assertFalse(validator.validate("https://arena.ai/nextjs-api/test"))
    }

    @Test fun `forbidden agent path rejected`() {
        assertFalse(validator.validate("https://arena.ai/agent/chat"))
    }

    @Test fun `auth path opens in browser`() {
        assertTrue(validator.shouldOpenInBrowser("https://arena.ai/auth/login"))
    }
}
