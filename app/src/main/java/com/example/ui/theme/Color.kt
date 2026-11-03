package com.example.ui.theme

import androidx.compose.ui.graphics.Color

// Canvas & Obsidian Dark Space
val IOSMidnightBackground = Color(0xFF04060A)
val IOSMidnightDark = Color(0xFF080C14)
val IOSMidnightElevated = Color(0xFF0F1522)

// Vibrancy & Liquid Neon Accents
val ElectricMint = Color(0xFF10E599)
val ElectricMintDark = Color(0xFF059669)
val ElectricMintGlow = Color(0xFF10E599).copy(alpha = 0.25f)
val CyanAccent = Color(0xFF00F2FE)
val CyanSecondary = Color(0xFF00F2FE)
val CyanGlow = Color(0xFF00F2FE).copy(alpha = 0.20f)
val AmberWarning = Color(0xFFFBBF24)
val MintAccent = ElectricMint

// Liquid Glass Frosted Layer Fills
val GlassFill = Color(0xFFFFFFFF).copy(alpha = 0.06f)
val GlassFillSubtle = Color(0xFFFFFFFF).copy(alpha = 0.03f)
val GlassFillElevated = Color(0xFFFFFFFF).copy(alpha = 0.09f)
val GlassHighlightTop = Color(0xFFFFFFFF).copy(alpha = 0.32f)
val GlassHighlightBottom = Color(0xFFFFFFFF).copy(alpha = 0.04f)

// Text Colors (Refined iOS Typography Palette)
val IOSTextPrimary = Color(0xFFFFFFFF)
val IOSTextSecondary = Color(0xFFA1A1AA)
val IOSTextTertiary = Color(0xFF71717A)
val TextPrimaryDark = IOSTextPrimary
val TextSecondaryDark = IOSTextSecondary
val TextMuted = IOSTextTertiary

// Theme Surface Compatibility
val DarkObsidian = IOSMidnightBackground
val DarkSurface = IOSMidnightDark
val DarkSurfaceVariant = IOSMidnightElevated
val DarkBorder = Color(0xFFFFFFFF).copy(alpha = 0.12f)
val EmeraldPrimary = ElectricMint
val EmeraldPrimaryDark = ElectricMintDark

// Fallbacks
val LightEmeraldPrimary = Color(0xFF059669)
val LightSurface = Color(0xFFF8FAFC)
val LightSurfaceVariant = Color(0xFFF1F5F9)
val TextPrimaryLight = Color(0xFF0F172A)
