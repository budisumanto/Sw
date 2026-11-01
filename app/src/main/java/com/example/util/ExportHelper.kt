package com.example.util

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.widget.Toast
import androidx.core.content.FileProvider
import com.example.model.VideoSegment
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

  fun shareToWhatsAppStatus(context: Context, segment: VideoSegment) {
    try {
      val contentUri = getContentUri(context, segment.file)
      val intent = Intent(Intent.ACTION_SEND).apply {
        type = "video/mp4"
        putExtra(Intent.EXTRA_STREAM, contentUri)
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        // Direct WhatsApp intent targeting
        setPackage("com.whatsapp")
        // Direct status broadcast flag in WhatsApp
        putExtra("jid", "status@broadcast")
      }

      val pm = context.packageManager
      val canHandleWhatsApp = intent.resolveActivity(pm) != null

      if (canHandleWhatsApp) {
        context.startActivity(intent)
      } else {
        // Fallback to business WhatsApp or general chooser
        val fallbackIntent = Intent(Intent.ACTION_SEND).apply {
          type = "video/mp4"
          putExtra(Intent.EXTRA_STREAM, contentUri)
          addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        val chooser = Intent.createChooser(fallbackIntent, "Share Status HD Video")
        chooser.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(chooser)
      }
    } catch (e: Exception) {
      e.printStackTrace()
      Toast.makeText(context, "Could not open WhatsApp: ${e.message}", Toast.LENGTH_SHORT).show()
    }
  }

  fun shareAllToWhatsApp(context: Context, segments: List<VideoSegment>) {
    if (segments.isEmpty()) return
    if (segments.size == 1) {
      shareToWhatsAppStatus(context, segments.first())
      return
    }

    try {
      val uris = ArrayList<Uri>()
      for (seg in segments) {
        uris.add(getContentUri(context, seg.file))
      }

      val intent = Intent(Intent.ACTION_SEND_MULTIPLE).apply {
        type = "video/mp4"
        putParcelableArrayListExtra(Intent.EXTRA_STREAM, uris)
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        setPackage("com.whatsapp")
      }

      val pm = context.packageManager
      if (intent.resolveActivity(pm) != null) {
        context.startActivity(intent)
      } else {
        val fallbackIntent = Intent(Intent.ACTION_SEND_MULTIPLE).apply {
          type = "video/mp4"
          putParcelableArrayListExtra(Intent.EXTRA_STREAM, uris)
          addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        context.startActivity(Intent.createChooser(fallbackIntent, "Share All Video Segments"))
      }
    } catch (e: Exception) {
      e.printStackTrace()
      Toast.makeText(context, "Sharing failed: ${e.message}", Toast.LENGTH_SHORT).show()
    }
  }

  fun shareToLinkedWhatsApp(context: Context, segment: VideoSegment, normalizedPhone: String) {
    try {
      val contentUri = getContentUri(context, segment.file)
      val jid = "$normalizedPhone@s.whatsapp.net"
      val intent = Intent(Intent.ACTION_SEND).apply {
        type = "video/mp4"
        putExtra(Intent.EXTRA_STREAM, contentUri)
        putExtra("jid", jid)
        putExtra(Intent.EXTRA_TEXT, "✨ Status HD (100% Kualitas Asli) - Teruskan (Forward) video ini ke Status Anda.")
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        setPackage("com.whatsapp")
      }

      val pm = context.packageManager
      if (intent.resolveActivity(pm) != null) {
        context.startActivity(intent)
      } else {
        intent.setPackage("com.whatsapp.w4b")
        if (intent.resolveActivity(pm) != null) {
          context.startActivity(intent)
        } else {
          val fallbackIntent = Intent(Intent.ACTION_SEND).apply {
            type = "video/mp4"
            putExtra(Intent.EXTRA_STREAM, contentUri)
            putExtra("jid", jid)
            putExtra(Intent.EXTRA_TEXT, "✨ Status HD - Teruskan ke Status Anda")
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
          }
          context.startActivity(Intent.createChooser(fallbackIntent, "Kirim ke WhatsApp Saya"))
        }
      }
    } catch (e: Exception) {
      e.printStackTrace()
      Toast.makeText(context, "Gagal membuka WhatsApp: ${e.message}", Toast.LENGTH_SHORT).show()
    }
  }

  fun shareAllToLinkedWhatsApp(context: Context, segments: List<VideoSegment>, normalizedPhone: String) {
    if (segments.isEmpty()) return
    if (segments.size == 1) {
      shareToLinkedWhatsApp(context, segments.first(), normalizedPhone)
      return
    }

    try {
      val uris = ArrayList<Uri>()
      for (seg in segments) {
        uris.add(getContentUri(context, seg.file))
      }
      val jid = "$normalizedPhone@s.whatsapp.net"
      val intent = Intent(Intent.ACTION_SEND_MULTIPLE).apply {
        type = "video/mp4"
        putParcelableArrayListExtra(Intent.EXTRA_STREAM, uris)
        putExtra("jid", jid)
        putExtra(Intent.EXTRA_TEXT, "✨ Status HD (Semua Bagian) - Teruskan (Forward) ke Status Anda.")
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        setPackage("com.whatsapp")
      }

      val pm = context.packageManager
      if (intent.resolveActivity(pm) != null) {
        context.startActivity(intent)
      } else {
        intent.setPackage("com.whatsapp.w4b")
        if (intent.resolveActivity(pm) != null) {
          context.startActivity(intent)
        } else {
          val fallbackIntent = Intent(Intent.ACTION_SEND_MULTIPLE).apply {
            type = "video/mp4"
            putParcelableArrayListExtra(Intent.EXTRA_STREAM, uris)
            putExtra(Intent.EXTRA_TEXT, "✨ Status HD - Teruskan ke Status Anda")
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
          }
          context.startActivity(Intent.createChooser(fallbackIntent, "Kirim Semua Bagian ke WhatsApp Saya"))
        }
      }
    } catch (e: Exception) {
      e.printStackTrace()
      Toast.makeText(context, "Sharing failed: ${e.message}", Toast.LENGTH_SHORT).show()
    }
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
