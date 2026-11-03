package com.example.ui.components

import android.os.Build
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.ui.theme.LocalGlass
import dev.chrisbanes.haze.hazeEffect

private val canBlur = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S // API 31+

@Composable
fun Modifier.glass(radius: Dp = 24.dp, strong: Boolean = false): Modifier {
    val g = LocalGlass.current
    val haze = LocalHazeState.current
    val shape = RoundedCornerShape(radius)
    val useStrong = strong || !canBlur // below API 31 there is no blur, so use a more opaque fill
    return this
        .shadow(16.dp, shape, ambientColor = g.shadow, spotColor = g.shadow)
        .clip(shape)
        .hazeEffect(state = haze) {
            blurRadius = g.blur
            noiseFactor = 0.03f
            backgroundColor = g.bg
        }
        .background(if (useStrong) g.fillStrong else g.fill, shape)
        .border(
            1.dp,
            Brush.verticalGradient(listOf(g.highlight, g.border, g.border.copy(alpha = g.border.alpha * 0.6f))),
            shape,
        )
}

/** NO blur. For long lists, items, and anything inside a Dialog/ModalBottomSheet (separate window, can't backdrop-blur). */
@Composable
fun Modifier.glassFlat(radius: Dp = 24.dp): Modifier {
    val g = LocalGlass.current
    val shape = RoundedCornerShape(radius)
    return this
        .clip(shape)
        .background(g.fillStrong, shape)
        .border(1.dp, Brush.verticalGradient(listOf(g.highlight, g.border)), shape)
}
