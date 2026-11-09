package com.pankxu.arenacompanion.domain

import com.pankxu.arenacompanion.core.logging.Logger
import com.pankxu.arenacompanion.domain.models.VoteResult

/** Manages vote submission and state. Prevents duplicate submissions. */
class VotingManager {

    private val tag = "VotingManager"
    private val votedConversationIds = mutableSetOf<String>()

    fun hasVoted(conversationId: String): Boolean = conversationId in votedConversationIds

    fun recordVote(conversationId: String, result: VoteResult): Boolean {
        if (conversationId in votedConversationIds) {
            Logger.w(tag, "Duplicate vote prevented for conversation $conversationId")
            return false
        }
        votedConversationIds.add(conversationId)
        Logger.i(tag, "Vote recorded for conversation $conversationId")
        return true
    }

    fun clearVote(conversationId: String) {
        votedConversationIds.remove(conversationId)
    }

    fun getVoteCount(): Int = votedConversationIds.size

    fun clearAll() {
        votedConversationIds.clear()
    }
}
