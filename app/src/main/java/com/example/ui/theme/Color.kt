package com.example.ui.theme

import androidx.compose.ui.graphics.Color

// Authentic iOS Liquid Glass Palette
val IOSMidnightBackground = Color(0xFF06080D)
val IOSMidnightDark = Color(0xFF0B0F17)
val IOSMidnightElevated = Color(0xFF111724)

// Vibrancy & Accent Colors (iOS Apple Style)
val ElectricMint = Color(0xFF10E599)
val ElectricMintDark = Color(0xFF059669)
val CyanAccent = Color(0xFF00F2FE)
val CyanSecondary = Color(0xFF00F2FE)
val AmberWarning = Color(0xFFFBBF24)
val MintAccent = Color(0xFF10E599)

// Neutral Translucent Glass Fills
val GlassFill = Color(0xFFFFFFFF).copy(alpha = 0.08f)
val GlassFillSubtle = Color(0xFFFFFFFF).copy(alpha = 0.04f)
val GlassFillElevated = Color(0xFFFFFFFF).copy(alpha = 0.12f)
val GlassHighlightTop = Color(0xFFFFFFFF).copy(alpha = 0.28f)
val GlassHighlightBottom = Color(0xFFFFFFFF).copy(alpha = 0.03f)

// Text Colors (iOS Human Interface Guidelines)
val IOSTextPrimary = Color(0xFFFFFFFF)
val IOSTextSecondary = Color(0xFF98989F)
val IOSTextTertiary = Color(0xFF6E6E73)
val TextPrimaryDark = IOSTextPrimary
val TextSecondaryDark = IOSTextSecondary
val TextMuted = IOSTextTertiary

// Theme Surface Compatibility
val DarkObsidian = IOSMidnightBackground
val DarkSurface = IOSMidnightDark
val DarkSurfaceVariant = IOSMidnightElevated
val DarkBorder = Color(0xFFFFFFFF).copy(alpha = 0.15f)
val EmeraldPrimary = ElectricMint
val EmeraldPrimaryDark = ElectricMintDark

// Light Theme Fallbacks
val LightEmeraldPrimary = Color(0xFF059669)
val LightSurface = Color(0xFFF8FAFC)
val LightSurfaceVariant = Color(0xFFF1F5F9)
val TextPrimaryLight = Color(0xFF0F172A)
