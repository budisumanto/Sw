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
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
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
  modifier: Modifier = Modifier
) {
  LiquidGlassCard(
    modifier = modifier
      .fillMaxWidth()
      .testTag("result_display_card")
  ) {
    Column(
      modifier = Modifier.fillMaxWidth(),
      verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
      // Header: Success & Bypass Verified
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Box(
            modifier = Modifier
              .size(38.dp)
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

          Spacer(modifier = Modifier.width(10.dp))

          Column {
            Text(
              text = "Optimization Complete",
              style = MaterialTheme.typography.titleMedium,
              fontWeight = FontWeight.Bold,
              color = Color.White
            )
            Text(
              text = "CRF 20 • ${result.smartConfig.targetResolutionLabel}",
              fontSize = 11.sp,
              color = Color(0xFFA7F3D0)
            )
          }
        }

        Box(
          modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(Color(0xFF064E3B).copy(alpha = 0.8f))
            .padding(horizontal = 8.dp, vertical = 4.dp)
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
              imageVector = Icons.Default.Verified,
              contentDescription = null,
              tint = MintAccent,
              modifier = Modifier.size(14.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
              text = "BYPASS VERIFIED",
              fontSize = 10.sp,
              fontWeight = FontWeight.ExtraBold,
              color = MintAccent
            )
          }
        }
      }

      // Comparison Metrics Bar: Original vs Final Size
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(16.dp))
          .background(Color.White.copy(alpha = 0.05f))
          .padding(14.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column {
          Text(text = "Original Size", fontSize = 11.sp, color = Color(0xFF94A3B8))
          Text(
            text = result.formattedOriginalSize,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFFCBD5E1)
          )
        }

        Box(
          modifier = Modifier
            .clip(RoundedCornerShape(10.dp))
            .background(Color(0xFF0C2B24).copy(alpha = 0.9f))
            .padding(horizontal = 12.dp, vertical = 6.dp)
        ) {
          Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
              text = "${result.segments.size} Part${if (result.segments.size > 1) "s" else ""}",
              fontSize = 10.sp,
              color = MintAccent,
              fontWeight = FontWeight.Bold
            )
            Text(
              text = if (result.savedPercentage > 0) "-${result.savedPercentage}%" else "HD Enhanced",
              fontSize = 12.sp,
              fontWeight = FontWeight.ExtraBold,
              color = Color.White
            )
          }
        }

        Column(horizontalAlignment = Alignment.End) {
          Text(text = "Optimized Size", fontSize = 11.sp, color = Color(0xFF94A3B8))
          Text(
            text = result.formattedOptimizedSize,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = MintAccent
          )
        }
      }

      // Segment List
      Text(
        text = "STATUS SEGMENTS",
        fontSize = 12.sp,
        fontWeight = FontWeight.Bold,
        color = Color(0xFF94A3B8),
        letterSpacing = 0.5.sp
      )

      Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        result.segments.forEach { segment ->
          LiquidSegmentItem(
            segment = segment,
            onShare = { onShareToWhatsApp(segment) },
            onSave = { onSaveToGallery(segment) },
            onPreview = { onPreviewSegment(segment) }
          )
        }
      }

      // Primary Action: Share directly to WhatsApp Status
      Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
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
            .height(54.dp)
            .testTag("share_whatsapp_status_button"),
          shape = RoundedCornerShape(16.dp),
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
            text = if (result.segments.size == 1) "Share to WhatsApp Status" else "Share All (${result.segments.size} Parts) to WhatsApp",
            fontWeight = FontWeight.Black,
            fontSize = 15.sp
          )
        }

        // Secondary: Save All to Gallery
        OutlinedButton(
          onClick = { onSaveAllToGallery(result.segments) },
          modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
            .testTag("save_to_gallery_button"),
          shape = RoundedCornerShape(14.dp),
          border = BorderStroke(1.dp, Color.White.copy(alpha = 0.15f))
        ) {
          Icon(
            imageVector = Icons.Default.Download,
            contentDescription = null,
            modifier = Modifier.size(18.dp)
          )
          Spacer(modifier = Modifier.width(8.dp))
          Text(
            text = "Save to Gallery (Movies/StatusHD)",
            fontWeight = FontWeight.SemiBold,
            color = Color.White
          )
        }
      }
    }
  }
}

@Composable
fun LiquidSegmentItem(
  segment: VideoSegment,
  onShare: () -> Unit,
  onSave: () -> Unit,
  onPreview: () -> Unit
) {
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(14.dp))
      .background(Color.White.copy(alpha = 0.05f))
      .border(BorderStroke(1.dp, Color.White.copy(alpha = 0.08f)), RoundedCornerShape(14.dp))
      .padding(10.dp),
    verticalAlignment = Alignment.CenterVertically
  ) {
    // Thumbnail preview with play icon
    Box(
      modifier = Modifier
        .size(width = 56.dp, height = 56.dp)
        .clip(RoundedCornerShape(10.dp))
        .background(Color.Black.copy(alpha = 0.5f))
        .clickable(onClick = onPreview),
      contentAlignment = Alignment.Center
    ) {
      if (segment.thumbnailBitmap != null) {
        Image(
          bitmap = segment.thumbnailBitmap.asImageBitmap(),
          contentDescription = "Segment Thumbnail",
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
          .size(24.dp)
          .clip(CircleShape)
          .background(Color.Black.copy(alpha = 0.65f)),
        contentAlignment = Alignment.Center
      ) {
        Icon(
          imageVector = Icons.Default.PlayArrow,
          contentDescription = "Preview",
          tint = Color.White,
          modifier = Modifier.size(16.dp)
        )
      }
    }

    Spacer(modifier = Modifier.width(12.dp))

    Column(modifier = Modifier.weight(1f)) {
      Text(
        text = "Part ${segment.partIndex} of ${segment.totalParts}",
        fontWeight = FontWeight.Bold,
        fontSize = 14.sp,
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
        modifier = Modifier
          .size(36.dp)
          .testTag("save_segment_${segment.partIndex}")
      ) {
        Icon(
          imageVector = Icons.Default.Save,
          contentDescription = "Save segment",
          tint = Color(0xFF94A3B8),
          modifier = Modifier.size(18.dp)
        )
      }

      IconButton(
        onClick = onShare,
        modifier = Modifier
          .size(36.dp)
          .testTag("share_segment_${segment.partIndex}")
      ) {
        Icon(
          imageVector = Icons.Default.Share,
          contentDescription = "Share segment",
          tint = MintAccent,
          modifier = Modifier.size(18.dp)
        )
      }
    }
  }
}
