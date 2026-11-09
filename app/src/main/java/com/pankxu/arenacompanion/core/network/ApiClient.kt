package com.pankxu.arenacompanion.core.network

import android.content.Context
import com.pankxu.arenacompanion.core.config.AppConfig
import com.pankxu.arenacompanion.core.logging.Logger
import com.pankxu.arenacompanion.core.utils.AppError
import com.pankxu.arenacompanion.core.utils.AppResult
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.Response
import okio.IOException
import java.util.concurrent.TimeUnit

class ApiClient(context: Context) {

    private val tag = "ApiClient"
    private val client: OkHttpClient

    init {
        client = OkHttpClient.Builder()
            .connectTimeout(AppConfig.CONNECT_TIMEOUT_MS, TimeUnit.MILLISECONDS)
            .readTimeout(AppConfig.READ_TIMEOUT_MS, TimeUnit.MILLISECONDS)
            .writeTimeout(AppConfig.WRITE_TIMEOUT_MS, TimeUnit.MILLISECONDS)
            .retryOnConnectionFailure(false)
            .build()
    }

    fun get(path: String, headers: Map<String, String> = emptyMap()): AppResult<Response> {
        return execute(Request.Builder()
            .url("${AppConfig.PLATFORM_API_BASE}$path")
            .get()
            .headers(headers.toHeaders())
            .build())
    }

    fun post(path: String, body: RequestBody, headers: Map<String, String> = emptyMap()): AppResult<Response> {
        return execute(Request.Builder()
            .url("${AppConfig.PLATFORM_API_BASE}$path")
            .post(body)
            .headers(headers.toHeaders())
            .build())
    }

    fun postJson(path: String, json: String, apiKey: String): AppResult<Response> {
        return post(path, json.toRequestBody("application/json"),
            mapOf("Authorization" to "Bearer $apiKey"))
    }

    fun postMultipart(
        path: String,
        fileBody: RequestBody,
        filename: String,
        mimeType: String,
        apiKey: String,
    ): AppResult<Response> {
        val body = okhttp3.MultipartBody.Builder()
            .setType(okhttp3.MultipartBody.FORM)
            .addPart(okhttp3.MultipartBody.Part.createFormData("file", filename, fileBody))
            .build()
        return post(path, body, mapOf(
            "Authorization" to "Bearer $apiKey",
            "Content-Type" to "multipart/form-data",
        ))
    }

    private fun execute(request: Request): AppResult<Response> {
        return try {
            Logger.d(tag, "${request.method} ${request.url}")
            val response = client.newCall(request).execute()
            if (response.isSuccessful) {
                AppResult.Success(response)
            } else {
                AppResult.Failure(NetworkErrorHandler.classify(response))
            }
        } catch (e: IOException) {
            if (e is java.net.SocketTimeoutException) {
                AppResult.Failure(AppError(AppError.TIMEOUT, "Request timed out", e))
            } else {
                AppResult.Failure(AppError(AppError.NETWORK, "Network error: ${e.message}", e))
            }
        } catch (e: Exception) {
            AppResult.Failure(AppError(AppError.UNKNOWN, e.message ?: "Unknown error", e))
        }
    }

    private fun Map<String, String>.toHeaders(): okhttp3.Headers {
        val builder = okhttp3.Headers.Builder()
        this.forEach { (k, v) -> builder.add(k, v) }
        return builder.build()
    }

    fun shutdown() {
        client.dispatcher.executorService.shutdown()
        client.connectionPool.evictAll()
    }
}

private fun String.toRequestBody(contentType: String): RequestBody {
    return RequestBody.create(contentType.toMediaType(), this)
}
