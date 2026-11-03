package com.example.ui.util

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.widthIn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

enum class ScreenSize { Compact, Medium, Expanded }

@Composable
fun rememberScreenSize(): ScreenSize {
    val w = LocalConfiguration.current.screenWidthDp
    return when {
        w < 600 -> ScreenSize.Compact
        w < 1024 -> ScreenSize.Medium
        else -> ScreenSize.Expanded
    }
}

@Composable
fun ContentWidth(maxWidth: Dp = 1100.dp, content: @Composable () -> Unit) {
    Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.TopCenter) {
        Box(Modifier.widthIn(max = maxWidth)) { content() }
    }
}
