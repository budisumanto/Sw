package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.ErrorOutline
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.res.stringResource
import com.example.R
import com.example.model.ProcessingState
import com.example.ui.theme.LocalGlass
import com.example.ui.theme.pressable

@Composable
fun ExecutionProgressCard(
  processingState: ProcessingState,
  hasVideoSelected: Boolean,
  onStartOptimize: () -> Unit,
  onCancel: () -> Unit,
  onReset: () -> Unit,
  modifier: Modifier = Modifier
) {
  val g = LocalGlass.current

  when (processingState) {
    is ProcessingState.Idle -> {
      if (hasVideoSelected) {
        GlassButton(
          text = stringResource(id = R.string.btn_optimize),
          onClick = onStartOptimize,
          primary = true,
          modifier = modifier
            .fillMaxWidth()
            .testTag("optimize_for_wa_status_button")
        )
      }
    }

    is ProcessingState.Preparing -> {
      GlassCard(
        modifier = modifier.fillMaxWidth(),
        radius = 24.dp
      ) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          CircularProgressIndicator(
            color = g.accent,
            strokeWidth = 3.dp,
            modifier = Modifier.size(24.dp)
          )
          Spacer(modifier = Modifier.width(16.dp))
          Text(
            text = processingState.message,
            style = MaterialTheme.typography.bodyLarge,
            color = g.text
          )
        }
      }
    }

    is ProcessingState.Compressing -> {
      GlassCard(
        modifier = modifier
          .fillMaxWidth()
          .testTag("execution_progress_card"),
        radius = 24.dp
      ) {
        Column(
          modifier = Modifier.fillMaxWidth(),
          verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Column {
              Text(
                text = if (processingState.totalChunks > 1) {
                  stringResource(id = R.string.processing_chunk, processingState.activeChunk, processingState.totalChunks)
                } else {
                  stringResource(id = R.string.processing_single)
                },
                style = MaterialTheme.typography.titleMedium,
                color = g.text
              )
              Text(
                text = stringResource(id = R.string.processing_subtitle),
                style = MaterialTheme.typography.bodyMedium,
                color = g.textMuted
              )
            }

            val pct = (processingState.overallProgress * 100).toInt()
            Text(
              text = "$pct%",
              style = MaterialTheme.typography.titleMedium,
              color = g.accent
            )
          }

          LinearProgressIndicator(
            progress = { processingState.overallProgress },
            modifier = Modifier
              .fillMaxWidth()
              .height(8.dp)
              .clip(RoundedCornerShape(999.dp))
              .testTag("encoding_progress_bar"),
            color = g.accent,
            trackColor = Color.White.copy(alpha = 0.08f)
          )

          // Kotak Catatan Edukatif
          Box(
            modifier = Modifier
              .fillMaxWidth()
              .clip(RoundedCornerShape(16.dp))
              .background(g.accent2.copy(alpha = 0.08f))
              .border(1.dp, g.accent2.copy(alpha = 0.20f), RoundedCornerShape(16.dp))
              .padding(14.dp)
          ) {
            Row(
              verticalAlignment = Alignment.Top,
              horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
              Icon(
                imageVector = Icons.Rounded.Info,
                contentDescription = null,
                tint = g.accent2,
                modifier = Modifier
                  .size(16.dp)
                  .padding(top = 1.dp)
              )
              Text(
                text = stringResource(id = R.string.important_warning),
                style = MaterialTheme.typography.bodyMedium,
                color = g.text
              )
            }
          }

          // Tombol Batalkan
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
          ) {
            Box(
              modifier = Modifier
                .clip(RoundedCornerShape(999.dp))
                .background(Color.White.copy(alpha = 0.06f))
                .border(1.dp, Color(0xFFEF4444).copy(alpha = 0.35f), RoundedCornerShape(999.dp))
                .pressable(onCancel)
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .testTag("cancel_optimization_button"),
              contentAlignment = Alignment.Center
            ) {
              Text(
                text = stringResource(id = R.string.btn_cancel),
                style = MaterialTheme.typography.labelSmall,
                color = Color(0xFFFCA5A5)
              )
            }
          }
        }
      }
    }

    is ProcessingState.Uploading -> {
      GlassCard(
        modifier = modifier.fillMaxWidth(),
        radius = 24.dp
      ) {
        Column(
          modifier = Modifier.fillMaxWidth(),
          verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .padding(vertical = 2.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            CircularProgressIndicator(
              color = g.accent,
              strokeWidth = 3.dp,
              modifier = Modifier.size(22.dp)
            )
            Spacer(modifier = Modifier.width(14.dp))
            Text(
              text = if (processingState.totalChunks > 1) {
                stringResource(id = R.string.uploading_chunk, processingState.activeChunk, processingState.totalChunks)
              } else {
                stringResource(id = R.string.uploading_single)
              },
              style = MaterialTheme.typography.titleMedium,
              color = g.text
            )
          }

          val uploadFraction = if (processingState.totalBytes > 0) {
            (processingState.bytesUploaded.toFloat() / processingState.totalBytes.toFloat()).coerceIn(0f, 1f)
          } else 0f

          LinearProgressIndicator(
            progress = { uploadFraction },
            modifier = Modifier
              .fillMaxWidth()
              .height(6.dp)
              .clip(RoundedCornerShape(999.dp)),
            color = g.accent,
            trackColor = Color.White.copy(alpha = 0.08f)
          )
        }
      }
    }

    is ProcessingState.Cancelled -> {
      GlassCard(
        modifier = modifier.fillMaxWidth(),
        radius = 24.dp
      ) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
              imageVector = Icons.Rounded.Close,
              contentDescription = null,
              tint = Color(0xFFFCD34D),
              modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Text(
              text = stringResource(id = R.string.cancelled_msg),
              style = MaterialTheme.typography.bodyLarge,
              color = g.text
            )
          }

          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(999.dp))
              .background(g.fillStrong)
              .border(1.dp, g.border, RoundedCornerShape(999.dp))
              .pressable(onReset)
              .padding(horizontal = 14.dp, vertical = 8.dp),
            contentAlignment = Alignment.Center
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(imageVector = Icons.Rounded.Refresh, contentDescription = null, modifier = Modifier.size(13.dp), tint = g.text)
              Spacer(modifier = Modifier.width(4.dp))
              Text(stringResource(id = R.string.btn_repeat), style = MaterialTheme.typography.labelSmall, color = g.text)
            }
          }
        }
      }
    }

    is ProcessingState.Error -> {
      GlassCard(
        modifier = modifier.fillMaxWidth(),
        radius = 24.dp,
        strong = true
      ) {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
              imageVector = Icons.Rounded.ErrorOutline,
              contentDescription = null,
              tint = Color(0xFFF87171),
              modifier = Modifier.size(22.dp)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Text(
              text = processingState.message,
              style = MaterialTheme.typography.bodyLarge,
              color = g.text
            )
          }

          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
          ) {
            GlassButton(
              text = stringResource(id = R.string.btn_retry),
              onClick = onReset,
              primary = true
            )
          }
        }
      }
    }

    is ProcessingState.Success -> {
      // Handled via SuccessDialog
    }
  }
}
