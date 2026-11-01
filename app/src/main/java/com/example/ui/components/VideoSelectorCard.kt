package com.example.ui.components

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.material.icons.filled.Autorenew
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.VideoMetadata
import com.example.ui.theme.ElectricMint
import com.example.ui.theme.ElectricMintDark
import com.example.ui.theme.IOSTextPrimary
import com.example.ui.theme.IOSTextSecondary

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

  IOSLiquidGlassCard(
    modifier = modifier
      .fillMaxWidth()
      .testTag("video_selector_card"),
    cornerRadius = 28.dp,
    padding = 22.dp
  ) {
    if (selectedVideo == null) {
      // State 1: Pemilihan Video (Idle) - Hero Glass Card
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(vertical = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
      ) {
        // Floating Video Icon with Soft Radiant Glow
        Box(
          modifier = Modifier
            .size(80.dp)
            .clip(CircleShape)
            .background(
              Brush.radialGradient(
                listOf(
                  ElectricMint.copy(alpha = 0.22f),
                  Color(0xFF00F2FE).copy(alpha = 0.08f),
                  Color.Transparent
                )
              )
            )
            .border(
              BorderStroke(1.dp, IOSGlassHighlightTop),
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
            tint = ElectricMint,
            modifier = Modifier.size(38.dp)
          )
        }

        Spacer(modifier = Modifier.height(18.dp))

        Text(
          text = "Pilih Video",
          fontSize = 20.sp,
          fontWeight = FontWeight.Bold,
          color = IOSTextPrimary,
          letterSpacing = (-0.3).sp
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
          text = "Format dan ukuran disesuaikan otomatis agar tidak buram.",
          fontSize = 13.sp,
          color = IOSTextSecondary,
          textAlign = TextAlign.Center,
          lineHeight = 19.sp,
          modifier = Modifier
            .padding(horizontal = 16.dp)
            .padding(bottom = 22.dp)
        )

        // Tombol aksi iOS Kaca Berpendar
        Button(
          onClick = {
            mediaPicker.launch(
              PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.VideoOnly)
            )
          },
          modifier = Modifier
            .fillMaxWidth()
            .height(54.dp)
            .testTag("pick_video_button"),
          shape = RoundedCornerShape(20.dp),
          colors = ButtonDefaults.buttonColors(
            containerColor = Color.Transparent,
            contentColor = Color(0xFF021E14)
          ),
          border = BorderStroke(1.dp, IOSGlassHighlightTop)
        ) {
          Box(
            modifier = Modifier
              .fillMaxSize()
              .background(
                Brush.horizontalGradient(
                  listOf(ElectricMint, ElectricMintDark)
                ),
                shape = RoundedCornerShape(20.dp)
              ),
            contentAlignment = Alignment.Center
          ) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.Center
            ) {
              Icon(
                imageVector = Icons.Default.PhotoLibrary,
                contentDescription = null,
                tint = Color(0xFF022115),
                modifier = Modifier.size(19.dp)
              )
              Spacer(modifier = Modifier.width(8.dp))
              Text(
                text = "Buka Galeri",
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                color = Color(0xFF022115)
              )
            }
          }
        }
      }
    } else {
      // State 2: Video Terpilih & Siap Dioptimalkan (Selected)
      Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
      ) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          verticalAlignment = Alignment.CenterVertically
        ) {
          // Thumbnail video melengkung lembut
          Box(
            modifier = Modifier
              .size(width = 78.dp, height = 78.dp)
              .clip(RoundedCornerShape(18.dp))
              .background(Color.Black.copy(alpha = 0.5f))
              .border(
                BorderStroke(1.dp, IOSGlassHighlightTop),
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
                tint = ElectricMint,
                modifier = Modifier.size(32.dp)
              )
            }

            // Duration Pill in bottom right
            Box(
              modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(4.dp)
                .clip(RoundedCornerShape(6.dp))
                .background(Color.Black.copy(alpha = 0.8f))
                .padding(horizontal = 5.dp, vertical = 2.dp)
            ) {
              Text(
                text = selectedVideo.formattedDuration,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
              )
            }
          }

          Spacer(modifier = Modifier.width(14.dp))

          // Video Information
          Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.Center
          ) {
            Text(
              text = selectedVideo.fileName,
              fontSize = 15.sp,
              fontWeight = FontWeight.Bold,
              color = IOSTextPrimary,
              maxLines = 1,
              overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
              text = "${selectedVideo.formattedDuration} • ${selectedVideo.formattedSize}",
              fontSize = 13.sp,
              color = ElectricMint,
              fontWeight = FontWeight.SemiBold
            )

            if (selectedVideo.needsSplitting) {
              Spacer(modifier = Modifier.height(6.dp))
              Box(
                modifier = Modifier
                  .clip(RoundedCornerShape(8.dp))
                  .background(Color(0xFFFEF3C7).copy(alpha = 0.12f))
                  .border(
                    BorderStroke(1.dp, Color(0xFFFDE68A).copy(alpha = 0.35f)),
                    RoundedCornerShape(8.dp)
                  )
                  .padding(horizontal = 8.dp, vertical = 3.dp)
              ) {
                Text(
                  text = "Dibagi ${selectedVideo.estimatedChunkCount} bagian otomatis (per 90s)",
                  fontSize = 11.sp,
                  color = Color(0xFFFDE68A),
                  fontWeight = FontWeight.Medium
                )
              }
            }
          }
        }

        // Tombol teks kaca halus di kanan: "Ganti Video"
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.End
        ) {
          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(14.dp))
              .background(Color(0xFFFFFFFF).copy(alpha = 0.08f))
              .border(
                BorderStroke(1.dp, IOSGlassHighlightTop),
                RoundedCornerShape(14.dp)
              )
              .clickable {
                mediaPicker.launch(
                  PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.VideoOnly)
                )
              }
              .padding(horizontal = 14.dp, vertical = 8.dp)
              .testTag("change_video_button"),
            contentAlignment = Alignment.Center
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(
                imageVector = Icons.Default.Autorenew,
                contentDescription = null,
                modifier = Modifier.size(15.dp),
                tint = IOSTextPrimary
              )
              Spacer(modifier = Modifier.width(6.dp))
              Text(
                text = "Ganti Video",
                fontSize = 13.sp,
                color = IOSTextPrimary,
                fontWeight = FontWeight.SemiBold
              )
            }
          }
        }
      }
    }
  }
}
