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
    // Show link dialog on initial startup if user hasn't connected WhatsApp yet
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
    return LinkedWhatsAppAccount(
      rawPhoneNumber = raw,
      normalizedPhoneNumber = norm,
      isLinked = isLinked,
      linkedAtTimestamp = time,
      deviceLabel = label
    )
  }

  fun openLinkDialog() {
    _showLinkDialog.value = true
  }

  fun closeLinkDialog() {
    _showLinkDialog.value = false
  }

  fun markCompanionLinked(label: String = "Perangkat Tertaut (WhatsApp Web)") {
    val now = System.currentTimeMillis()
    prefs.edit()
      .putBoolean("wa_is_linked", true)
      .putString("wa_device_label", label)
      .putLong("wa_linked_at", now)
      .apply()

    _linkedAccount.value = _linkedAccount.value.copy(
      isLinked = true,
      deviceLabel = label,
      linkedAtTimestamp = now
    )
  }

  fun linkWhatsAppNumber(context: Context, rawNumber: String): Boolean {
    val normalized = normalizePhoneNumber(rawNumber)
    if (normalized.length < 9) {
      Toast.makeText(context, "Nomor WhatsApp tidak valid (minimal 9 digit)", Toast.LENGTH_SHORT).show()
      return false
    }

    val now = System.currentTimeMillis()
    prefs.edit()
      .putBoolean("wa_is_linked", true)
      .putString("wa_raw_phone", rawNumber.trim())
      .putString("wa_norm_phone", normalized)
      .putString("wa_device_label", "WA: +$normalized")
      .putLong("wa_linked_at", now)
      .apply()

    _linkedAccount.value = LinkedWhatsAppAccount(
      rawPhoneNumber = rawNumber.trim(),
      normalizedPhoneNumber = normalized,
      isLinked = true,
      linkedAtTimestamp = now,
      deviceLabel = "WA: +$normalized"
    )
    _showLinkDialog.value = false
    Toast.makeText(context, "WhatsApp Berhasil Ditautkan!", Toast.LENGTH_SHORT).show()
    return true
  }

  fun unlinkWhatsAppNumber(context: Context) {
    prefs.edit()
      .remove("wa_is_linked")
      .remove("wa_raw_phone")
      .remove("wa_norm_phone")
      .remove("wa_device_label")
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

  fun sendDirectToServer(context: Context, segment: VideoSegment) {
    val account = _linkedAccount.value
    if (!account.isLinked) {
      _showLinkDialog.value = true
      Toast.makeText(context, "Tautkan WhatsApp Anda terlebih dahulu", Toast.LENGTH_SHORT).show()
      return
    }
    com.example.service.WhatsAppCompanionService.sendDirectly(context, segment, account.normalizedPhoneNumber)
  }

  fun sendAllDirectToServer(context: Context, segments: List<VideoSegment>) {
    val account = _linkedAccount.value
    if (!account.isLinked) {
      _showLinkDialog.value = true
      Toast.makeText(context, "Tautkan WhatsApp Anda terlebih dahulu", Toast.LENGTH_SHORT).show()
      return
    }
    viewModelScope.launch {
      for (seg in segments) {
        com.example.service.WhatsAppCompanionService.sendDirectly(context, seg, account.normalizedPhoneNumber)
      }
    }
  }

  fun resetDirectSendState() {
    com.example.service.WhatsAppCompanionService.resetState()
  }

  fun setPreviewSegment(segment: VideoSegment?) {
    _previewSegment.value = segment
  }
}
