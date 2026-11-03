package com.example.ui.theme

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Immutable
data class GlassTokens(
    val bg: Color,
    val accent: Color,
    val accent2: Color,
    val orb3: Color,
    val text: Color,
    val textMuted: Color,
    val fill: Brush,
    val fillStrong: Brush,
    val border: Color,
    val highlight: Color,
    val shadow: Color,
    val blur: Dp,
    val isDark: Boolean,
) {
    val accentGlow: Color get() = accent.copy(alpha = 0.45f)
    val accentGradient: Brush
        get() = Brush.linearGradient(listOf(accent, accent2))

    companion object {
        private fun glassBrush(top: Float, bottom: Float) = Brush.linearGradient(
            colors = listOf(Color.White.copy(alpha = top), Color.White.copy(alpha = bottom)),
            start = Offset.Zero,
            end = Offset.Infinite,
        )

        fun dark(accent: Color, accent2: Color, orb3: Color) = GlassTokens(
            bg = Color(0xFF07090E),
            accent = accent,
            accent2 = accent2,
            orb3 = orb3,
            text = Color(0xFFF4F4FA),
            textMuted = Color(0xFFF4F4FA).copy(alpha = 0.62f),
            fill = glassBrush(0.12f, 0.04f),
            fillStrong = glassBrush(0.20f, 0.08f),
            border = Color.White.copy(alpha = 0.18f),
            highlight = Color.White.copy(alpha = 0.35f),
            shadow = Color.Black.copy(alpha = 0.28f),
            blur = 18.dp,
            isDark = true,
        )

        fun light(accent: Color, accent2: Color, orb3: Color) = GlassTokens(
            bg = Color(0xFFEEF0F8),
            accent = accent,
            accent2 = accent2,
            orb3 = orb3,
            text = Color(0xFF14141F),
            textMuted = Color(0xFF14141F).copy(alpha = 0.6f),
            fill = glassBrush(0.70f, 0.35f),
            fillStrong = glassBrush(0.85f, 0.50f),
            border = Color.White.copy(alpha = 0.8f),
            highlight = Color.White,
            shadow = Color(0xFF282F5A).copy(alpha = 0.12f),
            blur = 18.dp,
            isDark = false,
        )
    }
}

val LocalGlass = staticCompositionLocalOf<GlassTokens> { error("GlassTokens not provided") }
