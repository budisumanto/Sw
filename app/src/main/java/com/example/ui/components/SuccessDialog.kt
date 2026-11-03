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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
    // Pop-up mewah Liquid Glass iOS-inspired: deep dark card, luminous gradient, frosted surface, specular highlight
    Box(
      modifier = Modifier
        .fillMaxWidth(0.90f)
        .shadow(
          elevation = 32.dp,
          shape = RoundedCornerShape(32.dp),
          ambientColor = Color.Black,
          spotColor = Color.Black
        )
        .clip(RoundedCornerShape(32.dp))
        .background(
          Brush.verticalGradient(
            colors = listOf(
              Color(0xFF161B26),
              Color(0xFF0D1017)
            )
          )
        )
        .border(
          width = 1.dp,
          brush = Brush.verticalGradient(
            colors = listOf(
              Color.White.copy(alpha = 0.28f),
              Color.White.copy(alpha = 0.08f)
            )
          ),
          shape = RoundedCornerShape(32.dp)
        )
        .padding(26.dp)
        .testTag("success_dialog"),
      contentAlignment = Alignment.Center
    ) {
      Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(18.dp)
      ) {
        // Lingkaran Centang Luminous
        Box(
          modifier = Modifier
            .size(76.dp)
            .clip(CircleShape)
            .background(
              Brush.radialGradient(
                colors = listOf(
                  g.accent.copy(alpha = 0.30f),
                  g.accent.copy(alpha = 0.08f)
                )
              )
            )
            .border(
              1.dp,
              Brush.linearGradient(
                listOf(
                  g.accent.copy(alpha = 0.70f),
                  g.accent.copy(alpha = 0.25f)
                )
              ),
              CircleShape
            ),
          contentAlignment = Alignment.Center
        ) {
          Icon(
            imageVector = Icons.Rounded.Check,
            contentDescription = null,
            tint = g.accent,
            modifier = Modifier.size(38.dp)
          )
        }

        // Teks Judul & Deskripsi Sukses
        Column(
          horizontalAlignment = Alignment.CenterHorizontally,
          verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          Text(
            text = "Berhasil Dikirim!",
            style = MaterialTheme.typography.titleLarge.copy(
              fontWeight = FontWeight.SemiBold,
              letterSpacing = (-0.5).sp
            ),
            color = Color(0xFFF9FAFB),
            textAlign = TextAlign.Center
          )

          Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium,
            color = Color(0xFF9CA3AF),
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 4.dp),
            lineHeight = 20.sp
          )
        }

        Spacer(modifier = Modifier.height(2.dp))

        // Tombol Aksi Buka WhatsApp
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
            style = MaterialTheme.typography.labelMedium,
            color = Color(0xFF9CA3AF)
          )
        }
      }
    }
  }
}
