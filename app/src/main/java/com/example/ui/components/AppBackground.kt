package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.ui.theme.LocalGlass
import com.example.ui.theme.rememberReducedMotion
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.hazeSource

val LocalHazeState = staticCompositionLocalOf<HazeState> { error("HazeState not provided") }

@Composable
fun AppBackground(content: @Composable BoxScope.() -> Unit) {
    val g = LocalGlass.current
    val hazeState = remember { HazeState() }
    val reduceMotion = rememberReducedMotion()

    val transition = rememberInfiniteTransition(label = "orbs")
    val animated by transition.animateFloat(
        initialValue = 0f, targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(30_000, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "orbProgress",
    )
    val p = if (reduceMotion) 0.5f else animated

    CompositionLocalProvider(LocalHazeState provides hazeState) {
        Box(Modifier.fillMaxSize().background(g.bg)) {
            Canvas(Modifier.fillMaxSize().hazeSource(hazeState)) {
                val base = if (size.minDimension < 600.dp.toPx()) 420.dp.toPx() else 620.dp.toPx()
                fun orb(cx: Float, cy: Float, color: Color, r: Float) {
                    val c = Offset(cx, cy)
                    drawCircle(
                        brush = Brush.radialGradient(
                            listOf(color.copy(alpha = 0.50f), Color.Transparent),
                            center = c,
                            radius = r
                        ),
                        radius = r,
                        center = c,
                    )
                }
                orb(size.width * (0.10f + 0.25f * p), size.height * (0.10f + 0.10f * p), g.accent, base)
                orb(size.width * (0.95f - 0.20f * p), size.height * (0.40f + 0.15f * p), g.accent2, base * 0.9f)
                orb(size.width * (0.30f + 0.25f * p), size.height * (0.95f - 0.12f * p), g.orb3, base * 0.8f)
            }
            content()
        }
    }
}
