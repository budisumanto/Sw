package com.example.ui.components

import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Devices
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.LinkOff
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.model.LinkedWhatsAppAccount
import com.example.model.normalizePhoneNumber
import com.example.service.WhatsAppWebAuthStatus
import com.example.service.WhatsAppWebManager
import com.example.ui.theme.ElectricMint
import com.example.ui.theme.ElectricMintDark
import com.example.ui.theme.IOSTextPrimary
import com.example.ui.theme.IOSTextSecondary
import com.example.ui.theme.IOSTextTertiary

@Composable
fun LinkWhatsAppDialog(
  linkedAccount: LinkedWhatsAppAccount,
  onLinkWithPairingCode: (phone: String, code: String) -> Unit,
  onConfirmLinkedCompanion: (label: String) -> Unit,
  onUnlink: () -> Unit,
  onDismiss: () -> Unit,
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current
  val clipboardManager = LocalClipboardManager.current

  var selectedTab by remember {
    mutableIntStateOf(if (linkedAccount.isLinked && linkedAccount.connectionMethod == "PAIRING_CODE") 1 else 0)
  }

  val authStatus by WhatsAppWebManager.authStatus.collectAsState()
  val detectedCode by WhatsAppWebManager.detectedPairingCode.collectAsState()

  var phoneInput by remember { mutableStateOf(linkedAccount.rawPhoneNumber) }
  var isRequestingPairingCode by remember { mutableStateOf(false) }

  // Listen for successful authentication from WebView
  LaunchedEffect(Unit) {
    WhatsAppWebManager.setOnAuthSuccessListener {
      onConfirmLinkedCompanion("WhatsApp Web Companion (Resmi)")
      Toast.makeText(context, "WhatsApp Berhasil Terhubung!", Toast.LENGTH_LONG).show()
      onDismiss()
    }
  }

  LaunchedEffect(detectedCode) {
    if (detectedCode.isNotBlank()) {
      isRequestingPairingCode = false
    }
  }

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
        .fillMaxWidth(0.95f)
        .clip(RoundedCornerShape(32.dp))
        .background(Color(0xFF090D15))
        .border(
          BorderStroke(
            1.dp,
            Brush.verticalGradient(
              listOf(
                Color.White.copy(alpha = 0.30f),
                Color.White.copy(alpha = 0.06f)
              )
            )
          ),
          RoundedCornerShape(32.dp)
        )
        .padding(18.dp)
        .testTag("link_whatsapp_dialog")
    ) {
      Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
      ) {
        // Header
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
              modifier = Modifier
                .size(42.dp)
                .clip(CircleShape)
                .background(
                  Brush.radialGradient(
                    listOf(
                      ElectricMint.copy(alpha = 0.25f),
                      Color.Transparent
                    )
                  )
                )
                .border(
                  BorderStroke(1.dp, Color.White.copy(alpha = 0.22f)),
                  CircleShape
                ),
              contentAlignment = Alignment.Center
            ) {
              Icon(
                imageVector = if (linkedAccount.isLinked) Icons.Default.CheckCircle else Icons.Default.Devices,
                contentDescription = null,
                tint = ElectricMint,
                modifier = Modifier.size(22.dp)
              )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
              Text(
                text = "Perangkat Tertaut",
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                color = IOSTextPrimary,
                letterSpacing = (-0.3).sp
              )
              Text(
                text = if (linkedAccount.isLinked) "Status: Terhubung & Aktif" else "WhatsApp Web Engine Resmi",
                fontSize = 12.sp,
                color = if (linkedAccount.isLinked) ElectricMint else Color(0xFFFF9F0A),
                fontWeight = FontWeight.Medium
              )
            }
          }

          IconButton(
            onClick = onDismiss,
            modifier = Modifier
              .size(34.dp)
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

        Spacer(modifier = Modifier.height(14.dp))

        // Tab Navigation
        TabRow(
          selectedTabIndex = selectedTab,
          containerColor = Color.White.copy(alpha = 0.04f),
          contentColor = ElectricMint,
          indicator = { tabPositions ->
            if (selectedTab < tabPositions.size) {
              TabRowDefaults.SecondaryIndicator(
                Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                color = ElectricMint,
                height = 3.dp
              )
            }
          },
          modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
        ) {
          Tab(
            selected = selectedTab == 0,
            onClick = { selectedTab = 0 },
            text = {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                  imageVector = Icons.Default.QrCodeScanner,
                  contentDescription = null,
                  modifier = Modifier.size(16.dp),
                  tint = if (selectedTab == 0) ElectricMint else IOSTextSecondary
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                  text = "Opsi 1: Scan QR Resmi",
                  fontSize = 12.sp,
                  fontWeight = if (selectedTab == 0) FontWeight.Bold else FontWeight.Normal,
                  color = if (selectedTab == 0) IOSTextPrimary else IOSTextSecondary
                )
              }
            }
          )

          Tab(
            selected = selectedTab == 1,
            onClick = { selectedTab = 1 },
            text = {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                  imageVector = Icons.Default.Key,
                  contentDescription = null,
                  modifier = Modifier.size(16.dp),
                  tint = if (selectedTab == 1) ElectricMint else IOSTextSecondary
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                  text = "Opsi 2: Kode Telepon",
                  fontSize = 12.sp,
                  fontWeight = if (selectedTab == 1) FontWeight.Bold else FontWeight.Normal,
                  color = if (selectedTab == 1) IOSTextPrimary else IOSTextSecondary
                )
              }
            }
          )
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Card Linked Account (if already linked)
        if (linkedAccount.isLinked) {
          Box(
            modifier = Modifier
              .fillMaxWidth()
              .clip(RoundedCornerShape(18.dp))
              .background(ElectricMint.copy(alpha = 0.12f))
              .border(BorderStroke(1.dp, ElectricMint.copy(alpha = 0.4f)), RoundedCornerShape(18.dp))
              .padding(12.dp)
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Box(
                modifier = Modifier
                  .size(10.dp)
                  .clip(CircleShape)
                  .background(ElectricMint)
              )
              Spacer(modifier = Modifier.width(10.dp))
              Column(modifier = Modifier.weight(1f)) {
                Text(
                  text = "WhatsApp Terhubung Aktif",
                  fontSize = 12.sp,
                  color = ElectricMint,
                  fontWeight = FontWeight.Bold
                )
                Text(
                  text = linkedAccount.formattedDisplayNumber,
                  fontSize = 13.sp,
                  fontWeight = FontWeight.SemiBold,
                  color = IOSTextPrimary
                )
              }
              Box(
                modifier = Modifier
                  .clip(RoundedCornerShape(10.dp))
                  .background(Color(0xFFFF453A).copy(alpha = 0.15f))
                  .border(BorderStroke(1.dp, Color(0xFFFF453A).copy(alpha = 0.35f)), RoundedCornerShape(10.dp))
                  .clickable {
                    WhatsAppWebManager.clearSession()
                    onUnlink()
                  }
                  .padding(horizontal = 10.dp, vertical = 6.dp)
                  .testTag("unlink_number_button")
              ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                  Icon(
                    imageVector = Icons.Default.LinkOff,
                    contentDescription = null,
                    tint = Color(0xFFFF453A),
                    modifier = Modifier.size(14.dp)
                  )
                  Spacer(modifier = Modifier.width(4.dp))
                  Text(
                    text = "Putus",
                    color = Color(0xFFFF453A),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                  )
                }
              }
            }
          }
          Spacer(modifier = Modifier.height(12.dp))
        }

        // Web Engine Progress indicator
        if (authStatus is WhatsAppWebAuthStatus.Loading) {
          val progress = (authStatus as WhatsAppWebAuthStatus.Loading).progress
          LinearProgressIndicator(
            progress = { progress / 100f },
            modifier = Modifier
              .fillMaxWidth()
              .height(4.dp)
              .clip(RoundedCornerShape(2.dp)),
            color = ElectricMint,
            trackColor = Color.White.copy(alpha = 0.1f)
          )
          Spacer(modifier = Modifier.height(10.dp))
        }

        // =========================================================
        // TAB 0: RESMI WHATSAPP WEB QR CODE ENGINE
        // =========================================================
        if (selectedTab == 0) {
          Box(
            modifier = Modifier
              .fillMaxWidth()
              .clip(RoundedCornerShape(16.dp))
              .background(Color.White.copy(alpha = 0.05f))
              .border(BorderStroke(1.dp, Color.White.copy(alpha = 0.12f)), RoundedCornerShape(16.dp))
              .padding(12.dp)
          ) {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
              Text(
                text = "📷 QR Code Resmi WhatsApp Web:",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = ElectricMint
              )
              Text(
                text = "1. Buka WhatsApp di HP Anda > Titik Tiga (⋮) > Perangkat Tertaut",
                fontSize = 11.sp,
                color = IOSTextSecondary,
                lineHeight = 15.sp
              )
              Text(
                text = "2. Ketuk 'Tautkan Perangkat' lalu arahkan kamera ke QR di bawah ini.",
                fontSize = 11.sp,
                color = IOSTextSecondary,
                lineHeight = 15.sp
              )
            }
          }

          Spacer(modifier = Modifier.height(12.dp))

          // Authentic WhatsApp Web WebView Viewport
          Box(
            modifier = Modifier
              .fillMaxWidth()
              .height(260.dp)
              .clip(RoundedCornerShape(20.dp))
              .background(Color(0xFF06090E))
              .border(BorderStroke(1.5.dp, ElectricMint.copy(alpha = 0.4f)), RoundedCornerShape(20.dp))
              .testTag("qr_code_display_box"),
            contentAlignment = Alignment.Center
          ) {
            AndroidView(
              modifier = Modifier.fillMaxSize(),
              factory = { ctx ->
                WhatsAppWebManager.getOrCreateWebView(ctx).apply {
                  (parent as? ViewGroup)?.removeView(this)
                  layoutParams = FrameLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                  )
                }
              },
              update = { view ->
                (view.parent as? ViewGroup)?.let {
                  // Already safely attached to this container
                }
              }
            )

            if (authStatus is WhatsAppWebAuthStatus.Initializing) {
              Column(horizontalAlignment = Alignment.CenterHorizontally) {
                CircularProgressIndicator(
                  modifier = Modifier.size(32.dp),
                  color = ElectricMint,
                  strokeWidth = 3.dp
                )
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                  text = "Memuat Server WhatsApp Web...",
                  fontSize = 12.sp,
                  color = IOSTextSecondary
                )
              }
            }
          }

          Spacer(modifier = Modifier.height(12.dp))

          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
          ) {
            Box(
              modifier = Modifier
                .weight(1f)
                .height(44.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(Color.White.copy(alpha = 0.08f))
                .border(BorderStroke(1.dp, Color.White.copy(alpha = 0.18f)), RoundedCornerShape(14.dp))
                .clickable {
                  WhatsAppWebManager.reload()
                  Toast.makeText(context, "Memuat ulang WhatsApp Web...", Toast.LENGTH_SHORT).show()
                }
                .testTag("refresh_qr_button"),
              contentAlignment = Alignment.Center
            ) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                  imageVector = Icons.Default.Refresh,
                  contentDescription = null,
                  tint = IOSTextPrimary,
                  modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                  text = "Muat Ulang QR",
                  fontSize = 12.sp,
                  fontWeight = FontWeight.Medium,
                  color = IOSTextPrimary
                )
              }
            }

            Box(
              modifier = Modifier
                .weight(1.2f)
                .height(44.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(Brush.horizontalGradient(listOf(ElectricMint, ElectricMintDark)))
                .clickable {
                  onConfirmLinkedCompanion("WhatsApp Web Companion (Resmi)")
                  Toast.makeText(context, "WhatsApp Berhasil Ditautkan!", Toast.LENGTH_SHORT).show()
                  onDismiss()
                }
                .testTag("confirm_qr_linked_button"),
              contentAlignment = Alignment.Center
            ) {
              Text(
                text = "Sudah Pindai QR",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF022115)
              )
            }
          }
        } else {
          // =========================================================
          // TAB 1: KODE PAIRING 8-KARAKTER RESMI DARI SERVER WHATSAPP
          // =========================================================
          Box(
            modifier = Modifier
              .fillMaxWidth()
              .clip(RoundedCornerShape(16.dp))
              .background(Color.White.copy(alpha = 0.05f))
              .border(BorderStroke(1.dp, Color.White.copy(alpha = 0.12f)), RoundedCornerShape(16.dp))
              .padding(12.dp)
          ) {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
              Text(
                text = "🔑 Tautkan via Nomor Telepon:",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = ElectricMint
              )
              Text(
                text = "Masukkan nomor Anda. Sistem akan meminta 8 digit kode pairing resmi langsung dari server WhatsApp.",
                fontSize = 11.sp,
                color = IOSTextSecondary,
                lineHeight = 15.sp
              )
            }
          }

          Spacer(modifier = Modifier.height(12.dp))

          // Phone input
          OutlinedTextField(
            value = phoneInput,
            onValueChange = { phoneInput = it },
            modifier = Modifier
              .fillMaxWidth()
              .testTag("whatsapp_pairing_phone_input"),
            placeholder = {
              Text(
                text = "Contoh: 0881010271751 atau 62881...",
                color = IOSTextTertiary,
                fontSize = 13.sp
              )
            },
            leadingIcon = {
              Icon(
                imageVector = Icons.Default.PhoneAndroid,
                contentDescription = null,
                tint = ElectricMint,
                modifier = Modifier.size(18.dp)
              )
            },
            singleLine = true,
            keyboardOptions = KeyboardOptions(
              keyboardType = KeyboardType.Phone,
              imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(
              onDone = {
                val clean = normalizePhoneNumber(phoneInput)
                if (clean.length >= 9) {
                  isRequestingPairingCode = true
                  WhatsAppWebManager.triggerPairWithPhone(clean)
                }
              }
            ),
            shape = RoundedCornerShape(16.dp),
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

          // Button Minta Kode Pairing Resmi
          val isValidPhone = phoneInput.trim().length >= 9
          Box(
            modifier = Modifier
              .fillMaxWidth()
              .height(44.dp)
              .clip(RoundedCornerShape(14.dp))
              .background(
                if (isValidPhone) {
                  Brush.horizontalGradient(listOf(ElectricMint, ElectricMintDark))
                } else {
                  Brush.horizontalGradient(listOf(Color.White.copy(alpha = 0.15f), Color.White.copy(alpha = 0.10f)))
                }
              )
              .clickable(enabled = isValidPhone && !isRequestingPairingCode) {
                val clean = normalizePhoneNumber(phoneInput)
                isRequestingPairingCode = true
                WhatsAppWebManager.triggerPairWithPhone(clean)
                Toast.makeText(context, "Meminta kode pairing resmi dari WhatsApp...", Toast.LENGTH_SHORT).show()
              }
              .testTag("generate_pairing_code_button"),
            contentAlignment = Alignment.Center
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              if (isRequestingPairingCode) {
                CircularProgressIndicator(
                  modifier = Modifier.size(16.dp),
                  color = Color(0xFF022115),
                  strokeWidth = 2.dp
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                  text = "Menghubungi Server WhatsApp...",
                  fontWeight = FontWeight.Bold,
                  fontSize = 12.sp,
                  color = Color(0xFF022115)
                )
              } else {
                Icon(
                  imageVector = Icons.Default.Key,
                  contentDescription = null,
                  tint = if (isValidPhone) Color(0xFF022115) else IOSTextTertiary,
                  modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                  text = if (detectedCode.isNotBlank()) "Minta Kode Baru" else "Dapatkan Kode Pairing Resmi",
                  fontWeight = FontWeight.Bold,
                  fontSize = 12.sp,
                  color = if (isValidPhone) Color(0xFF022115) else IOSTextTertiary
                )
              }
            }
          }

          // Instructions for entering code into WhatsApp
          Spacer(modifier = Modifier.height(12.dp))
          Box(
            modifier = Modifier
              .fillMaxWidth()
              .clip(RoundedCornerShape(16.dp))
              .background(ElectricMint.copy(alpha = 0.08f))
              .border(BorderStroke(1.dp, ElectricMint.copy(alpha = 0.25f)), RoundedCornerShape(16.dp))
              .padding(12.dp)
          ) {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
              Text(
                text = "📲 Masukkan di WhatsApp Anda:",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = ElectricMint
              )
              Text(
                text = "1. Buka WhatsApp > Perangkat Tertaut > Tautkan Perangkat",
                fontSize = 11.sp,
                color = IOSTextSecondary,
                lineHeight = 15.sp
              )
              Text(
                text = "2. Ketuk 'Tautkan dengan nomor telepon saja' di bagian bawah",
                fontSize = 11.sp,
                color = IOSTextSecondary,
                lineHeight = 15.sp
              )
              Text(
                text = "3. Masukkan kode 8 karakter resmi yang muncul di layar",
                fontSize = 11.sp,
                color = IOSTextSecondary,
                lineHeight = 15.sp
              )
            }
          }

          // If code is detected from official DOM, display prominently
          val activeCode = detectedCode.ifBlank { "" }
          AnimatedVisibility(
            visible = activeCode.isNotBlank(),
            enter = fadeIn(),
            exit = fadeOut()
          ) {
            Column(
              modifier = Modifier
                .fillMaxWidth()
                .padding(top = 12.dp),
              horizontalAlignment = Alignment.CenterHorizontally
            ) {
              Text(
                text = "KODE PAIRING RESMI WHATSAPP",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = ElectricMint,
                letterSpacing = 1.sp
              )

              Spacer(modifier = Modifier.height(8.dp))

              val cleanCode = activeCode.replace("-", "")
              val p1 = cleanCode.take(4)
              val p2 = cleanCode.drop(4).take(4)

              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                  p1.forEach { char ->
                    CodeCharacterBox(char = char.toString())
                  }
                }

                Text(
                  text = "—",
                  fontSize = 18.sp,
                  fontWeight = FontWeight.Bold,
                  color = IOSTextSecondary,
                  modifier = Modifier.padding(horizontal = 6.dp)
                )

                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                  p2.forEach { char ->
                    CodeCharacterBox(char = char.toString())
                  }
                }
              }

              Spacer(modifier = Modifier.height(8.dp))

              Row(
                modifier = Modifier
                  .clip(RoundedCornerShape(10.dp))
                  .background(Color.White.copy(alpha = 0.08f))
                  .clickable {
                    clipboardManager.setText(AnnotatedString(activeCode))
                    Toast.makeText(context, "Kode disalin: $activeCode", Toast.LENGTH_SHORT).show()
                  }
                  .padding(horizontal = 12.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
              ) {
                Icon(
                  imageVector = Icons.Default.ContentCopy,
                  contentDescription = "Salin Kode",
                  tint = ElectricMint,
                  modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                  text = "Salin Kode",
                  fontSize = 11.sp,
                  color = ElectricMint,
                  fontWeight = FontWeight.SemiBold
                )
              }
            }
          }

          Spacer(modifier = Modifier.height(14.dp))

          // Tombol Selesai / Konfirmasi
          Box(
            modifier = Modifier
              .fillMaxWidth()
              .height(44.dp)
              .clip(RoundedCornerShape(14.dp))
              .background(Brush.horizontalGradient(listOf(ElectricMint, ElectricMintDark)))
              .clickable {
                onLinkWithPairingCode(phoneInput, activeCode.ifBlank { "WHATSAPP-WEB" })
                onDismiss()
              }
              .testTag("confirm_pairing_code_button"),
            contentAlignment = Alignment.Center
          ) {
            Text(
              text = "Verifikasi & Hubungkan Perangkat",
              fontWeight = FontWeight.Bold,
              fontSize = 12.sp,
              color = Color(0xFF022115)
            )
          }
        }
      }
    }
  }
}

@Composable
private fun CodeCharacterBox(char: String) {
  Box(
    modifier = Modifier
      .size(34.dp)
      .clip(RoundedCornerShape(8.dp))
      .background(Color.White.copy(alpha = 0.12f))
      .border(BorderStroke(1.dp, ElectricMint.copy(alpha = 0.5f)), RoundedCornerShape(8.dp)),
    contentAlignment = Alignment.Center
  ) {
    Text(
      text = char,
      fontSize = 16.sp,
      fontWeight = FontWeight.ExtraBold,
      fontFamily = FontFamily.Monospace,
      color = IOSTextPrimary
    )
  }
}
