package com.example.util

import android.content.Context
import android.graphics.Bitmap
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.provider.OpenableColumns
import com.example.model.VideoMetadata
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream

object VideoMetadataHelper {

  suspend fun extractMetadata(context: Context, uri: Uri): VideoMetadata? =
    withContext(Dispatchers.IO) {
      val retriever = MediaMetadataRetriever()
      var tempFile: File? = null
      try {
        // Copy URI to a working cache file so FFmpeg has a real local POSIX file path
        tempFile = copyUriToCache(context, uri) ?: return@withContext null
        retriever.setDataSource(tempFile.absolutePath)

        val durationStr = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
        val widthStr = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH)
        val heightStr = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT)
        val bitrateStr = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_BITRATE)
        val rotationStr = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_ROTATION)

        val durationMs = durationStr?.toLongOrNull() ?: 0L
        var width = widthStr?.toIntOrNull() ?: 0
        var height = heightStr?.toIntOrNull() ?: 0
        val rotation = rotationStr?.toIntOrNull() ?: 0
        val bitrate = bitrateStr?.toLongOrNull() ?: 0L

        // Swap width and height if rotated 90 or 270 degrees (e.g. vertical portrait video)
        if (rotation == 90 || rotation == 270) {
          val tmp = width
          width = height
          height = tmp
        }

        val thumbnailBitmap: Bitmap? = try {
          retriever.getFrameAtTime(
            (durationMs * 500).coerceAtLeast(0),
            MediaMetadataRetriever.OPTION_CLOSEST_SYNC
          )
        } catch (_: Exception) {
          null
        }

        val fileName = getFileNameFromUri(context, uri) ?: tempFile.name

        VideoMetadata(
          uri = uri,
          filePath = tempFile.absolutePath,
          fileName = fileName,
          durationMs = durationMs,
          width = width,
          height = height,
          fps = 30.0,
          bitrateBps = bitrate,
          sizeBytes = tempFile.length(),
          thumbnailBitmap = thumbnailBitmap
        )
      } catch (e: Exception) {
        e.printStackTrace()
        null
      } finally {
        try {
          retriever.release()
        } catch (_: Exception) {}
      }
    }

  suspend fun extractMetadataFromFile(file: File): VideoMetadata? =
    withContext(Dispatchers.IO) {
      if (!file.exists()) return@withContext null
      val retriever = MediaMetadataRetriever()
      try {
        retriever.setDataSource(file.absolutePath)
        val durationStr = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
        val widthStr = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH)
        val heightStr = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT)
        val rotationStr = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_ROTATION)
        val bitrateStr = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_BITRATE)

        val durationMs = durationStr?.toLongOrNull() ?: 0L
        var width = widthStr?.toIntOrNull() ?: 0
        var height = heightStr?.toIntOrNull() ?: 0
        val rotation = rotationStr?.toIntOrNull() ?: 0
        val bitrate = bitrateStr?.toLongOrNull() ?: 0L

        if (rotation == 90 || rotation == 270) {
          val tmp = width
          width = height
          height = tmp
        }

        val thumbnail = try {
          retriever.getFrameAtTime(
            (durationMs * 500).coerceAtLeast(0),
            MediaMetadataRetriever.OPTION_CLOSEST_SYNC
          )
        } catch (_: Exception) {
          null
        }

        VideoMetadata(
          uri = null,
          filePath = file.absolutePath,
          fileName = file.name,
          durationMs = durationMs,
          width = width,
          height = height,
          fps = 30.0,
          bitrateBps = bitrate,
          sizeBytes = file.length(),
          thumbnailBitmap = thumbnail
        )
      } catch (e: Exception) {
        e.printStackTrace()
        null
      } finally {
        try {
          retriever.release()
        } catch (_: Exception) {}
      }
    }

  fun copyUriToCache(context: Context, uri: Uri): File? {
    return try {
      val fileName = getFileNameFromUri(context, uri) ?: "input_source.mp4"
      val cacheDir = File(context.cacheDir, "status_hd_workspace").apply { mkdirs() }
      val targetFile = File(cacheDir, "source_${System.currentTimeMillis()}_$fileName")
      context.contentResolver.openInputStream(uri)?.use { input ->
        FileOutputStream(targetFile).use { output ->
          input.copyTo(output)
        }
      }
      targetFile
    } catch (e: Exception) {
      e.printStackTrace()
      null
    }
  }

  fun getThumbnailForFile(file: File): Bitmap? {
    if (!file.exists()) return null
    val retriever = MediaMetadataRetriever()
    return try {
      retriever.setDataSource(file.absolutePath)
      retriever.getFrameAtTime(500_000, MediaMetadataRetriever.OPTION_CLOSEST_SYNC)
    } catch (_: Exception) {
      null
    } finally {
      try {
        retriever.release()
      } catch (_: Exception) {}
    }
  }

  private fun getFileNameFromUri(context: Context, uri: Uri): String? {
    var name: String? = null
    try {
      context.contentResolver.query(uri, null, null, null, null)?.use { cursor ->
        if (cursor.moveToFirst()) {
          val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
          if (nameIndex != -1) {
            name = cursor.getString(nameIndex)
          }
        }
      }
    } catch (_: Exception) {}
    if (name.isNullOrBlank()) {
      name = uri.lastPathSegment
    }
    return name
  }
}
