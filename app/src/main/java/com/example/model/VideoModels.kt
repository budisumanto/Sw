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

  data class Success(val result: OptimizationResult) : ProcessingState

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

data class LinkedWhatsAppAccount(
  val rawPhoneNumber: String = "",
  val normalizedPhoneNumber: String = "",
  val isLinked: Boolean = false,
  val linkedAtTimestamp: Long = 0L
) {
  val formattedDisplayNumber: String
    get() {
      if (!isLinked || normalizedPhoneNumber.isBlank()) return "Belum Terhubung"
      val clean = normalizedPhoneNumber
      return if (clean.startsWith("62") && clean.length >= 10) {
        val rest = clean.substring(2)
        val part1 = rest.take(3)
        val part2 = rest.drop(3).take(4)
        val part3 = rest.drop(7)
        "+62 $part1-$part2-$part3"
      } else {
        "+$clean"
      }
    }
}

fun normalizePhoneNumber(input: String): String {
  val trimmed = input.trim()
  var digits = trimmed.replace(Regex("[^0-9]"), "")
  if (digits.startsWith("0")) {
    digits = "62" + digits.substring(1)
  } else if (digits.startsWith("8")) {
    digits = "62$digits"
  } else if (!digits.startsWith("62") && digits.length in 8..15) {
    digits = "62$digits"
  }
  return digits
}

