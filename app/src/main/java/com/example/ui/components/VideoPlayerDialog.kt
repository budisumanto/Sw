package com.example.ui.components

import android.widget.MediaController
import android.widget.VideoView
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import com.example.model.VideoSegment
import com.example.ui.theme.DarkObsidian
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.MintAccent

@Composable
fun VideoPlayerDialog(
  segment: VideoSegment,
  onDismiss: () -> Unit,
  onShare: (VideoSegment) -> Unit
) {
  val context = LocalContext.current

  Dialog(onDismissRequest = onDismiss) {
    Card(
      modifier = Modifier
        .fillMaxWidth()
        .padding(8.dp)
        .testTag("video_player_dialog"),
      shape = RoundedCornerShape(20.dp),
      colors = CardDefaults.cardColors(containerColor = DarkSurface)
    ) {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(16.dp)
      ) {
        // Header
        Row(
          modifier = Modifier.fillMaxWidth(),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Column(modifier = Modifier.weight(1f)) {
            Text(
              text = "Part ${segment.partIndex} of ${segment.totalParts} Preview",
              style = MaterialTheme.typography.titleMedium,
              fontWeight = FontWeight.Bold,
              color = Color.White
            )
            Text(
              text = "${segment.formattedDuration} • ${segment.formattedSize}",
              style = MaterialTheme.typography.bodySmall,
              color = Color(0xFF94A3B8)
            )
          }

          IconButton(
            onClick = onDismiss,
            modifier = Modifier.testTag("close_player_dialog")
          ) {
            Icon(
              imageVector = Icons.Default.Close,
              contentDescription = "Close preview",
              tint = Color.White
            )
          }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Video Surface
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(9f / 16f, matchHeightConstraintsFirst = false)
            .clip(RoundedCornerShape(12.dp))
            .background(DarkObsidian)
        ) {
          AndroidView(
            modifier = Modifier
              .fillMaxWidth()
              .align(Alignment.Center),
            factory = { ctx ->
              VideoView(ctx).apply {
                val mediaController = MediaController(ctx)
                mediaController.setAnchorView(this)
                setMediaController(mediaController)
                setVideoPath(segment.file.absolutePath)
                setOnPreparedListener { mp ->
                  mp.isLooping = true
                  start()
                }
              }
            }
          )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Quick Actions
        Row(modifier = Modifier.fillMaxWidth()) {
          FilledTonalButton(
            onClick = { onShare(segment) },
            modifier = Modifier
              .weight(1f)
              .height(48.dp)
              .testTag("dialog_send_server_button"),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.filledTonalButtonColors(
              containerColor = MintAccent,
              contentColor = Color(0xFF003822)
            )
          ) {
            Icon(
              imageVector = Icons.Default.CloudUpload,
              contentDescription = null
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
              text = "Kirim ke Server WhatsApp",
              fontWeight = FontWeight.Bold,
              fontSize = 14.sp
            )
          }
        }
      }
    }
  }
}
