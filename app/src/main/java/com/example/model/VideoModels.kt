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

  val resolutionText: String
    get() = if (width > 0 && height > 0) "${width}x${height}" else "Unknown"

  val needsSplitting: Boolean
    get() = durationMs > 90_000L

  val estimatedChunkCount: Int
    get() = if (durationMs <= 0) 1 else kotlin.math.ceil(durationMs / 90_000.0).toInt().coerceAtLeast(1)
}

enum class ResolutionMode(
  val label: String,
  val badge: String,
  val maxDimension: Int,
  val scaleFilter: String,
  val description: String
) {
  HD_720P(
    label = "720p HD",
    badge = "Balanced & Fast",
    maxDimension = 720,
    scaleFilter = "scale='min(720,iw)':-2",
    description = "Optimized for fast encoding and standard mobile display."
  ),
  CRISP_1080P(
    label = "1080p Crisp HD",
    badge = "Max Density",
    maxDimension = 1080,
    scaleFilter = "scale='min(1080,iw)':-2",
    description = "Packs maximum pixel sharpness within identical WhatsApp bitrate caps."
  )
}

enum class ContentProfile(
  val label: String,
  val badge: String,
  val description: String,
  val crf: Int,
  val preset: String,
  val maxrate: String,
  val bufsize: String,
  val gop: Int
) {
  STANDARD(
    label = "Standard Video",
    badge = "Vlogs / Casual",
    description = "Ideal for talking heads, lifestyle clips, and steady cameras without rapid flashing.",
    crf = 23,
    preset = "medium",
    maxrate = "2.2M",
    bufsize = "4.4M",
    gop = 30
  ),
  HIGH_MOTION(
    label = "High Motion / Jedag-Jedug",
    badge = "Fast Cuts & Shake",
    description = "Enforces dense GOP keyframes (1s) and higher bitrate burst to prevent pixelation on transitions.",
    crf = 20,
    preset = "medium",
    maxrate = "2.8M",
    bufsize = "5.6M",
    gop = 30
  )
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
  val commandUsed: String,
  val resolutionMode: ResolutionMode,
  val contentProfile: ContentProfile,
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
    val currentFps: Double,
    val speed: Double,
    val timeRenderedMs: Long,
    val currentBitrateKbps: Double,
    val recentLogs: List<String>
  ) : ProcessingState

  data class Success(val result: OptimizationResult) : ProcessingState

  data object Cancelled : ProcessingState

  data class Error(val message: String, val logs: List<String> = emptyList()) : ProcessingState
}

fun formatFileSize(bytes: Long): String {
  if (bytes <= 0) return "0 B"
  val kb = bytes / 1024.0
  val mb = kb / 1024.0
  val gb = mb / 1024.0
  return when {
    gb >= 1.0 -> String.format("%.2f GB", gb)
    mb >= 1.0 -> String.format("%.2f MB", mb)
    kb >= 1.0 -> String.format("%.1f KB", kb)
    else -> "$bytes B"
  }
}
