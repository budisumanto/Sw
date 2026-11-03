package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color

object Brand {
    // Project Override: Hijau / WhatsApp HD
    val accent = Color(0xFF10E599)   // Vibrant Electric Emerald Mint
    val accent2 = Color(0xFF00D4FF)  // Analogous Electric Cyan
    val orb3 = Color(0xFF059669)     // Deep Forest Teal
}

@Composable
fun AppTheme(
    darkTheme: Boolean = true, // Default dark per user specification
    content: @Composable () -> Unit,
) {
    val g = remember(darkTheme) {
        if (darkTheme) GlassTokens.dark(Brand.accent, Brand.accent2, Brand.orb3)
        else GlassTokens.light(Brand.accent, Brand.accent2, Brand.orb3)
    }
    val scheme = if (darkTheme) {
        darkColorScheme(
            primary = g.accent,
            secondary = g.accent2,
            background = g.bg,
            surface = g.bg,
            onSurface = g.text,
            onBackground = g.text
        )
    } else {
        lightColorScheme(
            primary = g.accent,
            secondary = g.accent2,
            background = g.bg,
            surface = g.bg,
            onSurface = g.text,
            onBackground = g.text
        )
    }
    CompositionLocalProvider(LocalGlass provides g) {
        MaterialTheme(colorScheme = scheme, typography = AppTypography(g), content = content)
    }
}
