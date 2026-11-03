package com.example.ui.components

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
import androidx.compose.ui.draw.shadow
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
    // Pop-up mewah Liquid Glass iOS-inspired: deep dark card, subtle luminous gradient, frosted surface, specular highlight
    Box(
      modifier = Modifier
        .fillMaxWidth(0.92f)
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
        .testTag("required_phone_dialog"),
      contentAlignment = Alignment.Center
    ) {
      Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(18.dp)
      ) {
        // Lingkaran Ikon Luminous dengan efek glow specular
        Box(
          modifier = Modifier
            .size(72.dp)
            .clip(CircleShape)
            .background(
              Brush.radialGradient(
                colors = listOf(
                  g.accent.copy(alpha = 0.28f),
                  g.accent.copy(alpha = 0.08f)
                )
              )
            )
            .border(
              1.dp,
              Brush.linearGradient(
                listOf(
                  g.accent.copy(alpha = 0.65f),
                  g.accent.copy(alpha = 0.20f)
                )
              ),
              CircleShape
            ),
          contentAlignment = Alignment.Center
        ) {
          Icon(
            imageVector = Icons.Rounded.PhoneAndroid,
            contentDescription = null,
            tint = g.accent,
            modifier = Modifier.size(34.dp)
          )
        }

        // Teks Judul & Deskripsi
        Column(
          horizontalAlignment = Alignment.CenterHorizontally,
          verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          Text(
            text = "Nomor WhatsApp",
            style = MaterialTheme.typography.titleLarge.copy(
              fontWeight = FontWeight.SemiBold,
              letterSpacing = (-0.5).sp
            ),
            color = Color(0xFFF9FAFB),
            textAlign = TextAlign.Center
          )

          Text(
            text = "Video kualitas jernih akan dikirimkan otomatis langsung ke nomor WhatsApp ini.",
            style = MaterialTheme.typography.bodyMedium,
            color = Color(0xFF9CA3AF),
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 4.dp)
          )
        }

        // Input Field Mewah dengan Pemisah Jarak Spasi Otomatis
        Column(
          modifier = Modifier.fillMaxWidth(),
          verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
          OutlinedTextField(
            value = textInput,
            onValueChange = { input ->
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
                style = MaterialTheme.typography.bodyLarge,
                color = Color(0xFF6B7280)
              )
            },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
            shape = RoundedCornerShape(18.dp),
            textStyle = MaterialTheme.typography.bodyLarge.copy(
              fontWeight = FontWeight.Medium,
              color = Color.White
            ),
            colors = OutlinedTextFieldDefaults.colors(
              focusedTextColor = Color.White,
              unfocusedTextColor = Color.White,
              focusedContainerColor = Color(0xFF1E2433).copy(alpha = 0.85f),
              unfocusedContainerColor = Color(0xFF131722).copy(alpha = 0.70f),
              focusedBorderColor = g.accent,
              unfocusedBorderColor = Color.White.copy(alpha = 0.16f),
              cursorColor = g.accent
            )
          )

          if (errorMessage != null) {
            Text(
              text = errorMessage.orEmpty(),
              style = MaterialTheme.typography.labelSmall,
              color = Color(0xFFF87171),
              modifier = Modifier.padding(start = 4.dp)
            )
          }
        }

        // Kartu Panduan Edukatif Halus
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xFFF59E0B).copy(alpha = 0.08f))
            .border(
              1.dp,
              Color(0xFFF59E0B).copy(alpha = 0.22f),
              RoundedCornerShape(16.dp)
            )
            .padding(14.dp)
        ) {
          Row(
            verticalAlignment = Alignment.Top,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
          ) {
            Icon(
              imageVector = Icons.Rounded.Info,
              contentDescription = null,
              tint = Color(0xFFFBBF24),
              modifier = Modifier
                .size(17.dp)
                .padding(top = 1.dp)
            )
            Text(
              text = "Pastikan nomor aktif dan benar. Jika video tidak masuk ke chat WhatsApp Anda, kemungkinan terdapat salah ketik nomor.",
              style = MaterialTheme.typography.bodySmall,
              color = Color(0xFFFDE68A),
              lineHeight = 18.sp
            )
          }
        }

        Spacer(modifier = Modifier.height(2.dp))

        // Tombol Simpan Mewah
        GlassButton(
          text = "Simpan & Lanjutkan",
          onClick = {
            val normalized = normalizeWhatsAppPhoneNumber(textInput)
            if (normalized.length < 10) {
              errorMessage = "Nomor WhatsApp belum lengkap (minimal 10 digit)."
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
}
