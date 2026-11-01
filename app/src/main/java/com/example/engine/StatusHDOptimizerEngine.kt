package com.example.engine

import android.content.Context
import com.arthenica.ffmpegkit.FFmpegKit
import com.arthenica.ffmpegkit.FFmpegSession
import com.arthenica.ffmpegkit.ReturnCode
import com.example.model.ContentProfile
import com.example.model.OptimizationResult
import com.example.model.ProcessingState
import com.example.model.ResolutionMode
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
    const val MAX_CHUNK_DURATION_MS = MAX_CHUNK_DURATION_SEC * 1000L
  }

  fun buildExecutionArgs(
    inputFile: String,
    outputFile: String,
    resolutionMode: ResolutionMode,
    contentProfile: ContentProfile,
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

    // Video stream mapping & filter
    args.add("-map")
    args.add("0:v:0")

    args.add("-vf")
    args.add(resolutionMode.scaleFilter)

    // H.264 Video Codec & Profile parameters
    args.add("-c:v")
    args.add("libx264")
    args.add("-crf")
    args.add(contentProfile.crf.toString())
    args.add("-preset")
    args.add(contentProfile.preset)
    args.add("-g")
    args.add(contentProfile.gop.toString())
    args.add("-maxrate")
    args.add(contentProfile.maxrate)
    args.add("-bufsize")
    args.add(contentProfile.bufsize)
    args.add("-pix_fmt")
    args.add("yuv420p")

    // Audio parameters (only if source has audio)
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

    // Force faststart for WhatsApp instant playback
    args.add("-movflags")
    args.add("+faststart")

    // Destination file
    args.add(outputFile)

    return args.toTypedArray()
  }

  fun buildDisplayCommand(
    inputFile: String = "input.mp4",
    outputFile: String = "output.mp4",
    resolutionMode: ResolutionMode,
    contentProfile: ContentProfile,
    isSegment: Boolean = false,
    startSec: Int = 0,
    durationSec: Int = MAX_CHUNK_DURATION_SEC,
    hasAudio: Boolean = true
  ): String {
    val args = buildExecutionArgs(
      inputFile = inputFile,
      outputFile = outputFile,
      resolutionMode = resolutionMode,
      contentProfile = contentProfile,
      isSegment = isSegment,
      startSec = startSec,
      durationSec = durationSec,
      hasAudio = hasAudio
    )
    return "ffmpeg " + args.joinToString(" ") { arg ->
      if (arg.contains(" ") || arg.contains("'") || arg.contains("\"") || arg.contains(":")) {
        "\"$arg\""
      } else {
        arg
      }
    }
  }

  suspend fun optimizeVideo(
    sourceVideo: VideoMetadata,
    resolutionMode: ResolutionMode,
    contentProfile: ContentProfile,
    forceAutoSplit: Boolean = true
  ): OptimizationResult? = withContext(Dispatchers.IO) {
    isCancelledByUser = false
    _processingState.value = ProcessingState.Preparing("Initializing StatusHD encoding engine...")

    val inputFile = File(sourceVideo.filePath)
    if (!inputFile.exists()) {
      _processingState.value = ProcessingState.Error("Input file not found at ${sourceVideo.filePath}")
      return@withContext null
    }

    val outputDir = File(context.cacheDir, "status_hd_output_${System.currentTimeMillis()}").apply { mkdirs() }
    val totalDurationSec = (sourceVideo.durationMs / 1000).toInt().coerceAtLeast(1)
    val shouldSplit = forceAutoSplit && totalDurationSec > MAX_CHUNK_DURATION_SEC
    val chunkCount = if (shouldSplit) ceil(totalDurationSec / MAX_CHUNK_DURATION_SEC.toDouble()).toInt() else 1

    val segments = mutableListOf<VideoSegment>()
    val startTimeMs = System.currentTimeMillis()
    val logBuffer = ArrayDeque<String>(60)
    var primaryCommandUsed = ""

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
          resolutionMode = resolutionMode,
          contentProfile = contentProfile,
          isSegment = shouldSplit,
          startSec = startSec,
          durationSec = chunkDurationSec,
          hasAudio = sourceVideo.hasAudio
        )

        if (chunkIndex == 0) {
          primaryCommandUsed = buildDisplayCommand(
            inputFile = inputFile.absolutePath,
            outputFile = outputFile.absolutePath,
            resolutionMode = resolutionMode,
            contentProfile = contentProfile,
            isSegment = shouldSplit,
            startSec = startSec,
            durationSec = chunkDurationSec,
            hasAudio = sourceVideo.hasAudio
          )
        }

        val success = executeChunkSessionWithArgs(
          args = args,
          chunkIndex = chunkIndex + 1,
          totalChunks = chunkCount,
          chunkDurationMs = chunkDurationMs,
          logBuffer = logBuffer
        )

        if (!success) {
          if (isCancelledByUser) {
            _processingState.value = ProcessingState.Cancelled
            return@withContext null
          }
          val lastLogs = logBuffer.takeLast(10).joinToString("\n")
          _processingState.value = ProcessingState.Error(
            message = if (lastLogs.isNotBlank()) "Encoding failed on Part ${chunkIndex + 1}/$chunkCount:\n$lastLogs"
                      else "Encoding failed on Part ${chunkIndex + 1}/$chunkCount. Check log details.",
            logs = logBuffer.toList()
          )
          return@withContext null
        }

        // Verify output file was created and is non-empty
        if (!outputFile.exists() || outputFile.length() <= 0) {
          _processingState.value = ProcessingState.Error(
            message = "Output file was not produced for Part ${chunkIndex + 1}.",
            logs = logBuffer.toList()
          )
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
        executionDurationMs = executionDurationMs,
        commandUsed = primaryCommandUsed,
        resolutionMode = resolutionMode,
        contentProfile = contentProfile
      )

      _processingState.value = ProcessingState.Success(finalResult)
      finalResult
    } catch (e: Exception) {
      if (e is CancellationException || isCancelledByUser) {
        _processingState.value = ProcessingState.Cancelled
        null
      } else {
        e.printStackTrace()
        _processingState.value = ProcessingState.Error(
          message = e.localizedMessage ?: "Unknown optimization error occurred",
          logs = logBuffer.toList()
        )
        null
      }
    } finally {
      currentSession = null
    }
  }

  private suspend fun executeChunkSessionWithArgs(
    args: Array<String>,
    chunkIndex: Int,
    totalChunks: Int,
    chunkDurationMs: Long,
    logBuffer: ArrayDeque<String>
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
          // Failure: collect logs from completedSession
          completedSession.allLogs?.forEach { l ->
            val msg = l.message?.trim()
            if (!msg.isNullOrBlank()) {
              synchronized(logBuffer) {
                if (logBuffer.size >= 60) logBuffer.removeFirst()
                logBuffer.addLast(msg)
              }
            }
          }
          val stackTrace = completedSession.failStackTrace
          if (!stackTrace.isNullOrBlank()) {
            synchronized(logBuffer) {
              logBuffer.addLast(stackTrace.trim())
            }
          }
          completionLatch.complete(false)
        }
      },
      { log ->
        val msg = log.message?.trim()
        if (!msg.isNullOrBlank()) {
          synchronized(logBuffer) {
            if (logBuffer.size >= 60) logBuffer.removeFirst()
            logBuffer.addLast(msg)
          }
        }
      },
      { stats ->
        val timeMs = stats.time.toLong()
        val fps = stats.videoFps.toDouble()
        val speed = stats.speed
        val bitrate = stats.bitrate

        val chunkProgress = if (chunkDurationMs > 0) {
          (timeMs.toFloat() / chunkDurationMs.toFloat()).coerceIn(0f, 1f)
        } else 0f

        val completedChunksFraction = (chunkIndex - 1).toFloat() / totalChunks.toFloat()
        val currentChunkFraction = chunkProgress / totalChunks.toFloat()
        val overallProgress = (completedChunksFraction + currentChunkFraction).coerceIn(0f, 1f)

        val recentLogsSnapshot: List<String>
        synchronized(logBuffer) {
          recentLogsSnapshot = logBuffer.takeLast(15)
        }

        _processingState.value = ProcessingState.Compressing(
          activeChunk = chunkIndex,
          totalChunks = totalChunks,
          chunkProgress = chunkProgress,
          overallProgress = overallProgress,
          currentFps = fps,
          speed = speed,
          timeRenderedMs = timeMs,
          currentBitrateKbps = bitrate,
          recentLogs = recentLogsSnapshot
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
