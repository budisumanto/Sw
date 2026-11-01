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
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.ChangeCircle
import androidx.compose.material.icons.filled.ContentCut
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.VideoFile
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilledTonalButton
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.VideoMetadata
import com.example.ui.theme.AmberWarning
import com.example.ui.theme.MintAccent

@Composable
fun VideoSelectorCard(
  selectedVideo: VideoMetadata?,
  isGeneratingSample: Boolean,
  onVideoSelected: (android.net.Uri) -> Unit,
  onLoadSample: (isLong: Boolean) -> Unit,
  modifier: Modifier = Modifier
) {
  val mediaPicker = rememberLauncherForActivityResult(
    contract = ActivityResultContracts.PickVisualMedia()
  ) { uri ->
    if (uri != null) {
      onVideoSelected(uri)
    }
  }

  LiquidGlassCard(
    modifier = modifier
      .fillMaxWidth()
      .testTag("video_selector_card")
  ) {
    if (selectedVideo == null) {
      // Empty State: Glass Dropzone
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(vertical = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
      ) {
        Box(
          modifier = Modifier
            .size(72.dp)
            .clip(CircleShape)
            .background(
              Brush.radialGradient(
                listOf(Color(0xFF10B981).copy(alpha = 0.3f), Color.Transparent)
              )
            )
            .border(
              androidx.compose.foundation.BorderStroke(
                1.5.dp,
                Brush.verticalGradient(
                  listOf(Color(0xFF34D399).copy(alpha = 0.5f), Color.White.copy(alpha = 0.1f))
                )
              ),
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
            contentDescription = "Pick Video",
            tint = MintAccent,
            modifier = Modifier.size(36.dp)
          )
        }

        Spacer(modifier = Modifier.height(14.dp))

        Text(
          text = "Select Video to Optimize",
          style = MaterialTheme.typography.titleMedium,
          fontWeight = FontWeight.Bold,
          color = Color.White
        )

        Text(
          text = "Liquid Engine auto-detects resolution & enforces CRF 20",
          style = MaterialTheme.typography.bodySmall,
          color = Color(0xFF94A3B8),
          modifier = Modifier.padding(top = 4.dp, bottom = 18.dp)
        )

        // Glass Action Button
        FilledTonalButton(
          onClick = {
            mediaPicker.launch(
              PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.VideoOnly)
            )
          },
          modifier = Modifier
            .fillMaxWidth()
            .height(52.dp)
            .testTag("pick_video_button"),
          shape = RoundedCornerShape(14.dp),
          colors = ButtonDefaults.filledTonalButtonColors(
            containerColor = MintAccent,
            contentColor = Color(0xFF003822)
          )
        ) {
          Icon(imageVector = Icons.Default.VideoFile, contentDescription = null)
          Spacer(modifier = Modifier.width(8.dp))
          Text("Choose from Gallery", fontWeight = FontWeight.Bold, fontSize = 15.sp)
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Demo sample shortcuts for quick emulator testing
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          OutlinedButton(
            onClick = { onLoadSample(false) },
            enabled = !isGeneratingSample,
            modifier = Modifier
              .weight(1f)
              .height(42.dp)
              .testTag("try_sample_15s_button"),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFA7F3D0))
          ) {
            if (isGeneratingSample) {
              CircularProgressIndicator(
                modifier = Modifier.size(16.dp),
                strokeWidth = 2.dp,
                color = MintAccent
              )
            } else {
              Icon(imageVector = Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(14.dp))
              Spacer(modifier = Modifier.width(4.dp))
              Text("15s Demo", fontSize = 12.sp)
            }
          }

          OutlinedButton(
            onClick = { onLoadSample(true) },
            enabled = !isGeneratingSample,
            modifier = Modifier
              .weight(1f)
              .height(42.dp)
              .testTag("try_sample_100s_button"),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = AmberWarning)
          ) {
            Icon(imageVector = Icons.Default.ContentCut, contentDescription = null, modifier = Modifier.size(14.dp))
            Spacer(modifier = Modifier.width(4.dp))
            Text("100s Split Demo", fontSize = 12.sp)
          }
        }
      }
    } else {
      // Video Selected Glass View
      Column(modifier = Modifier.fillMaxWidth()) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          verticalAlignment = Alignment.CenterVertically
        ) {
          // Thumbnail Box
          Box(
            modifier = Modifier
              .size(width = 84.dp, height = 100.dp)
              .clip(RoundedCornerShape(16.dp))
              .background(Color.Black.copy(alpha = 0.4f))
              .border(
                androidx.compose.foundation.BorderStroke(
                  1.dp,
                  Color.White.copy(alpha = 0.15f)
                ),
                RoundedCornerShape(16.dp)
              ),
            contentAlignment = Alignment.Center
          ) {
            if (selectedVideo.thumbnailBitmap != null) {
              Image(
                bitmap = selectedVideo.thumbnailBitmap.asImageBitmap(),
                contentDescription = "Video Thumbnail",
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

            // Duration Pill overlay
            Box(
              modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(4.dp)
                .clip(RoundedCornerShape(6.dp))
                .background(Color.Black.copy(alpha = 0.75f))
                .padding(horizontal = 4.dp, vertical = 2.dp)
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

          // Video Specs
          Column(modifier = Modifier.weight(1f)) {
            Text(
              text = selectedVideo.fileName,
              style = MaterialTheme.typography.titleMedium,
              fontWeight = FontWeight.Bold,
              color = Color.White,
              maxLines = 1
            )

            Spacer(modifier = Modifier.height(6.dp))

            // Metadata Chips
            Row(
              horizontalArrangement = Arrangement.spacedBy(6.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              GlassPill(text = selectedVideo.resolutionText)
              GlassPill(text = selectedVideo.formattedSize)
              GlassPill(text = "${selectedVideo.fps.toInt()} fps")
            }

            Spacer(modifier = Modifier.height(8.dp))

            if (selectedVideo.needsSplitting) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                  imageVector = Icons.Default.ContentCut,
                  contentDescription = null,
                  tint = AmberWarning,
                  modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                  text = "Exceeds 90s: Auto-splits into ${selectedVideo.estimatedChunkCount} parts",
                  fontSize = 11.sp,
                  fontWeight = FontWeight.SemiBold,
                  color = AmberWarning
                )
              }
            } else {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                  modifier = Modifier
                    .size(6.dp)
                    .clip(CircleShape)
                    .background(MintAccent)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                  text = "Single Status Clip (<= 90s)",
                  fontSize = 11.sp,
                  fontWeight = FontWeight.Medium,
                  color = Color(0xFFA7F3D0)
                )
              }
            }
          }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Change Video Bar
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
            shape = RoundedCornerShape(10.dp),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 12.dp, vertical = 6.dp)
          ) {
            Icon(imageVector = Icons.Default.ChangeCircle, contentDescription = null, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text("Change Video", fontSize = 12.sp)
          }
        }
      }
    }
  }
}
