package com.example.ui.components

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.rounded.Autorenew
import androidx.compose.material.icons.rounded.Movie
import androidx.compose.material.icons.rounded.PlayCircle
import androidx.compose.material.icons.rounded.VideoLibrary
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.VideoMetadata
import com.example.ui.theme.LocalGlass
import com.example.ui.theme.pressable

@Composable
fun VideoSelectorCard(
  selectedVideo: VideoMetadata?,
  onVideoSelected: (android.net.Uri) -> Unit,
  modifier: Modifier = Modifier
) {
  val g = LocalGlass.current

  val mediaPicker = rememberLauncherForActivityResult(
    contract = ActivityResultContracts.PickVisualMedia()
  ) { uri ->
    if (uri != null) {
      onVideoSelected(uri)
    }
  }

  val openGallery = {
    mediaPicker.launch(
      PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.VideoOnly)
    )
  }

  GlassCard(
    modifier = modifier
      .fillMaxWidth()
      .testTag("video_selector_card"),
    radius = 24.dp,
    onClick = if (selectedVideo == null) openGallery else null
  ) {
    if (selectedVideo == null) {
      // State 1: Dropzone Kaca Minimalis Menawan - Seluruh card dapat di-tap langsung untuk buka galeri
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(vertical = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
      ) {
        // Lingkaran Pendar Halus
        Box(
          modifier = Modifier
            .size(80.dp)
            .clip(CircleShape)
            .background(g.accent.copy(alpha = 0.16f))
            .border(1.dp, g.accent.copy(alpha = 0.40f), CircleShape)
            .pressable { openGallery() },
          contentAlignment = Alignment.Center
        ) {
          Icon(
            imageVector = Icons.Rounded.VideoLibrary,
            contentDescription = "Pilih Video",
            tint = g.accent,
            modifier = Modifier.size(36.dp)
          )
        }

        Spacer(modifier = Modifier.height(18.dp))

        Text(
          text = "Pilih Video untuk Dioptimalkan",
          style = MaterialTheme.typography.titleMedium,
          color = g.text,
          textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
          text = "Video akan diproses agar tetap jernih dan tajam saat diunggah ke status WhatsApp kamu.",
          style = MaterialTheme.typography.bodyMedium,
          color = g.textMuted,
          textAlign = TextAlign.Center,
          modifier = Modifier.padding(horizontal = 14.dp)
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Tombol Elegan Pilih Video
        GlassButton(
          text = "Buka Galeri Video",
          onClick = openGallery,
          primary = true,
          modifier = Modifier
            .fillMaxWidth()
            .testTag("pick_video_button")
        )
      }
    } else {
      // State 2: Video Terpilih - Tampilan Sinematik Kaca Bersih & Cantik
      Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
      ) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(Color(0xFF0F131D).copy(alpha = 0.60f))
            .border(
              1.dp,
              Color.White.copy(alpha = 0.12f),
              RoundedCornerShape(20.dp)
            )
            .padding(12.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          // Poster Sinematik Murni (tanpa badge durasi ganda di dalam)
          Box(
            modifier = Modifier
              .size(width = 88.dp, height = 88.dp)
              .clip(RoundedCornerShape(16.dp))
              .background(Color(0xFF0A0D14))
              .border(
                1.dp,
                Color.White.copy(alpha = 0.18f),
                RoundedCornerShape(16.dp)
              ),
            contentAlignment = Alignment.Center
          ) {
            if (selectedVideo.thumbnailBitmap != null) {
              Image(
                bitmap = selectedVideo.thumbnailBitmap.asImageBitmap(),
                contentDescription = "Poster Video",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
              )
            } else {
              Icon(
                imageVector = Icons.Rounded.PlayCircle,
                contentDescription = null,
                tint = g.accent,
                modifier = Modifier.size(40.dp)
              )
            }
          }

          Spacer(modifier = Modifier.width(16.dp))

          // Detail Video: Judul Nama File, Durasi & Ukuran
          Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.Center
          ) {
            Text(
              text = selectedVideo.fileName,
              style = MaterialTheme.typography.titleMedium,
              color = Color.White,
              maxLines = 1,
              overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(6.dp))

            Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
              // Badge Durasi Bersih
              Box(
                modifier = Modifier
                  .clip(RoundedCornerShape(999.dp))
                  .background(g.accent.copy(alpha = 0.15f))
                  .border(1.dp, g.accent.copy(alpha = 0.35f), RoundedCornerShape(999.dp))
                  .padding(horizontal = 8.dp, vertical = 3.dp)
              ) {
                Text(
                  text = selectedVideo.formattedDuration,
                  fontSize = 11.sp,
                  style = MaterialTheme.typography.labelSmall,
                  color = g.accent
                )
              }

              // Badge Ukuran Bersih
              Box(
                modifier = Modifier
                  .clip(RoundedCornerShape(999.dp))
                  .background(Color.White.copy(alpha = 0.08f))
                  .border(1.dp, Color.White.copy(alpha = 0.18f), RoundedCornerShape(999.dp))
                  .padding(horizontal = 8.dp, vertical = 3.dp)
              ) {
                Text(
                  text = selectedVideo.formattedSize,
                  fontSize = 11.sp,
                  style = MaterialTheme.typography.labelSmall,
                  color = Color(0xFFD1D5DB)
                )
              }
            }

            if (selectedVideo.needsSplitting) {
              Spacer(modifier = Modifier.height(8.dp))
              Text(
                text = "Dibagi ${selectedVideo.estimatedChunkCount} bagian otomatis (per 90s)",
                fontSize = 11.sp,
                color = Color(0xFFFDE68A)
              )
            }
          }
        }

        // Tombol Ganti Video
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.End
        ) {
          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(999.dp))
              .background(g.fillStrong, RoundedCornerShape(999.dp))
              .border(1.dp, g.border, RoundedCornerShape(999.dp))
              .pressable {
                mediaPicker.launch(
                  PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.VideoOnly)
                )
              }
              .padding(horizontal = 16.dp, vertical = 8.dp)
              .testTag("change_video_button"),
            contentAlignment = Alignment.Center
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(
                imageVector = Icons.Rounded.Autorenew,
                contentDescription = null,
                modifier = Modifier.size(15.dp),
                tint = g.text
              )
              Spacer(modifier = Modifier.width(6.dp))
              Text(
                text = "Ganti Video",
                style = MaterialTheme.typography.labelSmall,
                color = g.text
              )
            }
          }
        }
      }
    }
  }
}
