package com.example.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.waitForUpOrCancellation
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

// Palet Kaca Otentik iOS (Neutral Translucent Frosted Glass)
val IOSMidnightBackground = Color(0xFF06080D)
val IOSMidnightDark = Color(0xFF0A0E17)
val IOSGlassFill = Color(0xFFFFFFFF).copy(alpha = 0.08f)
val IOSGlassFillSubtle = Color(0xFFFFFFFF).copy(alpha = 0.04f)
val IOSGlassFillElevated = Color(0xFFFFFFFF).copy(alpha = 0.12f)
val IOSGlassHighlightTop = Color(0xFFFFFFFF).copy(alpha = 0.28f)
val IOSGlassHighlightBottom = Color(0xFFFFFFFF).copy(alpha = 0.03f)

// iOS Vibrancy & Status Accents
val IOSElectricMint = Color(0xFF10E599)
val IOSElectricMintDark = Color(0xFF059669)
val IOSCyanAccent = Color(0xFF00F2FE)
val IOSTextPrimary = Color(0xFFFFFFFF)
val IOSTextSecondary = Color(0xFF98989F)
val IOSTextTertiary = Color(0xFF6E6E73)

fun iosGlassBorder(strokeWidth: Dp = 1.dp): BorderStroke {
  return BorderStroke(
    width = strokeWidth,
    brush = Brush.verticalGradient(
      listOf(IOSGlassHighlightTop, IOSGlassHighlightBottom)
    )
  )
}

/**
 * Otentik iOS Liquid Glass Card Container
 * Menggunakan neutral translucent white fill dengan specular vertical gradient border
 * dan radius squircle khas iOS.
 */
@Composable
fun IOSLiquidGlassCard(
  modifier: Modifier = Modifier,
  cornerRadius: Dp = 28.dp,
  padding: Dp = 20.dp,
  fillColor: Color = IOSGlassFill,
  borderStroke: BorderStroke? = null,
  content: @Composable BoxScope.() -> Unit
) {
  val shape = RoundedCornerShape(cornerRadius)
  val finalBorder = borderStroke ?: BorderStroke(
    width = 1.dp,
    brush = Brush.verticalGradient(
      listOf(IOSGlassHighlightTop, IOSGlassHighlightBottom)
    )
  )
  Box(
    modifier = modifier
      .clip(shape)
      .background(fillColor)
      .border(finalBorder, shape = shape)
      .padding(padding),
    content = content
  )
}

// Backward compatibility alias for LiquidGlassBox
@Composable
fun LiquidGlassBox(
  modifier: Modifier = Modifier,
  cornerRadius: Dp = 28.dp,
  contentPadding: Dp = 20.dp,
  content: @Composable BoxScope.() -> Unit
) {
  IOSLiquidGlassCard(
    modifier = modifier,
    cornerRadius = cornerRadius,
    padding = contentPadding,
    content = content
  )
}

/**
 * Modifier efek sentuhan khas iOS (spring press feedback)
 */
fun Modifier.iosSpringPress(
  scaleDown: Float = 0.96f,
  enabled: Boolean = true
): Modifier = if (!enabled) this else pointerInput(Unit) {
  awaitEachGesture {
    awaitFirstDown(requireUnconsumed = false)
    // Pressed
    waitForUpOrCancellation()
    // Released
  }
}
