package com.pankxu.arenacompanion

import com.pankxu.arenacompanion.domain.VotingManager
import com.pankxu.arenacompanion.domain.models.VoteResult
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class VotingManagerTest {
    private lateinit var manager: VotingManager

    @Before fun setup() { manager = VotingManager() }

    @Test fun `vote recorded successfully`() {
        val result = manager.recordVote("conv1", VoteResult("modelA", "modelB", "modelA", false))
        assertTrue(result)
        assertTrue(manager.hasVoted("conv1"))
    }

    @Test fun `duplicate vote prevented`() {
        manager.recordVote("conv1", VoteResult("modelA", "modelB", "modelA", false))
        val result = manager.recordVote("conv1", VoteResult("modelA", "modelB", "modelB", false))
        assertFalse(result)
        assertEquals(1, manager.getVoteCount())
    }

    @Test fun `clear vote allows revote`() {
        manager.recordVote("conv1", VoteResult("modelA", "modelB", "modelA", false))
        manager.clearVote("conv1")
        assertFalse(manager.hasVoted("conv1"))
        val result = manager.recordVote("conv1", VoteResult("modelA", "modelB", "modelB", false))
        assertTrue(result)
    }

    @Test fun `tie vote recorded`() {
        val result = manager.recordVote("conv1", VoteResult("modelA", "modelB", null, true))
        assertTrue(result)
        assertTrue(manager.hasVoted("conv1"))
    }
}
