package com.example.util

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.asRequestBody
import okio.buffer
import org.json.JSONObject
import java.io.File
import java.io.IOException
import java.util.concurrent.TimeUnit

object ApiUploader {
    const val UPLOAD_URL = "https://harbor-flux-disposition-tutorials.trycloudflare.com/api/send-hd"
    const val HEADER_API_KEY = "x-api-key"
    const val API_KEY_VALUE = "IPUL_GANTENG_123"

    // Set timeout koneksi, write, dan read ke 60 detik untuk upload file video
    private val client = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .build()

    /**
     * RequestBody yang memantau byte terkirim untuk mengestimasi kecepatan internet dan waktu sisa.
     */
    class ProgressRequestBody(
        private val delegate: okhttp3.RequestBody,
        private val onProgress: (bytesWritten: Long, totalBytes: Long) -> Unit
    ) : okhttp3.RequestBody() {
        override fun contentType() = delegate.contentType()
        override fun contentLength() = delegate.contentLength()

        override fun writeTo(sink: okio.BufferedSink) {
            val countingSink = object : okio.ForwardingSink(sink) {
                var bytesWritten = 0L
                val totalLength = contentLength()

                override fun write(source: okio.Buffer, byteCount: Long) {
                    super.write(source, byteCount)
                    bytesWritten += byteCount
                    onProgress(bytesWritten, totalLength)
                }
            }
            val bufferedSink = countingSink.buffer()
            delegate.writeTo(bufferedSink)
            bufferedSink.flush()
        }
    }

    /**
     * Membangun request multipart/form-data dengan 2 field:
     * - phone: nomor WhatsApp tujuan (format 62...)
     * - video: file video hasil encode HD
     * Serta menyertakan Header 'x-api-key: IPUL_GANTENG_123'
     */
    fun buildUploadRequest(
        phone: String,
        file: File,
        onProgress: ((bytesWritten: Long, totalBytes: Long) -> Unit)? = null
    ): Request {
        val fileBody = file.asRequestBody("video/mp4".toMediaTypeOrNull())
        val videoPart = if (onProgress != null) {
            ProgressRequestBody(fileBody, onProgress)
        } else {
            fileBody
        }

        val requestBody = MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            .addFormDataPart("phone", phone)
            .addFormDataPart("video", file.name, videoPart)
            .build()

        return Request.Builder()
            .url(UPLOAD_URL)
            .addHeader(HEADER_API_KEY, API_KEY_VALUE)
            .post(requestBody)
            .build()
    }

    /**
     * Mengunggah file video ke server dengan pemantauan progress real-time sesuai kecepatan koneksi internet pengguna.
     * onProgress callback memberikan: bytesWritten, totalBytes, speedBps, remainingSeconds.
     */
    suspend fun uploadVideo(
        phone: String,
        file: File,
        onProgress: ((bytesUploaded: Long, totalBytes: Long, speedBps: Long, remainingSeconds: Long) -> Unit)? = null
    ): Result<String> = withContext(Dispatchers.IO) {
        try {
            if (!file.exists()) {
                return@withContext Result.failure(IOException("File tidak ditemukan: ${file.absolutePath}"))
            }

            val startTimeMs = System.currentTimeMillis()
            var lastUpdateMs = 0L

            val request = buildUploadRequest(phone, file) { bytesWritten, totalBytes ->
                val now = System.currentTimeMillis()
                if (now - lastUpdateMs >= 200 || bytesWritten == totalBytes) {
                    lastUpdateMs = now
                    val elapsedSec = ((now - startTimeMs) / 1000.0).coerceAtLeast(0.1)
                    val speedBps = (bytesWritten / elapsedSec).toLong()
                    val remainingBytes = (totalBytes - bytesWritten).coerceAtLeast(0L)
                    val remainingSec = if (speedBps > 0) remainingBytes / speedBps else -1L
                    onProgress?.invoke(bytesWritten, totalBytes, speedBps, remainingSec)
                }
            }

            client.newCall(request).execute().use { response ->
                val responseBody = response.body?.string().orEmpty()
                if (response.isSuccessful) {
                    try {
                        val json = JSONObject(responseBody)
                        val status = json.optBoolean("status", false)
                        val message = json.optString("message", "")
                        if (status) {
                            Result.success(if (message.isNotBlank()) message else responseBody)
                        } else {
                            val err = if (message.isNotBlank()) message else "Server menolak pengiriman video"
                            Result.failure(IOException(err))
                        }
                    } catch (e: Exception) {
                        // Jika respons bukan JSON atau parsing gagal, anggap sukses jika HTTP 200
                        Result.success(responseBody)
                    }
                } else {
                    var errorMsg = "Server error ${response.code}: ${response.message}"
                    if (responseBody.isNotBlank()) {
                        try {
                            val json = JSONObject(responseBody)
                            val msg = json.optString("message", "")
                            if (msg.isNotBlank()) errorMsg = msg
                        } catch (_: Exception) {}
                    }
                    Result.failure(IOException(errorMsg))
                }
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
