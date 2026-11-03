package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.ui.theme.LocalGlass

@Composable
fun SuccessDialog(
  message: String,
  onOpenWhatsApp: () -> Unit,
  onDismiss: () -> Unit
) {
  val g = LocalGlass.current

  Dialog(
    onDismissRequest = onDismiss,
    properties = DialogProperties(
      usePlatformDefaultWidth = false
    )
  ) {
    // Pop-up jelas, tajam, berlatar solid kontras tinggi
    Column(
      modifier = Modifier
        .fillMaxWidth(0.90f)
        .clip(RoundedCornerShape(28.dp))
        .background(Color(0xFF10141E))
        .border(1.5.dp, Color.White.copy(alpha = 0.22f), RoundedCornerShape(28.dp))
        .padding(24.dp)
        .testTag("success_dialog"),
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
      // Lingkaran Centang Sukses
      Box(
        modifier = Modifier
          .size(68.dp)
          .clip(CircleShape)
          .background(g.accent.copy(alpha = 0.20f))
          .border(1.dp, g.accent.copy(alpha = 0.55f), CircleShape),
        contentAlignment = Alignment.Center
      ) {
        Icon(
          imageVector = Icons.Rounded.Check,
          contentDescription = null,
          tint = g.accent,
          modifier = Modifier.size(36.dp)
        )
      }

      Text(
        text = "Berhasil Dikirim!",
        style = MaterialTheme.typography.headlineMedium,
        color = g.text,
        textAlign = TextAlign.Center
      )

      Text(
        text = message,
        style = MaterialTheme.typography.bodyMedium,
        color = Color(0xFFD1D5DB),
        textAlign = TextAlign.Center
      )

      Spacer(modifier = Modifier.height(4.dp))

      // Tombol Buka WhatsApp
      GlassButton(
        text = "Buka WhatsApp",
        onClick = {
          onOpenWhatsApp()
          onDismiss()
        },
        primary = true,
        modifier = Modifier
          .fillMaxWidth()
          .testTag("open_whatsapp_button")
      )

      TextButton(onClick = onDismiss) {
        Text(
          text = "Tutup",
          style = MaterialTheme.typography.labelSmall,
          color = g.textMuted
        )
      }
    }
  }
}
