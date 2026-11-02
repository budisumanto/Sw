package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.example.ui.components.ExecutionProgressCard
import com.example.ui.components.PhoneNumberInputCard
import com.example.ui.components.RequiredPhoneDialog
import com.example.ui.components.SuccessDialog
import com.example.ui.components.TopBarHeader
import com.example.ui.components.VideoSelectorCard
import com.example.ui.theme.IOSMidnightBackground
import com.example.ui.theme.IOSMidnightDark
import com.example.viewmodel.OptimizerViewModel

@Composable
fun StatusHDOptimizerScreen(
  viewModel: OptimizerViewModel,
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current

  val phoneNumber by viewModel.phoneNumber.collectAsState()
  val showPhonePromptDialog by viewModel.showPhonePromptDialog.collectAsState()
  val selectedVideo by viewModel.selectedVideo.collectAsState()
  val processingState by viewModel.processingState.collectAsState()
  val showSuccessDialog by viewModel.showSuccessDialog.collectAsState()
  val successMessage by viewModel.successMessage.collectAsState()

  Scaffold(
    modifier = modifier
      .fillMaxSize()
      .background(IOSMidnightBackground)
      .testTag("status_hd_optimizer_screen"),
    contentWindowInsets = WindowInsets.safeDrawing,
    containerColor = IOSMidnightBackground
  ) { innerPadding ->
    // Canvas: Deep Midnight Obsidian with soft radial ambient glow
    Box(
      modifier = Modifier
        .fillMaxSize()
        .background(
          Brush.verticalGradient(
            listOf(
              IOSMidnightDark,
              IOSMidnightBackground,
              Color(0xFF040609)
            )
          )
        )
        .padding(innerPadding)
        .navigationBarsPadding(),
      contentAlignment = Alignment.TopCenter
    ) {
      // Ambient radial lighting layer at the top
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .height(300.dp)
          .background(
            Brush.radialGradient(
              colors = listOf(
                Color(0xFF10E599).copy(alpha = 0.08f),
                Color(0xFF00F2FE).copy(alpha = 0.04f),
                Color.Transparent
              )
            )
          )
      )

      Column(
        modifier = Modifier
          .fillMaxWidth()
          .widthIn(max = 600.dp)
          .verticalScroll(rememberScrollState())
          .padding(horizontal = 20.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
      ) {
        // 1. Top Header Minimalis iOS
        TopBarHeader()

        // 2. Card Nomor WhatsApp (Estetik, jelas kesan wajibnya, dengan indikator & pesan edukasi)
        PhoneNumberInputCard(
          phoneNumber = phoneNumber,
          onClickEdit = { viewModel.openPhonePromptDialog() }
        )

        // 3. Area Pemilihan Video
        VideoSelectorCard(
          selectedVideo = selectedVideo,
          onVideoSelected = { uri -> viewModel.onVideoSelected(uri, context) }
        )

        // 4. Tombol Aksi Utama ("Optimalkan Kualitas") & Status Berjalan (Compressing / Uploading)
        ExecutionProgressCard(
          processingState = processingState,
          hasVideoSelected = selectedVideo != null,
          onStartOptimize = { viewModel.startFullAutomatedPipeline(context) },
          onCancel = { viewModel.cancelOptimization() },
          onReset = { viewModel.resetOptimization() }
        )

        Spacer(modifier = Modifier.height(24.dp))
      }

      // Pop-up Wajib Isi Nomor WhatsApp saat pertama kali buka atau saat diedit
      if (showPhonePromptDialog) {
        RequiredPhoneDialog(
          initialPhone = phoneNumber,
          onSave = { newPhone -> viewModel.savePhoneNumberFromDialog(newPhone) }
        )
      }

      // Pop-up Sukses Kirim ke WhatsApp
      if (showSuccessDialog) {
        SuccessDialog(
          message = successMessage,
          onOpenWhatsApp = { viewModel.openWhatsApp(context) },
          onDismiss = { viewModel.dismissSuccessDialog() }
        )
      }
    }
  }
}
