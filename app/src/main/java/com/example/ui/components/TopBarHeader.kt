package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
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
    verticalAlignment = Alignment.CenterVertically
  ) {
    // Glass icon badge with subtle mint glow
    Box(
      modifier = Modifier
        .size(46.dp)
        .clip(RoundedCornerShape(16.dp))
        .background(
          Brush.linearGradient(
            listOf(
              Color(0xFFFFFFFF).copy(alpha = 0.12f),
              Color(0xFFFFFFFF).copy(alpha = 0.04f)
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

    Column(modifier = Modifier.weight(1f)) {
      Text(
        text = "StatusHD",
        fontSize = 20.sp,
        fontWeight = FontWeight.Bold,
        color = IOSTextPrimary,
        letterSpacing = (-0.5).sp
      )
      Text(
        text = "Kualitas jernih untuk Status WA",
        fontSize = 12.sp,
        color = IOSTextSecondary,
        fontWeight = FontWeight.Normal
      )
    }
  }
}
