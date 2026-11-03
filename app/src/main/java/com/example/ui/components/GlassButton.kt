package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.ui.theme.LocalGlass
import com.example.ui.theme.pressable

@Composable
fun GlassButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    primary: Boolean = true,
) {
    val g = LocalGlass.current
    val shape = RoundedCornerShape(999.dp)
    val base = modifier.heightIn(min = 52.dp).pressable(onClick)
    val styled = if (primary) {
        base
            .shadow(20.dp, shape, ambientColor = g.accentGlow, spotColor = g.accentGlow)
            .clip(shape)
            .background(g.accentGradient, shape)
            .border(1.dp, Brush.verticalGradient(listOf(g.highlight, Color.Transparent)), shape)
    } else {
        base.glass(radius = 999.dp)
    }
    Box(styled.padding(horizontal = 28.dp), contentAlignment = Alignment.Center) {
        Text(text, style = MaterialTheme.typography.labelLarge, color = if (primary) Color(0xFF021E14) else g.text)
    }
}
