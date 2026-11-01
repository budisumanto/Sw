package com.example.ui.components

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.webkit.CookieManager
import android.webkit.JavascriptInterface
import android.webkit.WebChromeClient
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
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
import androidx.compose.material.icons.filled.Devices
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.LinkOff
import androidx.compose.material.icons.filled.OpenInBrowser
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.model.LinkedWhatsAppAccount
import com.example.ui.theme.ElectricMint
import com.example.ui.theme.ElectricMintDark
import com.example.ui.theme.IOSTextPrimary
import com.example.ui.theme.IOSTextSecondary
import com.example.ui.theme.IOSTextTertiary

private const val DESKTOP_USER_AGENT =
  "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/122.0.0.0 Safari/537.36"

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun LinkWhatsAppDialog(
  linkedAccount: LinkedWhatsAppAccount,
  onLinkNumber: (String) -> Unit,
  onConfirmLinkedCompanion: (String) -> Unit,
  onUnlink: () -> Unit,
  onDismiss: () -> Unit,
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current
  var selectedTab by remember { mutableIntStateOf(if (linkedAccount.isLinked) 1 else 0) }
  var isWebViewLoading by remember { mutableStateOf(true) }
  var webViewRef by remember { mutableStateOf<WebView?>(null) }
  var manualPhoneInput by remember { mutableStateOf(linkedAccount.rawPhoneNumber) }
  var webViewError by remember { mutableStateOf<String?>(null) }

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
                text = if (linkedAccount.isLinked) "Status: Terhubung & Aktif" else "Tautkan Akun WhatsApp Anda",
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

        // Tabs: [1. Scan QR WhatsApp] & [2. Info / Nomor Sendiri]
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
                  text = "Scan QR / Pairing",
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
                  imageVector = Icons.Default.PhoneAndroid,
                  contentDescription = null,
                  modifier = Modifier.size(16.dp),
                  tint = if (selectedTab == 1) ElectricMint else IOSTextSecondary
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                  text = "Status / Nomor",
                  fontSize = 12.sp,
                  fontWeight = if (selectedTab == 1) FontWeight.Bold else FontWeight.Normal,
                  color = if (selectedTab == 1) IOSTextPrimary else IOSTextSecondary
                )
              }
            }
          )
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (selectedTab == 0) {
          // TAB 0: TAMPILAN LIVE QR CODE & PAIRING WHATSAPP WEB
          // Petunjuk Langkah Resmi WhatsApp (Sesuai Screenshot User)
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
                text = "📱 Cara Menautkan Perangkat:",
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
                text = "3. Arahkan kamera ke Kode QR di bawah ini (atau gunakan 'Tautkan dengan nomor telepon' di WhatsApp Web untuk kode 8 karakter jika hanya memakai 1 HP).",
                fontSize = 12.sp,
                color = IOSTextSecondary,
                lineHeight = 16.sp
              )
            }
          }

          Spacer(modifier = Modifier.height(14.dp))

          // Embedded WhatsApp Web Container (Desktop UA)
          Box(
            modifier = Modifier
              .fillMaxWidth()
              .height(320.dp)
              .clip(RoundedCornerShape(20.dp))
              .background(Color(0xFF111B21))
              .border(
                BorderStroke(1.dp, Brush.verticalGradient(listOf(Color.White.copy(alpha = 0.25f), Color.Transparent))),
                RoundedCornerShape(20.dp)
              ),
            contentAlignment = Alignment.Center
          ) {
            AndroidView(
              modifier = Modifier.fillMaxSize(),
              factory = { ctx ->
                WebView(ctx).apply {
                  settings.apply {
                    javaScriptEnabled = true
                    domStorageEnabled = true
                    databaseEnabled = true
                    useWideViewPort = true
                    loadWithOverviewMode = true
                    setSupportZoom(true)
                    builtInZoomControls = true
                    displayZoomControls = false
                    cacheMode = WebSettings.LOAD_DEFAULT
                    userAgentString = DESKTOP_USER_AGENT
                  }

                  val cookieManager = CookieManager.getInstance()
                  cookieManager.setAcceptCookie(true)
                  cookieManager.setAcceptThirdPartyCookies(this, true)

                  // JS Interface to notify login status
                  addJavascriptInterface(object {
                    @JavascriptInterface
                    fun notifyLoginSuccess(info: String) {
                      onConfirmLinkedCompanion("Perangkat Tertaut WhatsApp Web")
                    }
                  }, "AndroidBridge")

                  webViewClient = object : WebViewClient() {
                    override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                      super.onPageStarted(view, url, favicon)
                      isWebViewLoading = true
                      webViewError = null
                    }

                    override fun onPageFinished(view: WebView?, url: String?) {
                      super.onPageFinished(view, url)
                      isWebViewLoading = false
                      // Inject lightweight detector for chat list or logged-in state
                      view?.evaluateJavascript(
                        """
                        (function() {
                          function checkLogin() {
                            if (document.querySelector('#side') || document.querySelector('[data-testid="chat-list"]')) {
                              if (window.AndroidBridge) {
                                window.AndroidBridge.notifyLoginSuccess('connected');
                              }
                            }
                          }
                          setInterval(checkLogin, 3000);
                        })();
                        """.trimIndent(),
                        null
                      )
                    }

                    override fun onReceivedError(view: WebView?, request: WebResourceRequest?, error: WebResourceError?) {
                      super.onReceivedError(view, request, error)
                      if (request?.isForMainFrame == true) {
                        isWebViewLoading = false
                        webViewError = "Koneksi terganggu. Pastikan internet aktif."
                      }
                    }
                  }

                  webChromeClient = object : WebChromeClient() {
                    override fun onProgressChanged(view: WebView?, newProgress: Int) {
                      if (newProgress >= 90) {
                        isWebViewLoading = false
                      }
                    }
                  }

                  loadUrl("https://web.whatsapp.com")
                  webViewRef = this
                }
              },
              update = { webView ->
                webViewRef = webView
              }
            )

            // Loading indicator overlay
            if (isWebViewLoading) {
              Box(
                modifier = Modifier
                  .fillMaxSize()
                  .background(Color(0xFF111B21).copy(alpha = 0.85f)),
                contentAlignment = Alignment.Center
              ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                  CircularProgressIndicator(color = ElectricMint, strokeWidth = 3.dp)
                  Spacer(modifier = Modifier.height(10.dp))
                  Text(
                    text = "Memuat QR Code WhatsApp...",
                    fontSize = 12.sp,
                    color = IOSTextSecondary
                  )
                }
              }
            }

            // Error overlay with retry
            webViewError?.let { err ->
              Box(
                modifier = Modifier
                  .fillMaxSize()
                  .background(Color(0xFF111B21).copy(alpha = 0.95f))
                  .padding(16.dp),
                contentAlignment = Alignment.Center
              ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                  Text(
                    text = err,
                    color = Color(0xFFFF453A),
                    fontSize = 13.sp,
                    textAlign = TextAlign.Center
                  )
                  Spacer(modifier = Modifier.height(12.dp))
                  Box(
                    modifier = Modifier
                      .clip(RoundedCornerShape(12.dp))
                      .background(Color.White.copy(alpha = 0.1f))
                      .clickable { webViewRef?.reload() }
                      .padding(horizontal = 14.dp, vertical = 8.dp)
                  ) {
                    Text("Coba Muat Ulang", color = IOSTextPrimary, fontSize = 12.sp)
                  }
                }
              }
            }
          }

          Spacer(modifier = Modifier.height(10.dp))

          // Controls Bar: [Muat Ulang QR] & [Tandai Sudah Terhubung]
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            Box(
              modifier = Modifier
                .weight(1f)
                .height(44.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(Color.White.copy(alpha = 0.08f))
                .border(BorderStroke(1.dp, Color.White.copy(alpha = 0.18f)), RoundedCornerShape(14.dp))
                .clickable {
                  webViewRef?.reload()
                  Toast.makeText(context, "Memuat ulang QR Code...", Toast.LENGTH_SHORT).show()
                },
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
                  text = "Segarkan QR",
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
                  onConfirmLinkedCompanion("Perangkat Tertaut WhatsApp")
                  Toast.makeText(context, "WhatsApp Ditandai Tertaut!", Toast.LENGTH_SHORT).show()
                  onDismiss()
                }
                .testTag("confirm_linked_button"),
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
                  text = "Sudah Scan QR",
                  fontSize = 13.sp,
                  fontWeight = FontWeight.Bold,
                  color = Color(0xFF022115)
                )
              }
            }
          }
        } else {
          // TAB 1: STATUS KONEKSI & ATUR NOMOR PRIBADI
          if (linkedAccount.isLinked) {
            // State: Terhubung
            Box(
              modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .background(Color(0xFF10E599).copy(alpha = 0.12f))
                .border(BorderStroke(1.dp, ElectricMint.copy(alpha = 0.4f)), RoundedCornerShape(20.dp))
                .padding(16.dp)
            ) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                  modifier = Modifier
                    .size(12.dp)
                    .clip(CircleShape)
                    .background(ElectricMint)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                  Text(
                    text = "WhatsApp Terhubung",
                    fontSize = 12.sp,
                    color = ElectricMint,
                    fontWeight = FontWeight.Bold
                  )
                  Text(
                    text = linkedAccount.formattedDisplayNumber,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = IOSTextPrimary
                  )
                  Text(
                    text = "Video akan dikirim otomatis ke nomor/chat ini tanpa kompresi.",
                    fontSize = 11.sp,
                    color = IOSTextSecondary,
                    lineHeight = 15.sp
                  )
                }
              }
            }

            Spacer(modifier = Modifier.height(16.dp))
          }

          // Form Nomor WhatsApp (Opsional agar direct chat JID selalu tepat sasaran)
          Text(
            text = "Nomor WhatsApp Anda (Kirim Otomatis):",
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold,
            color = IOSTextPrimary,
            modifier = Modifier.fillMaxWidth()
          )

          Spacer(modifier = Modifier.height(8.dp))

          OutlinedTextField(
            value = manualPhoneInput,
            onValueChange = { manualPhoneInput = it },
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
                if (manualPhoneInput.isNotBlank()) {
                  onLinkNumber(manualPhoneInput)
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

          Spacer(modifier = Modifier.height(8.dp))

          Text(
            text = "Nomor ini digunakan untuk mengirim video langsung ke chat nomor Anda sendiri tanpa lewat galeri HP WhatsApp.",
            fontSize = 11.sp,
            color = IOSTextTertiary,
            modifier = Modifier.fillMaxWidth()
          )

          Spacer(modifier = Modifier.height(18.dp))

          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
          ) {
            Box(
              modifier = Modifier
                .weight(1f)
                .height(48.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(Brush.horizontalGradient(listOf(ElectricMint, ElectricMintDark)))
                .clickable {
                  if (manualPhoneInput.isNotBlank()) {
                    onLinkNumber(manualPhoneInput)
                  }
                }
                .testTag("submit_link_whatsapp_button"),
              contentAlignment = Alignment.Center
            ) {
              Text(
                text = "Simpan Nomor",
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = Color(0xFF022115)
              )
            }

            if (linkedAccount.isLinked) {
              Box(
                modifier = Modifier
                  .weight(1f)
                  .height(48.dp)
                  .clip(RoundedCornerShape(16.dp))
                  .background(Color(0xFFFF453A).copy(alpha = 0.12f))
                  .border(BorderStroke(1.dp, Color(0xFFFF453A).copy(alpha = 0.35f)), RoundedCornerShape(16.dp))
                  .clickable {
                    onUnlink()
                    manualPhoneInput = ""
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
                    fontWeight = FontWeight.Bold
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
