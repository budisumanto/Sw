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
import com.example.ui.components.CommandInspectorCard
import com.example.ui.components.ExecutionProgressCard
import com.example.ui.components.ResultDisplayCard
import com.example.ui.components.SmartEngineGlassCard
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
  val smartConfig by viewModel.smartConfig.collectAsState()
  val autoSplitEnabled by viewModel.forceAutoSplit.collectAsState()
  val processingState by viewModel.processingState.collectAsState()
  val isCommandExpanded by viewModel.isCommandInspectorExpanded.collectAsState()
  val isLogsExpanded by viewModel.isLiveLogsExpanded.collectAsState()
  val isGeneratingSample by viewModel.isGeneratingSample.collectAsState()
  val previewSegment by viewModel.previewSegment.collectAsState()

  val generatedCommand = viewModel.getGeneratedCommand()

  Scaffold(
    modifier = modifier
      .fillMaxSize()
      .background(DarkObsidian)
      .testTag("status_hd_optimizer_screen"),
    contentWindowInsets = WindowInsets.safeDrawing,
    containerColor = DarkObsidian
  ) { innerPadding ->
    // Liquid Glass Ambient Background Mesh
    Box(
      modifier = Modifier
        .fillMaxSize()
        .background(
          Brush.verticalGradient(
            listOf(
              Color(0xFF07120D),
              DarkObsidian,
              Color(0xFF050E0B)
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
          .widthIn(max = 640.dp)
          .verticalScroll(rememberScrollState())
          .padding(horizontal = 16.dp, vertical = 6.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
      ) {
        // App Header
        TopBarHeader()

        // 1. Media Selector (Liquid Glass)
        VideoSelectorCard(
          selectedVideo = selectedVideo,
          isGeneratingSample = isGeneratingSample,
          onVideoSelected = { uri -> viewModel.onVideoSelected(uri, context) },
          onLoadSample = { isLong -> viewModel.onLoadSampleVideo(context, isLong) }
        )

        // 2. Smart Engine Glass Card (Auto-detected resolution, CRF 20, 4000k limit)
        SmartEngineGlassCard(
          config = smartConfig,
          autoSplitEnabled = autoSplitEnabled,
          onAutoSplitToggled = { enabled -> viewModel.setForceAutoSplit(enabled) }
        )

        // 3. Dynamic Command Inspector (Liquid Glass)
        CommandInspectorCard(
          command = generatedCommand,
          isExpanded = isCommandExpanded,
          onToggleExpand = { viewModel.toggleCommandInspector() }
        )

        // 4. Execution & Status View (Liquid Glass Action Button & Progress)
        ExecutionProgressCard(
          processingState = processingState,
          isLogsExpanded = isLogsExpanded,
          hasVideoSelected = selectedVideo != null,
          onStartOptimize = { viewModel.startOptimization(context) },
          onCancel = { viewModel.cancelOptimization() },
          onReset = { viewModel.resetOptimization() },
          onToggleLogs = { viewModel.toggleLiveLogs() }
        )

        // 5. Result Display (Liquid Glass)
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
              onPreviewSegment = { segment -> viewModel.setPreviewSegment(segment) }
            )
          }
        }

        Spacer(modifier = Modifier.height(20.dp))
      }

      // Video Player Dialog overlay
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
