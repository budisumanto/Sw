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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
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
  onPhoneNumberChange: (String) -> Unit,
  modifier: Modifier = Modifier
) {
  val normalized = normalizeWhatsAppPhoneNumber(phoneNumber)
  val isValid = normalized.length in 10..15

  IOSLiquidGlassCard(
    modifier = modifier
      .fillMaxWidth()
      .testTag("phone_number_input_card"),
    cornerRadius = 24.dp,
    padding = 20.dp
  ) {
    Column(
      modifier = Modifier.fillMaxWidth(),
      verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
      ) {
        Box(
          modifier = Modifier
            .size(36.dp)
            .clip(CircleShape)
            .background(ElectricMint.copy(alpha = 0.15f)),
          contentAlignment = Alignment.Center
        ) {
          Icon(
            imageVector = Icons.Default.Phone,
            contentDescription = null,
            tint = ElectricMint,
            modifier = Modifier.size(18.dp)
          )
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column {
          Text(
            text = "Nomor WhatsApp Anda",
            fontSize = 15.sp,
            fontWeight = FontWeight.SemiBold,
            color = IOSTextPrimary,
            letterSpacing = (-0.2).sp
          )
          Text(
            text = "Video HD akan dikirim ke chat WA nomor ini",
            fontSize = 11.sp,
            color = IOSTextSecondary
          )
        }
      }

      OutlinedTextField(
        value = phoneNumber,
        onValueChange = onPhoneNumberChange,
        modifier = Modifier
          .fillMaxWidth()
          .testTag("phone_number_text_field"),
        placeholder = {
          Text(
            text = "Contoh: 08123456789 atau +62 812-3456-7890",
            fontSize = 13.sp,
            color = IOSTextSecondary.copy(alpha = 0.6f)
          )
        },
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
        shape = RoundedCornerShape(16.dp),
        colors = OutlinedTextFieldDefaults.colors(
          focusedTextColor = IOSTextPrimary,
          unfocusedTextColor = IOSTextPrimary,
          focusedContainerColor = Color(0xFFFFFFFF).copy(alpha = 0.05f),
          unfocusedContainerColor = Color(0xFFFFFFFF).copy(alpha = 0.03f),
          focusedBorderColor = ElectricMint,
          unfocusedBorderColor = Color(0xFFFFFFFF).copy(alpha = 0.15f),
          cursorColor = ElectricMint
        )
      )

      if (phoneNumber.isNotBlank()) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = "Format Server: $normalized",
            fontSize = 11.sp,
            color = if (isValid) ElectricMint else Color(0xFFF87171),
            fontWeight = FontWeight.Medium
          )
          if (isValid) {
            Text(
              text = "Tampilan: ${formatDisplayPhoneNumber(phoneNumber)}",
              fontSize = 11.sp,
              color = IOSTextSecondary
            )
          }
        }
      }
    }
  }
}
