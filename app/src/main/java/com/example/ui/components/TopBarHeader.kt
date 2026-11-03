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
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Hd
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.ElectricMint
import com.example.ui.theme.IOSTextPrimary
import com.example.ui.theme.IOSTextSecondary

@Composable
fun TopBarHeader(
  modifier: Modifier = Modifier
) {
  Row(
    modifier = modifier
      .fillMaxWidth()
      .padding(horizontal = 4.dp, vertical = 12.dp),
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.SpaceBetween
  ) {
    Row(verticalAlignment = Alignment.CenterVertically) {
      // Frosted Glass Badge with subtle mint halo glow
      Box(
        modifier = Modifier
          .size(44.dp)
          .clip(RoundedCornerShape(16.dp))
          .background(
            Brush.linearGradient(
              listOf(
                Color(0xFFFFFFFF).copy(alpha = 0.12f),
                Color(0xFFFFFFFF).copy(alpha = 0.03f)
              )
            )
          )
          .border(
            iosGlassBorder(1.dp),
            RoundedCornerShape(16.dp)
          ),
        contentAlignment = Alignment.Center
      ) {
        Icon(
          imageVector = Icons.Default.AutoAwesome,
          contentDescription = "StatusHD Logo",
          tint = ElectricMint,
          modifier = Modifier.size(22.dp)
        )
      }

      Spacer(modifier = Modifier.width(12.dp))

      Column {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Text(
            text = "StatusHD",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = IOSTextPrimary,
            letterSpacing = (-0.5).sp
          )
          Spacer(modifier = Modifier.width(6.dp))
          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(6.dp))
              .background(ElectricMint.copy(alpha = 0.15f))
              .border(
                androidx.compose.foundation.BorderStroke(1.dp, ElectricMint.copy(alpha = 0.35f)),
                RoundedCornerShape(6.dp)
              )
              .padding(horizontal = 5.dp, vertical = 2.dp)
          ) {
            Text(
              text = "PRO",
              fontSize = 9.sp,
              fontWeight = FontWeight.Black,
              color = ElectricMint,
              letterSpacing = 0.5.sp
            )
          }
        }
        Text(
          text = "Kualitas Jernih Tanpa Pecah",
          fontSize = 12.sp,
          color = IOSTextSecondary,
          fontWeight = FontWeight.Normal
        )
      }
    }

    // Right Pill Indicator: Pure 1080p Badge
    Box(
      modifier = Modifier
        .clip(RoundedCornerShape(14.dp))
        .background(Color(0xFFFFFFFF).copy(alpha = 0.05f))
        .border(
          iosGlassBorder(1.dp),
          RoundedCornerShape(14.dp)
        )
        .padding(horizontal = 10.dp, vertical = 6.dp)
    ) {
      Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
          modifier = Modifier
            .size(6.dp)
            .clip(CircleShape)
            .background(ElectricMint)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
          text = "1080p HD",
          fontSize = 11.sp,
          fontWeight = FontWeight.SemiBold,
          color = IOSTextPrimary
        )
      }
    }
  }
}
