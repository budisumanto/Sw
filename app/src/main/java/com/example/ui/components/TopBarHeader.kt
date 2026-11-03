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
import androidx.compose.foundation.Image
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.R
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
    verticalAlignment = Alignment.CenterVertically
  ) {
    // Glass icon square with custom header SVG logo
    Box(
      modifier = Modifier
        .size(46.dp)
        .glass(radius = 16.dp),
      contentAlignment = Alignment.Center
    ) {
      Image(
        painter = painterResource(id = R.drawable.ic_header_logo),
        contentDescription = "StatusHD Logo",
        modifier = Modifier.size(28.dp)
      )
    }

    Spacer(modifier = Modifier.width(14.dp))

    Column {
      Text(
        text = androidx.compose.ui.res.stringResource(id = R.string.app_name),
        style = MaterialTheme.typography.titleMedium,
        color = g.text
      )
      Text(
        text = androidx.compose.ui.res.stringResource(id = R.string.tagline),
        style = MaterialTheme.typography.bodyMedium,
        color = g.textMuted
      )
    }
  }
}
