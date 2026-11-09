package com.pankxu.arenacompanion.domain

import com.pankxu.arenacompanion.domain.models.LeaderboardEntry

/** Manages leaderboard data (cached locally). */
class LeaderboardManager {

    private val cache = mutableListOf<LeaderboardEntry>()

    fun getLeaderboard(limit: Int = 100): List<LeaderboardEntry> {
        // TODO: Fetch from Arena API when available
        // Currently returns cached data only
        return cache.take(limit)
    }

    fun refresh(): Boolean {
        // TODO: Fetch from Arena API
        // Returns false until backend integration is complete
        return false
    }

    fun isCached(): Boolean = cache.isNotEmpty()

    fun clearCache() {
        cache.clear()
    }
}
