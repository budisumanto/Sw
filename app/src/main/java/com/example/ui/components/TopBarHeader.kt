package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.VideoLibrary
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.MintAccent

@Composable
fun TopBarHeader(
  modifier: Modifier = Modifier
) {
  var showInfoDialog by remember { mutableStateOf(false) }

  Row(
    modifier = modifier
      .fillMaxWidth()
      .padding(horizontal = 4.dp, vertical = 8.dp),
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.SpaceBetween
  ) {
    Row(verticalAlignment = Alignment.CenterVertically) {
      Box(
        modifier = Modifier
          .size(44.dp)
          .clip(RoundedCornerShape(14.dp))
          .background(Color(0xFF10B981).copy(alpha = 0.2f))
          .border(
            androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF34D399).copy(alpha = 0.4f)),
            RoundedCornerShape(14.dp)
          ),
        contentAlignment = Alignment.Center
      ) {
        Icon(
          imageVector = Icons.Default.VideoLibrary,
          contentDescription = "StatusHD Logo",
          tint = MintAccent,
          modifier = Modifier.size(24.dp)
        )
      }

      Spacer(modifier = Modifier.width(12.dp))

      Column {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Text(
            text = "StatusHD",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Black,
            color = Color.White
          )
          Spacer(modifier = Modifier.width(8.dp))
          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(6.dp))
              .background(Color(0xFF064E3B).copy(alpha = 0.8f))
              .padding(horizontal = 6.dp, vertical = 2.dp)
          ) {
            Text(
              text = "LIQUID GLASS",
              fontSize = 9.sp,
              fontWeight = FontWeight.Black,
              color = MintAccent
            )
          }
        }

        Text(
          text = "Offline Smart WhatsApp Status Optimizer",
          style = MaterialTheme.typography.labelSmall,
          color = Color(0xFF94A3B8)
        )
      }
    }

    Row(verticalAlignment = Alignment.CenterVertically) {
      Box(
        modifier = Modifier
          .clip(CircleShape)
          .background(Color.White.copy(alpha = 0.06f))
          .border(
            androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.1f)),
            CircleShape
          )
          .padding(horizontal = 8.dp, vertical = 4.dp)
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Box(
            modifier = Modifier
              .size(6.dp)
              .clip(CircleShape)
              .background(MintAccent)
          )
          Spacer(modifier = Modifier.width(4.dp))
          Text(
            text = "Offline",
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium,
            color = Color(0xFFA7F3D0)
          )
        }
      }

      IconButton(
        onClick = { showInfoDialog = true },
        modifier = Modifier.testTag("app_info_button")
      ) {
        Icon(
          imageVector = Icons.Default.Info,
          contentDescription = "Engineering Info",
          tint = Color(0xFF94A3B8)
        )
      }
    }
  }

  if (showInfoDialog) {
    AlertDialog(
      onDismissRequest = { showInfoDialog = false },
      title = {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(
            imageVector = Icons.Default.ElectricBolt,
            contentDescription = null,
            tint = MintAccent
          )
          Spacer(modifier = Modifier.width(8.dp))
          Text(
            text = "Smart Liquid Engine Specs",
            fontWeight = FontWeight.Bold,
            color = Color.White
          )
        }
      },
      text = {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
          Text(
            text = "StatusHD automatically optimizes video for WhatsApp Status:",
            style = MaterialTheme.typography.bodyMedium,
            color = Color(0xFFE2E8F0)
          )
          Text(
            text = "• Auto Resolution: Analyzes input resolution, preserves 720p/1080p, and scales down 4K.\n" +
              "• Quality: Fixed master CRF 20 for crystal-clear playback.\n" +
              "• Bitrate Limit: Capped at 4000k with anti-bloat protection.\n" +
              "• WA Bypass: Enforces FastStart atom, strict yuv420p color space, and fixed GOP (-g 30).\n" +
              "• Auto-Split: Automatically divides videos >90 seconds into sequential Status-ready parts.",
            style = MaterialTheme.typography.bodySmall,
            color = Color(0xFF94A3B8)
          )
        }
      },
      confirmButton = {
        TextButton(onClick = { showInfoDialog = false }) {
          Text("Got it", color = MintAccent, fontWeight = FontWeight.Bold)
        }
      },
      containerColor = DarkSurface,
      shape = RoundedCornerShape(20.dp)
    )
  }
}
