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
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.ContentCut
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.SmartEncodingConfig
import com.example.ui.theme.MintAccent

@Composable
fun SmartEngineGlassCard(
  config: SmartEncodingConfig,
  autoSplitEnabled: Boolean,
  onAutoSplitToggled: (Boolean) -> Unit,
  modifier: Modifier = Modifier
) {
  LiquidGlassCard(
    modifier = modifier
      .fillMaxWidth()
      .testTag("smart_engine_glass_card")
  ) {
    Column(
      modifier = Modifier.fillMaxWidth(),
      verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
      // Header: Smart Auto-Engine
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Box(
            modifier = Modifier
              .size(32.dp)
              .clip(CircleShape)
              .background(Color(0xFF10B981).copy(alpha = 0.2f)),
            contentAlignment = Alignment.Center
          ) {
            Icon(
              imageVector = Icons.Default.AutoAwesome,
              contentDescription = null,
              tint = MintAccent,
              modifier = Modifier.size(18.dp)
            )
          }

          Spacer(modifier = Modifier.width(10.dp))

          Column {
            Text(
              text = "Smart Auto-Engine",
              style = MaterialTheme.typography.titleMedium,
              fontWeight = FontWeight.Bold,
              color = Color.White
            )
            Text(
              text = "Auto resolution • CRF 20 • 4000k limit",
              style = MaterialTheme.typography.labelSmall,
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
            Box(
              modifier = Modifier
                .size(6.dp)
                .clip(CircleShape)
                .background(MintAccent)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
              text = "BYPASS READY",
              fontSize = 9.sp,
              fontWeight = FontWeight.Black,
              color = MintAccent
            )
          }
        }
      }

      // Smart Parameter Matrix (Liquid Glass Tiles)
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        SmartTile(
          label = "RESOLUTION",
          value = config.targetResolutionLabel,
          subValue = "Auto Detected",
          modifier = Modifier.weight(1f)
        )
        SmartTile(
          label = "QUALITY",
          value = "CRF ${config.crf}",
          subValue = "Master Clarity",
          modifier = Modifier.weight(1f)
        )
      }

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        SmartTile(
          label = "BITRATE CAP",
          value = config.maxrate,
          subValue = "Anti-Bloat Guard",
          modifier = Modifier.weight(1f)
        )
        SmartTile(
          label = "GOP STRUCTURE",
          value = "-g ${config.gop}",
          subValue = "1s Keyframes",
          modifier = Modifier.weight(1f)
        )
      }

      // Auto-Split Toggle Row (Liquid glass style)
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(16.dp))
          .background(Color.White.copy(alpha = 0.04f))
          .padding(horizontal = 12.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
      ) {
        Row(
          modifier = Modifier.weight(1f),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Icon(
            imageVector = Icons.Default.ContentCut,
            contentDescription = null,
            tint = MintAccent,
            modifier = Modifier.size(18.dp)
          )
          Spacer(modifier = Modifier.width(10.dp))
          Column {
            Text(
              text = "Auto-Split Videos > 90s",
              fontSize = 13.sp,
              fontWeight = FontWeight.SemiBold,
              color = Color.White
            )
            Text(
              text = "Segments long videos into 90s Status clips",
              fontSize = 11.sp,
              color = Color(0xFF94A3B8)
            )
          }
        }

        Switch(
          checked = autoSplitEnabled,
          onCheckedChange = onAutoSplitToggled,
          modifier = Modifier.testTag("smart_auto_split_switch"),
          colors = SwitchDefaults.colors(
            checkedThumbColor = Color(0xFF003822),
            checkedTrackColor = MintAccent,
            uncheckedThumbColor = Color(0xFF94A3B8),
            uncheckedTrackColor = Color.White.copy(alpha = 0.1f)
          )
        )
      }
    }
  }
}

@Composable
fun SmartTile(
  label: String,
  value: String,
  subValue: String,
  modifier: Modifier = Modifier
) {
  Box(
    modifier = modifier
      .clip(RoundedCornerShape(14.dp))
      .background(Color.White.copy(alpha = 0.05f))
      .padding(10.dp)
  ) {
    Column {
      Text(
        text = label,
        fontSize = 9.sp,
        fontWeight = FontWeight.Bold,
        color = Color(0xFF94A3B8),
        letterSpacing = 0.5.sp
      )
      Text(
        text = value,
        fontSize = 13.sp,
        fontWeight = FontWeight.Bold,
        color = Color.White,
        maxLines = 1
      )
      Text(
        text = subValue,
        fontSize = 10.sp,
        color = Color(0xFF34D399)
      )
    }
  }
}
