package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// Liquid Glass Surface Styling
val GlassBackground = Color(0xFF0F1E19).copy(alpha = 0.65f)
val GlassBackgroundElevated = Color(0xFF162B23).copy(alpha = 0.75f)
val GlassBorderTop = Color(0xFF34D399).copy(alpha = 0.35f)
val GlassBorderBottom = Color(0xFFFFFFFF).copy(alpha = 0.05f)

fun glassBorder(strokeWidth: Dp = 1.dp): BorderStroke {
  return BorderStroke(
    width = strokeWidth,
    brush = Brush.verticalGradient(
      listOf(GlassBorderTop, GlassBorderBottom)
    )
  )
}

@Composable
fun LiquidGlassCard(
  modifier: Modifier = Modifier,
  cornerRadius: Dp = 24.dp,
  backgroundColor: Color = GlassBackground,
  content: @Composable BoxScope.() -> Unit
) {
  Box(
    modifier = modifier
      .clip(RoundedCornerShape(cornerRadius))
      .background(backgroundColor)
      .border(glassBorder(), RoundedCornerShape(cornerRadius))
      .padding(16.dp),
    content = content
  )
}

@Composable
fun GlassPill(
  text: String,
  modifier: Modifier = Modifier,
  accentColor: Color = Color(0xFF34D399),
  textColor: Color = Color.White
) {
  Box(
    modifier = modifier
      .clip(RoundedCornerShape(12.dp))
      .background(accentColor.copy(alpha = 0.12f))
      .border(
        BorderStroke(1.dp, accentColor.copy(alpha = 0.3f)),
        RoundedCornerShape(12.dp)
      )
      .padding(horizontal = 10.dp, vertical = 5.dp)
  ) {
    androidx.compose.material3.Text(
      text = text,
      fontSize = 11.sp,
      fontWeight = androidx.compose.ui.text.font.FontWeight.SemiBold,
      color = textColor
    )
  }
}
