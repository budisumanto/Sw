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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.PhoneAndroid
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import com.example.ui.theme.LocalGlass
import com.example.ui.util.PhoneNumberVisualTransformation

@Composable
fun RequiredPhoneDialog(
  initialPhone: String = "",
  onSave: (String) -> Unit
) {
  val g = LocalGlass.current
  var textInput by remember { mutableStateOf(initialPhone) }
  var errorMessage by remember { mutableStateOf<String?>(null) }
  val phoneTransformation = remember { PhoneNumberVisualTransformation() }

  Dialog(
    onDismissRequest = { /* Modal wajib: tidak dapat di-dismiss dengan klik di luar */ },
    properties = DialogProperties(
      dismissOnBackPress = false,
      dismissOnClickOutside = false,
      usePlatformDefaultWidth = false
    )
  ) {
    // Pop-up jelas, solid, berlatar gelap kontras tinggi (solid dark canvas), shadow kuat, border tegas
    Column(
      modifier = Modifier
        .fillMaxWidth(0.92f)
        .clip(RoundedCornerShape(28.dp))
        .background(Color(0xFF10141E))
        .border(1.5.dp, Color.White.copy(alpha = 0.22f), RoundedCornerShape(28.dp))
        .padding(24.dp)
        .testTag("required_phone_dialog"),
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
      // Lingkaran Ikon
      Box(
        modifier = Modifier
          .size(66.dp)
          .clip(CircleShape)
          .background(g.accent.copy(alpha = 0.18f))
          .border(1.dp, g.accent.copy(alpha = 0.50f), CircleShape),
        contentAlignment = Alignment.Center
      ) {
        Icon(
          imageVector = Icons.Rounded.PhoneAndroid,
          contentDescription = null,
          tint = g.accent,
          modifier = Modifier.size(30.dp)
        )
      }

      // Header & Judul
      Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(6.dp)
      ) {
        Box(
          modifier = Modifier
            .clip(RoundedCornerShape(999.dp))
            .background(Color(0xFFEF4444).copy(alpha = 0.18f))
            .border(1.dp, Color(0xFFEF4444).copy(alpha = 0.45f), RoundedCornerShape(999.dp))
            .padding(horizontal = 10.dp, vertical = 4.dp)
        ) {
          Text(
            text = "WAJIB DIISI",
            fontSize = 10.sp,
            style = MaterialTheme.typography.labelSmall,
            color = Color(0xFFFF6B6B),
            letterSpacing = 1.sp
          )
        }

        Text(
          text = "Nomor WhatsApp Kamu",
          style = MaterialTheme.typography.headlineMedium,
          color = g.text,
          textAlign = TextAlign.Center
        )

        Text(
          text = "Video kualitas terbaik yang telah diproses akan dikirimkan langsung ke chat WhatsApp nomor ini.",
          style = MaterialTheme.typography.bodyMedium,
          color = Color(0xFFD1D5DB),
          textAlign = TextAlign.Center
        )
      }

      // Input Field dengan pemisah jarak spasi otomatis saat mengetik
      Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(6.dp)) {
        OutlinedTextField(
          value = textInput,
          onValueChange = { input ->
            // Simpan hanya karakter valid (angka dan '+')
            val filtered = input.filter { it.isDigit() || it == '+' }
            textInput = filtered
            if (errorMessage != null) errorMessage = null
          },
          visualTransformation = phoneTransformation,
          modifier = Modifier
            .fillMaxWidth()
            .testTag("dialog_phone_input_field"),
          placeholder = {
            Text(
              text = "+62 812 3456 7890",
              style = MaterialTheme.typography.bodyMedium,
              color = Color.Gray
            )
          },
          singleLine = true,
          keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
          shape = RoundedCornerShape(16.dp),
          colors = OutlinedTextFieldDefaults.colors(
            focusedTextColor = Color.White,
            unfocusedTextColor = Color.White,
            focusedContainerColor = Color(0xFF1E2433),
            unfocusedContainerColor = Color(0xFF171B26),
            focusedBorderColor = g.accent,
            unfocusedBorderColor = Color.White.copy(alpha = 0.25f),
            cursorColor = g.accent
          )
        )

        if (errorMessage != null) {
          Text(
            text = errorMessage.orEmpty(),
            style = MaterialTheme.typography.labelSmall,
            color = Color(0xFFF87171)
          )
        }
      }

      // Kotak Catatan Edukatif
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(16.dp))
          .background(Color(0xFFFFB340).copy(alpha = 0.10f))
          .border(1.dp, Color(0xFFFFB340).copy(alpha = 0.30f), RoundedCornerShape(16.dp))
          .padding(14.dp)
      ) {
        Row(
          verticalAlignment = Alignment.Top,
          horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          Icon(
            imageVector = Icons.Rounded.Info,
            contentDescription = null,
            tint = Color(0xFFFFB340),
            modifier = Modifier.size(16.dp)
          )
          Text(
            text = "Pastikan nomor aktif dan benar. Jika video tidak kunjung masuk ke chat WhatsApp Anda, kemungkinan besar terdapat kekeliruan pada penulisan nomor.",
            style = MaterialTheme.typography.bodyMedium,
            color = Color(0xFFFFD480)
          )
        }
      }

      // Tombol Simpan
      GlassButton(
        text = "Simpan & Lanjutkan",
        onClick = {
          val normalized = normalizeWhatsAppPhoneNumber(textInput)
          if (normalized.length < 10) {
            errorMessage = "Nomor WhatsApp belum lengkap (minimal 10 digit angka)."
          } else {
            onSave(textInput.trim())
          }
        },
        primary = true,
        modifier = Modifier
          .fillMaxWidth()
          .testTag("save_mandatory_phone_button")
      )
    }
  }
}
