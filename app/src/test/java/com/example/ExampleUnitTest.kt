package com.example

import com.example.engine.StatusHDOptimizerEngine
import com.example.model.OptimizationResult
import com.example.model.VideoMetadata
import com.example.model.VideoSegment
import com.example.model.formatFileSize
import com.example.service.WhatsAppWebManager
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

class ExampleUnitTest {

  @Test
  fun `anti-bloat bitrate formula clamps high bitrates to 6000k`() {
    // 50 MB in 10 seconds -> ~40,000 kbps
    val sizeBytes = 50L * 1024 * 1024
    val durationMs = 10_000L

    val (maxrate, bufsize) = StatusHDOptimizerEngine.calculateMaxrateAndBufsize(sizeBytes, durationMs)

    assertEquals(6000, maxrate)
    assertEquals(12000, bufsize)
  }

  @Test
  fun `anti-bloat bitrate formula clamps low bitrates to 500k`() {
    // 500 KB in 30 seconds -> ~136 kbps
    val sizeBytes = 500L * 1024
    val durationMs = 30_000L

    val (maxrate, bufsize) = StatusHDOptimizerEngine.calculateMaxrateAndBufsize(sizeBytes, durationMs)

    assertEquals(500, maxrate)
    assertEquals(1000, bufsize)
  }

  @Test
  fun `anti-bloat bitrate formula preserves original bitrate when within bounds`() {
    // Target 2500 kbps: 2500 kbps * 30s = 75,000 kbits = 9,375,000 bytes
    val durationMs = 30_000L
    val sizeBytes = 9_375_000L

    val (maxrate, bufsize) = StatusHDOptimizerEngine.calculateMaxrateAndBufsize(sizeBytes, durationMs)

    assertEquals(2500, maxrate)
    assertEquals(5000, bufsize)
  }

  @Test
  fun `execution args strictly match WhatsApp HD engine specifications with dense keyframe structure`() {
    val args = StatusHDOptimizerEngine.buildExecutionArgs(
      inputFile = "/storage/input.mp4",
      outputFile = "/storage/output.mp4",
      maxrate = 2500,
      bufsize = 5000,
      isSegment = false,
      hasAudio = true
    )

    val argsList = args.toList()

    // Must start with -y (never "ffmpeg")
    assertEquals("-y", args[0])

    // Codec & Tuning
    assertEquals("libx264", argsList[argsList.indexOf("-c:v") + 1])
    assertEquals("faster", argsList[argsList.indexOf("-preset") + 1])
    assertEquals("film", argsList[argsList.indexOf("-tune") + 1])
    assertEquals("aq-mode=3", argsList[argsList.indexOf("-x264-params") + 1])
    assertEquals("20", argsList[argsList.indexOf("-crf") + 1])
    assertEquals("48", argsList[argsList.indexOf("-g") + 1])

    // Bitrate
    assertEquals("2500k", argsList[argsList.indexOf("-maxrate") + 1])
    assertEquals("5000k", argsList[argsList.indexOf("-bufsize") + 1])

    // Scale filter
    assertTrue(argsList.contains("-vf"))
    val vfIndex = argsList.indexOf("-vf")
    assertEquals(StatusHDOptimizerEngine.SCALE_FILTER, argsList[vfIndex + 1])

    // Color matrix
    assertEquals("yuv420p", argsList[argsList.indexOf("-pix_fmt") + 1])

    // Audio & Flags
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
      durationMs = 195_000L, // 3 min 15 sec -> chunks: 90s + 90s + 15s = 3 chunks
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
  fun `normalizePhoneNumber handles various Indonesian formats accurately`() {
    assertEquals("628123456789", com.example.model.normalizePhoneNumber("08123456789"))
    assertEquals("628123456789", com.example.model.normalizePhoneNumber("+628123456789"))
    assertEquals("628123456789", com.example.model.normalizePhoneNumber("628123456789"))
    assertEquals("628123456789", com.example.model.normalizePhoneNumber("8123456789"))
    assertEquals("6281234567890", com.example.model.normalizePhoneNumber("0812-3456-7890"))
    assertEquals("6281234567890", com.example.model.normalizePhoneNumber("+62 812 3456 7890"))
  }

  @Test
  fun `LinkedWhatsAppAccount formats display number appropriately`() {
    val unlinked = com.example.model.LinkedWhatsAppAccount()
    assertEquals("Belum Terhubung", unlinked.formattedDisplayNumber)

    val linked = com.example.model.LinkedWhatsAppAccount(
      rawPhoneNumber = "08123456789",
      normalizedPhoneNumber = "628123456789",
      isLinked = true
    )
    assertEquals("+62 812-3456-789", linked.formattedDisplayNumber)
  }

  @Test
  fun `generateRandomPairingCode produces 8-character alphanumeric code with hyphen`() {
    val code = com.example.model.generateRandomPairingCode()
    assertEquals(9, code.length) // 4 chars + '-' + 4 chars
    assertEquals('-', code[4])
    val clean = code.replace("-", "")
    assertEquals(8, clean.length)
    assertTrue(clean.all { it.isLetterOrDigit() })
  }

  @Test
  fun `QRCodeGenerator generates valid non-empty matrix for companion pairing`() {
    val matrix = com.example.util.QRCodeGenerator.generateMatrix("2@WA-TEST,StatusHD,Baileys")
    assertTrue(matrix.isNotEmpty())
    assertTrue(matrix.size >= 25)
    assertTrue(matrix[0].isNotEmpty())
    // Finder pattern at (0,0) must be true
    assertTrue(matrix[0][0])
  }

  @Test
  fun `WhatsAppWebManager configured with official desktop web url and desktop agent`() {
    assertEquals("https://web.whatsapp.com", WhatsAppWebManager.WA_WEB_URL)
    assertTrue(WhatsAppWebManager.DESKTOP_USER_AGENT.contains("Windows NT"))
    assertTrue(WhatsAppWebManager.DESKTOP_USER_AGENT.contains("Chrome"))
  }
}
