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

  val resolutionText: String
    get() = if (width > 0 && height > 0) "${width}x${height}" else "Unknown"

  val needsSplitting: Boolean
    get() = durationMs > 90_000L

  val estimatedChunkCount: Int
    get() = if (durationMs <= 0) 1 else kotlin.math.ceil(durationMs / 90_000.0).toInt().coerceAtLeast(1)
}

data class SmartEncodingConfig(
  val targetResolutionLabel: String,
  val scaleFilter: String,
  val crf: Int = 20,
  val preset: String = "medium",
  val maxrate: String = "4000k",
  val bufsize: String = "8000k",
  val gop: Int = 30
) {
  companion object {
    fun createDefault(): SmartEncodingConfig = SmartEncodingConfig(
      targetResolutionLabel = "Auto Smart HD",
      scaleFilter = "scale='trunc(min(1080,iw)/2)*2':-2",
      crf = 20,
      preset = "medium",
      maxrate = "4000k",
      bufsize = "8000k",
      gop = 30
    )

    fun computeSmartConfig(video: VideoMetadata): SmartEncodingConfig {
      val isPortrait = video.height >= video.width
      val (scaleFilter, resLabel) = if (isPortrait) {
        if (video.width in 1..720) {
          "scale='trunc(min(720,iw)/2)*2':-2" to "${video.width}x${video.height} (Native)"
        } else if (video.width in 721..1080) {
          "scale='trunc(min(1080,iw)/2)*2':-2" to "${video.width}x${video.height} (1080p HD)"
        } else {
          "scale='trunc(min(1080,iw)/2)*2':-2" to "1080p (Downscaled 4K)"
        }
      } else {
        if (video.height in 1..720) {
          "scale=-2:'trunc(min(720,ih)/2)*2'" to "${video.width}x${video.height} (Native)"
        } else {
          "scale=-2:'trunc(min(1080,ih)/2)*2'" to "1080p HD"
        }
      }

      // Bitrate limit at 4000k max, without exceeding original bitrate to prevent size bloat
      val originalKbps = if (video.bitrateBps > 0) video.bitrateBps / 1000L else 4000L
      val targetKbps = minOf(4000L, originalKbps.coerceAtLeast(1500L))

      return SmartEncodingConfig(
        targetResolutionLabel = resLabel,
        scaleFilter = scaleFilter,
        crf = 20,
        preset = "medium",
        maxrate = "${targetKbps}k",
        bufsize = "${targetKbps * 2}k",
        gop = 30
      )
    }
  }
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
  val smartConfig: SmartEncodingConfig,
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
