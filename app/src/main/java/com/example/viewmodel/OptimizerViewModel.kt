package com.example.viewmodel

import android.app.Application
import android.content.Context
import android.net.Uri
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.engine.StatusHDOptimizerEngine
import com.example.model.OptimizationResult
import com.example.model.ProcessingState
import com.example.model.VideoMetadata
import com.example.util.ApiUploader
import com.example.util.ExportHelper
import com.example.util.VideoMetadataHelper
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class OptimizerViewModel(application: Application) : AndroidViewModel(application) {

  private val engine = StatusHDOptimizerEngine(application)

  private val _selectedVideo = MutableStateFlow<VideoMetadata?>(null)
  val selectedVideo: StateFlow<VideoMetadata?> = _selectedVideo.asStateFlow()

  private val _processingState = MutableStateFlow<ProcessingState>(ProcessingState.Idle)
  val processingState: StateFlow<ProcessingState> = _processingState.asStateFlow()

  private val _showSuccessDialog = MutableStateFlow<Boolean>(false)
  val showSuccessDialog: StateFlow<Boolean> = _showSuccessDialog.asStateFlow()

  private val _successMessage = MutableStateFlow<String>("")
  val successMessage: StateFlow<String> = _successMessage.asStateFlow()

  init {
    viewModelScope.launch {
      engine.processingState.collect { state ->
        // Forward engine states during compression
        if (_processingState.value is ProcessingState.Preparing ||
            _processingState.value is ProcessingState.Compressing ||
            _processingState.value is ProcessingState.Idle
        ) {
          if (state is ProcessingState.Compressing ||
              state is ProcessingState.Preparing ||
              state is ProcessingState.Error ||
              state is ProcessingState.Cancelled
          ) {
            _processingState.value = state
          }
        }
      }
    }
  }

  fun onVideoSelected(uri: Uri, context: Context) {
    viewModelScope.launch {
      val meta = VideoMetadataHelper.extractMetadata(context, uri)
      if (meta != null) {
        _selectedVideo.value = meta
        _processingState.value = ProcessingState.Idle
        _showSuccessDialog.value = false
        engine.resetToIdle()
      } else {
        Toast.makeText(context, "Video tidak dapat dimuat", Toast.LENGTH_SHORT).show()
      }
    }
  }

  /**
   * Alur Otomatis Penuh:
   * 1. Kompres/Encode secara lokal sesuai spek HD.
   * 2. Upload setiap segmen hasil encode ke http://208.76.40.198:3000/api/send-hd (field: video).
   * 3. Simpan file hasil encode ke Galeri HP (MediaStore).
   * 4. Tampilkan dialog sukses "Video HD berhasil disimpan ke Galeri! Silakan buka WhatsApp dan posting ke Status".
   */
  fun startFullAutomatedPipeline(context: Context) {
    val video = _selectedVideo.value ?: return

    viewModelScope.launch {
      try {
        _processingState.value = ProcessingState.Preparing("Menyiapkan kualitas terbaik...")

        // Langkah 1: Encode video lokal
        val optResult: OptimizationResult? = engine.optimizeVideo(sourceVideo = video)
        if (optResult == null || optResult.segments.isEmpty()) {
          // Jika dibatalkan atau error, state sudah diset oleh engine
          return@launch
        }

        val totalSegments = optResult.segments.size

        // Langkah 2 & 3: Upload dan Simpan ke Galeri untuk setiap segmen
        for ((index, segment) in optResult.segments.withIndex()) {
          val currentPart = index + 1

          // 2. Upload hasil encode ke server
          _processingState.value = ProcessingState.Uploading(
            activeChunk = currentPart,
            totalChunks = totalSegments,
            statusText = if (totalSegments > 1) {
              "Mengunggah bagian $currentPart dari $totalSegments..."
            } else {
              "Mengunggah video..."
            }
          )

          val uploadResult = ApiUploader.uploadVideo(segment.file)
          if (uploadResult.isFailure) {
            val errorMsg = uploadResult.exceptionOrNull()?.localizedMessage ?: "Gagal mengunggah video ke server"
            _processingState.value = ProcessingState.Error("Gagal upload: $errorMsg")
            return@launch
          }

          // 3. Simpan file hasil encode ke Galeri HP (MediaStore)
          _processingState.value = ProcessingState.Saving(
            statusText = if (totalSegments > 1) {
              "Menyimpan bagian $currentPart ke Galeri..."
            } else {
              "Menyimpan ke Galeri..."
            }
          )

          val saved = ExportHelper.saveToGallery(context, segment.file)
          if (!saved) {
            _processingState.value = ProcessingState.Error("Gagal menyimpan video ke Galeri.")
            return@launch
          }
        }

        // Langkah 4: Tampilkan pesan sukses
        val msg = "Video HD berhasil disimpan ke Galeri! Silakan buka WhatsApp dan posting ke Status."
        _successMessage.value = msg
        _processingState.value = ProcessingState.Success(optResult, msg)
        _showSuccessDialog.value = true

      } catch (e: Exception) {
        if (e is CancellationException) {
          _processingState.value = ProcessingState.Cancelled
        } else {
          e.printStackTrace()
          _processingState.value = ProcessingState.Error("Terjadi kesalahan: ${e.localizedMessage}")
        }
      }
    }
  }

  fun cancelOptimization() {
    engine.cancel()
    _processingState.value = ProcessingState.Cancelled
  }

  fun resetOptimization() {
    resetAll()
  }

  fun resetAll() {
    _selectedVideo.value = null
    _showSuccessDialog.value = false
    _successMessage.value = ""
    _processingState.value = ProcessingState.Idle
    engine.resetToIdle()
  }

  fun dismissSuccessDialog() {
    _showSuccessDialog.value = false
    resetAll()
  }

  fun openWhatsApp(context: Context) {
    ExportHelper.openWhatsApp(context)
  }
}
