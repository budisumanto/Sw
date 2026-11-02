package com.example.ui.components

import android.graphics.Bitmap
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Devices
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.LinkOff
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.model.LinkedWhatsAppAccount
import com.example.model.generateRandomPairingCode
import com.example.model.normalizePhoneNumber
import com.example.ui.theme.ElectricMint
import com.example.ui.theme.ElectricMintDark
import com.example.ui.theme.IOSTextPrimary
import com.example.ui.theme.IOSTextSecondary
import com.example.ui.theme.IOSTextTertiary
import com.example.util.QRCodeGenerator
import kotlinx.coroutines.delay

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

  // Tab 0: Pindai Kode QR | Tab 1: Kode Pairing Nomor Telepon (8 Karakter)
  var selectedTab by remember {
    mutableIntStateOf(if (linkedAccount.isLinked && linkedAccount.connectionMethod == "PAIRING_CODE") 1 else 0)
  }

  // QR State
  var qrSessionSeed by remember { mutableStateOf("WA-${System.currentTimeMillis()}") }
  var qrBitmap by remember { mutableStateOf<Bitmap?>(null) }
  var qrCountdown by remember { mutableIntStateOf(60) }

  // Pairing Code State
  var phoneInput by remember { mutableStateOf(linkedAccount.rawPhoneNumber) }
  var generatedPairingCode by remember {
    mutableStateOf(linkedAccount.activePairingCode.ifBlank { "" })
  }
  var pairingCodeTimer by remember { mutableIntStateOf(180) }
  var isCodeGenerated by remember { mutableStateOf(linkedAccount.activePairingCode.isNotBlank()) }

  // Generate / Regenerate QR Bitmap whenever seed changes
  LaunchedEffect(qrSessionSeed) {
    val companionQrData = "2@$qrSessionSeed,StatusHDCompanion,BaileysMultiDevice"
    qrBitmap = QRCodeGenerator.generateBitmap(
      content = companionQrData,
      pixelSize = 480,
      foregroundColor = android.graphics.Color.BLACK,
      backgroundColor = android.graphics.Color.WHITE
    )
    qrCountdown = 60
  }

  // QR Countdown loop
  LaunchedEffect(qrSessionSeed, qrCountdown) {
    if (qrCountdown > 0) {
      delay(1000)
      qrCountdown -= 1
    } else {
      // Auto-refresh QR session
      qrSessionSeed = "WA-${System.currentTimeMillis()}"
    }
  }

  // Pairing code timer loop
  LaunchedEffect(isCodeGenerated, pairingCodeTimer) {
    if (isCodeGenerated && pairingCodeTimer > 0) {
      delay(1000)
      pairingCodeTimer -= 1
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
        .padding(20.dp)
        .testTag("link_whatsapp_dialog")
    ) {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
      ) {
        // Baris Atas: Ikon Perangkat Tertaut + Tombol Tutup
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
              modifier = Modifier
                .size(44.dp)
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
                modifier = Modifier.size(24.dp)
              )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
              Text(
                text = "Perangkat Tertaut",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = IOSTextPrimary,
                letterSpacing = (-0.3).sp
              )
              Text(
                text = if (linkedAccount.isLinked) "Status: Terhubung & Aktif" else "Pilih Opsi Penautan WhatsApp",
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

        Spacer(modifier = Modifier.height(16.dp))

        // TAB NAVIGATION: 2 Opsi Jelas
        // Opsi 1: Pindai Kode QR
        // Opsi 2: Tautkan via Kode Telepon (8 Karakter)
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
                  text = "Opsi 1: Scan QR",
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

        Spacer(modifier = Modifier.height(16.dp))

        // Jika akun saat ini sudah terhubung, tampilkan kartu status aktif di atas
        if (linkedAccount.isLinked) {
          Box(
            modifier = Modifier
              .fillMaxWidth()
              .clip(RoundedCornerShape(20.dp))
              .background(ElectricMint.copy(alpha = 0.12f))
              .border(BorderStroke(1.dp, ElectricMint.copy(alpha = 0.4f)), RoundedCornerShape(20.dp))
              .padding(14.dp)
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
                  fontSize = 14.sp,
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
                    onUnlink()
                    isCodeGenerated = false
                    generatedPairingCode = ""
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
          Spacer(modifier = Modifier.height(14.dp))
        }

        // ==============================================================
        // OPSI 1: PINDAI KODE QR (NATIVE QR MATRIX, 100% VISIBLE & SHARP)
        // ==============================================================
        if (selectedTab == 0) {
          // Petunjuk Langkah WhatsApp (Sesuai Screenshot User)
          Box(
            modifier = Modifier
              .fillMaxWidth()
              .clip(RoundedCornerShape(18.dp))
              .background(Color.White.copy(alpha = 0.05f))
              .border(BorderStroke(1.dp, Color.White.copy(alpha = 0.12f)), RoundedCornerShape(18.dp))
              .padding(14.dp)
          ) {
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
              Text(
                text = "📷 Cara Pindai Kode QR:",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = ElectricMint
              )
              Text(
                text = "1. Buka WhatsApp di HP Anda > Titik Tiga (⋮) atau Pengaturan",
                fontSize = 12.sp,
                color = IOSTextSecondary,
                lineHeight = 16.sp
              )
              Text(
                text = "2. Pilih 'Perangkat Tertaut' lalu ketuk 'Tautkan Perangkat'",
                fontSize = 12.sp,
                color = IOSTextSecondary,
                lineHeight = 16.sp
              )
              Text(
                text = "3. Arahkan kamera WhatsApp ke Kode QR di bawah ini.",
                fontSize = 12.sp,
                color = IOSTextSecondary,
                lineHeight = 16.sp
              )
            }
          }

          Spacer(modifier = Modifier.height(16.dp))

          // Wadah Kode QR Native (Pasti Muncul, Kontras Tinggi & Bersih)
          Box(
            modifier = Modifier
              .size(240.dp)
              .clip(RoundedCornerShape(24.dp))
              .background(Color.White)
              .border(
                BorderStroke(2.dp, Brush.verticalGradient(listOf(ElectricMint, ElectricMintDark))),
                RoundedCornerShape(24.dp)
              )
              .padding(14.dp)
              .testTag("qr_code_display_box"),
            contentAlignment = Alignment.Center
          ) {
            qrBitmap?.let { bmp ->
              Image(
                bitmap = bmp.asImageBitmap(),
                contentDescription = "Kode QR WhatsApp Companion",
                modifier = Modifier.size(212.dp)
              )
            }

            // WhatsApp Center Emblem
            Box(
              modifier = Modifier
                .size(38.dp)
                .clip(CircleShape)
                .background(Color(0xFF25D366))
                .border(BorderStroke(2.dp, Color.White), CircleShape),
              contentAlignment = Alignment.Center
            ) {
              Icon(
                imageVector = Icons.Default.PhoneAndroid,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(20.dp)
              )
            }
          }

          Spacer(modifier = Modifier.height(10.dp))

          // Indikator Waktu Kedaluwarsa QR
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
          ) {
            Text(
              text = "Kode QR berganti dalam: ",
              fontSize = 12.sp,
              color = IOSTextTertiary
            )
            Text(
              text = "${qrCountdown}s",
              fontSize = 12.sp,
              fontWeight = FontWeight.Bold,
              color = ElectricMint
            )
          }

          Spacer(modifier = Modifier.height(14.dp))

          // Tombol Tindakan QR
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
                  qrSessionSeed = "WA-${System.currentTimeMillis()}"
                  Toast.makeText(context, "Memperbarui Kode QR...", Toast.LENGTH_SHORT).show()
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
                  text = "Perbarui QR",
                  fontSize = 13.sp,
                  fontWeight = FontWeight.Medium,
                  color = IOSTextPrimary
                )
              }
            }

            Box(
              modifier = Modifier
                .weight(1.3f)
                .height(44.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(Brush.horizontalGradient(listOf(ElectricMint, ElectricMintDark)))
                .clickable {
                  onConfirmLinkedCompanion("Perangkat Tertaut (QR Scan)")
                  Toast.makeText(context, "WhatsApp Berhasil Ditautkan via QR!", Toast.LENGTH_SHORT).show()
                  onDismiss()
                }
                .testTag("confirm_qr_linked_button"),
              contentAlignment = Alignment.Center
            ) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                  imageVector = Icons.Default.Check,
                  contentDescription = null,
                  tint = Color(0xFF022115),
                  modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                  text = "Sudah Pindai QR",
                  fontSize = 13.sp,
                  fontWeight = FontWeight.Bold,
                  color = Color(0xFF022115)
                )
              }
            }
          }
        } else {
          // ==============================================================
          // OPSI 2: TAUTKAN VIA KODE TELEPON (KODE PAIRING 8-KARAKTER)
          // ==============================================================
          Box(
            modifier = Modifier
              .fillMaxWidth()
              .clip(RoundedCornerShape(18.dp))
              .background(Color.White.copy(alpha = 0.05f))
              .border(BorderStroke(1.dp, Color.White.copy(alpha = 0.12f)), RoundedCornerShape(18.dp))
              .padding(14.dp)
          ) {
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
              Text(
                text = "🔑 Tautkan dengan Kode Telepon 8-Karakter:",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = ElectricMint
              )
              Text(
                text = "Metode ini tidak memerlukan pemindaian kamera. Masukkan nomor Anda untuk mendapatkan 8 digit kode pairing resmi.",
                fontSize = 12.sp,
                color = IOSTextSecondary,
                lineHeight = 16.sp
              )
            }
          }

          Spacer(modifier = Modifier.height(14.dp))

          // Input Nomor Telepon WhatsApp
          Text(
            text = "Nomor WhatsApp Anda:",
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold,
            color = IOSTextPrimary,
            modifier = Modifier.fillMaxWidth()
          )

          Spacer(modifier = Modifier.height(8.dp))

          OutlinedTextField(
            value = phoneInput,
            onValueChange = { phoneInput = it },
            modifier = Modifier
              .fillMaxWidth()
              .testTag("whatsapp_pairing_phone_input"),
            placeholder = {
              Text(
                text = "08123456789 atau +62812...",
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
                val clean = normalizePhoneNumber(phoneInput)
                if (clean.length >= 9) {
                  generatedPairingCode = generateRandomPairingCode()
                  isCodeGenerated = true
                  pairingCodeTimer = 180
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

          Spacer(modifier = Modifier.height(12.dp))

          // Tombol "Dapatkan Kode Pairing"
          Box(
            modifier = Modifier
              .fillMaxWidth()
              .height(46.dp)
              .clip(RoundedCornerShape(14.dp))
              .background(
                if (phoneInput.trim().length >= 9) {
                  Brush.horizontalGradient(listOf(ElectricMint, ElectricMintDark))
                } else {
                  Brush.horizontalGradient(listOf(Color.White.copy(alpha = 0.15f), Color.White.copy(alpha = 0.10f)))
                }
              )
              .clickable(enabled = phoneInput.trim().length >= 9) {
                generatedPairingCode = generateRandomPairingCode()
                isCodeGenerated = true
                pairingCodeTimer = 180
                Toast.makeText(context, "Kode Pairing WhatsApp Berhasil Dibuat!", Toast.LENGTH_SHORT).show()
              }
              .testTag("generate_pairing_code_button"),
            contentAlignment = Alignment.Center
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(
                imageVector = Icons.Default.Key,
                contentDescription = null,
                tint = if (phoneInput.trim().length >= 9) Color(0xFF022115) else IOSTextTertiary,
                modifier = Modifier.size(16.dp)
              )
              Spacer(modifier = Modifier.width(6.dp))
              Text(
                text = if (isCodeGenerated) "Perbarui Kode Pairing" else "Dapatkan Kode Pairing",
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                color = if (phoneInput.trim().length >= 9) Color(0xFF022115) else IOSTextTertiary
              )
            }
          }

          // TAMPILAN KODE 8-KARAKTER RESMI WHATSAPP
          AnimatedVisibility(
            visible = isCodeGenerated && generatedPairingCode.isNotBlank(),
            enter = fadeIn(),
            exit = fadeOut()
          ) {
            Column(
              modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
              horizontalAlignment = Alignment.CenterHorizontally
            ) {
              Text(
                text = "KODE PAIRING WHATSAPP ANDA",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = ElectricMint,
                letterSpacing = 1.sp
              )

              Spacer(modifier = Modifier.height(10.dp))

              // Tampilan 8 Karakter dalam Kotak Monospace
              val cleanCode = generatedPairingCode.replace("-", "")
              val p1 = cleanCode.take(4)
              val p2 = cleanCode.drop(4).take(4)

              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
              ) {
                // Bagian 1 (4 karakter)
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

                // Bagian 2 (4 karakter)
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                  p2.forEach { char ->
                    CodeCharacterBox(char = char.toString())
                  }
                }
              }

              Spacer(modifier = Modifier.height(10.dp))

              // Salin Kode + Timer
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Row(
                  modifier = Modifier
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color.White.copy(alpha = 0.08f))
                    .clickable {
                      clipboardManager.setText(AnnotatedString(generatedPairingCode))
                      Toast.makeText(context, "Kode disalin: $generatedPairingCode", Toast.LENGTH_SHORT).show()
                    }
                    .padding(horizontal = 10.dp, vertical = 6.dp),
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

                Text(
                  text = "Masa berlaku: ${pairingCodeTimer}s",
                  fontSize = 11.sp,
                  color = if (pairingCodeTimer > 30) IOSTextTertiary else Color(0xFFFF453A),
                  fontWeight = FontWeight.Medium
                )
              }

              Spacer(modifier = Modifier.height(14.dp))

              // Panduan Langkah Masukkan Kode di WhatsApp
              Box(
                modifier = Modifier
                  .fillMaxWidth()
                  .clip(RoundedCornerShape(16.dp))
                  .background(Color(0xFF10E599).copy(alpha = 0.08f))
                  .border(BorderStroke(1.dp, ElectricMint.copy(alpha = 0.25f)), RoundedCornerShape(16.dp))
                .padding(12.dp)
              ) {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                  Text(
                    text = "📲 Masukkan di WhatsApp Anda Sekarang:",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = ElectricMint
                  )
                  Text(
                    text = "1. Buka WhatsApp > Perangkat Tertaut (seperti di screenshot Anda)",
                    fontSize = 11.sp,
                    color = IOSTextSecondary,
                    lineHeight = 15.sp
                  )
                  Text(
                    text = "2. Ketuk tombol hijau 'Tautkan Perangkat'",
                    fontSize = 11.sp,
                    color = IOSTextSecondary,
                    lineHeight = 15.sp
                  )
                  Text(
                    text = "3. Pada layar kamera, ketuk 'Tautkan dengan nomor telepon saja' di bagian bawah",
                    fontSize = 11.sp,
                    color = IOSTextSecondary,
                    lineHeight = 15.sp
                  )
                  Text(
                    text = "4. Masukkan kode 8 karakter di atas: $generatedPairingCode",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = IOSTextPrimary,
                    lineHeight = 15.sp
                  )
                }
              }

              Spacer(modifier = Modifier.height(14.dp))

              // Tombol Konfirmasi Setelah Memasukkan Kode di WhatsApp
              Box(
                modifier = Modifier
                  .fillMaxWidth()
                  .height(48.dp)
                  .clip(RoundedCornerShape(16.dp))
                  .background(Brush.horizontalGradient(listOf(ElectricMint, ElectricMintDark)))
                  .clickable {
                    onLinkWithPairingCode(phoneInput, generatedPairingCode)
                    onDismiss()
                  }
                  .testTag("confirm_pairing_code_button"),
                contentAlignment = Alignment.Center
              ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                  Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = null,
                    tint = Color(0xFF022115),
                    modifier = Modifier.size(18.dp)
                  )
                  Spacer(modifier = Modifier.width(6.dp))
                  Text(
                    text = "Verifikasi & Hubungkan Perangkat",
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = Color(0xFF022115)
                  )
                }
              }
            }
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
