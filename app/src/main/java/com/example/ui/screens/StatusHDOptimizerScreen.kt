package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import com.example.model.ProcessingState
import com.example.ui.components.ExecutionProgressCard
import com.example.ui.components.ResultDisplayCard
import com.example.ui.components.TopBarHeader
import com.example.ui.components.VideoPlayerDialog
import com.example.ui.components.VideoSelectorCard
import com.example.ui.theme.DarkObsidian
import com.example.viewmodel.OptimizerViewModel

@Composable
fun StatusHDOptimizerScreen(
  viewModel: OptimizerViewModel,
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current

  val selectedVideo by viewModel.selectedVideo.collectAsState()
  val processingState by viewModel.processingState.collectAsState()
  val previewSegment by viewModel.previewSegment.collectAsState()

  Scaffold(
    modifier = modifier
      .fillMaxSize()
      .background(DarkObsidian)
      .testTag("status_hd_optimizer_screen"),
    contentWindowInsets = WindowInsets.safeDrawing,
    containerColor = DarkObsidian
  ) { innerPadding ->
    // iOS Liquid Glass Canvas with subtle emerald radial aura
    Box(
      modifier = Modifier
        .fillMaxSize()
        .background(
          Brush.verticalGradient(
            listOf(
              Color(0xFF0C1914),
              DarkObsidian,
              Color(0xFF070D0B)
            )
          )
        )
        .padding(innerPadding)
        .navigationBarsPadding(),
      contentAlignment = Alignment.TopCenter
    ) {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .widthIn(max = 600.dp)
          .verticalScroll(rememberScrollState())
          .padding(horizontal = 20.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
      ) {
        // 1. Header Minimalis
        TopBarHeader()

        // 2. Area Pemilihan Video
        VideoSelectorCard(
          selectedVideo = selectedVideo,
          onVideoSelected = { uri -> viewModel.onVideoSelected(uri, context) }
        )

        // 3. Tombol Aksi Utama & Status Berjalan
        ExecutionProgressCard(
          processingState = processingState,
          hasVideoSelected = selectedVideo != null,
          onStartOptimize = { viewModel.startOptimization(context) },
          onCancel = { viewModel.cancelOptimization() },
          onReset = { viewModel.resetOptimization() }
        )

        // 4. Hasil & Opsi Berbagi
        AnimatedVisibility(
          visible = processingState is ProcessingState.Success,
          enter = fadeIn(),
          exit = fadeOut()
        ) {
          val successState = processingState as? ProcessingState.Success
          if (successState != null) {
            ResultDisplayCard(
              result = successState.result,
              onShareToWhatsApp = { segment -> viewModel.shareSegment(context, segment) },
              onShareAll = { segments -> viewModel.shareAll(context, segments) },
              onSaveToGallery = { segment -> viewModel.saveSegment(context, segment) },
              onSaveAllToGallery = { segments -> viewModel.saveAllSegments(context, segments) },
              onPreviewSegment = { segment -> viewModel.setPreviewSegment(segment) },
              onReset = { viewModel.resetOptimization() }
            )
          }
        }

        Spacer(modifier = Modifier.height(24.dp))
      }

      // Dialog Pratinjau Pemutar Video
      previewSegment?.let { segment ->
        VideoPlayerDialog(
          segment = segment,
          onDismiss = { viewModel.setPreviewSegment(null) },
          onShare = { seg -> viewModel.shareSegment(context, seg) }
        )
      }
    }
  }
}
