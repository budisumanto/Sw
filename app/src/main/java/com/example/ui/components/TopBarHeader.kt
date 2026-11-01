package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.VideoLibrary
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.MintAccent

@Composable
fun TopBarHeader(
  modifier: Modifier = Modifier
) {
  Row(
    modifier = modifier
      .fillMaxWidth()
      .padding(horizontal = 4.dp, vertical = 12.dp),
    verticalAlignment = Alignment.CenterVertically
  ) {
    Box(
      modifier = Modifier
        .size(46.dp)
        .clip(RoundedCornerShape(14.dp))
        .background(Color(0xFF10B981).copy(alpha = 0.15f))
        .border(
          androidx.compose.foundation.BorderStroke(1.dp, GlassBorderTopHighlight),
          RoundedCornerShape(14.dp)
        ),
      contentAlignment = Alignment.Center
    ) {
      Icon(
        imageVector = Icons.Default.VideoLibrary,
        contentDescription = "StatusHD Logo",
        tint = MintAccent,
        modifier = Modifier.size(24.dp)
      )
    }

    Spacer(modifier = Modifier.width(14.dp))

    Column {
      Text(
        text = "StatusHD",
        style = MaterialTheme.typography.titleLarge,
        fontWeight = FontWeight.Bold,
        color = Color.White
      )
      Text(
        text = "Kualitas jernih untuk Status WhatsApp",
        style = MaterialTheme.typography.bodySmall,
        color = Color(0xFF94A3B8),
        fontSize = 13.sp
      )
    }
  }
}
