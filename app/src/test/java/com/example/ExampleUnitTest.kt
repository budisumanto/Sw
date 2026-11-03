package com.example

import com.example.engine.StatusHDOptimizerEngine
import com.example.model.OptimizationResult
import com.example.model.ProcessingState
import com.example.model.VideoMetadata
import com.example.model.VideoSegment
import com.example.model.formatDisplayPhoneNumber
import com.example.model.formatFileSize
import com.example.model.normalizeWhatsAppPhoneNumber
import com.example.util.ApiUploader
import okhttp3.MultipartBody
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

class ExampleUnitTest {

  @Test
  fun `anti-bloat bitrate formula clamps high bitrates to 8000k`() {
    // 50 MB in 10 seconds -> ~40,000 kbps
    val sizeBytes = 50L * 1024 * 1024
    val durationMs = 10_000L

    val (maxrate, bufsize) = StatusHDOptimizerEngine.calculateMaxrateAndBufsize(sizeBytes, durationMs)

    assertEquals(8000, maxrate)
    assertEquals(16000, bufsize)
  }

  @Test
  fun `anti-bloat bitrate formula clamps low bitrates to 500k`() {
    val sizeBytes = 500L * 1024
    val durationMs = 30_000L

    val (maxrate, bufsize) = StatusHDOptimizerEngine.calculateMaxrateAndBufsize(sizeBytes, durationMs)

    assertEquals(500, maxrate)
    assertEquals(1000, bufsize)
  }

  @Test
  fun `anti-bloat bitrate formula preserves original bitrate when within bounds`() {
    val durationMs = 30_000L
    val sizeBytes = 9_375_000L

    val (maxrate, bufsize) = StatusHDOptimizerEngine.calculateMaxrateAndBufsize(sizeBytes, durationMs)

    assertEquals(2500, maxrate)
    assertEquals(5000, bufsize)
  }

  @Test
  fun `execution args strictly match WhatsApp HD engine specifications with lanczos scale and high profile`() {
    val args = StatusHDOptimizerEngine.buildExecutionArgs(
      inputFile = "/storage/input.mp4",
      outputFile = "/storage/output.mp4",
      maxrate = 2500,
      bufsize = 5000,
      isSegment = false,
      hasAudio = true
    )

    val argsList = args.toList()

    assertEquals("-y", args[0])
    assertEquals("libx264", argsList[argsList.indexOf("-c:v") + 1])
    assertEquals("medium", argsList[argsList.indexOf("-preset") + 1])
    assertEquals("high", argsList[argsList.indexOf("-profile:v") + 1])
    assertEquals("4.0", argsList[argsList.indexOf("-level") + 1])
    assertEquals("film", argsList[argsList.indexOf("-tune") + 1])
    assertEquals("aq-mode=3", argsList[argsList.indexOf("-x264-params") + 1])
    assertEquals("20", argsList[argsList.indexOf("-crf") + 1])
    assertEquals("48", argsList[argsList.indexOf("-g") + 1])

    assertEquals("2500k", argsList[argsList.indexOf("-maxrate") + 1])
    assertEquals("5000k", argsList[argsList.indexOf("-bufsize") + 1])

    assertTrue(argsList.contains("-vf"))
    val vfIndex = argsList.indexOf("-vf")
    assertEquals(StatusHDOptimizerEngine.SCALE_FILTER, argsList[vfIndex + 1])
    assertTrue(StatusHDOptimizerEngine.SCALE_FILTER.endsWith(":flags=lanczos"))

    assertEquals("yuv420p", argsList[argsList.indexOf("-pix_fmt") + 1])
    assertEquals("aac", argsList[argsList.indexOf("-c:a") + 1])
    assertEquals("128k", argsList[argsList.indexOf("-b:a") + 1])
    assertEquals("+faststart", argsList[argsList.indexOf("-movflags") + 1])
  }

  @Test
  fun `execution args use -an when video has no audio`() {
    val args = StatusHDOptimizerEngine.buildExecutionArgs(
      inputFile = "/storage/silent.mp4",
      outputFile = "/storage/output.mp4",
      maxrate = 2000,
      bufsize = 4000,
      hasAudio = false
    )

    val argsList = args.toList()
    assertTrue(argsList.contains("-an"))
    assertFalse(argsList.contains("-c:a"))
    assertTrue(argsList.contains("-g"))
  }

  @Test
  fun `video metadata accurately computes split needs`() {
    val shortVideo = VideoMetadata(
      filePath = "/dummy/short.mp4",
      fileName = "short.mp4",
      durationMs = 45_000L,
      width = 1080,
      height = 1920
    )
    assertFalse(shortVideo.needsSplitting)
    assertEquals(1, shortVideo.estimatedChunkCount)
    assertEquals("00:45", shortVideo.formattedDuration)

    val longVideo = VideoMetadata(
      filePath = "/dummy/long.mp4",
      fileName = "long.mp4",
      durationMs = 195_000L,
      width = 1080,
      height = 1920
    )
    assertTrue(longVideo.needsSplitting)
    assertEquals(3, longVideo.estimatedChunkCount)
    assertEquals("03:15", longVideo.formattedDuration)
  }

  @Test
  fun `file size formatting utility works accurately`() {
    assertEquals("500 B", formatFileSize(500L))
    assertEquals("1.5 KB", formatFileSize(1536L))
    assertEquals("10.0 MB", formatFileSize(10 * 1024 * 1024L))
    assertEquals("1.25 GB", formatFileSize((1.25 * 1024 * 1024 * 1024).toLong()))
  }

  @Test
  fun `optimization result computes savings percentage correctly`() {
    val seg = VideoSegment(
      partIndex = 1,
      totalParts = 1,
      file = File("/dummy/out.mp4"),
      durationMs = 30_000L,
      sizeBytes = 8 * 1024 * 1024L,
      startSec = 0,
      endSec = 30
    )

    val result = OptimizationResult(
      segments = listOf(seg),
      originalSizeBytes = 20 * 1024 * 1024L,
      totalOptimizedSizeBytes = 8 * 1024 * 1024L,
      executionDurationMs = 2500L
    )

    assertEquals(60, result.savedPercentage)
    assertEquals("20.0 MB", result.formattedOriginalSize)
    assertEquals("8.0 MB", result.formattedOptimizedSize)
  }

  @Test
  fun `processing state success contains expected user message`() {
    val seg = VideoSegment(
      partIndex = 1,
      totalParts = 1,
      file = File("/dummy/out.mp4"),
      durationMs = 15_000L,
      sizeBytes = 4 * 1024 * 1024L,
      startSec = 0,
      endSec = 15
    )
    val result = OptimizationResult(
      segments = listOf(seg),
      originalSizeBytes = 10 * 1024 * 1024L,
      totalOptimizedSizeBytes = 4 * 1024 * 1024L,
      executionDurationMs = 1500L
    )

    val state = ProcessingState.Success(result)
    assertEquals(
      "Video HD sudah dikirim ke WhatsApp kamu! Buka WA dan simpan atau teruskan videonya ke Status.",
      state.message
    )
  }

  @Test
  fun `normalizeWhatsAppPhoneNumber normalizes Indonesian numbers accurately`() {
    assertEquals("6281234567890", normalizeWhatsAppPhoneNumber("+62 812-3456-7890"))
    assertEquals("6281234567890", normalizeWhatsAppPhoneNumber("081234567890"))
    assertEquals("6281234567890", normalizeWhatsAppPhoneNumber("6281234567890"))
    assertEquals("6281234567890", normalizeWhatsAppPhoneNumber("81234567890"))
    assertEquals("628571234567", normalizeWhatsAppPhoneNumber("0857-1234-567"))
    assertEquals("628571234567", normalizeWhatsAppPhoneNumber("+628571234567"))
  }

  @Test
  fun `formatDisplayPhoneNumber formats phone number for readable UI`() {
    assertEquals("+62 812-3456-7890", formatDisplayPhoneNumber("081234567890"))
  }

  @Test
  fun `ApiUploader request includes phone and video fields with x-api-key header`() {
    val dummyFile = File.createTempFile("test_video", ".mp4").apply {
      writeText("sample video content")
      deleteOnExit()
    }

    val request = ApiUploader.buildUploadRequest("6281234567890", dummyFile)

    assertEquals("https://harbor-flux-disposition-tutorials.trycloudflare.com/api/send-hd", request.url.toString())
    assertEquals("IPUL_GANTENG_123", request.header("x-api-key"))

    val body = request.body as? MultipartBody
    assertTrue(body != null)
    assertEquals(2, body!!.parts.size)
  }
}
