package com.example.ui.components

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
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.OptimizationResult
import com.example.model.VideoSegment
import com.example.ui.theme.MintAccent

@Composable
fun ResultDisplayCard(
  result: OptimizationResult,
  onShareToWhatsApp: (VideoSegment) -> Unit,
  onShareAll: (List<VideoSegment>) -> Unit,
  onSaveToGallery: (VideoSegment) -> Unit,
  onSaveAllToGallery: (List<VideoSegment>) -> Unit,
  onPreviewSegment: (VideoSegment) -> Unit,
  onReset: () -> Unit,
  modifier: Modifier = Modifier
) {
  LiquidGlassBox(
    modifier = modifier
      .fillMaxWidth()
      .testTag("result_display_card")
  ) {
    Column(
      modifier = Modifier.fillMaxWidth(),
      verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
      // Header: Video Siap Dibagikan!
      Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
      ) {
        Box(
          modifier = Modifier
            .size(42.dp)
            .clip(CircleShape)
            .background(MintAccent),
          contentAlignment = Alignment.Center
        ) {
          Icon(
            imageVector = Icons.Default.Check,
            contentDescription = null,
            tint = Color(0xFF003822),
            modifier = Modifier.size(24.dp)
          )
        }

        Spacer(modifier = Modifier.width(14.dp))

        Column {
          Text(
            text = "Video Siap Dibagikan!",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = Color.White
          )
          Text(
            text = "Kualitas dioptimalkan tanpa kompresi buram",
            fontSize = 12.sp,
            color = Color(0xFFA7F3D0)
          )
        }
      }

      // Ringkasan Perbandingan Ukuran Simpel
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(18.dp))
          .background(Color.White.copy(alpha = 0.05f))
          .border(BorderStroke(1.dp, GlassBorderTopHighlight), RoundedCornerShape(18.dp))
          .padding(16.dp)
      ) {
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Column {
              Text(text = "Ukuran Asli", fontSize = 11.sp, color = Color(0xFF94A3B8))
              Text(
                text = result.formattedOriginalSize,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
              )
            }

            Text(text = "➔", fontSize = 16.sp, color = MintAccent, fontWeight = FontWeight.Bold)

            Column(horizontalAlignment = Alignment.End) {
              Text(text = "Ukuran Optimal", fontSize = 11.sp, color = Color(0xFF94A3B8))
              Text(
                text = result.formattedOptimizedSize,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = MintAccent
              )
            }
          }

          if (result.savedPercentage > 0) {
            Spacer(modifier = Modifier.height(4.dp))
            Text(
              text = "Hemat ${result.savedPercentage}% tanpa turun kualitas",
              fontSize = 12.sp,
              color = Color(0xFFA7F3D0),
              fontWeight = FontWeight.SemiBold
            )
          }
        }
      }

      // Daftar Klip (Jika Video Terbagi > 90 detik)
      if (result.segments.size > 1) {
        Text(
          text = "Bagian Video (${result.segments.size} Klip):",
          fontSize = 13.sp,
          fontWeight = FontWeight.SemiBold,
          color = Color.White
        )

        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
          result.segments.forEach { segment ->
            LiquidSegmentRow(
              segment = segment,
              onShare = { onShareToWhatsApp(segment) },
              onSave = { onSaveToGallery(segment) },
              onPreview = { onPreviewSegment(segment) }
            )
          }
        }
      }

      // Tombol Utama: Kirim ke Status WhatsApp
      Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Button(
          onClick = {
            if (result.segments.size == 1) {
              onShareToWhatsApp(result.segments.first())
            } else {
              onShareAll(result.segments)
            }
          },
          modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .testTag("share_whatsapp_status_button"),
          shape = RoundedCornerShape(20.dp),
          colors = ButtonDefaults.buttonColors(
            containerColor = MintAccent,
            contentColor = Color(0xFF003822)
          )
        ) {
          Icon(
            imageVector = Icons.Default.Share,
            contentDescription = null,
            modifier = Modifier.size(20.dp)
          )
          Spacer(modifier = Modifier.width(8.dp))
          Text(
            text = if (result.segments.size == 1) "Kirim ke Status WhatsApp" else "Kirim Semua (${result.segments.size} Bagian) ke Status",
            fontWeight = FontWeight.Bold,
            fontSize = 15.sp
          )
        }

        // Tombol Kedua: Simpan ke Galeri
        OutlinedButton(
          onClick = { onSaveAllToGallery(result.segments) },
          modifier = Modifier
            .fillMaxWidth()
            .height(50.dp)
            .testTag("save_to_gallery_button"),
          shape = RoundedCornerShape(18.dp),
          border = BorderStroke(1.dp, GlassBorderTopHighlight)
        ) {
          Icon(
            imageVector = Icons.Default.Download,
            contentDescription = null,
            modifier = Modifier.size(18.dp),
            tint = Color.White
          )
          Spacer(modifier = Modifier.width(8.dp))
          Text(
            text = "Simpan ke Galeri",
            fontWeight = FontWeight.SemiBold,
            color = Color.White,
            fontSize = 14.sp
          )
        }

        // Teks kecil: Proses video lainnya
        Box(
          modifier = Modifier.fillMaxWidth(),
          contentAlignment = Alignment.Center
        ) {
          TextButton(onClick = onReset) {
            Text(
              text = "Proses video lainnya",
              color = Color(0xFF94A3B8),
              fontSize = 13.sp,
              fontWeight = FontWeight.Medium
            )
          }
        }
      }
    }
  }
}

@Composable
fun LiquidSegmentRow(
  segment: VideoSegment,
  onShare: () -> Unit,
  onSave: () -> Unit,
  onPreview: () -> Unit
) {
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(16.dp))
      .background(Color.White.copy(alpha = 0.05f))
      .border(BorderStroke(1.dp, GlassBorderTopHighlight), RoundedCornerShape(16.dp))
      .padding(10.dp),
    verticalAlignment = Alignment.CenterVertically
  ) {
    // Thumbnail preview
    Box(
      modifier = Modifier
        .size(width = 54.dp, height = 54.dp)
        .clip(RoundedCornerShape(12.dp))
        .background(Color.Black.copy(alpha = 0.5f))
        .clickable(onClick = onPreview),
      contentAlignment = Alignment.Center
    ) {
      if (segment.thumbnailBitmap != null) {
        Image(
          bitmap = segment.thumbnailBitmap.asImageBitmap(),
          contentDescription = "Pratinjau Bagian",
          modifier = Modifier.fillMaxSize(),
          contentScale = ContentScale.Crop
        )
      } else {
        Icon(
          imageVector = Icons.Default.Movie,
          contentDescription = null,
          tint = MintAccent,
          modifier = Modifier.size(24.dp)
        )
      }

      Box(
        modifier = Modifier
          .size(22.dp)
          .clip(CircleShape)
          .background(Color.Black.copy(alpha = 0.65f)),
        contentAlignment = Alignment.Center
      ) {
        Icon(
          imageVector = Icons.Default.PlayArrow,
          contentDescription = "Putar",
          tint = Color.White,
          modifier = Modifier.size(14.dp)
        )
      }
    }

    Spacer(modifier = Modifier.width(12.dp))

    Column(modifier = Modifier.weight(1f)) {
      Text(
        text = "Bagian ${segment.partIndex} dari ${segment.totalParts}",
        fontWeight = FontWeight.Bold,
        fontSize = 13.sp,
        color = Color.White
      )
      Text(
        text = "${segment.formattedDuration} • ${segment.formattedSize}",
        fontSize = 11.sp,
        color = Color(0xFF94A3B8)
      )
    }

    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
      IconButton(
        onClick = onSave,
        modifier = Modifier.size(36.dp)
      ) {
        Icon(
          imageVector = Icons.Default.Save,
          contentDescription = "Simpan",
          tint = Color(0xFF94A3B8),
          modifier = Modifier.size(18.dp)
        )
      }

      IconButton(
        onClick = onShare,
        modifier = Modifier.size(36.dp)
      ) {
        Icon(
          imageVector = Icons.Default.Share,
          contentDescription = "Bagikan",
          tint = MintAccent,
          modifier = Modifier.size(18.dp)
        )
      }
    }
  }
}
