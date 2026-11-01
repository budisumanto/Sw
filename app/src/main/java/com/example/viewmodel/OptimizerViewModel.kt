package com.example.viewmodel

import android.app.Application
import android.content.Context
import android.net.Uri
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.engine.StatusHDOptimizerEngine
import com.example.model.ProcessingState
import com.example.model.VideoMetadata
import com.example.model.VideoSegment
import com.example.util.ExportHelper
import com.example.util.VideoMetadataHelper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class OptimizerViewModel(application: Application) : AndroidViewModel(application) {

  private val engine = StatusHDOptimizerEngine(application)

  private val _selectedVideo = MutableStateFlow<VideoMetadata?>(null)
  val selectedVideo: StateFlow<VideoMetadata?> = _selectedVideo.asStateFlow()

  val processingState: StateFlow<ProcessingState> = engine.processingState

  private val _previewSegment = MutableStateFlow<VideoSegment?>(null)
  val previewSegment: StateFlow<VideoSegment?> = _previewSegment.asStateFlow()

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

  fun shareSegment(context: Context, segment: VideoSegment) {
    ExportHelper.shareToWhatsAppStatus(context, segment)
  }

  fun shareAll(context: Context, segments: List<VideoSegment>) {
    ExportHelper.shareAllToWhatsApp(context, segments)
  }

  fun setPreviewSegment(segment: VideoSegment?) {
    _previewSegment.value = segment
  }
}
