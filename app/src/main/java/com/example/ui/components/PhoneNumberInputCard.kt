package com.example.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.ElectricMint
import com.example.ui.theme.IOSTextPrimary
import com.example.ui.theme.IOSTextSecondary

@Composable
fun PhoneNumberInputCard(
  phoneNumber: String,
  onPhoneNumberChange: (String) -> Unit,
  modifier: Modifier = Modifier
) {
  IOSLiquidGlassCard(
    modifier = modifier
      .fillMaxWidth()
      .testTag("phone_number_input_card"),
    cornerRadius = 22.dp,
    padding = 16.dp
  ) {
    Column(
      modifier = Modifier.fillMaxWidth(),
      verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
      Text(
        text = "Nomor WhatsApp (Wajib)",
        fontSize = 13.sp,
        fontWeight = FontWeight.SemiBold,
        color = IOSTextPrimary
      )

      OutlinedTextField(
        value = phoneNumber,
        onValueChange = onPhoneNumberChange,
        modifier = Modifier
          .fillMaxWidth()
          .testTag("phone_number_text_field"),
        placeholder = {
          Text(
            text = "08xxxxxxxxxx atau +62 8xx...",
            fontSize = 14.sp,
            color = IOSTextSecondary.copy(alpha = 0.5f)
          )
        },
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
        shape = RoundedCornerShape(14.dp),
        colors = OutlinedTextFieldDefaults.colors(
          focusedTextColor = IOSTextPrimary,
          unfocusedTextColor = IOSTextPrimary,
          focusedContainerColor = Color(0xFFFFFFFF).copy(alpha = 0.05f),
          unfocusedContainerColor = Color(0xFFFFFFFF).copy(alpha = 0.03f),
          focusedBorderColor = ElectricMint,
          unfocusedBorderColor = Color(0xFFFFFFFF).copy(alpha = 0.12f),
          cursorColor = ElectricMint
        )
      )
    }
  }
}
