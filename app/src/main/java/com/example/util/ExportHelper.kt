package com.example.util

import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import androidx.core.content.FileProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileInputStream

object ExportHelper {

  fun getContentUri(context: Context, file: File): Uri {
    return FileProvider.getUriForFile(
      context,
      "${context.packageName}.fileprovider",
      file
    )
  }

  suspend fun saveToGallery(context: Context, file: File): Boolean = withContext(Dispatchers.IO) {
    if (!file.exists()) return@withContext false

    val resolver = context.contentResolver
    val now = System.currentTimeMillis()
    val displayName = "StatusHD_${now}_${file.name}"

    val contentValues = ContentValues().apply {
      put(MediaStore.Video.Media.DISPLAY_NAME, displayName)
      put(MediaStore.Video.Media.MIME_TYPE, "video/mp4")
      put(MediaStore.Video.Media.DATE_ADDED, now / 1000)
      put(MediaStore.Video.Media.DATE_MODIFIED, now / 1000)
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        put(MediaStore.Video.Media.RELATIVE_PATH, "${Environment.DIRECTORY_MOVIES}/StatusHD")
        put(MediaStore.Video.Media.IS_PENDING, 1)
      }
    }

    val collection = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
      MediaStore.Video.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
    } else {
      MediaStore.Video.Media.EXTERNAL_CONTENT_URI
    }

    val itemUri = resolver.insert(collection, contentValues) ?: return@withContext false

    try {
      resolver.openOutputStream(itemUri)?.use { out ->
        FileInputStream(file).use { input ->
          input.copyTo(out)
        }
      }

      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        contentValues.clear()
        contentValues.put(MediaStore.Video.Media.IS_PENDING, 0)
        resolver.update(itemUri, contentValues, null, null)
      }
      true
    } catch (e: Exception) {
      e.printStackTrace()
      try {
        resolver.delete(itemUri, null, null)
      } catch (_: Exception) {}
      false
    }
  }
}
