package com.example.engine

import android.content.Context
import com.arthenica.ffmpegkit.FFmpegKit
import com.arthenica.ffmpegkit.FFmpegSession
import com.arthenica.ffmpegkit.ReturnCode
import com.example.model.OptimizationResult
import com.example.model.ProcessingState
import com.example.model.VideoMetadata
import com.example.model.VideoSegment
import com.example.util.VideoMetadataHelper
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext
import java.io.File
import kotlin.math.ceil
import kotlin.math.min

class StatusHDOptimizerEngine(private val context: Context) {

  private val _processingState = MutableStateFlow<ProcessingState>(ProcessingState.Idle)
  val processingState: StateFlow<ProcessingState> = _processingState.asStateFlow()

  @Volatile
  private var currentSession: FFmpegSession? = null

  @Volatile
  private var isCancelledByUser = false

  companion object {
    const val MAX_CHUNK_DURATION_SEC = 90
    const val SCALE_FILTER = "scale=w=if(gte(iw\\,ih)\\,-2\\,min(1080\\,iw)):h=if(gte(iw\\,ih)\\,min(1080\\,ih)\\,-2)"

    fun calculateMaxrateAndBufsize(sizeBytes: Long, durationMs: Long): Pair<Int, Int> {
      val durationSec = (durationMs / 1000.0).coerceAtLeast(1.0)
      val originalBitrateKbps = ((sizeBytes * 8.0) / durationSec) / 1000.0

      // WhatsApp Status hard ceiling is ~2800 kbps. Above this, WhatsApp transcodes aggressively.
      val maxrate = when {
        originalBitrateKbps > 2800.0 -> 2800
        originalBitrateKbps < 500.0 -> 500
        else -> originalBitrateKbps.toInt()
      }
      val bufsize = maxrate * 2
      return Pair(maxrate, bufsize)
    }

    fun buildExecutionArgs(
      inputFile: String,
      outputFile: String,
      maxrate: Int,
      bufsize: Int,
      isSegment: Boolean = false,
      startSec: Int = 0,
      durationSec: Int = MAX_CHUNK_DURATION_SEC,
      hasAudio: Boolean = true
    ): Array<String> {
    val args = mutableListOf<String>()
    args.add("-y")

    if (isSegment) {
      args.add("-ss")
      args.add(startSec.toString())
    }

    args.add("-i")
    args.add(inputFile)

    if (isSegment) {
      args.add("-t")
      args.add(durationSec.toString())
    }

    // Video stream mapping & smart filter
    args.add("-map")
    args.add("0:v:0")

    args.add("-vf")
    args.add(SCALE_FILTER)

    // Codec, Tuning & High Density Streaming Profile (Bypass WhatsApp recompression)
    args.add("-c:v")
    args.add("libx264")
    args.add("-preset")
    args.add("medium")
    args.add("-profile:v")
    args.add("high")
    args.add("-level")
    args.add("4.1")
    args.add("-tune")
    args.add("film")
    args.add("-x264-params")
    args.add("aq-mode=3")
    args.add("-crf")
    args.add("20")

    // High Keyframe Density ("padat & matang") to ensure instant seek points & no WA transcode
    args.add("-g")
    args.add("30")
    args.add("-keyint_min")
    args.add("30")
    args.add("-sc_threshold")
    args.add("0")

    // Bitrate anti-bloat capped to WhatsApp bypass limit
    args.add("-maxrate")
    args.add("${maxrate}k")
    args.add("-bufsize")
    args.add("${bufsize}k")

    // Color matrix standards (prevent WA color degradation)
    args.add("-pix_fmt")
    args.add("yuv420p")
    args.add("-colorspace")
    args.add("bt709")
    args.add("-color_primaries")
    args.add("bt709")
    args.add("-color_trc")
    args.add("bt709")

    // Audio parameters (AAC 128k stereo)
    if (hasAudio) {
      args.add("-map")
      args.add("0:a:0?")
      args.add("-c:a")
      args.add("aac")
      args.add("-b:a")
      args.add("128k")
      args.add("-ar")
      args.add("44100")
      args.add("-ac")
      args.add("2")
    } else {
      args.add("-an")
    }

    // Suppress incompatible subtitle streams
    args.add("-sn")

    // Atom FastStart for instant playback
    args.add("-movflags")
    args.add("+faststart")

    // Output path
    args.add(outputFile)

    return args.toTypedArray()
  }
}

  suspend fun optimizeVideo(sourceVideo: VideoMetadata): OptimizationResult? =
    withContext(Dispatchers.IO) {
      isCancelledByUser = false
      _processingState.value = ProcessingState.Preparing("Menyiapkan kualitas terbaik...")

      val inputFile = File(sourceVideo.filePath)
      if (!inputFile.exists()) {
        _processingState.value = ProcessingState.Error("Video tidak dapat ditemukan. Coba pilih video lain.")
        return@withContext null
      }

      val (maxrate, bufsize) = calculateMaxrateAndBufsize(sourceVideo.sizeBytes, sourceVideo.durationMs)
      val outputDir = File(context.cacheDir, "status_hd_output_${System.currentTimeMillis()}").apply { mkdirs() }
      val totalDurationSec = (sourceVideo.durationMs / 1000).toInt().coerceAtLeast(1)
      val shouldSplit = totalDurationSec > MAX_CHUNK_DURATION_SEC
      val chunkCount = if (shouldSplit) ceil(totalDurationSec / MAX_CHUNK_DURATION_SEC.toDouble()).toInt() else 1

      val segments = mutableListOf<VideoSegment>()
      val startTimeMs = System.currentTimeMillis()

      try {
        for (chunkIndex in 0 until chunkCount) {
          if (isCancelledByUser) {
            _processingState.value = ProcessingState.Cancelled
            return@withContext null
          }

          val startSec = chunkIndex * MAX_CHUNK_DURATION_SEC
          val chunkDurationSec = min(MAX_CHUNK_DURATION_SEC, totalDurationSec - startSec)
          val chunkDurationMs = chunkDurationSec * 1000L

          val outputFileName = if (chunkCount > 1) {
            "status_part_${chunkIndex + 1}_of_${chunkCount}.mp4"
          } else {
            "status_${sourceVideo.fileName.substringBeforeLast(".")}_hd.mp4"
          }
          val outputFile = File(outputDir, outputFileName)

          val args = buildExecutionArgs(
            inputFile = inputFile.absolutePath,
            outputFile = outputFile.absolutePath,
            maxrate = maxrate,
            bufsize = bufsize,
            isSegment = shouldSplit,
            startSec = startSec,
            durationSec = chunkDurationSec,
            hasAudio = sourceVideo.hasAudio
          )

          val success = executeChunk(
            args = args,
            chunkIndex = chunkIndex + 1,
            totalChunks = chunkCount,
            chunkDurationMs = chunkDurationMs
          )

          if (!success) {
            if (isCancelledByUser) {
              _processingState.value = ProcessingState.Cancelled
              return@withContext null
            }
            _processingState.value = ProcessingState.Error("Video tidak dapat diproses. Coba pilih video lain.")
            return@withContext null
          }

          if (!outputFile.exists() || outputFile.length() <= 0) {
            _processingState.value = ProcessingState.Error("Video tidak dapat diproses. Coba pilih video lain.")
            return@withContext null
          }

          val segmentThumb = VideoMetadataHelper.getThumbnailForFile(outputFile)
          val segment = VideoSegment(
            partIndex = chunkIndex + 1,
            totalParts = chunkCount,
            file = outputFile,
            durationMs = chunkDurationMs,
            sizeBytes = outputFile.length(),
            startSec = startSec,
            endSec = startSec + chunkDurationSec,
            thumbnailBitmap = segmentThumb
          )
          segments.add(segment)
        }

        val executionDurationMs = System.currentTimeMillis() - startTimeMs
        val totalOptimizedSize = segments.sumOf { it.sizeBytes }

        val finalResult = OptimizationResult(
          segments = segments,
          originalSizeBytes = sourceVideo.sizeBytes,
          totalOptimizedSizeBytes = totalOptimizedSize,
          executionDurationMs = executionDurationMs
        )

        _processingState.value = ProcessingState.Success(finalResult)
        finalResult
      } catch (e: Exception) {
        if (e is CancellationException || isCancelledByUser) {
          _processingState.value = ProcessingState.Cancelled
          null
        } else {
          e.printStackTrace()
          _processingState.value = ProcessingState.Error("Video tidak dapat diproses. Coba pilih video lain.")
          null
        }
      } finally {
        currentSession = null
      }
    }

  private suspend fun executeChunk(
    args: Array<String>,
    chunkIndex: Int,
    totalChunks: Int,
    chunkDurationMs: Long
  ): Boolean = withContext(Dispatchers.IO) {
    val completionLatch = kotlinx.coroutines.CompletableDeferred<Boolean>()

    val session = FFmpegKit.executeWithArgumentsAsync(
      args,
      { completedSession ->
        currentSession = null
        val code = completedSession.returnCode
        if (ReturnCode.isSuccess(code)) {
          completionLatch.complete(true)
        } else if (ReturnCode.isCancel(code)) {
          isCancelledByUser = true
          completionLatch.complete(false)
        } else {
          completionLatch.complete(false)
        }
      },
      { /* Silent logs on front-end */ },
      { stats ->
        val timeMs = stats.time.toLong()
        val chunkProgress = if (chunkDurationMs > 0) {
          (timeMs.toFloat() / chunkDurationMs.toFloat()).coerceIn(0f, 1f)
        } else 0f

        val completedChunksFraction = (chunkIndex - 1).toFloat() / totalChunks.toFloat()
        val currentChunkFraction = chunkProgress / totalChunks.toFloat()
        val overallProgress = (completedChunksFraction + currentChunkFraction).coerceIn(0f, 1f)

        val statusText = if (totalChunks > 1) {
          "Memproses bagian $chunkIndex dari $totalChunks..."
        } else {
          "Menyiapkan kualitas terbaik..."
        }

        _processingState.value = ProcessingState.Compressing(
          activeChunk = chunkIndex,
          totalChunks = totalChunks,
          chunkProgress = chunkProgress,
          overallProgress = overallProgress,
          statusText = statusText
        )
      }
    )

    currentSession = session
    completionLatch.await()
  }

  fun cancel() {
    isCancelledByUser = true
    try {
      currentSession?.cancel()
      FFmpegKit.cancel()
    } catch (_: Exception) {}
    _processingState.value = ProcessingState.Cancelled
  }

  fun resetToIdle() {
    _processingState.value = ProcessingState.Idle
  }
}
