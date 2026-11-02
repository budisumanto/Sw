package com.example.util

import android.content.ContentValues
import android.content.Context
import android.content.Intent
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

  /**
   * Kirim video langsung ke nomor WhatsApp sendiri ("Message Yourself").
   * Melalui chat dengan nomor sendiri, video terkirim dalam kualitas tinggi dan bisa langsung
   * di-forward ke Status WhatsApp tanpa kompresi ulang WhatsApp.
   */
  fun sendToSelfOnWhatsApp(context: Context, file: File, rawPhoneNumber: String): Boolean {
    if (!file.exists()) return false
    val uri = getContentUri(context, file)
    val cleanPhone = com.example.model.normalizePhoneNumber(rawPhoneNumber)

    try {
      // Buka chat langsung ke nomor sendiri dengan video terlampir
      val intent = Intent(Intent.ACTION_SEND).apply {
        type = "video/mp4"
        putExtra(Intent.EXTRA_STREAM, uri)
        if (cleanPhone.isNotBlank()) {
          putExtra("jid", "$cleanPhone@s.whatsapp.net")
        }
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        setPackage("com.whatsapp")
      }
      context.startActivity(intent)
      return true
    } catch (_: Exception) {
      // Fallback jika com.whatsapp tidak ditemukan atau melempar ActivityNotFound
      return try {
        val fallbackIntent = Intent(Intent.ACTION_SEND).apply {
          type = "video/mp4"
          putExtra(Intent.EXTRA_STREAM, uri)
          addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
          addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(Intent.createChooser(fallbackIntent, "Kirim Video ke WhatsApp"))
        true
      } catch (_: Exception) {
        false
      }
    }
  }

  /**
   * Kirim beberapa segmen sekaligus ke WhatsApp nomor sendiri
   */
  fun sendAllToSelfOnWhatsApp(context: Context, files: List<File>, rawPhoneNumber: String): Boolean {
    if (files.isEmpty()) return false
    val uris = ArrayList(files.map { getContentUri(context, it) })
    val cleanPhone = com.example.model.normalizePhoneNumber(rawPhoneNumber)

    try {
      val intent = Intent(Intent.ACTION_SEND_MULTIPLE).apply {
        type = "video/mp4"
        putParcelableArrayListExtra(Intent.EXTRA_STREAM, uris)
        if (cleanPhone.isNotBlank()) {
          putExtra("jid", "$cleanPhone@s.whatsapp.net")
        }
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        setPackage("com.whatsapp")
      }
      context.startActivity(intent)
      return true
    } catch (_: Exception) {
      return try {
        val fallbackIntent = Intent(Intent.ACTION_SEND_MULTIPLE).apply {
          type = "video/mp4"
          putParcelableArrayListExtra(Intent.EXTRA_STREAM, uris)
          addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
          addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(Intent.createChooser(fallbackIntent, "Kirim Semua ke WhatsApp"))
        true
      } catch (_: Exception) {
        false
      }
    }
  }
}
