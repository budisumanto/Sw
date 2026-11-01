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
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import com.example.model.OptimizationResult
import com.example.model.VideoSegment
import com.example.ui.theme.ElectricMint
import com.example.ui.theme.ElectricMintDark
import com.example.ui.theme.IOSTextPrimary
import com.example.ui.theme.IOSTextSecondary

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
  IOSLiquidGlassCard(
    modifier = modifier
      .fillMaxWidth()
      .testTag("result_display_card"),
    cornerRadius = 28.dp,
    padding = 22.dp
  ) {
    Column(
      modifier = Modifier.fillMaxWidth(),
      verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
      // Header: Ikon centang berpendar hijau segar + Judul
      Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
      ) {
        Box(
          modifier = Modifier
            .size(46.dp)
            .clip(CircleShape)
            .background(
              Brush.radialGradient(
                listOf(ElectricMint, ElectricMintDark)
              )
            ),
          contentAlignment = Alignment.Center
        ) {
          Icon(
            imageVector = Icons.Default.Check,
            contentDescription = null,
            tint = Color(0xFF022115),
            modifier = Modifier.size(26.dp)
          )
        }

        Spacer(modifier = Modifier.width(14.dp))

        Column {
          Text(
            text = "Video Siap Dibagikan!",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = IOSTextPrimary,
            letterSpacing = (-0.3).sp
          )
          Text(
            text = "Kualitas dioptimalkan tanpa kompresi buram",
            fontSize = 12.sp,
            color = ElectricMint
          )
        }
      }

      // Kartu perbandingan simpel: 48 MB ➔ 14 MB (Hemat 70% • Kualitas HD)
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(18.dp))
          .background(Color(0xFFFFFFFF).copy(alpha = 0.05f))
          .border(BorderStroke(1.dp, IOSGlassHighlightTop), RoundedCornerShape(18.dp))
          .padding(16.dp)
      ) {
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Column {
              Text(text = "Ukuran Asli", fontSize = 11.sp, color = IOSTextSecondary)
              Text(
                text = result.formattedOriginalSize,
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                color = IOSTextPrimary
              )
            }

            Text(
              text = "➔",
              fontSize = 18.sp,
              color = ElectricMint,
              fontWeight = FontWeight.Bold
            )

            Column(horizontalAlignment = Alignment.End) {
              Text(text = "Ukuran Optimal", fontSize = 11.sp, color = IOSTextSecondary)
              Text(
                text = result.formattedOptimizedSize,
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                color = ElectricMint
              )
            }
          }

          val savingsText = if (result.savedPercentage > 0) {
            "Hemat ${result.savedPercentage}% • Kualitas HD"
          } else {
            "Kualitas Jernih Maksimal • Format HD"
          }

          Text(
            text = savingsText,
            fontSize = 12.sp,
            color = ElectricMint,
            fontWeight = FontWeight.SemiBold
          )
        }
      }

      // Daftar Bagian Video (Jika lebih dari 90 detik)
      if (result.segments.size > 1) {
        Text(
          text = "Bagian Video (${result.segments.size} Klip):",
          fontSize = 13.sp,
          fontWeight = FontWeight.SemiBold,
          color = IOSTextPrimary
        )

        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
          result.segments.forEach { segment ->
            IOSLiquidSegmentRow(
              segment = segment,
              onShare = { onShareToWhatsApp(segment) },
              onSave = { onSaveToGallery(segment) },
              onPreview = { onPreviewSegment(segment) }
            )
          }
        }
      }

      // Dua Tombol Aksi Bersih
      Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        // 1. Bagikan ke WhatsApp Status (Tombol utama paling terang)
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
                imageVector = Icons.Default.Share,
                contentDescription = null,
                tint = Color(0xFF022115),
                modifier = Modifier.size(20.dp)
              )
              Spacer(modifier = Modifier.width(8.dp))
              Text(
                text = if (result.segments.size == 1) "Bagikan ke WhatsApp Status" else "Bagikan Semua (${result.segments.size} Bagian)",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = Color(0xFF022115)
              )
            }
          }
        }

        // 2. Simpan ke Galeri (Tombol sekunder bergaya kaca transparan)
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .height(52.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(Color(0xFFFFFFFF).copy(alpha = 0.08f))
            .border(BorderStroke(1.dp, IOSGlassHighlightTop), RoundedCornerShape(20.dp))
            .clickable { onSaveAllToGallery(result.segments) }
            .testTag("save_to_gallery_button"),
          contentAlignment = Alignment.Center
        ) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
          ) {
            Icon(
              imageVector = Icons.Default.Download,
              contentDescription = null,
              modifier = Modifier.size(18.dp),
              tint = IOSTextPrimary
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
              text = "Simpan ke Galeri",
              fontWeight = FontWeight.SemiBold,
              color = IOSTextPrimary,
              fontSize = 15.sp
            )
          }
        }

        // 3. Tombol tautan bawah: "Pilih video lain"
        Box(
          modifier = Modifier.fillMaxWidth(),
          contentAlignment = Alignment.Center
        ) {
          TextButton(onClick = onReset) {
            Text(
              text = "Pilih video lain",
              color = IOSTextSecondary,
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
fun IOSLiquidSegmentRow(
  segment: VideoSegment,
  onShare: () -> Unit,
  onSave: () -> Unit,
  onPreview: () -> Unit
) {
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(16.dp))
      .background(Color(0xFFFFFFFF).copy(alpha = 0.05f))
      .border(BorderStroke(1.dp, IOSGlassHighlightTop), RoundedCornerShape(16.dp))
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
          tint = ElectricMint,
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
        color = IOSTextPrimary
      )
      Text(
        text = "${segment.formattedDuration} • ${segment.formattedSize}",
        fontSize = 11.sp,
        color = IOSTextSecondary
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
          tint = IOSTextSecondary,
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
          tint = ElectricMint,
          modifier = Modifier.size(18.dp)
        )
      }
    }
  }
}
