package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.RocketLaunch
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.ProcessingState
import com.example.ui.theme.MintAccent

@Composable
fun ExecutionProgressCard(
  processingState: ProcessingState,
  hasVideoSelected: Boolean,
  onStartOptimize: () -> Unit,
  onCancel: () -> Unit,
  onReset: () -> Unit,
  modifier: Modifier = Modifier
) {
  when (processingState) {
    is ProcessingState.Idle -> {
      if (hasVideoSelected) {
        Button(
          onClick = onStartOptimize,
          modifier = modifier
            .fillMaxWidth()
            .height(56.dp)
            .testTag("optimize_for_wa_status_button"),
          shape = RoundedCornerShape(20.dp),
          colors = ButtonDefaults.buttonColors(
            containerColor = MintAccent,
            contentColor = Color(0xFF003822)
          )
        ) {
          Icon(
            imageVector = Icons.Default.RocketLaunch,
            contentDescription = null,
            modifier = Modifier.size(20.dp)
          )
          Spacer(modifier = Modifier.width(10.dp))
          Text(
            text = "Optimalkan Video",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold
          )
        }
      }
    }

    is ProcessingState.Preparing -> {
      LiquidGlassBox(modifier = modifier.fillMaxWidth()) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          CircularProgressIndicator(
            color = MintAccent,
            strokeWidth = 3.dp,
            modifier = Modifier.size(24.dp)
          )
          Spacer(modifier = Modifier.width(14.dp))
          Text(
            text = processingState.message,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium,
            color = Color.White
          )
        }
      }
    }

    is ProcessingState.Compressing -> {
      LiquidGlassBox(
        modifier = modifier
          .fillMaxWidth()
          .testTag("execution_progress_card")
      ) {
        Column(
          modifier = Modifier.fillMaxWidth(),
          verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text(
              text = processingState.statusText,
              style = MaterialTheme.typography.bodyMedium,
              fontWeight = FontWeight.SemiBold,
              color = Color.White
            )

            val pct = (processingState.overallProgress * 100).toInt()
            Text(
              text = "$pct%",
              fontSize = 15.sp,
              fontWeight = FontWeight.Bold,
              color = MintAccent
            )
          }

          LinearProgressIndicator(
            progress = { processingState.overallProgress },
            modifier = Modifier
              .fillMaxWidth()
              .height(8.dp)
              .clip(RoundedCornerShape(4.dp))
              .testTag("encoding_progress_bar"),
            color = MintAccent,
            trackColor = Color.White.copy(alpha = 0.08f)
          )

          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
          ) {
            OutlinedButton(
              onClick = onCancel,
              modifier = Modifier.testTag("cancel_optimization_button"),
              shape = RoundedCornerShape(12.dp),
              border = BorderStroke(1.dp, Color(0xFFEF4444).copy(alpha = 0.4f)),
              colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFF87171)),
              contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 14.dp, vertical = 4.dp)
            ) {
              Text("Batal", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
            }
          }
        }
      }
    }

    is ProcessingState.Cancelled -> {
      LiquidGlassBox(modifier = modifier.fillMaxWidth()) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
              imageVector = Icons.Default.Close,
              contentDescription = null,
              tint = Color(0xFFFCD34D)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
              text = "Pengoptimalan dibatalkan",
              color = Color.White,
              fontWeight = FontWeight.SemiBold
            )
          }

          Button(
            onClick = onReset,
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color.White.copy(alpha = 0.12f))
          ) {
            Icon(imageVector = Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(14.dp))
            Spacer(modifier = Modifier.width(4.dp))
            Text("Ulangi", fontSize = 12.sp)
          }
        }
      }
    }

    is ProcessingState.Error -> {
      LiquidGlassBox(modifier = modifier.fillMaxWidth()) {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
              imageVector = Icons.Default.Error,
              contentDescription = null,
              tint = Color(0xFFF87171)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
              text = processingState.message,
              color = Color.White,
              fontWeight = FontWeight.Medium,
              fontSize = 14.sp
            )
          }

          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
          ) {
            Button(
              onClick = onReset,
              shape = RoundedCornerShape(12.dp),
              colors = ButtonDefaults.buttonColors(
                containerColor = MintAccent,
                contentColor = Color(0xFF003822)
              )
            ) {
              Text("Coba Lagi", fontSize = 13.sp, fontWeight = FontWeight.Bold)
            }
          }
        }
      }
    }

    is ProcessingState.Success -> {
      // In success state, the ResultDisplayCard handles the UI
    }
  }
}
