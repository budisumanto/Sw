package com.example.ui.components

import androidx.compose.foundation.background
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
import com.example.ui.theme.DarkBorder
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
      .padding(horizontal = 16.dp, vertical = 12.dp),
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.SpaceBetween
  ) {
    Row(verticalAlignment = Alignment.CenterVertically) {
      Box(
        modifier = Modifier
          .size(42.dp)
          .clip(RoundedCornerShape(12.dp))
          .background(Color(0xFF0C2B24)),
        contentAlignment = Alignment.Center
      ) {
        Icon(
          imageVector = Icons.Default.VideoLibrary,
          contentDescription = "StatusHD Icon",
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
          Spacer(modifier = Modifier.width(6.dp))
          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(6.dp))
              .background(Color(0xFF064E3B))
              .padding(horizontal = 6.dp, vertical = 2.dp)
          ) {
            Text(
              text = "BYPASS",
              fontSize = 10.sp,
              fontWeight = FontWeight.ExtraBold,
              color = MintAccent
            )
          }
        }

        Text(
          text = "WhatsApp Status HD Video Optimizer",
          style = MaterialTheme.typography.labelSmall,
          color = Color(0xFF94A3B8)
        )
      }
    }

    Row(verticalAlignment = Alignment.CenterVertically) {
      Box(
        modifier = Modifier
          .clip(CircleShape)
          .background(Color(0xFF13221C))
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
            text = "How StatusHD Bypasses Blur",
            fontWeight = FontWeight.Bold,
            color = Color.White
          )
        }
      },
      text = {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
          Text(
            text = "WhatsApp compresses uploaded videos unless they match precise structural standards:",
            style = MaterialTheme.typography.bodyMedium,
            color = Color(0xFFE2E8F0)
          )
          Text(
            text = "1. Container & Atom: FastStart MOOV atom (-movflags +faststart) placed at the file head for instant parsing.\n" +
              "2. Color Space: Strictly enforced 8-bit yuv420p.\n" +
              "3. GOP Structure: Fixed keyframe interval (-g 30) ensures 1 full I-frame per second, stopping pixelation.\n" +
              "4. Bitrate & Buffer: Capped to 2.2M-2.8M with 2x bufsize to prevent server-side downsampling.\n" +
              "5. Auto-Splitting: Automatically slices videos longer than 90 seconds into sequential Status-ready segments.",
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
      shape = RoundedCornerShape(16.dp)
    )
  }
}
