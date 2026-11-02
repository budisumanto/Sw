package com.example.util

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.io.IOException
import java.util.concurrent.TimeUnit

object ApiUploader {
    const val UPLOAD_URL = "http://208.76.40.198:3000/api/send-hd"
    const val HEADER_API_KEY = "x-api-key"
    const val API_KEY_VALUE = "IPUL_GANTENG_123"

    private val client = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(120, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .build()

    /**
     * Helper to build a request with the required authentication header.
     * Tanpa header 'x-api-key: IPUL_GANTENG_123', server akan membalas dengan error 403 Forbidden.
     */
    fun buildUploadRequest(file: File): Request {
        val requestBody = MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            .addFormDataPart(
                "video",
                file.name,
                file.asRequestBody("video/mp4".toMediaTypeOrNull())
            )
            .build()

        return Request.Builder()
            .url(UPLOAD_URL)
            .addHeader(HEADER_API_KEY, API_KEY_VALUE)
            .post(requestBody)
            .build()
    }

    /**
     * Uploads the encoded video file to http://208.76.40.198:3000/api/send-hd as multipart/form-data.
     * Field name: "video".
     * Menyertakan HTTP Header 'x-api-key: IPUL_GANTENG_123' agar server tidak menolak dengan 403 Forbidden.
     */
    suspend fun uploadVideo(file: File): Result<String> = withContext(Dispatchers.IO) {
        try {
            if (!file.exists()) {
                return@withContext Result.failure(IOException("File tidak ditemukan: ${file.absolutePath}"))
            }

            val request = buildUploadRequest(file)

            client.newCall(request).execute().use { response ->
                if (response.isSuccessful) {
                    val responseBody = response.body?.string().orEmpty()
                    Result.success(responseBody)
                } else {
                    Result.failure(IOException("Server error ${response.code}: ${response.message}"))
                }
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
