package com.example.model

import android.graphics.Bitmap
import android.net.Uri
import java.io.File

data class VideoMetadata(
  val uri: Uri? = null,
  val filePath: String,
  val fileName: String,
  val durationMs: Long,
  val width: Int,
  val height: Int,
  val fps: Double = 30.0,
  val bitrateBps: Long = 0L,
  val sizeBytes: Long = 0L,
  val hasAudio: Boolean = true,
  val thumbnailBitmap: Bitmap? = null
) {
  val formattedDuration: String
    get() {
      val totalSeconds = (durationMs / 1000).toInt()
      val minutes = totalSeconds / 60
      val seconds = totalSeconds % 60
      return String.format("%02d:%02d", minutes, seconds)
    }

  val formattedSize: String
    get() = formatFileSize(sizeBytes)

  val needsSplitting: Boolean
    get() = durationMs > 90_000L

  val estimatedChunkCount: Int
    get() = if (durationMs <= 0) 1 else kotlin.math.ceil(durationMs / 90_000.0).toInt().coerceAtLeast(1)
}

data class VideoSegment(
  val partIndex: Int,
  val totalParts: Int,
  val file: File,
  val durationMs: Long,
  val sizeBytes: Long,
  val startSec: Int,
  val endSec: Int,
  val thumbnailBitmap: Bitmap? = null
) {
  val formattedDuration: String
    get() {
      val totalSeconds = (durationMs / 1000).toInt()
      val minutes = totalSeconds / 60
      val seconds = totalSeconds % 60
      return String.format("%02d:%02d", minutes, seconds)
    }

  val formattedSize: String
    get() = formatFileSize(sizeBytes)
}

data class OptimizationResult(
  val segments: List<VideoSegment>,
  val originalSizeBytes: Long,
  val totalOptimizedSizeBytes: Long,
  val executionDurationMs: Long,
  val timestamp: Long = System.currentTimeMillis()
) {
  val savedPercentage: Int
    get() {
      if (originalSizeBytes <= 0) return 0
      val diff = originalSizeBytes - totalOptimizedSizeBytes
      return ((diff.toDouble() / originalSizeBytes) * 100).toInt()
    }

  val formattedOriginalSize: String
    get() = formatFileSize(originalSizeBytes)

  val formattedOptimizedSize: String
    get() = formatFileSize(totalOptimizedSizeBytes)
}

sealed interface ProcessingState {
  data object Idle : ProcessingState

  data class Preparing(val message: String) : ProcessingState

  data class Compressing(
    val activeChunk: Int,
    val totalChunks: Int,
    val chunkProgress: Float,
    val overallProgress: Float,
    val statusText: String
  ) : ProcessingState

  data class Uploading(
    val activeChunk: Int,
    val totalChunks: Int,
    val bytesUploaded: Long = 0L,
    val totalBytes: Long = 0L,
    val speedBps: Long = 0L,
    val remainingSeconds: Long = -1L,
    val statusText: String
  ) : ProcessingState

  data class Success(
    val result: OptimizationResult,
    val message: String = "Video HD sudah dikirim ke WhatsApp kamu! Buka WA dan simpan atau teruskan videonya ke Status."
  ) : ProcessingState

  data object Cancelled : ProcessingState

  data class Error(val message: String) : ProcessingState
}

fun formatFileSize(bytes: Long): String {
  if (bytes <= 0) return "0 B"
  val kb = bytes / 1024.0
  val mb = kb / 1024.0
  val gb = mb / 1024.0
  return when {
    gb >= 1.0 -> String.format("%.2f GB", gb)
    mb >= 1.0 -> String.format("%.1f MB", mb)
    kb >= 1.0 -> String.format("%.1f KB", kb)
    else -> "$bytes B"
  }
}

/**
 * Normalisasi nomor WhatsApp pengguna agar server dapat menerima format standar angka '62...'.
 * Menerima variasi input: '+', spasi, strip '-', awalan '0', '62', '08', '+628'.
 * Contoh:
 *   "+62 812-3456-7890" -> "6281234567890"
 *   "081234567890"      -> "6281234567890"
 *   "6281234567890"     -> "6281234567890"
 *   "81234567890"       -> "6281234567890"
 */
fun normalizeWhatsAppPhoneNumber(input: String): String {
  val digitsOnly = input.replace(Regex("[^0-9]"), "")
  return when {
    digitsOnly.startsWith("0") -> "62" + digitsOnly.substring(1)
    digitsOnly.startsWith("62") -> digitsOnly
    digitsOnly.startsWith("8") -> "62$digitsOnly"
    digitsOnly.isNotEmpty() -> "62$digitsOnly"
    else -> ""
  }
}

/**
 * Memformat nomor telepon dengan spasi rapi untuk kenyamanan pengguna (contoh: +62 812 3456 7890).
 */
fun formatDisplayPhoneNumber(rawOrNormalized: String): String {
  val digits = rawOrNormalized.replace(Regex("[^0-9]"), "")
  if (digits.isEmpty()) return ""

  // Jika diawali 62 atau dinormalisasi
  val clean = normalizeWhatsAppPhoneNumber(rawOrNormalized)
  if (clean.startsWith("62")) {
    val rest = clean.substring(2)
    val chunks = mutableListOf<String>()
    if (rest.isNotEmpty()) chunks.add(rest.take(3))
    if (rest.length > 3) chunks.add(rest.substring(3, kotlin.math.min(7, rest.length)))
    if (rest.length > 7) chunks.add(rest.substring(7, kotlin.math.min(11, rest.length)))
    if (rest.length > 11) chunks.add(rest.substring(11))
    return "+62 " + chunks.joinToString(" ")
  }

  // Jika dimulai dengan 0
  if (digits.startsWith("0")) {
    val chunks = mutableListOf<String>()
    if (digits.isNotEmpty()) chunks.add(digits.take(4))
    if (digits.length > 4) chunks.add(digits.substring(4, kotlin.math.min(8, digits.length)))
    if (digits.length > 8) chunks.add(digits.substring(8, kotlin.math.min(12, digits.length)))
    if (digits.length > 12) chunks.add(digits.substring(12))
    return chunks.joinToString(" ")
  }

  return rawOrNormalized
}
