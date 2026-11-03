package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.formatDisplayPhoneNumber
import com.example.model.normalizeWhatsAppPhoneNumber
import com.example.ui.theme.ElectricMint
import com.example.ui.theme.IOSTextPrimary
import com.example.ui.theme.IOSTextSecondary

@Composable
fun PhoneNumberInputCard(
  phoneNumber: String,
  onClickEdit: () -> Unit,
  modifier: Modifier = Modifier
) {
  val normalized = normalizeWhatsAppPhoneNumber(phoneNumber)
  val isConfigured = normalized.length in 10..15

  IOSLiquidGlassCard(
    modifier = modifier
      .fillMaxWidth()
      .clickable(onClick = onClickEdit)
      .testTag("phone_number_input_card"),
    cornerRadius = 26.dp,
    padding = 18.dp,
    fillColor = if (isConfigured) Color(0xFFFFFFFF).copy(alpha = 0.05f) else Color(0xFFEF4444).copy(alpha = 0.08f),
    borderStroke = BorderStroke(
      1.dp,
      if (isConfigured) {
        Brush.verticalGradient(
          listOf(
            ElectricMint.copy(alpha = 0.45f),
            Color(0xFFFFFFFF).copy(alpha = 0.10f),
            Color(0xFFFFFFFF).copy(alpha = 0.02f)
          )
        )
      } else {
        Brush.verticalGradient(
          listOf(
            Color(0xFFEF4444).copy(alpha = 0.55f),
            Color(0xFFEF4444).copy(alpha = 0.20f)
          )
        )
      }
    )
  ) {
    Column(
      modifier = Modifier.fillMaxWidth(),
      verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
      // Baris Atas: Ikon Avatar + Info + Badge
      Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          // Circular Glowing Avatar
          Box(
            modifier = Modifier
              .size(42.dp)
              .clip(CircleShape)
              .background(
                if (isConfigured) {
                  Brush.radialGradient(
                    listOf(ElectricMint.copy(alpha = 0.22f), Color.Transparent)
                  )
                } else {
                  Brush.radialGradient(
                    listOf(Color(0xFFEF4444).copy(alpha = 0.25f), Color.Transparent)
                  )
                }
              )
              .border(
                BorderStroke(
                  1.dp,
                  if (isConfigured) ElectricMint.copy(alpha = 0.5f)
                  else Color(0xFFEF4444).copy(alpha = 0.6f)
                ),
                CircleShape
              ),
            contentAlignment = Alignment.Center
          ) {
            Icon(
              imageVector = Icons.Default.Phone,
              contentDescription = null,
              tint = if (isConfigured) ElectricMint else Color(0xFFFF6B6B),
              modifier = Modifier.size(19.dp)
            )
          }

          Spacer(modifier = Modifier.width(12.dp))

          Column {
            Text(
              text = "Nomor WhatsApp Tujuan",
              fontSize = 14.sp,
              fontWeight = FontWeight.SemiBold,
              color = IOSTextPrimary,
              letterSpacing = (-0.2).sp
            )
            Text(
              text = if (isConfigured) "Tujuan pengiriman video HD" else "Nomor belum diisi",
              fontSize = 11.sp,
              color = if (isConfigured) ElectricMint else Color(0xFFFCA5A5)
            )
          }
        }

        // Pill Badge Status
        Box(
          modifier = Modifier
            .clip(RoundedCornerShape(10.dp))
            .background(
              if (isConfigured) ElectricMint.copy(alpha = 0.12f)
              else Color(0xFFEF4444).copy(alpha = 0.18f)
            )
            .border(
              BorderStroke(
                1.dp,
                if (isConfigured) ElectricMint.copy(alpha = 0.35f)
                else Color(0xFFEF4444).copy(alpha = 0.45f)
              ),
              RoundedCornerShape(10.dp)
            )
            .padding(horizontal = 9.dp, vertical = 4.dp)
        ) {
          Text(
            text = if (isConfigured) "SIAP DIGUNAKAN" else "WAJIB DIISI",
            fontSize = 9.sp,
            fontWeight = FontWeight.ExtraBold,
            color = if (isConfigured) ElectricMint else Color(0xFFFF6B6B),
            letterSpacing = 0.6.sp
          )
        }
      }

      // Baris Display Nomor Kaca Melengkung
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(16.dp))
          .background(Color(0xFF000000).copy(alpha = 0.35f))
          .border(
            BorderStroke(1.dp, Color(0xFFFFFFFF).copy(alpha = 0.08f)),
            RoundedCornerShape(16.dp)
          )
          .padding(horizontal = 14.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
      ) {
        if (isConfigured) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
              modifier = Modifier
                .size(8.dp)
                .clip(CircleShape)
                .background(ElectricMint)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Text(
              text = formatDisplayPhoneNumber(phoneNumber),
              fontSize = 15.sp,
              fontWeight = FontWeight.Bold,
              color = IOSTextPrimary,
              letterSpacing = 0.4.sp
            )
          }
        } else {
          Text(
            text = "Sentuh untuk melengkapi nomor WhatsApp...",
            fontSize = 13.sp,
            color = Color(0xFFFCA5A5),
            fontWeight = FontWeight.Medium
          )
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
          Text(
            text = if (isConfigured) "Ubah" else "Isi Sekarang",
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            color = if (isConfigured) IOSTextSecondary else Color(0xFFFF6B6B)
          )
          Spacer(modifier = Modifier.width(2.dp))
          Icon(
            imageVector = Icons.Default.ChevronRight,
            contentDescription = null,
            tint = if (isConfigured) IOSTextSecondary else Color(0xFFFF6B6B),
            modifier = Modifier.size(16.dp)
          )
        }
      }

      // Catatan Edukasi Elegan
      Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        Icon(
          imageVector = Icons.Default.Info,
          contentDescription = null,
          tint = Color(0xFFFFB340),
          modifier = Modifier
            .size(13.dp)
            .padding(top = 1.dp)
        )
        Text(
          text = "Jika video tidak masuk ke chat WhatsApp Anda, mohon periksa kembali apakah nomor sudah ditulis dengan benar.",
          fontSize = 11.sp,
          color = IOSTextSecondary,
          lineHeight = 15.sp
        )
      }
    }
  }
}
