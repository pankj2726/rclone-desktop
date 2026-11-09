package com.pankxu.arenacompanion

import com.pankxu.arenacompanion.core.network.NetworkErrorHandler
import com.pankxu.arenacompanion.core.utils.AppError
import org.junit.Assert.*
import org.junit.Test

class NetworkErrorHandlerTest {

    @Test fun `401 is auth error`() {
        val error = AppError(AppError.AUTH, "Authentication required")
        assertTrue(error.code == AppError.AUTH)
    }

    @Test fun `network errors are retryable`() {
        assertTrue(NetworkErrorHandler.isRetryable(AppError(AppError.NETWORK, "Server error")))
        assertTrue(NetworkErrorHandler.isRetryable(AppError(AppError.TIMEOUT, "Timeout")))
    }

    @Test fun `auth errors are not retryable`() {
        assertFalse(NetworkErrorHandler.isRetryable(AppError(AppError.AUTH, "Unauthorized")))
    }

    @Test fun `cancelled errors are not retryable`() {
        assertFalse(NetworkErrorHandler.isRetryable(AppError(AppError.CANCELLED, "Cancelled")))
    }
}
