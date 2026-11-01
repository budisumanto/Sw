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

  fun buildDisplayCommand(
    inputFile: String = "input.mp4",
    outputFile: String = "output.mp4",
    resolutionMode: ResolutionMode,
    contentProfile: ContentProfile,
    isSegment: Boolean = false,
    startSec: Int = 0,
    durationSec: Int = MAX_CHUNK_DURATION_SEC
  ): String {
    val seekParam = if (isSegment) "-ss $startSec -t $durationSec " else ""
    return buildString {
      append("ffmpeg -y ")
      if (isSegment) append("-ss $startSec ")
      append("-i \"$inputFile\" ")
      if (isSegment) append("-t $durationSec ")
      append("-vf \"${resolutionMode.scaleFilter}\" ")
      append("-c:v libx264 ")
      append("-crf ${contentProfile.crf} ")
      append("-preset ${contentProfile.preset} ")
      append("-g ${contentProfile.gop} ")
      append("-maxrate ${contentProfile.maxrate} ")
      append("-bufsize ${contentProfile.bufsize} ")
      append("-pix_fmt yuv420p ")
      append("-c:a aac ")
      append("-b:a 128k ")
      append("-movflags +faststart ")
      append("\"$outputFile\"")
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
    val logBuffer = ArrayDeque<String>(50)
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

        val command = buildDisplayCommand(
          inputFile = inputFile.absolutePath,
          outputFile = outputFile.absolutePath,
          resolutionMode = resolutionMode,
          contentProfile = contentProfile,
          isSegment = shouldSplit,
          startSec = startSec,
          durationSec = chunkDurationSec
        )

        if (chunkIndex == 0) {
          primaryCommandUsed = command
        }

        // FFmpegKit takes command arguments without the leading "ffmpeg " executable name
        val executionCommand = if (command.startsWith("ffmpeg ")) {
          command.removePrefix("ffmpeg ")
        } else {
          command
        }

        val success = executeChunkSession(
          command = executionCommand,
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
          _processingState.value = ProcessingState.Error(
            message = "Encoding failed on Part ${chunkIndex + 1}/$chunkCount. Check log details.",
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

  private suspend fun executeChunkSession(
    command: String,
    chunkIndex: Int,
    totalChunks: Int,
    chunkDurationMs: Long,
    logBuffer: ArrayDeque<String>
  ): Boolean = withContext(Dispatchers.IO) {
    val completionLatch = kotlinx.coroutines.CompletableDeferred<Boolean>()

    val session = FFmpegKit.executeAsync(
      command,
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
      { log ->
        val msg = log.message?.trim()
        if (!msg.isNullOrBlank()) {
          synchronized(logBuffer) {
            if (logBuffer.size >= 50) logBuffer.removeFirst()
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
