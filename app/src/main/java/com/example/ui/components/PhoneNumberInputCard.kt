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
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.Warning
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
import com.example.ui.theme.ElectricMintDark
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
    cornerRadius = 24.dp,
    padding = 18.dp,
    fillColor = if (isConfigured) Color(0xFFFFFFFF).copy(alpha = 0.05f) else Color(0xFFEF4444).copy(alpha = 0.08f),
    borderStroke = BorderStroke(
      1.dp,
      if (isConfigured) ElectricMint.copy(alpha = 0.35f) else Color(0xFFEF4444).copy(alpha = 0.5f)
    )
  ) {
    Column(
      modifier = Modifier.fillMaxWidth(),
      verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
      // Header: Ikon + Label + Badge Wajib
      Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Box(
            modifier = Modifier
              .size(38.dp)
              .clip(CircleShape)
              .background(
                if (isConfigured) ElectricMint.copy(alpha = 0.15f)
                else Color(0xFFEF4444).copy(alpha = 0.2f)
              )
              .border(
                BorderStroke(
                  1.dp,
                  if (isConfigured) ElectricMint.copy(alpha = 0.4f)
                  else Color(0xFFEF4444).copy(alpha = 0.5f)
                ),
                CircleShape
              ),
            contentAlignment = Alignment.Center
          ) {
            Icon(
              imageVector = Icons.Default.PhoneAndroid,
              contentDescription = null,
              tint = if (isConfigured) ElectricMint else Color(0xFFF87171),
              modifier = Modifier.size(18.dp)
            )
          }

          Spacer(modifier = Modifier.width(10.dp))

          Column {
            Text(
              text = "Nomor WhatsApp Tujuan",
              fontSize = 14.sp,
              fontWeight = FontWeight.Bold,
              color = IOSTextPrimary,
              letterSpacing = (-0.2).sp
            )
            Text(
              text = if (isConfigured) "Tujuan pengiriman video HD" else "Belum diatur (Wajib)",
              fontSize = 11.sp,
              color = if (isConfigured) ElectricMint else Color(0xFFFCA5A5)
            )
          }
        }

        // Badge Wajib
        Box(
          modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(
              if (isConfigured) ElectricMint.copy(alpha = 0.12f)
              else Color(0xFFEF4444).copy(alpha = 0.2f)
            )
            .border(
              BorderStroke(
                1.dp,
                if (isConfigured) ElectricMint.copy(alpha = 0.3f)
                else Color(0xFFEF4444).copy(alpha = 0.45f)
              ),
              RoundedCornerShape(8.dp)
            )
            .padding(horizontal = 9.dp, vertical = 4.dp)
        ) {
          Text(
            text = if (isConfigured) "SIAP DIGUNAKAN" else "WAJIB DIISI",
            fontSize = 9.sp,
            fontWeight = FontWeight.ExtraBold,
            color = if (isConfigured) ElectricMint else Color(0xFFFF6B6B),
            letterSpacing = 0.8.sp
          )
        }
      }

      // Baris Nomor & Tombol Ubah
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(14.dp))
          .background(Color(0xFF000000).copy(alpha = 0.3f))
          .border(BorderStroke(1.dp, Color(0xFFFFFFFF).copy(alpha = 0.08f)), RoundedCornerShape(14.dp))
          .padding(horizontal = 14.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
      ) {
        if (isConfigured) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
              imageVector = Icons.Default.CheckCircle,
              contentDescription = null,
              tint = ElectricMint,
              modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
              text = formatDisplayPhoneNumber(phoneNumber),
              fontSize = 15.sp,
              fontWeight = FontWeight.SemiBold,
              color = IOSTextPrimary,
              letterSpacing = 0.2.sp
            )
          }
        } else {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
              imageVector = Icons.Default.Warning,
              contentDescription = null,
              tint = Color(0xFFF87171),
              modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
              text = "Sentuh di sini untuk mengisi nomor",
              fontSize = 13.sp,
              fontWeight = FontWeight.Medium,
              color = Color(0xFFFCA5A5)
            )
          }
        }

        Box(
          modifier = Modifier
            .clip(RoundedCornerShape(10.dp))
            .background(Color(0xFFFFFFFF).copy(alpha = 0.08f))
            .border(BorderStroke(1.dp, Color(0xFFFFFFFF).copy(alpha = 0.15f)), RoundedCornerShape(10.dp))
            .padding(horizontal = 10.dp, vertical = 5.dp)
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
              imageVector = Icons.Default.Edit,
              contentDescription = null,
              tint = IOSTextPrimary,
              modifier = Modifier.size(12.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
              text = if (isConfigured) "Ubah" else "Isi",
              fontSize = 11.sp,
              fontWeight = FontWeight.Bold,
              color = IOSTextPrimary
            )
          }
        }
      }

      // Keterangan Edukatif: Jika video tidak masuk
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
            .size(14.dp)
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
