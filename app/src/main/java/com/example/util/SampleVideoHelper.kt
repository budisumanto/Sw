package com.example.util

import android.content.Context
import com.arthenica.ffmpegkit.FFmpegKit
import com.arthenica.ffmpegkit.ReturnCode
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

object SampleVideoHelper {

  suspend fun generateSampleVideo(
    context: Context,
    durationSeconds: Int = 15,
    highMotionPatterns: Boolean = false
  ): File? = withContext(Dispatchers.IO) {
    try {
      val dir = File(context.cacheDir, "sample_videos").apply { mkdirs() }
      val fileName = if (durationSeconds > 90) {
        "sample_multisegment_${durationSeconds}s.mp4"
      } else if (highMotionPatterns) {
        "sample_highmotion_${durationSeconds}s.mp4"
      } else {
        "sample_vlog_${durationSeconds}s.mp4"
      }
      val outputFile = File(dir, fileName)

      // If already cached and valid, reuse
      if (outputFile.exists() && outputFile.length() > 10_000) {
        return@withContext outputFile
      }

      // Generate a synthetic test video with moving elements, clock, and audio beep
      // Use FFmpeg testsrc filter:
      val filter = if (highMotionPatterns) {
        // High motion: dynamic smptebars with rapid color animation and moving text
        "testsrc=duration=$durationSeconds:size=1080x1920:rate=30,drawbox=y=ih/2:color=red@0.5:width=iw:height=20:t=fill"
      } else {
        // Standard video: 1080x1920 portrait test clip
        "testsrc=duration=$durationSeconds:size=1080x1920:rate=30"
      }

      val cmd = "-y -f lavfi -i $filter -f lavfi -i sine=frequency=440:duration=$durationSeconds -c:v libx264 -preset ultrafast -pix_fmt yuv420p -c:a aac -shortest \"${outputFile.absolutePath}\""

      val session = FFmpegKit.execute(cmd)
      if (ReturnCode.isSuccess(session.returnCode) && outputFile.exists()) {
        outputFile
      } else {
        null
      }
    } catch (e: Exception) {
      e.printStackTrace()
      null
    }
  }
}
