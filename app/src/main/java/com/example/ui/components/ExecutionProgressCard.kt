package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.RocketLaunch
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.ProcessingState
import com.example.ui.theme.DarkBorder
import com.example.ui.theme.DarkObsidian
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceElevated
import com.example.ui.theme.MintAccent

@Composable
fun ExecutionProgressCard(
  processingState: ProcessingState,
  isLogsExpanded: Boolean,
  hasVideoSelected: Boolean,
  onStartOptimize: () -> Unit,
  onCancel: () -> Unit,
  onReset: () -> Unit,
  onToggleLogs: () -> Unit,
  modifier: Modifier = Modifier
) {
  val logListState = rememberLazyListState()

  Card(
    modifier = modifier
      .fillMaxWidth()
      .testTag("execution_progress_card"),
    shape = RoundedCornerShape(20.dp),
    colors = CardDefaults.cardColors(containerColor = DarkSurface),
    border = BorderStroke(1.dp, DarkBorder)
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(16.dp),
      verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
      when (processingState) {
        is ProcessingState.Idle -> {
          // Action Button: Start Optimization
          Button(
            onClick = onStartOptimize,
            enabled = hasVideoSelected,
            modifier = Modifier
              .fillMaxWidth()
              .height(54.dp)
              .testTag("optimize_for_wa_status_button"),
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(
              containerColor = MintAccent,
              contentColor = Color(0xFF003822),
              disabledContainerColor = DarkSurfaceElevated,
              disabledContentColor = Color(0xFF64748B)
            )
          ) {
            Icon(
              imageVector = Icons.Default.RocketLaunch,
              contentDescription = null,
              modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Text(
              text = if (hasVideoSelected) "Optimize for WA Status" else "Select a Video First",
              fontSize = 16.sp,
              fontWeight = FontWeight.Black
            )
          }
        }

        is ProcessingState.Preparing -> {
          Column(
            modifier = Modifier
              .fillMaxWidth()
              .padding(vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
          ) {
            CircularProgressIndicator(
              color = MintAccent,
              strokeWidth = 3.dp,
              modifier = Modifier.size(36.dp)
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
              text = processingState.message,
              style = MaterialTheme.typography.bodyMedium,
              fontWeight = FontWeight.Medium,
              color = Color.White
            )
          }
        }

        is ProcessingState.Compressing -> {
          // Live Real-Time Encoding Dashboard
          Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            // Header with chunk info and cancel
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                  Box(
                    modifier = Modifier
                      .size(8.dp)
                      .clip(androidx.compose.foundation.shape.CircleShape)
                      .background(MintAccent)
                  )
                  Spacer(modifier = Modifier.width(6.dp))
                  Text(
                    text = "Encoding Part ${processingState.activeChunk} of ${processingState.totalChunks}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                  )
                }

                val pct = (processingState.overallProgress * 100).toInt()
                Text(
                  text = "$pct% completed • ${(processingState.timeRenderedMs / 1000)}s rendered",
                  fontSize = 12.sp,
                  color = Color(0xFF94A3B8)
                )
              }

              OutlinedButton(
                onClick = onCancel,
                modifier = Modifier.testTag("cancel_optimization_button"),
                shape = RoundedCornerShape(8.dp),
                border = BorderStroke(1.dp, Color(0xFFEF4444)),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFEF4444))
              ) {
                Icon(
                  imageVector = Icons.Default.Stop,
                  contentDescription = null,
                  modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text("Cancel", fontSize = 12.sp, fontWeight = FontWeight.Bold)
              }
            }

            // Progress Bar
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
              LinearProgressIndicator(
                progress = { processingState.overallProgress },
                modifier = Modifier
                  .fillMaxWidth()
                  .height(8.dp)
                  .clip(RoundedCornerShape(4.dp))
                  .testTag("encoding_progress_bar"),
                color = MintAccent,
                trackColor = DarkSurfaceElevated
              )

              if (processingState.totalChunks > 1) {
                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.SpaceBetween
                ) {
                  Text(
                    text = "Part ${processingState.activeChunk} Progress",
                    fontSize = 10.sp,
                    color = Color(0xFF94A3B8)
                  )
                  Text(
                    text = "${(processingState.chunkProgress * 100).toInt()}%",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = MintAccent
                  )
                }
              }
            }

            // Live Telemetry Row
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(10.dp))
                .background(DarkSurfaceElevated)
                .padding(horizontal = 12.dp, vertical = 8.dp),
              horizontalArrangement = Arrangement.SpaceBetween
            ) {
              LiveStat(label = "Speed", value = String.format("%.2fx", processingState.speed.coerceAtLeast(0.0)))
              LiveStat(label = "FPS", value = String.format("%.0f", processingState.currentFps.coerceAtLeast(0.0)))
              LiveStat(label = "Bitrate", value = "${processingState.currentBitrateKbps.toInt()}k")
            }

            // Toggleable FFmpeg Console Logs
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onToggleLogs),
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.SpaceBetween
            ) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                  imageVector = Icons.Default.Terminal,
                  contentDescription = null,
                  tint = Color(0xFF94A3B8),
                  modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                  text = "Live FFmpeg Console",
                  fontSize = 11.sp,
                  fontWeight = FontWeight.Medium,
                  color = Color(0xFF94A3B8)
                )
              }
              Icon(
                imageVector = if (isLogsExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                contentDescription = null,
                tint = Color(0xFF94A3B8),
                modifier = Modifier.size(16.dp)
              )
            }

            AnimatedVisibility(visible = isLogsExpanded) {
              LaunchedEffect(processingState.recentLogs.size) {
                if (processingState.recentLogs.isNotEmpty()) {
                  logListState.scrollToItem(processingState.recentLogs.size - 1)
                }
              }

              Box(
                modifier = Modifier
                  .fillMaxWidth()
                  .height(120.dp)
                  .clip(RoundedCornerShape(8.dp))
                  .background(DarkObsidian)
                  .padding(8.dp)
              ) {
                LazyColumn(state = logListState) {
                  items(processingState.recentLogs) { log ->
                    Text(
                      text = log,
                      fontFamily = FontFamily.Monospace,
                      fontSize = 9.sp,
                      color = Color(0xFF6EE7B7),
                      lineHeight = 13.sp
                    )
                  }
                }
              }
            }
          }
        }

        is ProcessingState.Cancelled -> {
          Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(
                imageVector = Icons.Default.Close,
                contentDescription = null,
                tint = Color(0xFFF59E0B)
              )
              Spacer(modifier = Modifier.width(8.dp))
              Text(
                text = "Optimization Cancelled",
                color = Color.White,
                fontWeight = FontWeight.Bold
              )
            }

            Button(
              onClick = onReset,
              shape = RoundedCornerShape(8.dp),
              colors = ButtonDefaults.buttonColors(containerColor = DarkSurfaceElevated)
            ) {
              Icon(imageVector = Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(14.dp))
              Spacer(modifier = Modifier.width(4.dp))
              Text("Retry", fontSize = 12.sp)
            }
          }
        }

        is ProcessingState.Error -> {
          val context = androidx.compose.ui.platform.LocalContext.current
          var showErrorLogs by androidx.compose.runtime.remember { androidx.compose.runtime.mutableStateOf(false) }

          Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              verticalAlignment = Alignment.CenterVertically
            ) {
              Icon(
                imageVector = Icons.Default.Error,
                contentDescription = null,
                tint = Color(0xFFEF4444)
              )
              Spacer(modifier = Modifier.width(8.dp))
              Text(
                text = "Optimization Error",
                color = Color(0xFFEF4444),
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp
              )
            }

            Text(
              text = processingState.message,
              fontSize = 12.sp,
              color = Color(0xFFFCA5A5)
            )

            if (processingState.logs.isNotEmpty()) {
              Row(
                modifier = Modifier
                  .fillMaxWidth()
                  .clip(RoundedCornerShape(6.dp))
                  .background(DarkSurfaceElevated)
                  .clickable { showErrorLogs = !showErrorLogs }
                  .padding(horizontal = 8.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
              ) {
                Text(
                  text = if (showErrorLogs) "Hide Error Logs" else "Show Detailed FFmpeg Logs (${processingState.logs.size} lines)",
                  fontSize = 11.sp,
                  color = MintAccent,
                  fontWeight = FontWeight.SemiBold
                )
                Icon(
                  imageVector = if (showErrorLogs) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                  contentDescription = null,
                  tint = MintAccent,
                  modifier = Modifier.size(16.dp)
                )
              }

              AnimatedVisibility(visible = showErrorLogs) {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                  Box(
                    modifier = Modifier
                      .fillMaxWidth()
                      .height(140.dp)
                      .clip(RoundedCornerShape(8.dp))
                      .background(DarkObsidian)
                      .padding(8.dp)
                  ) {
                    LazyColumn {
                      items(processingState.logs) { log ->
                        Text(
                          text = log,
                          fontFamily = FontFamily.Monospace,
                          fontSize = 9.sp,
                          color = Color(0xFFF87171),
                          lineHeight = 13.sp
                        )
                      }
                    }
                  }

                  OutlinedButton(
                    onClick = {
                      val cm = context.getSystemService(android.content.Context.CLIPBOARD_SERVICE) as android.content.ClipboardManager
                      cm.setPrimaryClip(android.content.ClipData.newPlainText("FFmpeg Error Logs", processingState.logs.joinToString("\n")))
                      android.widget.Toast.makeText(context, "Error logs copied", android.widget.Toast.LENGTH_SHORT).show()
                    },
                    modifier = Modifier.height(36.dp),
                    shape = RoundedCornerShape(8.dp)
                  ) {
                    Text("Copy Error Logs", fontSize = 11.sp)
                  }
                }
              }
            }

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
              Button(
                onClick = onReset,
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MintAccent, contentColor = Color(0xFF003822))
              ) {
                Text("Try Again", fontSize = 12.sp, fontWeight = FontWeight.Bold)
              }
            }
          }
        }

        is ProcessingState.Success -> {
          // In success state, the ResultDisplayCard takes the spotlight,
          // but we provide a re-encode / optimize again button
          Button(
            onClick = onReset,
            modifier = Modifier
              .fillMaxWidth()
              .height(44.dp)
              .testTag("reoptimize_button"),
            shape = RoundedCornerShape(10.dp),
            colors = ButtonDefaults.buttonColors(containerColor = DarkSurfaceElevated)
          ) {
            Icon(imageVector = Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text("Optimize with Different Settings", fontSize = 13.sp, fontWeight = FontWeight.Bold)
          }
        }
      }
    }
  }
}

@Composable
fun LiveStat(label: String, value: String) {
  Row(verticalAlignment = Alignment.CenterVertically) {
    Text(text = "$label: ", fontSize = 11.sp, color = Color(0xFF94A3B8))
    Text(
      text = value,
      fontSize = 12.sp,
      fontWeight = FontWeight.Bold,
      color = Color.White
    )
  }
}
