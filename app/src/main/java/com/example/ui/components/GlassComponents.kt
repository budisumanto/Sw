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

// Palet Kaca iOS
val GlassSurface = Color(0xFF0F1E19).copy(alpha = 0.55f)
val GlassSurfaceElevated = Color(0xFF162B23).copy(alpha = 0.65f)
val GlassBorderTopHighlight = Color.White.copy(alpha = 0.22f)
val GlassBorderBottomShadow = Color.White.copy(alpha = 0.04f)

fun glassBorder(strokeWidth: Dp = 1.dp): BorderStroke {
  return BorderStroke(
    width = strokeWidth,
    brush = Brush.verticalGradient(
      listOf(GlassBorderTopHighlight, GlassBorderBottomShadow)
    )
  )
}

@Composable
fun LiquidGlassBox(
  modifier: Modifier = Modifier,
  cornerRadius: Dp = 26.dp,
  contentPadding: Dp = 18.dp,
  content: @Composable BoxScope.() -> Unit
) {
  val glassShape = RoundedCornerShape(cornerRadius)

  Box(
    modifier = modifier
      .clip(glassShape)
      .background(GlassSurface)
      .border(
        BorderStroke(
          width = 1.dp,
          brush = Brush.verticalGradient(
            listOf(GlassBorderTopHighlight, GlassBorderBottomShadow)
          )
        ),
        shape = glassShape
      )
      .padding(contentPadding),
    content = content
  )
}
