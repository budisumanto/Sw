package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.ui.theme.ElectricMint
import com.example.ui.theme.ElectricMintDark
import com.example.ui.theme.IOSTextPrimary
import com.example.ui.theme.IOSTextSecondary

@Composable
fun SuccessDialog(
  message: String,
  onOpenWhatsApp: () -> Unit,
  onDismiss: () -> Unit
) {
  Dialog(onDismissRequest = onDismiss) {
    IOSLiquidGlassCard(
      modifier = Modifier
        .fillMaxWidth()
        .testTag("success_dialog"),
      cornerRadius = 28.dp,
      padding = 24.dp
    ) {
      Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
      ) {
        // Lingkaran centang sukses
        Box(
          modifier = Modifier
            .size(64.dp)
            .clip(CircleShape)
            .background(
              Brush.radialGradient(
                listOf(ElectricMint, ElectricMintDark)
              )
            ),
          contentAlignment = Alignment.Center
        ) {
          Icon(
            imageVector = Icons.Default.Check,
            contentDescription = null,
            tint = Color(0xFF022115),
            modifier = Modifier.size(36.dp)
          )
        }

        Text(
          text = "Berhasil Dioptimalkan!",
          fontSize = 19.sp,
          fontWeight = FontWeight.Bold,
          color = IOSTextPrimary,
          letterSpacing = (-0.3).sp,
          textAlign = TextAlign.Center
        )

        Text(
          text = message,
          fontSize = 14.sp,
          color = IOSTextSecondary,
          textAlign = TextAlign.Center,
          lineHeight = 20.sp
        )

        Spacer(modifier = Modifier.height(4.dp))

        // Tombol Buka WhatsApp
        Button(
          onClick = {
            onOpenWhatsApp()
            onDismiss()
          },
          modifier = Modifier
            .fillMaxWidth()
            .height(52.dp)
            .testTag("open_whatsapp_button"),
          shape = RoundedCornerShape(20.dp),
          colors = ButtonDefaults.buttonColors(
            containerColor = Color.Transparent,
            contentColor = Color(0xFF021E14)
          ),
          border = BorderStroke(1.dp, IOSGlassHighlightTop)
        ) {
          Box(
            modifier = Modifier
              .fillMaxWidth()
              .height(52.dp)
              .background(
                Brush.horizontalGradient(
                  listOf(ElectricMint, ElectricMintDark)
                ),
                shape = RoundedCornerShape(20.dp)
              ),
            contentAlignment = Alignment.Center
          ) {
            Text(
              text = "Buka WhatsApp",
              fontWeight = FontWeight.Bold,
              fontSize = 15.sp,
              color = Color(0xFF022115)
            )
          }
        }

        TextButton(onClick = onDismiss) {
          Text(
            text = "Tutup",
            color = IOSTextSecondary,
            fontSize = 14.sp
          )
        }
      }
    }
  }
}
