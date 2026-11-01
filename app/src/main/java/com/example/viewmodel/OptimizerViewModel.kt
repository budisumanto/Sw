package com.example.viewmodel

import android.app.Application
import android.content.Context
import android.net.Uri
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.engine.StatusHDOptimizerEngine
import com.example.model.ContentProfile
import com.example.model.OptimizationResult
import com.example.model.ProcessingState
import com.example.model.ResolutionMode
import com.example.model.VideoMetadata
import com.example.model.VideoSegment
import com.example.util.ExportHelper
import com.example.util.SampleVideoHelper
import com.example.util.VideoMetadataHelper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.File

class OptimizerViewModel(application: Application) : AndroidViewModel(application) {

  private val engine = StatusHDOptimizerEngine(application)

  private val _selectedVideo = MutableStateFlow<VideoMetadata?>(null)
  val selectedVideo: StateFlow<VideoMetadata?> = _selectedVideo.asStateFlow()

  private val _resolutionMode = MutableStateFlow(ResolutionMode.HD_720P)
  val resolutionMode: StateFlow<ResolutionMode> = _resolutionMode.asStateFlow()

  private val _contentProfile = MutableStateFlow(ContentProfile.STANDARD)
  val contentProfile: StateFlow<ContentProfile> = _contentProfile.asStateFlow()

  private val _forceAutoSplit = MutableStateFlow(true)
  val forceAutoSplit: StateFlow<Boolean> = _forceAutoSplit.asStateFlow()

  val processingState: StateFlow<ProcessingState> = engine.processingState

  private val _isCommandInspectorExpanded = MutableStateFlow(false)
  val isCommandInspectorExpanded: StateFlow<Boolean> = _isCommandInspectorExpanded.asStateFlow()

  private val _isLiveLogsExpanded = MutableStateFlow(false)
  val isLiveLogsExpanded: StateFlow<Boolean> = _isLiveLogsExpanded.asStateFlow()

  private val _history = MutableStateFlow<List<OptimizationResult>>(emptyList())
  val history: StateFlow<List<OptimizationResult>> = _history.asStateFlow()

  private val _previewSegment = MutableStateFlow<VideoSegment?>(null)
  val previewSegment: StateFlow<VideoSegment?> = _previewSegment.asStateFlow()

  private val _isGeneratingSample = MutableStateFlow(false)
  val isGeneratingSample: StateFlow<Boolean> = _isGeneratingSample.asStateFlow()

  fun onVideoSelected(uri: Uri, context: Context) {
    viewModelScope.launch {
      val meta = VideoMetadataHelper.extractMetadata(context, uri)
      if (meta != null) {
        _selectedVideo.value = meta
        engine.resetToIdle()
      } else {
        Toast.makeText(context, "Could not load video metadata", Toast.LENGTH_SHORT).show()
      }
    }
  }

  fun onLoadSampleVideo(context: Context, longDuration: Boolean = false, highMotion: Boolean = false) {
    viewModelScope.launch {
      _isGeneratingSample.value = true
      Toast.makeText(context, "Preparing demo video with FFmpeg...", Toast.LENGTH_SHORT).show()
      val duration = if (longDuration) 100 else 15
      val file = SampleVideoHelper.generateSampleVideo(context, duration, highMotion)
      _isGeneratingSample.value = false
      if (file != null) {
        val meta = VideoMetadataHelper.extractMetadataFromFile(file)
        if (meta != null) {
          _selectedVideo.value = meta
          if (highMotion) {
            _contentProfile.value = ContentProfile.HIGH_MOTION
          }
          engine.resetToIdle()
        }
      } else {
        Toast.makeText(context, "Failed to generate sample video", Toast.LENGTH_SHORT).show()
      }
    }
  }

  fun setResolutionMode(mode: ResolutionMode) {
    _resolutionMode.value = mode
  }

  fun setContentProfile(profile: ContentProfile) {
    _contentProfile.value = profile
  }

  fun setForceAutoSplit(enabled: Boolean) {
    _forceAutoSplit.value = enabled
  }

  fun toggleCommandInspector() {
    _isCommandInspectorExpanded.value = !_isCommandInspectorExpanded.value
  }

  fun toggleLiveLogs() {
    _isLiveLogsExpanded.value = !_isLiveLogsExpanded.value
  }

  fun getGeneratedCommand(): String {
    val video = _selectedVideo.value
    val inputPath = video?.filePath ?: "input.mp4"
    val outputPath = if (video != null) {
      "output_${video.fileName.substringBeforeLast(".")}_hd.mp4"
    } else "output.mp4"

    return engine.buildDisplayCommand(
      inputFile = inputPath,
      outputFile = outputPath,
      resolutionMode = _resolutionMode.value,
      contentProfile = _contentProfile.value,
      isSegment = (video?.durationMs ?: 0L) > 90_000L && _forceAutoSplit.value,
      startSec = 0,
      durationSec = 90
    )
  }

  fun startOptimization(context: Context) {
    val video = _selectedVideo.value ?: return
    viewModelScope.launch {
      val result = engine.optimizeVideo(
        sourceVideo = video,
        resolutionMode = _resolutionMode.value,
        contentProfile = _contentProfile.value,
        forceAutoSplit = _forceAutoSplit.value
      )
      if (result != null) {
        _history.value = listOf(result) + _history.value
      }
    }
  }

  fun cancelOptimization() {
    engine.cancel()
  }

  fun resetOptimization() {
    engine.resetToIdle()
  }

  fun saveSegment(context: Context, segment: VideoSegment) {
    viewModelScope.launch {
      val success = ExportHelper.saveToGallery(context, segment.file)
      if (success) {
        Toast.makeText(context, "Saved Part ${segment.partIndex} to Movies/StatusHD", Toast.LENGTH_SHORT).show()
      } else {
        Toast.makeText(context, "Failed to save to gallery", Toast.LENGTH_SHORT).show()
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
      Toast.makeText(context, "Saved $savedCount segments to Movies/StatusHD", Toast.LENGTH_SHORT).show()
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
