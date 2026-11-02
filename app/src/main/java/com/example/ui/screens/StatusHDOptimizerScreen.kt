package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
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
import androidx.compose.foundation.clickable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Link
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.model.ProcessingState
import com.example.ui.components.ExecutionProgressCard
import com.example.ui.components.IOSLiquidGlassCard
import com.example.ui.components.LinkWhatsAppDialog
import com.example.ui.components.ResultDisplayCard
import com.example.ui.components.TopBarHeader
import com.example.ui.components.VideoPlayerDialog
import com.example.ui.components.VideoSelectorCard
import com.example.ui.theme.IOSMidnightBackground
import com.example.ui.theme.IOSMidnightDark
import com.example.ui.theme.IOSTextSecondary
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
  val linkedAccount by viewModel.linkedAccount.collectAsState()
  val showLinkDialog by viewModel.showLinkDialog.collectAsState()
  val directSendState by viewModel.directSendState.collectAsState()

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
        // 1. Top Header Minimalis iOS dengan status Perangkat Tertaut
        TopBarHeader(
          linkedAccount = linkedAccount,
          onOpenLinkDialog = { viewModel.openLinkDialog() }
        )

        // Banner Perangkat Belum Tertaut (jika user belum menghubungkan WhatsApp)
        if (!linkedAccount.isLinked && processingState !is ProcessingState.Success) {
          IOSLiquidGlassCard(
            modifier = Modifier
              .fillMaxWidth()
              .clickable { viewModel.openLinkDialog() }
              .testTag("unlinked_device_banner"),
            cornerRadius = 20.dp,
            padding = 14.dp,
            fillColor = Color(0xFFFF9F0A).copy(alpha = 0.08f)
          ) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              verticalAlignment = Alignment.CenterVertically
            ) {
              Box(
                modifier = Modifier
                  .size(34.dp)
                  .clip(CircleShape)
                  .background(Color(0xFFFF9F0A).copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
              ) {
                Icon(
                  imageVector = Icons.Default.Link,
                  contentDescription = null,
                  tint = Color(0xFFFF9F0A),
                  modifier = Modifier.size(17.dp)
                )
              }
              Spacer(modifier = Modifier.width(12.dp))
              Column(modifier = Modifier.weight(1f)) {
                Text(
                  text = "Hubungkan WhatsApp Dulu",
                  fontSize = 13.sp,
                  fontWeight = FontWeight.Bold,
                  color = Color(0xFFFFB340)
                )
                Text(
                  text = "Kirim video ke nomor sendiri untuk bypass kompresi status.",
                  fontSize = 11.sp,
                  color = IOSTextSecondary,
                  lineHeight = 15.sp
                )
              }
              Spacer(modifier = Modifier.width(8.dp))
              Text(
                text = "Tautkan",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFFFB340)
              )
            }
          }
        }

        // 2. Area Pemilihan Video (State 1: Hero Glass Card / State 2: Selected Video)
        if (processingState !is ProcessingState.Success) {
          VideoSelectorCard(
            selectedVideo = selectedVideo,
            onVideoSelected = { uri -> viewModel.onVideoSelected(uri, context) }
          )
        }

        // 3. Tombol Aksi Utama & Status Berjalan (State 2: Optimalkan Kualitas / State 3: Processing)
        if (processingState !is ProcessingState.Success) {
          ExecutionProgressCard(
            processingState = processingState,
            hasVideoSelected = selectedVideo != null,
            onStartOptimize = { viewModel.startOptimization(context) },
            onCancel = { viewModel.cancelOptimization() },
            onReset = { viewModel.resetOptimization() }
          )
        }

        // 4. Hasil & Opsi Berbagi (State 4: Selesai & Bagikan)
        AnimatedVisibility(
          visible = processingState is ProcessingState.Success,
          enter = fadeIn(),
          exit = fadeOut()
        ) {
          val successState = processingState as? ProcessingState.Success
          if (successState != null) {
            ResultDisplayCard(
              result = successState.result,
              linkedAccount = linkedAccount,
              directSendState = directSendState,
              onOpenLinkDialog = { viewModel.openLinkDialog() },
              onSendDirectToServer = { segment -> viewModel.sendDirectToServer(context, segment) },
              onSendAllDirectToServer = { segments -> viewModel.sendAllDirectToServer(context, segments) },
              onResetDirectSendState = { viewModel.resetDirectSendState() },
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
          onShare = { seg -> viewModel.sendDirectToServer(context, seg) }
        )
      }

      // Dialog Hubungkan WhatsApp (Perangkat Tertaut)
      if (showLinkDialog) {
        LinkWhatsAppDialog(
          linkedAccount = linkedAccount,
          onLinkNumber = { number -> viewModel.linkWhatsAppNumber(context, number) },
          onConfirmLinkedCompanion = { label -> viewModel.markCompanionLinked(label) },
          onUnlink = { viewModel.unlinkWhatsAppNumber(context) },
          onDismiss = { viewModel.closeLinkDialog() }
        )
      }
    }
  }
}
