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
import com.example.model.normalizeWhatsAppPhoneNumber
import com.example.util.ApiUploader
import com.example.util.ExportHelper
import com.example.util.VideoMetadataHelper
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class OptimizerViewModel(application: Application) : AndroidViewModel(application) {

  private val engine = StatusHDOptimizerEngine(application)
  private val prefs = application.getSharedPreferences("status_hd_prefs", Context.MODE_PRIVATE)

  private val _phoneNumber = MutableStateFlow<String>(prefs.getString("user_phone_number", "") ?: "")
  val phoneNumber: StateFlow<String> = _phoneNumber.asStateFlow()

  val isPhoneNumberValid: StateFlow<Boolean> = _phoneNumber.map { input ->
    val clean = normalizeWhatsAppPhoneNumber(input)
    clean.length in 10..15
  }.stateIn(viewModelScope, SharingStarted.Eagerly, false)

  // Pop-up modal wajib isi nomor WA jika saat buka aplikasi nomor belum ada
  private val _showPhonePromptDialog = MutableStateFlow<Boolean>(false)
  val showPhonePromptDialog: StateFlow<Boolean> = _showPhonePromptDialog.asStateFlow()

  private val _selectedVideo = MutableStateFlow<VideoMetadata?>(null)
  val selectedVideo: StateFlow<VideoMetadata?> = _selectedVideo.asStateFlow()

  private val _processingState = MutableStateFlow<ProcessingState>(ProcessingState.Idle)
  val processingState: StateFlow<ProcessingState> = _processingState.asStateFlow()

  private val _showSuccessDialog = MutableStateFlow<Boolean>(false)
  val showSuccessDialog: StateFlow<Boolean> = _showSuccessDialog.asStateFlow()

  private val _successMessage = MutableStateFlow<String>("")
  val successMessage: StateFlow<String> = _successMessage.asStateFlow()

  init {
    // Cek nomor saat aplikasi pertama kali dibuka
    val cleanInitial = normalizeWhatsAppPhoneNumber(_phoneNumber.value)
    if (cleanInitial.length < 10) {
      _showPhonePromptDialog.value = true
    }

    viewModelScope.launch {
      engine.processingState.collect { state ->
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

  fun updatePhoneNumber(input: String) {
    _phoneNumber.value = input
    prefs.edit().putString("user_phone_number", input).apply()
  }

  fun savePhoneNumberFromDialog(input: String) {
    updatePhoneNumber(input)
    val clean = normalizeWhatsAppPhoneNumber(input)
    if (clean.length >= 10) {
      _showPhonePromptDialog.value = false
    }
  }

  fun openPhonePromptDialog() {
    _showPhonePromptDialog.value = true
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
   * Alur Baru:
   * 1. Validasi nomor WhatsApp (Wajib diisi & format '62...').
   * 2. Kompres/Encode video secara lokal sesuai standar HD WhatsApp.
   * 3. Upload hasil encode ke server (field: phone & video) dengan header x-api-key: IPUL_GANTENG_123.
   * 4. Jika server merespons status: true, tampilkan pop-up:
   *    "Video HD sudah dikirim ke WhatsApp kamu! Buka WA dan simpan atau teruskan videonya ke Status."
   * 5. Jika gagal, tampilkan pesan error yang jelas.
   */
  fun startFullAutomatedPipeline(context: Context) {
    val rawPhone = _phoneNumber.value.trim()
    val cleanPhone = normalizeWhatsAppPhoneNumber(rawPhone)

    if (cleanPhone.length < 10) {
      _showPhonePromptDialog.value = true
      _processingState.value = ProcessingState.Error("Nomor WhatsApp wajib diisi dengan benar sebelum melanjutkan.")
      Toast.makeText(context, "Silakan lengkapi Nomor WhatsApp Anda", Toast.LENGTH_SHORT).show()
      return
    }

    val video = _selectedVideo.value ?: return

    viewModelScope.launch {
      try {
        _processingState.value = ProcessingState.Preparing("Menyiapkan kualitas terbaik...")

        // Langkah 1: Encode video secara lokal
        val optResult: OptimizationResult? = engine.optimizeVideo(sourceVideo = video)
        if (optResult == null || optResult.segments.isEmpty()) {
          return@launch
        }

        val totalSegments = optResult.segments.size

        // Langkah 2: Upload hasil encode ke server dengan field 'phone' dan 'video'
        for ((index, segment) in optResult.segments.withIndex()) {
          val currentPart = index + 1

          _processingState.value = ProcessingState.Uploading(
            activeChunk = currentPart,
            totalChunks = totalSegments,
            statusText = if (totalSegments > 1) {
              "Mengirim bagian $currentPart dari $totalSegments..."
            } else {
              "Menghubungkan ke server..."
            }
          )

          val uploadResult = ApiUploader.uploadVideo(
            phone = cleanPhone,
            file = segment.file
          ) { bytesUploaded, totalBytes, speedBps, remainingSeconds ->
            val speedMbS = speedBps / (1024.0 * 1024.0)
            val speedText = if (speedMbS >= 1.0) {
              String.format("%.1f MB/s", speedMbS)
            } else {
              val speedKbS = speedBps / 1024.0
              String.format("%.0f KB/s", speedKbS)
            }

            val timeText = when {
              remainingSeconds > 60 -> {
                val mins = remainingSeconds / 60
                val secs = remainingSeconds % 60
                "~$mins mnt $secs dtk lagi"
              }
              remainingSeconds > 0 -> "~$remainingSeconds detik lagi"
              remainingSeconds == 0L -> "Hampir selesai..."
              else -> "Mengirim..."
            }

            val prefix = if (totalSegments > 1) "Bagian $currentPart/$totalSegments: " else ""
            val dynamicStatusText = "$prefix$timeText ($speedText)"

            _processingState.value = ProcessingState.Uploading(
              activeChunk = currentPart,
              totalChunks = totalSegments,
              bytesUploaded = bytesUploaded,
              totalBytes = totalBytes,
              speedBps = speedBps,
              remainingSeconds = remainingSeconds,
              statusText = dynamicStatusText
            )
          }

          if (uploadResult.isFailure) {
            val errorMsg = uploadResult.exceptionOrNull()?.localizedMessage ?: "Gagal mengirim video ke server"
            _processingState.value = ProcessingState.Error("Gagal mengirim: $errorMsg")
            return@launch
          }
        }

        // Langkah 3: Pop-up Sukses
        val msg = "Video HD sudah dikirim ke WhatsApp kamu! Buka WA dan simpan atau teruskan videonya ke Status."
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
