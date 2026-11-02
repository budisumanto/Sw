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
    private const val UPLOAD_URL = "http://208.76.40.198:3000/api/send-hd"

    // Optional API key or custom header placeholder for future authentication
    // private const val API_KEY = "your-api-key-here"

    private val client = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(120, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .build()

    /**
     * Uploads the encoded video file to http://208.76.40.198:3000/api/send-hd as multipart/form-data.
     * Field name: "video".
     */
    suspend fun uploadVideo(file: File): Result<String> = withContext(Dispatchers.IO) {
        try {
            if (!file.exists()) {
                return@withContext Result.failure(IOException("File tidak ditemukan: ${file.absolutePath}"))
            }

            val requestBody = MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart(
                    "video",
                    file.name,
                    file.asRequestBody("video/mp4".toMediaTypeOrNull())
                )
                .build()

            val requestBuilder = Request.Builder()
                .url(UPLOAD_URL)
                .post(requestBody)

            // Siapkan header opsional jika nanti dibutuhkan autentikasi
            // requestBuilder.addHeader("x-api-key", API_KEY)
            // requestBuilder.addHeader("Authorization", "Bearer $API_KEY")

            val request = requestBuilder.build()

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
