package com.example.ui.components

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChangeCircle
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.VideoFile
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.VideoMetadata
import com.example.ui.theme.MintAccent

@Composable
fun VideoSelectorCard(
  selectedVideo: VideoMetadata?,
  onVideoSelected: (android.net.Uri) -> Unit,
  modifier: Modifier = Modifier
) {
  val mediaPicker = rememberLauncherForActivityResult(
    contract = ActivityResultContracts.PickVisualMedia()
  ) { uri ->
    if (uri != null) {
      onVideoSelected(uri)
    }
  }

  LiquidGlassBox(
    modifier = modifier
      .fillMaxWidth()
      .testTag("video_selector_card")
  ) {
    if (selectedVideo == null) {
      // State: Belum Ada Video
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(vertical = 14.dp),
        horizontalAlignment = Alignment.CenterHorizontally
      ) {
        Box(
          modifier = Modifier
            .size(76.dp)
            .clip(CircleShape)
            .background(
              Brush.radialGradient(
                listOf(Color(0xFF10B981).copy(alpha = 0.25f), Color.Transparent)
              )
            )
            .border(
              androidx.compose.foundation.BorderStroke(1.dp, GlassBorderTopHighlight),
              CircleShape
            )
            .clickable {
              mediaPicker.launch(
                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.VideoOnly)
              )
            },
          contentAlignment = Alignment.Center
        ) {
          Icon(
            imageVector = Icons.Default.Movie,
            contentDescription = "Pilih Video",
            tint = MintAccent,
            modifier = Modifier.size(36.dp)
          )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
          text = "Pilih Video dari Galeri",
          style = MaterialTheme.typography.titleMedium,
          fontWeight = FontWeight.Bold,
          color = Color.White
        )

        Text(
          text = "Format dan resolusi akan dioptimalkan otomatis agar jernih dan tidak pecah.",
          style = MaterialTheme.typography.bodySmall,
          color = Color(0xFF94A3B8),
          textAlign = TextAlign.Center,
          lineHeight = 18.sp,
          modifier = Modifier
            .padding(horizontal = 16.dp)
            .padding(top = 6.dp, bottom = 20.dp)
        )

        Button(
          onClick = {
            mediaPicker.launch(
              PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.VideoOnly)
            )
          },
          modifier = Modifier
            .fillMaxWidth()
            .height(52.dp)
            .testTag("pick_video_button"),
          shape = RoundedCornerShape(16.dp),
          colors = ButtonDefaults.buttonColors(
            containerColor = MintAccent,
            contentColor = Color(0xFF003822)
          )
        ) {
          Icon(imageVector = Icons.Default.VideoFile, contentDescription = null, modifier = Modifier.size(18.dp))
          Spacer(modifier = Modifier.width(8.dp))
          Text("Buka Galeri", fontWeight = FontWeight.Bold, fontSize = 15.sp)
        }
      }
    } else {
      // State: Video Dipilih
      Column(modifier = Modifier.fillMaxWidth()) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          verticalAlignment = Alignment.CenterVertically
        ) {
          // Thumbnail Box
          Box(
            modifier = Modifier
              .size(width = 84.dp, height = 98.dp)
              .clip(RoundedCornerShape(18.dp))
              .background(Color.Black.copy(alpha = 0.4f))
              .border(
                androidx.compose.foundation.BorderStroke(1.dp, GlassBorderTopHighlight),
                RoundedCornerShape(18.dp)
              ),
            contentAlignment = Alignment.Center
          ) {
            if (selectedVideo.thumbnailBitmap != null) {
              Image(
                bitmap = selectedVideo.thumbnailBitmap.asImageBitmap(),
                contentDescription = "Pratinjau Video",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
              )
            } else {
              Icon(
                imageVector = Icons.Default.Movie,
                contentDescription = null,
                tint = MintAccent,
                modifier = Modifier.size(32.dp)
              )
            }

            // Duration Pill
            Box(
              modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(6.dp)
                .clip(RoundedCornerShape(6.dp))
                .background(Color.Black.copy(alpha = 0.75f))
                .padding(horizontal = 5.dp, vertical = 2.dp)
            ) {
              Text(
                text = selectedVideo.formattedDuration,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
              )
            }
          }

          Spacer(modifier = Modifier.width(16.dp))

          // Video Info
          Column(modifier = Modifier.weight(1f)) {
            Text(
              text = selectedVideo.fileName,
              style = MaterialTheme.typography.titleMedium,
              fontWeight = FontWeight.Bold,
              color = Color.White,
              maxLines = 1
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
              text = "${selectedVideo.formattedDuration} • ${selectedVideo.formattedSize}",
              style = MaterialTheme.typography.bodyMedium,
              color = Color(0xFFA7F3D0),
              fontWeight = FontWeight.SemiBold
            )

            if (selectedVideo.needsSplitting) {
              Spacer(modifier = Modifier.height(6.dp))
              Text(
                text = "Video panjang akan otomatis dibagi menjadi bagian-bagian rapi.",
                fontSize = 11.sp,
                color = Color(0xFFFCD34D),
                lineHeight = 15.sp
              )
            }
          }
        }

        Spacer(modifier = Modifier.height(14.dp))

        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.End
        ) {
          OutlinedButton(
            onClick = {
              mediaPicker.launch(
                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.VideoOnly)
              )
            },
            modifier = Modifier.testTag("change_video_button"),
            shape = RoundedCornerShape(12.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, GlassBorderTopHighlight),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 14.dp, vertical = 6.dp)
          ) {
            Icon(imageVector = Icons.Default.ChangeCircle, contentDescription = null, modifier = Modifier.size(16.dp), tint = Color.White)
            Spacer(modifier = Modifier.width(6.dp))
            Text("Ganti Video", fontSize = 12.sp, color = Color.White, fontWeight = FontWeight.SemiBold)
          }
        }
      }
    }
  }
}
