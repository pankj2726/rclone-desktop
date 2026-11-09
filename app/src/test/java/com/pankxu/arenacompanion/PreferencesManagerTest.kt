package com.pankxu.arenacompanion

import com.pankxu.arenacompanion.core.utils.AppError
import com.pankxu.arenacompanion.core.utils.AppResult
import com.pankxu.arenacompanion.core.utils.runSafe
import com.pankxu.arenacompanion.core.utils.runSafeSuspend
import org.junit.Assert.*
import org.junit.Test

class PreferencesManagerTest {

    @Test fun `runSafe success returns data`() {
        val result = runSafe { "test" }
        assertTrue(result.isSuccess())
        assertEquals("test", result.dataOrNull())
    }

    @Test fun `runSafe failure returns error`() {
        val result = runSafe { throw RuntimeException("test error") }
        assertTrue(result.isFailure())
        assertEquals("test error", result.errorOrNull()?.message)
    }

    @Test fun `runSafe handles null result`() {
        val result = runSafe { null as String? }
        assertTrue(result.isSuccess())
        assertNull(result.dataOrNull())
    }

    @Test fun `appError has correct codes`() {
        assertEquals(1, AppError.NETWORK)
        assertEquals(2, AppError.AUTH)
        assertEquals(3, AppError.VALIDATION)
        assertEquals(4, AppError.UPLOAD)
        assertEquals(5, AppError.OFFLINE)
        assertEquals(6, AppError.TIMEOUT)
        assertEquals(7, AppError.BLOCKED)
        assertEquals(8, AppError.CANCELLED)
    }
}
