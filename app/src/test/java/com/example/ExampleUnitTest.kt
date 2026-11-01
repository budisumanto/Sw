package com.example

import com.example.model.OptimizationResult
import com.example.model.SmartEncodingConfig
import com.example.model.VideoMetadata
import com.example.model.VideoSegment
import com.example.model.formatFileSize
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

class ExampleUnitTest {

  @Test
  fun `smart config auto detects 1080p portrait video`() {
    val portrait1080p = VideoMetadata(
      filePath = "/dummy/sample.mp4",
      fileName = "sample.mp4",
      durationMs = 30_000L,
      width = 1080,
      height = 1920,
      bitrateBps = 8_000_000L
    )

    val config = SmartEncodingConfig.computeSmartConfig(portrait1080p)

    assertEquals("scale='trunc(min(1080,iw)/2)*2':-2", config.scaleFilter)
    assertEquals(20, config.crf)
    assertEquals("4000k", config.maxrate)
    assertEquals("8000k", config.bufsize)
    assertEquals(30, config.gop)
    assertTrue(config.targetResolutionLabel.contains("1080p"))
  }

  @Test
  fun `smart config preserves 720p without upscaling`() {
    val portrait720p = VideoMetadata(
      filePath = "/dummy/sample_720.mp4",
      fileName = "sample_720.mp4",
      durationMs = 20_000L,
      width = 720,
      height = 1280,
      bitrateBps = 3_000_000L
    )

    val config = SmartEncodingConfig.computeSmartConfig(portrait720p)

    assertEquals("scale='trunc(min(720,iw)/2)*2':-2", config.scaleFilter)
    assertEquals(20, config.crf)
    assertEquals("3000k", config.maxrate) // Capped to original 3000k to prevent size bloat!
    assertEquals("6000k", config.bufsize)
    assertEquals(30, config.gop)
    assertTrue(config.targetResolutionLabel.contains("Native"))
  }

  @Test
  fun `smart config downscales 4K video to 1080p`() {
    val video4k = VideoMetadata(
      filePath = "/dummy/4k.mp4",
      fileName = "4k.mp4",
      durationMs = 60_000L,
      width = 2160,
      height = 3840,
      bitrateBps = 45_000_000L
    )

    val config = SmartEncodingConfig.computeSmartConfig(video4k)

    assertEquals("scale='trunc(min(1080,iw)/2)*2':-2", config.scaleFilter)
    assertEquals(20, config.crf)
    assertEquals("4000k", config.maxrate) // Clamped to 4000k max
    assertEquals("8000k", config.bufsize)
    assertTrue(config.targetResolutionLabel.contains("Downscaled 4K"))
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
    assertEquals("10.00 MB", formatFileSize(10 * 1024 * 1024L))
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
      executionDurationMs = 2500L,
      commandUsed = "ffmpeg ...",
      smartConfig = SmartEncodingConfig.createDefault()
    )

    assertEquals(60, result.savedPercentage)
    assertEquals("20.00 MB", result.formattedOriginalSize)
    assertEquals("8.00 MB", result.formattedOptimizedSize)
  }
}
