package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.model.normalizeWhatsAppPhoneNumber
import com.example.ui.theme.ElectricMint
import com.example.ui.theme.ElectricMintDark
import com.example.ui.theme.IOSTextPrimary
import com.example.ui.theme.IOSTextSecondary

@Composable
fun RequiredPhoneDialog(
  initialPhone: String = "",
  onSave: (String) -> Unit
) {
  var textInput by remember { mutableStateOf(initialPhone) }
  var errorMessage by remember { mutableStateOf<String?>(null) }

  Dialog(
    onDismissRequest = { /* Modal wajib: tidak dapat di-dismiss dengan klik di luar */ },
    properties = DialogProperties(
      dismissOnBackPress = false,
      dismissOnClickOutside = false
    )
  ) {
    IOSLiquidGlassCard(
      modifier = Modifier
        .fillMaxWidth()
        .testTag("required_phone_dialog"),
      cornerRadius = 30.dp,
      padding = 24.dp,
      fillColor = Color(0xFF070B12).copy(alpha = 0.95f),
      borderStroke = BorderStroke(1.dp, ElectricMint.copy(alpha = 0.4f))
    ) {
      Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
      ) {
        // Lingkaran Ikon Bercahaya
        Box(
          modifier = Modifier
            .size(60.dp)
            .clip(CircleShape)
            .background(
              Brush.radialGradient(
                listOf(
                  ElectricMint.copy(alpha = 0.25f),
                  ElectricMint.copy(alpha = 0.05f)
                )
              )
            )
            .border(BorderStroke(1.dp, ElectricMint.copy(alpha = 0.5f)), CircleShape),
          contentAlignment = Alignment.Center
        ) {
          Icon(
            imageVector = Icons.Default.PhoneAndroid,
            contentDescription = null,
            tint = ElectricMint,
            modifier = Modifier.size(28.dp)
          )
        }

        // Header & Judul
        Column(
          horizontalAlignment = Alignment.CenterHorizontally,
          verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
          // Badge Wajib
          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(8.dp))
              .background(Color(0xFFEF4444).copy(alpha = 0.18f))
              .border(BorderStroke(1.dp, Color(0xFFEF4444).copy(alpha = 0.45f)), RoundedCornerShape(8.dp))
              .padding(horizontal = 10.dp, vertical = 4.dp)
          ) {
            Text(
              text = "WAJIB DIISI",
              fontSize = 10.sp,
              fontWeight = FontWeight.ExtraBold,
              color = Color(0xFFFF6B6B),
              letterSpacing = 1.sp
            )
          }

          Text(
            text = "Masukkan Nomor WhatsApp",
            fontSize = 19.sp,
            fontWeight = FontWeight.Bold,
            color = IOSTextPrimary,
            letterSpacing = (-0.4).sp,
            textAlign = TextAlign.Center
          )

          Text(
            text = "Video kualitas HD yang telah dioptimalkan akan dikirimkan langsung ke chat WhatsApp nomor Anda.",
            fontSize = 13.sp,
            color = IOSTextSecondary,
            textAlign = TextAlign.Center,
            lineHeight = 18.sp
          )
        }

        // Input Field
        Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(6.dp)) {
          OutlinedTextField(
            value = textInput,
            onValueChange = {
              textInput = it
              if (errorMessage != null) errorMessage = null
            },
            modifier = Modifier
              .fillMaxWidth()
              .testTag("dialog_phone_input_field"),
            placeholder = {
              Text(
                text = "Contoh: 08123456789",
                fontSize = 14.sp,
                color = IOSTextSecondary.copy(alpha = 0.5f)
              )
            },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
            shape = RoundedCornerShape(16.dp),
            colors = OutlinedTextFieldDefaults.colors(
              focusedTextColor = IOSTextPrimary,
              unfocusedTextColor = IOSTextPrimary,
              focusedContainerColor = Color(0xFFFFFFFF).copy(alpha = 0.06f),
              unfocusedContainerColor = Color(0xFFFFFFFF).copy(alpha = 0.03f),
              focusedBorderColor = ElectricMint,
              unfocusedBorderColor = Color(0xFFFFFFFF).copy(alpha = 0.15f),
              cursorColor = ElectricMint
            )
          )

          if (errorMessage != null) {
            Text(
              text = errorMessage.orEmpty(),
              fontSize = 11.sp,
              color = Color(0xFFF87171),
              fontWeight = FontWeight.Medium
            )
          }
        }

        // Kotak Catatan Edukatif (Jika video tidak masuk)
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(Color(0xFFFFB340).copy(alpha = 0.08f))
            .border(BorderStroke(1.dp, Color(0xFFFFB340).copy(alpha = 0.25f)), RoundedCornerShape(14.dp))
            .padding(12.dp)
        ) {
          Row(
            verticalAlignment = Alignment.Top,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
          ) {
            Icon(
              imageVector = Icons.Default.Info,
              contentDescription = null,
              tint = Color(0xFFFFB340),
              modifier = Modifier
                .size(16.dp)
                .padding(top = 1.dp)
            )
            Text(
              text = "Pastikan nomor aktif dan benar. Jika video tidak kunjung masuk ke chat WhatsApp Anda, kemungkinan besar terdapat kekeliruan pada penulisan nomor.",
              fontSize = 11.sp,
              color = Color(0xFFFFD480),
              lineHeight = 16.sp
            )
          }
        }

        // Tombol Simpan & Lanjutkan
        Button(
          onClick = {
            val normalized = normalizeWhatsAppPhoneNumber(textInput)
            if (normalized.length < 10) {
              errorMessage = "Nomor WhatsApp belum lengkap (minimal 10 digit angka)."
            } else {
              onSave(textInput.trim())
            }
          },
          modifier = Modifier
            .fillMaxWidth()
            .height(52.dp)
            .testTag("save_mandatory_phone_button"),
          shape = RoundedCornerShape(18.dp),
          colors = ButtonDefaults.buttonColors(
            containerColor = Color.Transparent,
            contentColor = Color(0xFF021E14)
          ),
          border = BorderStroke(1.dp, IOSGlassHighlightTop)
        ) {
          Box(
            modifier = Modifier
              .fillMaxSize()
              .background(
                Brush.horizontalGradient(
                  listOf(ElectricMint, ElectricMintDark)
                ),
                shape = RoundedCornerShape(18.dp)
              ),
            contentAlignment = Alignment.Center
          ) {
            Text(
              text = "Simpan & Lanjutkan",
              fontWeight = FontWeight.Bold,
              fontSize = 15.sp,
              color = Color(0xFF022115),
              letterSpacing = (-0.2).sp
            )
          }
        }
      }
    }
  }
}
