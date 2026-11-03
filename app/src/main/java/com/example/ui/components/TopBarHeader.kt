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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.LocalGlass

@Composable
fun TopBarHeader(
  modifier: Modifier = Modifier
) {
  val g = LocalGlass.current

  Row(
    modifier = modifier
      .fillMaxWidth()
      .padding(horizontal = 4.dp, vertical = 8.dp),
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.SpaceBetween
  ) {
    Row(verticalAlignment = Alignment.CenterVertically) {
      // Glass icon square
      Box(
        modifier = Modifier
          .size(46.dp)
          .glass(radius = 16.dp),
        contentAlignment = Alignment.Center
      ) {
        Icon(
          imageVector = Icons.Rounded.AutoAwesome,
          contentDescription = "StatusHD Logo",
          tint = g.accent,
          modifier = Modifier.size(24.dp)
        )
      }

      Spacer(modifier = Modifier.width(14.dp))

      Column {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Text(
            text = "StatusHD",
            style = MaterialTheme.typography.titleMedium,
            color = g.text
          )
          Spacer(modifier = Modifier.width(8.dp))
          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(999.dp))
              .background(g.accent.copy(alpha = 0.16f))
              .border(
                1.dp,
                g.accent.copy(alpha = 0.40f),
                RoundedCornerShape(999.dp)
              )
              .padding(horizontal = 7.dp, vertical = 2.dp)
          ) {
            Text(
              text = "PRO",
              fontSize = 10.sp,
              style = MaterialTheme.typography.labelSmall,
              color = g.accent
            )
          }
        }
        Text(
          text = "Kualitas Jernih untuk Status WA",
          style = MaterialTheme.typography.bodyMedium,
          color = g.textMuted
        )
      }
    }

    // Pill status 1080p
    Box(
      modifier = Modifier
        .glass(radius = 999.dp)
        .padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
      Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
          modifier = Modifier
            .size(7.dp)
            .clip(CircleShape)
            .background(g.accent)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
          text = "1080p HD",
          style = MaterialTheme.typography.labelSmall,
          color = g.text
        )
      }
    }
  }
}
