package com.pankxu.arenacompanion.core.network

import com.pankxu.arenacompanion.core.logging.Logger
import okhttp3.Interceptor
import okhttp3.Response

/**
 * Interceptor that adds Authorization header for API requests.
 * The API key is passed at runtime, never stored in plaintext.
 */
class AuthenticationInterceptor(
    private val apiKeyProvider: () -> String?,
) : Interceptor {

    private val tag = "AuthInterceptor"

    override fun intercept(chain: Interceptor.Chain): Response {
        val apiKey = apiKeyProvider()
        if (apiKey.isNullOrEmpty()) {
            Logger.w(tag, "No API key available for request to ${chain.request().url}")
            return chain.proceed(chain.request())
        }

        val authenticatedRequest = chain.request().newBuilder()
            .header("Authorization", "Bearer $apiKey")
            .header("Content-Type", "application/json")
            .build()

        return chain.proceed(authenticatedRequest)
    }
}
