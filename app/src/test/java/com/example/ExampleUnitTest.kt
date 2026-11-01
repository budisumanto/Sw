package com.example

import com.example.model.ContentProfile
import com.example.model.OptimizationResult
import com.example.model.ResolutionMode
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
  fun `resolution mode scale filters match specification`() {
    assertEquals("scale='min(720,iw)':-2", ResolutionMode.HD_720P.scaleFilter)
    assertEquals("scale='min(1080,iw)':-2", ResolutionMode.CRISP_1080P.scaleFilter)
  }

  @Test
  fun `content profiles configure exact required encoding parameters`() {
    // Standard Profile
    val standard = ContentProfile.STANDARD
    assertEquals(23, standard.crf)
    assertEquals("medium", standard.preset)
    assertEquals("2.2M", standard.maxrate)
    assertEquals("4.4M", standard.bufsize)
    assertEquals(30, standard.gop)

    // High Motion / Jedag-Jedug Profile
    val highMotion = ContentProfile.HIGH_MOTION
    assertEquals(20, highMotion.crf)
    assertEquals("medium", highMotion.preset)
    assertEquals("2.8M", highMotion.maxrate)
    assertEquals("5.6M", highMotion.bufsize)
    assertEquals(30, highMotion.gop)
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
      resolutionMode = ResolutionMode.HD_720P,
      contentProfile = ContentProfile.STANDARD
    )

    assertEquals(60, result.savedPercentage)
    assertEquals("20.00 MB", result.formattedOriginalSize)
    assertEquals("8.00 MB", result.formattedOptimizedSize)
  }
}
