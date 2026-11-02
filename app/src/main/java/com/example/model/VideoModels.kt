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
 * Memformat nomor untuk tampilan UI yang nyaman dibaca pengguna.
 * Contoh: "6281234567890" -> "+62 812-3456-7890"
 */
fun formatDisplayPhoneNumber(rawOrNormalized: String): String {
  val clean = normalizeWhatsAppPhoneNumber(rawOrNormalized)
  if (clean.length < 10) return rawOrNormalized
  val withoutCountry = clean.substring(2)
  return when {
    withoutCountry.length in 9..10 -> {
      val part1 = withoutCountry.substring(0, 3)
      val part2 = withoutCountry.substring(3, 7)
      val part3 = withoutCountry.substring(7)
      "+62 $part1-$part2-$part3"
    }
    withoutCountry.length >= 11 -> {
      val part1 = withoutCountry.substring(0, 3)
      val part2 = withoutCountry.substring(3, 7)
      val part3 = withoutCountry.substring(7)
      "+62 $part1-$part2-$part3"
    }
    else -> "+$clean"
  }
}
