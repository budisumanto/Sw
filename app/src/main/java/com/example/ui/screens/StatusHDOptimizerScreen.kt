package com.example.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.example.ui.components.ExecutionProgressCard
import com.example.ui.components.PhoneNumberInputCard
import com.example.ui.components.RequiredPhoneDialog
import com.example.ui.components.SuccessDialog
import com.example.ui.components.TopBarHeader
import com.example.ui.components.VideoSelectorCard
import com.example.ui.theme.entrance
import com.example.ui.util.ContentWidth
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

  ContentWidth {
    Column(
      modifier = modifier
        .fillMaxSize()
        .statusBarsPadding()
        .navigationBarsPadding()
        .verticalScroll(rememberScrollState())
        .padding(horizontal = 20.dp, vertical = 12.dp)
        .testTag("status_hd_optimizer_screen"),
      verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
      // 1. Top Header
      TopBarHeader(modifier = Modifier.entrance(0))

      // 2. Card Nomor WhatsApp
      PhoneNumberInputCard(
        phoneNumber = phoneNumber,
        onClickEdit = { viewModel.openPhonePromptDialog() },
        modifier = Modifier.entrance(1)
      )

      // 3. Card Pemilih Video
      VideoSelectorCard(
        selectedVideo = selectedVideo,
        onVideoSelected = { uri -> viewModel.onVideoSelected(uri, context) },
        modifier = Modifier.entrance(2)
      )

      // 4. Tombol Aksi Utama & Status Berjalan
      ExecutionProgressCard(
        processingState = processingState,
        hasVideoSelected = selectedVideo != null,
        onStartOptimize = { viewModel.startFullAutomatedPipeline(context) },
        onCancel = { viewModel.cancelOptimization() },
        onReset = { viewModel.resetOptimization() },
        modifier = Modifier.entrance(3)
      )

      Spacer(modifier = Modifier.height(32.dp))
    }
  }

  // Pop-up Modal Wajib Isi Nomor WhatsApp
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
