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
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.PhoneAndroid
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
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
import androidx.compose.ui.res.stringResource
import com.example.R
import com.example.model.normalizeWhatsAppPhoneNumber
import com.example.ui.theme.LocalGlass
import com.example.ui.util.PhoneNumberVisualTransformation

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RequiredPhoneDialog(
  initialPhone: String = "",
  onSave: (String) -> Unit
) {
  val g = LocalGlass.current
  var textInput by remember { mutableStateOf(initialPhone) }
  var errorMessage by remember { mutableStateOf<String?>(null) }
  val phoneTransformation = remember { PhoneNumberVisualTransformation() }

  // ModalBottomSheet meluncur dari bawah layar ke atas (bottom sheet pop-up)
  val sheetState = rememberModalBottomSheetState(
    skipPartiallyExpanded = true,
    confirmValueChange = { newState ->
      // Cegah dismiss ke Hidden jika nomor belum valid
      if (newState == SheetValue.Hidden) {
        val normalized = normalizeWhatsAppPhoneNumber(textInput)
        normalized.length >= 10
      } else true
    }
  )

  ModalBottomSheet(
    onDismissRequest = { /* Modal wajib diisi */ },
    sheetState = sheetState,
    containerColor = Color(0xFF0F131D),
    contentColor = g.text,
    scrimColor = Color.Black.copy(alpha = 0.65f),
    shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp),
    dragHandle = {
      // Handle drag kaca minimalis
      Box(
        modifier = Modifier
          .padding(top = 12.dp, bottom = 4.dp)
          .size(width = 44.dp, height = 4.dp)
          .clip(RoundedCornerShape(999.dp))
          .background(Color.White.copy(alpha = 0.25f))
      )
    },
    modifier = Modifier.testTag("required_phone_dialog")
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .navigationBarsPadding()
        .padding(horizontal = 24.dp, vertical = 16.dp),
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
      // Lingkaran Ikon Luminous Liquid Glass
      Box(
        modifier = Modifier
          .size(68.dp)
          .clip(CircleShape)
          .background(
            Brush.radialGradient(
              listOf(g.accent.copy(alpha = 0.25f), g.accent.copy(alpha = 0.06f))
            )
          )
          .border(
            1.dp,
            Brush.linearGradient(
              listOf(g.accent.copy(alpha = 0.60f), g.accent.copy(alpha = 0.15f))
            ),
            CircleShape
          ),
        contentAlignment = Alignment.Center
      ) {
        Icon(
          imageVector = Icons.Rounded.PhoneAndroid,
          contentDescription = null,
          tint = g.accent,
          modifier = Modifier.size(32.dp)
        )
      }

      // Judul & Subtitle
      Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(6.dp)
      ) {
        Text(
          text = stringResource(id = R.string.dialog_phone_title),
          style = MaterialTheme.typography.titleLarge.copy(
            fontWeight = FontWeight.SemiBold
          ),
          color = Color(0xFFF9FAFB),
          textAlign = TextAlign.Center
        )

        Text(
          text = stringResource(id = R.string.dialog_phone_desc),
          style = MaterialTheme.typography.bodyMedium,
          color = Color(0xFF9CA3AF),
          textAlign = TextAlign.Center,
          modifier = Modifier.padding(horizontal = 8.dp)
        )
      }

      // Input Field Berjarak Spasi Otomatis
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
            focusedContainerColor = Color(0xFF161B26),
            unfocusedContainerColor = Color(0xFF12151E),
            focusedBorderColor = g.accent,
            unfocusedBorderColor = Color.White.copy(alpha = 0.18f),
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

      // Catatan Edukatif Cantik
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(16.dp))
          .background(Color(0xFFF59E0B).copy(alpha = 0.08f))
          .border(1.dp, Color(0xFFF59E0B).copy(alpha = 0.20f), RoundedCornerShape(16.dp))
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

      val errorText = stringResource(id = R.string.dialog_phone_error)
      // Tombol Simpan
      GlassButton(
        text = stringResource(id = R.string.dialog_phone_save),
        onClick = {
          val normalized = normalizeWhatsAppPhoneNumber(textInput)
          if (normalized.length < 10) {
            errorMessage = errorText
          } else {
            onSave(textInput.trim())
          }
        },
        primary = true,
        modifier = Modifier
          .fillMaxWidth()
          .testTag("save_mandatory_phone_button")
      )

      Spacer(modifier = Modifier.height(8.dp))
    }
  }
}
