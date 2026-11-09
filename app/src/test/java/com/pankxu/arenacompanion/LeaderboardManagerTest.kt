package com.pankxu.arenacompanion

import com.pankxu.arenacompanion.domain.LeaderboardManager
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class LeaderboardManagerTest {
    private lateinit var manager: LeaderboardManager

    @Before fun setup() { manager = LeaderboardManager() }

    @Test fun `not cached initially`() {
        assertFalse(manager.isCached())
    }

    @Test fun `clear cache works`() {
        manager.clearCache()
        assertFalse(manager.isCached())
    }

    @Test fun `refresh returns false without backend`() {
        // TODO: Will return true when Arena API integration is complete
        assertFalse(manager.refresh())
    }

    @Test fun `get leaderboard returns empty without cache`() {
        assertTrue(manager.getLeaderboard().isEmpty())
    }
}
