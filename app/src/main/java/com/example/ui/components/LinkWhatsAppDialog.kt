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
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.LinkOff
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.model.LinkedWhatsAppAccount
import com.example.ui.theme.ElectricMint
import com.example.ui.theme.ElectricMintDark
import com.example.ui.theme.IOSTextPrimary
import com.example.ui.theme.IOSTextSecondary
import com.example.ui.theme.IOSTextTertiary

@Composable
fun LinkWhatsAppDialog(
  linkedAccount: LinkedWhatsAppAccount,
  onLink: (String) -> Unit,
  onUnlink: () -> Unit,
  onDismiss: () -> Unit,
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current
  var inputNumber by remember { mutableStateOf(linkedAccount.rawPhoneNumber) }
  var isEditing by remember { mutableStateOf(!linkedAccount.isLinked) }

  Dialog(
    onDismissRequest = onDismiss,
    properties = DialogProperties(
      dismissOnBackPress = true,
      dismissOnClickOutside = true,
      usePlatformDefaultWidth = false
    )
  ) {
    Box(
      modifier = modifier
        .fillMaxWidth(0.92f)
        .clip(RoundedCornerShape(32.dp))
        .background(Color(0xFF0B0F19))
        .border(
          BorderStroke(
            1.dp,
            Brush.verticalGradient(
              listOf(
                Color.White.copy(alpha = 0.35f),
                Color.White.copy(alpha = 0.05f)
              )
            )
          ),
          RoundedCornerShape(32.dp)
        )
        .padding(24.dp)
        .testTag("link_whatsapp_dialog")
    ) {
      Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
      ) {
        // Baris atas: Icon + Tombol Tutup
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          // Floating Icon Badge
          Box(
            modifier = Modifier
              .size(48.dp)
              .clip(CircleShape)
              .background(
                Brush.radialGradient(
                  listOf(
                    ElectricMint.copy(alpha = 0.25f),
                    Color(0xFF00F2FE).copy(alpha = 0.08f),
                    Color.Transparent
                  )
                )
              )
              .border(
                BorderStroke(1.dp, Color.White.copy(alpha = 0.25f)),
                CircleShape
              ),
            contentAlignment = Alignment.Center
          ) {
            Icon(
              imageVector = if (linkedAccount.isLinked) Icons.Default.CheckCircle else Icons.Default.Link,
              contentDescription = null,
              tint = ElectricMint,
              modifier = Modifier.size(26.dp)
            )
          }

          IconButton(
            onClick = onDismiss,
            modifier = Modifier
              .size(36.dp)
              .clip(CircleShape)
              .background(Color.White.copy(alpha = 0.08f))
              .testTag("close_link_dialog_button")
          ) {
            Icon(
              imageVector = Icons.Default.Close,
              contentDescription = "Tutup",
              tint = IOSTextSecondary,
              modifier = Modifier.size(18.dp)
            )
          }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Judul & Penjelasan
        Text(
          text = if (linkedAccount.isLinked && !isEditing) "Perangkat Tertaut" else "Hubungkan WhatsApp",
          fontSize = 20.sp,
          fontWeight = FontWeight.Bold,
          color = IOSTextPrimary,
          letterSpacing = (-0.4).sp
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
          text = "WhatsApp tidak akan mengompres video jika dikirim otomatis ke chat nomor Anda sendiri. Buka chat tersebut, lalu teruskan ke Status.",
          fontSize = 13.sp,
          color = IOSTextSecondary,
          textAlign = TextAlign.Center,
          lineHeight = 18.sp,
          modifier = Modifier.padding(horizontal = 4.dp)
        )

        Spacer(modifier = Modifier.height(20.dp))

        if (linkedAccount.isLinked && !isEditing) {
          // State: Sudah Tertaut
          Box(
            modifier = Modifier
              .fillMaxWidth()
              .clip(RoundedCornerShape(20.dp))
              .background(Color.White.copy(alpha = 0.06f))
              .border(BorderStroke(1.dp, Color.White.copy(alpha = 0.15f)), RoundedCornerShape(20.dp))
              .padding(16.dp)
          ) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              verticalAlignment = Alignment.CenterVertically
            ) {
              Box(
                modifier = Modifier
                  .size(10.dp)
                  .clip(CircleShape)
                  .background(ElectricMint)
              )
              Spacer(modifier = Modifier.width(12.dp))
              Column(modifier = Modifier.weight(1f)) {
                Text(
                  text = "Nomor Anda Terhubung",
                  fontSize = 12.sp,
                  color = ElectricMint,
                  fontWeight = FontWeight.Medium
                )
                Text(
                  text = linkedAccount.formattedDisplayNumber,
                  fontSize = 17.sp,
                  fontWeight = FontWeight.Bold,
                  color = IOSTextPrimary,
                  letterSpacing = 0.5.sp
                )
              }
            }
          }

          Spacer(modifier = Modifier.height(20.dp))

          // Tombol Ganti Nomor & Putuskan
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
          ) {
            Box(
              modifier = Modifier
                .weight(1f)
                .height(46.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(Color.White.copy(alpha = 0.08f))
                .border(BorderStroke(1.dp, Color.White.copy(alpha = 0.18f)), RoundedCornerShape(16.dp))
                .clickable { isEditing = true }
                .testTag("edit_number_button"),
              contentAlignment = Alignment.Center
            ) {
              Text(
                text = "Ganti Nomor",
                color = IOSTextPrimary,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold
              )
            }

            Box(
              modifier = Modifier
                .weight(1f)
                .height(46.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(Color(0xFFFF453A).copy(alpha = 0.12f))
                .border(BorderStroke(1.dp, Color(0xFFFF453A).copy(alpha = 0.3f)), RoundedCornerShape(16.dp))
                .clickable {
                  onUnlink()
                  isEditing = true
                  inputNumber = ""
                }
                .testTag("unlink_number_button"),
              contentAlignment = Alignment.Center
            ) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                  imageVector = Icons.Default.LinkOff,
                  contentDescription = null,
                  tint = Color(0xFFFF453A),
                  modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                  text = "Putuskan",
                  color = Color(0xFFFF453A),
                  fontSize = 14.sp,
                  fontWeight = FontWeight.SemiBold
                )
              }
            }
          }
        } else {
          // State: Input Form Nomor WhatsApp
          OutlinedTextField(
            value = inputNumber,
            onValueChange = { inputNumber = it },
            modifier = Modifier
              .fillMaxWidth()
              .testTag("whatsapp_number_input"),
            placeholder = {
              Text(
                text = "08123456789 atau 62812...",
                color = IOSTextTertiary,
                fontSize = 14.sp
              )
            },
            leadingIcon = {
              Icon(
                imageVector = Icons.Default.PhoneAndroid,
                contentDescription = null,
                tint = ElectricMint,
                modifier = Modifier.size(20.dp)
              )
            },
            singleLine = true,
            keyboardOptions = KeyboardOptions(
              keyboardType = KeyboardType.Phone,
              imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(
              onDone = {
                if (inputNumber.isNotBlank()) {
                  onLink(inputNumber)
                }
              }
            ),
            shape = RoundedCornerShape(18.dp),
            colors = OutlinedTextFieldDefaults.colors(
              focusedContainerColor = Color.White.copy(alpha = 0.08f),
              unfocusedContainerColor = Color.White.copy(alpha = 0.04f),
              focusedBorderColor = ElectricMint,
              unfocusedBorderColor = Color.White.copy(alpha = 0.15f),
              focusedTextColor = IOSTextPrimary,
              unfocusedTextColor = IOSTextPrimary
            )
          )

          Spacer(modifier = Modifier.height(10.dp))

          Text(
            text = "Ketik nomor WhatsApp Anda (diawali 08... atau 62...). Nomor disimpan aman di HP Anda.",
            fontSize = 11.sp,
            color = IOSTextTertiary,
            textAlign = TextAlign.Start,
            modifier = Modifier.fillMaxWidth()
          )

          Spacer(modifier = Modifier.height(24.dp))

          // Tombol Simpan / Hubungkan
          Box(
            modifier = Modifier
              .fillMaxWidth()
              .height(52.dp)
              .clip(RoundedCornerShape(18.dp))
              .background(
                Brush.horizontalGradient(
                  listOf(ElectricMint, ElectricMintDark)
                )
              )
              .clickable {
                if (inputNumber.isNotBlank()) {
                  onLink(inputNumber)
                }
              }
              .testTag("submit_link_whatsapp_button"),
            contentAlignment = Alignment.Center
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(
                imageVector = Icons.Default.Link,
                contentDescription = null,
                tint = Color(0xFF022115),
                modifier = Modifier.size(20.dp)
              )
              Spacer(modifier = Modifier.width(8.dp))
              Text(
                text = "Hubungkan WhatsApp",
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                color = Color(0xFF022115)
              )
            }
          }
        }
      }
    }
  }
}
