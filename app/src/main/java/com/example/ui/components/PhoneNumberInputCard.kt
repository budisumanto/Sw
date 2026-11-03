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
import androidx.compose.material.icons.rounded.ChevronRight
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.Phone
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.formatDisplayPhoneNumber
import com.example.model.normalizeWhatsAppPhoneNumber
import com.example.ui.theme.LocalGlass

@Composable
fun PhoneNumberInputCard(
  phoneNumber: String,
  onClickEdit: () -> Unit,
  modifier: Modifier = Modifier
) {
  val g = LocalGlass.current
  val normalized = normalizeWhatsAppPhoneNumber(phoneNumber)
  val isConfigured = normalized.length in 10..15

  GlassCard(
    modifier = modifier
      .fillMaxWidth()
      .testTag("phone_number_input_card"),
    radius = 24.dp,
    strong = !isConfigured,
    onClick = onClickEdit
  ) {
    Column(
      modifier = Modifier.fillMaxWidth(),
      verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
      // Baris Atas: Ikon + Label + Badge
      Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Box(
            modifier = Modifier
              .size(42.dp)
              .clip(CircleShape)
              .background(if (isConfigured) g.accent.copy(alpha = 0.16f) else Color(0xFFEF4444).copy(alpha = 0.18f))
              .border(
                1.dp,
                if (isConfigured) g.accent.copy(alpha = 0.45f) else Color(0xFFEF4444).copy(alpha = 0.50f),
                CircleShape
              ),
            contentAlignment = Alignment.Center
          ) {
            Icon(
              imageVector = Icons.Rounded.Phone,
              contentDescription = null,
              tint = if (isConfigured) g.accent else Color(0xFFFF6B6B),
              modifier = Modifier.size(20.dp)
            )
          }

          Spacer(modifier = Modifier.width(12.dp))

          Column {
            Text(
              text = "Nomor WhatsApp Tujuan",
              style = MaterialTheme.typography.titleMedium,
              color = g.text
            )
            Text(
              text = if (isConfigured) "Tujuan pengiriman video HD" else "Nomor belum diisi",
              style = MaterialTheme.typography.bodyMedium,
              color = if (isConfigured) g.accent else Color(0xFFFCA5A5)
            )
          }
        }

        // Pill Badge Status jika belum diisi
        if (!isConfigured) {
          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(999.dp))
              .background(Color(0xFFEF4444).copy(alpha = 0.18f))
              .border(
                1.dp,
                Color(0xFFEF4444).copy(alpha = 0.45f),
                RoundedCornerShape(999.dp)
              )
              .padding(horizontal = 10.dp, vertical = 4.dp)
          ) {
            Text(
              text = "WAJIB DIISI",
              fontSize = 10.sp,
              style = MaterialTheme.typography.labelSmall,
              color = Color(0xFFFF6B6B)
            )
          }
        }
      }

      // Baris Display Nomor Kaca Melengkung
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(16.dp))
          .background(g.bg.copy(alpha = 0.40f))
          .border(1.dp, g.border.copy(alpha = 0.20f), RoundedCornerShape(16.dp))
          .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
      ) {
        if (isConfigured) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
              modifier = Modifier
                .size(8.dp)
                .clip(CircleShape)
                .background(g.accent)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Text(
              text = formatDisplayPhoneNumber(phoneNumber),
              style = MaterialTheme.typography.bodyLarge,
              color = g.text
            )
          }
        } else {
          Text(
            text = "Sentuh untuk mengisi nomor WhatsApp...",
            style = MaterialTheme.typography.bodyMedium,
            color = Color(0xFFFCA5A5)
          )
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
          Text(
            text = if (isConfigured) "Ubah" else "Isi",
            style = MaterialTheme.typography.labelSmall,
            color = if (isConfigured) g.textMuted else Color(0xFFFF6B6B)
          )
          Spacer(modifier = Modifier.width(2.dp))
          Icon(
            imageVector = Icons.Rounded.ChevronRight,
            contentDescription = null,
            tint = if (isConfigured) g.textMuted else Color(0xFFFF6B6B),
            modifier = Modifier.size(18.dp)
          )
        }
      }

      // Catatan Edukatif
      Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        Icon(
          imageVector = Icons.Rounded.Info,
          contentDescription = null,
          tint = Color(0xFFFFB340),
          modifier = Modifier.size(15.dp)
        )
        Text(
          text = "Jika video tidak masuk ke chat WhatsApp Anda, mohon periksa kembali apakah nomor sudah ditulis dengan benar.",
          style = MaterialTheme.typography.bodyMedium,
          color = g.textMuted
        )
      }
    }
  }
}
