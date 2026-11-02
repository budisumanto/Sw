package com.example.viewmodel

import android.app.Application
import android.content.Context
import android.net.Uri
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.engine.StatusHDOptimizerEngine
import com.example.model.LinkedWhatsAppAccount
import com.example.model.ProcessingState
import com.example.model.VideoMetadata
import com.example.model.VideoSegment
import com.example.model.normalizePhoneNumber
import com.example.util.ExportHelper
import com.example.util.VideoMetadataHelper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class OptimizerViewModel(application: Application) : AndroidViewModel(application) {

  private val engine = StatusHDOptimizerEngine(application)
  private val prefs = application.getSharedPreferences("status_hd_prefs", Context.MODE_PRIVATE)

  private val _selectedVideo = MutableStateFlow<VideoMetadata?>(null)
  val selectedVideo: StateFlow<VideoMetadata?> = _selectedVideo.asStateFlow()

  val processingState: StateFlow<ProcessingState> = engine.processingState

  private val _previewSegment = MutableStateFlow<VideoSegment?>(null)
  val previewSegment: StateFlow<VideoSegment?> = _previewSegment.asStateFlow()

  private val _linkedAccount = MutableStateFlow<LinkedWhatsAppAccount>(loadLinkedAccount())
  val linkedAccount: StateFlow<LinkedWhatsAppAccount> = _linkedAccount.asStateFlow()

  private val _showLinkDialog = MutableStateFlow<Boolean>(false)
  val showLinkDialog: StateFlow<Boolean> = _showLinkDialog.asStateFlow()

  init {
    if (!_linkedAccount.value.isLinked) {
      _showLinkDialog.value = true
    }
  }

  private fun loadLinkedAccount(): LinkedWhatsAppAccount {
    val isLinked = prefs.getBoolean("wa_is_linked", false)
    val raw = prefs.getString("wa_raw_phone", "") ?: ""
    val norm = prefs.getString("wa_norm_phone", "") ?: ""
    val label = prefs.getString("wa_device_label", "Perangkat Tertaut (WhatsApp Web)") ?: "Perangkat Tertaut"
    val time = prefs.getLong("wa_linked_at", 0L)
    val method = prefs.getString("wa_method", "PAIRING_CODE") ?: "PAIRING_CODE"
    val code = prefs.getString("wa_code", "") ?: ""
    val sessionId = prefs.getString("wa_session_id", "") ?: ""
    return LinkedWhatsAppAccount(
      rawPhoneNumber = raw,
      normalizedPhoneNumber = norm,
      isLinked = isLinked,
      linkedAtTimestamp = time,
      deviceLabel = label,
      connectionMethod = method,
      activePairingCode = code,
      pairingSessionId = sessionId
    )
  }

  fun openLinkDialog() {
    _showLinkDialog.value = true
  }

  fun closeLinkDialog() {
    _showLinkDialog.value = false
  }

  fun markCompanionLinked(label: String = "Perangkat Tertaut (QR Scan)", method: String = "QR_SCAN") {
    val now = System.currentTimeMillis()
    prefs.edit()
      .putBoolean("wa_is_linked", true)
      .putString("wa_device_label", label)
      .putString("wa_method", method)
      .putLong("wa_linked_at", now)
      .apply()

    _linkedAccount.value = _linkedAccount.value.copy(
      isLinked = true,
      deviceLabel = label,
      connectionMethod = method,
      linkedAtTimestamp = now
    )
  }

  fun confirmPairingCode(context: Context, rawNumber: String, code: String): Boolean {
    val normalized = normalizePhoneNumber(rawNumber)
    if (normalized.length < 9) {
      Toast.makeText(context, "Nomor WhatsApp tidak valid (minimal 9 digit)", Toast.LENGTH_SHORT).show()
      return false
    }

    val now = System.currentTimeMillis()
    val displayLabel = "WA: +$normalized"
    prefs.edit()
      .putBoolean("wa_is_linked", true)
      .putString("wa_raw_phone", rawNumber.trim())
      .putString("wa_norm_phone", normalized)
      .putString("wa_device_label", displayLabel)
      .putString("wa_method", "PAIRING_CODE")
      .putString("wa_code", code)
      .putLong("wa_linked_at", now)
      .apply()

    _linkedAccount.value = LinkedWhatsAppAccount(
      rawPhoneNumber = rawNumber.trim(),
      normalizedPhoneNumber = normalized,
      isLinked = true,
      linkedAtTimestamp = now,
      deviceLabel = displayLabel,
      connectionMethod = "PAIRING_CODE",
      activePairingCode = code
    )
    _showLinkDialog.value = false
    Toast.makeText(context, "Nomor WhatsApp Berhasil Ditautkan!", Toast.LENGTH_SHORT).show()
    return true
  }

  fun unlinkWhatsAppNumber(context: Context) {
    prefs.edit()
      .remove("wa_is_linked")
      .remove("wa_raw_phone")
      .remove("wa_norm_phone")
      .remove("wa_device_label")
      .remove("wa_method")
      .remove("wa_code")
      .remove("wa_session_id")
      .remove("wa_linked_at")
      .apply()

    _linkedAccount.value = LinkedWhatsAppAccount()
    Toast.makeText(context, "Tautan WhatsApp Dihapus", Toast.LENGTH_SHORT).show()
  }

  fun onVideoSelected(uri: Uri, context: Context) {
    viewModelScope.launch {
      val meta = VideoMetadataHelper.extractMetadata(context, uri)
      if (meta != null) {
        _selectedVideo.value = meta
        engine.resetToIdle()
      } else {
        Toast.makeText(context, "Video tidak dapat dimuat", Toast.LENGTH_SHORT).show()
      }
    }
  }

  fun startOptimization(context: Context) {
    val video = _selectedVideo.value ?: return
    viewModelScope.launch {
      engine.optimizeVideo(sourceVideo = video)
    }
  }

  fun cancelOptimization() {
    engine.cancel()
  }

  fun resetOptimization() {
    resetAll()
  }

  fun resetAll() {
    _selectedVideo.value = null
    _previewSegment.value = null
    engine.resetToIdle()
  }

  fun saveSegment(context: Context, segment: VideoSegment) {
    viewModelScope.launch {
      val success = ExportHelper.saveToGallery(context, segment.file)
      if (success) {
        Toast.makeText(context, "Disimpan ke Galeri (Movies/StatusHD)", Toast.LENGTH_SHORT).show()
        resetAll()
      } else {
        Toast.makeText(context, "Gagal menyimpan ke galeri", Toast.LENGTH_SHORT).show()
      }
    }
  }

  fun saveAllSegments(context: Context, segments: List<VideoSegment>) {
    viewModelScope.launch {
      var savedCount = 0
      for (seg in segments) {
        if (ExportHelper.saveToGallery(context, seg.file)) {
          savedCount++
        }
      }
      if (savedCount > 0) {
        Toast.makeText(context, "Berhasil menyimpan $savedCount bagian ke Galeri", Toast.LENGTH_SHORT).show()
        resetAll()
      } else {
        Toast.makeText(context, "Gagal menyimpan ke galeri", Toast.LENGTH_SHORT).show()
      }
    }
  }

  val directSendState: StateFlow<com.example.service.DirectSendState> =
    com.example.service.WhatsAppCompanionService.sendState

  /**
   * Mengirimkan video langsung ke nomor WhatsApp sendiri ("Message Yourself").
   * Melalui nomor sendiri, video sampai secara instan tanpa re-kompresi galeri,
   * dan dapat langsung di-forward ke Status WhatsApp.
   */
  fun sendToSelfChat(context: Context, segment: VideoSegment) {
    val account = _linkedAccount.value
    val targetPhone = account.normalizedPhoneNumber.ifBlank { account.rawPhoneNumber }

    // Simpan juga ke galeri otomatis agar aman
    viewModelScope.launch {
      ExportHelper.saveToGallery(context, segment.file)
    }

    val sent = ExportHelper.sendToSelfOnWhatsApp(context, segment.file, targetPhone)
    if (!sent) {
      Toast.makeText(context, "Gagal membuka WhatsApp. Pastikan WhatsApp terpasang.", Toast.LENGTH_SHORT).show()
    }
  }

  fun sendAllToSelfChat(context: Context, segments: List<VideoSegment>) {
    val account = _linkedAccount.value
    val targetPhone = account.normalizedPhoneNumber.ifBlank { account.rawPhoneNumber }

    viewModelScope.launch {
      for (seg in segments) {
        ExportHelper.saveToGallery(context, seg.file)
      }
    }

    val sent = ExportHelper.sendAllToSelfOnWhatsApp(context, segments.map { it.file }, targetPhone)
    if (!sent) {
      Toast.makeText(context, "Gagal membuka WhatsApp. Pastikan WhatsApp terpasang.", Toast.LENGTH_SHORT).show()
    }
  }

  fun sendDirectToServer(context: Context, segment: VideoSegment) {
    sendToSelfChat(context, segment)
  }

  fun sendAllDirectToServer(context: Context, segments: List<VideoSegment>) {
    sendAllToSelfChat(context, segments)
  }

  fun resetDirectSendState() {
    com.example.service.WhatsAppCompanionService.resetState()
  }

  fun setPreviewSegment(segment: VideoSegment?) {
    _previewSegment.value = segment
  }
}
