package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ContentCut
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.HighQuality
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import com.example.model.ContentProfile
import com.example.model.ResolutionMode
import com.example.ui.theme.DarkBorder
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceElevated
import com.example.ui.theme.DarkSurfaceVariant
import com.example.ui.theme.MintAccent
import com.example.ui.theme.PurpleHighMotion

@Composable
fun CompressionControlsCard(
  selectedResolution: ResolutionMode,
  selectedProfile: ContentProfile,
  autoSplitEnabled: Boolean,
  onResolutionChanged: (ResolutionMode) -> Unit,
  onProfileChanged: (ContentProfile) -> Unit,
  onAutoSplitToggled: (Boolean) -> Unit,
  modifier: Modifier = Modifier
) {
  Card(
    modifier = modifier
      .fillMaxWidth()
      .testTag("compression_controls_card"),
    shape = RoundedCornerShape(20.dp),
    colors = CardDefaults.cardColors(containerColor = DarkSurface),
    border = BorderStroke(1.dp, DarkBorder)
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(16.dp),
      verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
      // Header
      Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
      ) {
        Icon(
          imageVector = Icons.Default.Tune,
          contentDescription = null,
          tint = MintAccent,
          modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
          text = "Dynamic Compression Controls",
          style = MaterialTheme.typography.titleMedium,
          fontWeight = FontWeight.Bold,
          color = Color.White
        )
      }

      // 1. Resolution Mode Toggle
      Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = "TARGET RESOLUTION",
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF94A3B8),
            letterSpacing = 0.5.sp
          )
          Text(
            text = selectedResolution.badge,
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            color = MintAccent
          )
        }

        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          ResolutionOptionTab(
            mode = ResolutionMode.HD_720P,
            isSelected = selectedResolution == ResolutionMode.HD_720P,
            onClick = { onResolutionChanged(ResolutionMode.HD_720P) },
            modifier = Modifier
              .weight(1f)
              .testTag("resolution_720p_tab")
          )

          ResolutionOptionTab(
            mode = ResolutionMode.CRISP_1080P,
            isSelected = selectedResolution == ResolutionMode.CRISP_1080P,
            onClick = { onResolutionChanged(ResolutionMode.CRISP_1080P) },
            modifier = Modifier
              .weight(1f)
              .testTag("resolution_1080p_tab")
          )
        }
      }

      // 2. Content Profile Toggle (Crucial)
      Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = "CONTENT PROFILE (CRITICAL)",
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF94A3B8),
            letterSpacing = 0.5.sp
          )
          Text(
            text = selectedProfile.badge,
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            color = if (selectedProfile == ContentProfile.HIGH_MOTION) PurpleHighMotion else MintAccent
          )
        }

        ProfileOptionCard(
          profile = ContentProfile.STANDARD,
          isSelected = selectedProfile == ContentProfile.STANDARD,
          icon = Icons.Default.Videocam,
          accentColor = MintAccent,
          onClick = { onProfileChanged(ContentProfile.STANDARD) },
          testTag = "profile_standard_option"
        )

        ProfileOptionCard(
          profile = ContentProfile.HIGH_MOTION,
          isSelected = selectedProfile == ContentProfile.HIGH_MOTION,
          icon = Icons.Default.FlashOn,
          accentColor = PurpleHighMotion,
          onClick = { onProfileChanged(ContentProfile.HIGH_MOTION) },
          testTag = "profile_highmotion_option"
        )
      }

      // 3. Auto-Split >90s Toggle
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(12.dp))
          .background(DarkSurfaceVariant)
          .padding(12.dp),
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
            modifier = Modifier.size(20.dp)
          )
          Spacer(modifier = Modifier.width(10.dp))
          Column {
            Text(
              text = "Auto-Split Videos > 90s",
              fontSize = 14.sp,
              fontWeight = FontWeight.SemiBold,
              color = Color.White
            )
            Text(
              text = "Segments into 90-second Status-compliant parts",
              fontSize = 11.sp,
              color = Color(0xFF94A3B8)
            )
          }
        }

        Switch(
          checked = autoSplitEnabled,
          onCheckedChange = onAutoSplitToggled,
          modifier = Modifier.testTag("auto_split_switch"),
          colors = SwitchDefaults.colors(
            checkedThumbColor = Color(0xFF003822),
            checkedTrackColor = MintAccent,
            uncheckedThumbColor = Color(0xFF94A3B8),
            uncheckedTrackColor = DarkSurfaceElevated
          )
        )
      }

      // 4. Live Technical Parameters Matrix
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(12.dp))
          .background(DarkSurfaceElevated)
          .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp)
      ) {
        Text(
          text = "ENGINE ENCODING MATRIX",
          fontSize = 11.sp,
          fontWeight = FontWeight.Bold,
          color = Color(0xFF94A3B8),
          letterSpacing = 0.5.sp
        )

        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          TechMatrixItem(label = "CRF", value = "${selectedProfile.crf}")
          TechMatrixItem(label = "Preset", value = selectedProfile.preset)
          TechMatrixItem(label = "Maxrate", value = selectedProfile.maxrate)
          TechMatrixItem(label = "Bufsize", value = selectedProfile.bufsize)
          TechMatrixItem(label = "GOP Size", value = "-g ${selectedProfile.gop}")
        }
      }
    }
  }
}

@Composable
fun ResolutionOptionTab(
  mode: ResolutionMode,
  isSelected: Boolean,
  onClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  val backgroundColor = if (isSelected) Color(0xFF064E3B) else DarkSurfaceVariant
  val borderColor = if (isSelected) MintAccent else Color.Transparent
  val textColor = if (isSelected) Color.White else Color(0xFFCBD5E1)

  Box(
    modifier = modifier
      .clip(RoundedCornerShape(12.dp))
      .background(backgroundColor)
      .border(1.5.dp, borderColor, RoundedCornerShape(12.dp))
      .clickable(onClick = onClick)
      .padding(vertical = 12.dp, horizontal = 10.dp),
    contentAlignment = Alignment.Center
  ) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
      Row(verticalAlignment = Alignment.CenterVertically) {
        if (isSelected) {
          Icon(
            imageVector = Icons.Default.CheckCircle,
            contentDescription = null,
            tint = MintAccent,
            modifier = Modifier.size(14.dp)
          )
          Spacer(modifier = Modifier.width(4.dp))
        }
        Text(
          text = mode.label,
          fontWeight = FontWeight.Bold,
          fontSize = 14.sp,
          color = textColor
        )
      }
      Text(
        text = if (mode == ResolutionMode.HD_720P) "scale='min(720,iw)':-2" else "scale='min(1080,iw)':-2",
        fontSize = 10.sp,
        color = Color(0xFF94A3B8),
        modifier = Modifier.padding(top = 2.dp)
      )
    }
  }
}

@Composable
fun ProfileOptionCard(
  profile: ContentProfile,
  isSelected: Boolean,
  icon: androidx.compose.ui.graphics.vector.ImageVector,
  accentColor: Color,
  onClick: () -> Unit,
  testTag: String
) {
  val backgroundColor = if (isSelected) DarkSurfaceElevated else DarkSurfaceVariant
  val borderColor = if (isSelected) accentColor else DarkBorder

  Row(
    modifier = Modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(14.dp))
      .background(backgroundColor)
      .border(1.5.dp, borderColor, RoundedCornerShape(14.dp))
      .clickable(onClick = onClick)
      .padding(12.dp)
      .testTag(testTag),
    verticalAlignment = Alignment.CenterVertically
  ) {
    Box(
      modifier = Modifier
        .size(38.dp)
        .clip(RoundedCornerShape(10.dp))
        .background(accentColor.copy(alpha = 0.15f)),
      contentAlignment = Alignment.Center
    ) {
      Icon(
        imageVector = icon,
        contentDescription = null,
        tint = accentColor,
        modifier = Modifier.size(20.dp)
      )
    }

    Spacer(modifier = Modifier.width(12.dp))

    Column(modifier = Modifier.weight(1f)) {
      Row(verticalAlignment = Alignment.CenterVertically) {
        Text(
          text = profile.label,
          fontWeight = FontWeight.Bold,
          fontSize = 14.sp,
          color = Color.White
        )
        Spacer(modifier = Modifier.width(6.dp))
        Box(
          modifier = Modifier
            .clip(RoundedCornerShape(4.dp))
            .background(accentColor.copy(alpha = 0.2f))
            .padding(horizontal = 4.dp, vertical = 2.dp)
        ) {
          Text(
            text = "CRF ${profile.crf} • ${profile.maxrate}",
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            color = accentColor
          )
        }
      }
      Text(
        text = profile.description,
        fontSize = 11.sp,
        color = Color(0xFF94A3B8),
        modifier = Modifier.padding(top = 2.dp)
      )
    }

    if (isSelected) {
      Icon(
        imageVector = Icons.Default.CheckCircle,
        contentDescription = "Selected",
        tint = accentColor,
        modifier = Modifier.size(20.dp)
      )
    }
  }
}

@Composable
fun TechMatrixItem(label: String, value: String) {
  Column(horizontalAlignment = Alignment.CenterHorizontally) {
    Text(text = label, fontSize = 10.sp, color = Color(0xFF94A3B8))
    Text(
      text = value,
      fontSize = 11.sp,
      fontWeight = FontWeight.Bold,
      color = Color(0xFFF1F5F9)
    )
  }
}
