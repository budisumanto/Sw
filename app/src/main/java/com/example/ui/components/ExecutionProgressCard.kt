package com.example.ui.components

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.ProcessingState
import com.example.ui.theme.ElectricMint
import com.example.ui.theme.ElectricMintDark
import com.example.ui.theme.IOSTextPrimary

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
        // Tombol kapsul penuh (56.dp) gradasi cerah Electric Mint ke Emerald
        Button(
          onClick = onStartOptimize,
          modifier = modifier
            .fillMaxWidth()
            .height(56.dp)
            .testTag("optimize_for_wa_status_button"),
          shape = RoundedCornerShape(22.dp),
          colors = ButtonDefaults.buttonColors(
            containerColor = Color.Transparent,
            contentColor = Color(0xFF021E14)
          ),
          border = BorderStroke(1.dp, IOSGlassHighlightTop)
        ) {
          Box(
            modifier = Modifier
              .fillMaxSize()
              .background(
                Brush.horizontalGradient(
                  listOf(ElectricMint, ElectricMintDark)
                ),
                shape = RoundedCornerShape(22.dp)
              ),
            contentAlignment = Alignment.Center
          ) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.Center
            ) {
              Text(
                text = "Optimalkan Kualitas",
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF022115),
                letterSpacing = (-0.3).sp
              )
            }
          }
        }
      }
    }

    is ProcessingState.Preparing -> {
      IOSLiquidGlassCard(
        modifier = modifier.fillMaxWidth(),
        cornerRadius = 24.dp,
        padding = 20.dp
      ) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          CircularProgressIndicator(
            color = ElectricMint,
            strokeWidth = 3.dp,
            modifier = Modifier.size(24.dp)
          )
          Spacer(modifier = Modifier.width(16.dp))
          Text(
            text = processingState.message,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = IOSTextPrimary
          )
        }
      }
    }

    is ProcessingState.Compressing -> {
      IOSLiquidGlassCard(
        modifier = modifier
          .fillMaxWidth()
          .testTag("execution_progress_card"),
        cornerRadius = 28.dp,
        padding = 22.dp
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
            Text(
              text = if (processingState.totalChunks > 1) {
                "Mengompres bagian ${processingState.activeChunk} dari ${processingState.totalChunks}..."
              } else {
                "Mengompres video HD..."
              },
              fontSize = 15.sp,
              fontWeight = FontWeight.SemiBold,
              color = IOSTextPrimary
            )

            val pct = (processingState.overallProgress * 100).toInt()
            Text(
              text = "$pct%",
              fontSize = 16.sp,
              fontWeight = FontWeight.Bold,
              color = ElectricMint
            )
          }

          LinearProgressIndicator(
            progress = { processingState.overallProgress },
            modifier = Modifier
              .fillMaxWidth()
              .height(8.dp)
              .clip(RoundedCornerShape(4.dp))
              .testTag("encoding_progress_bar"),
            color = ElectricMint,
            trackColor = Color(0xFFFFFFFF).copy(alpha = 0.08f)
          )

          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
          ) {
            Box(
              modifier = Modifier
                .clip(RoundedCornerShape(12.dp))
                .background(Color(0xFFFFFFFF).copy(alpha = 0.05f))
                .border(
                  BorderStroke(1.dp, Color(0xFFEF4444).copy(alpha = 0.35f)),
                  RoundedCornerShape(12.dp)
                )
                .clickable(onClick = onCancel)
                .padding(horizontal = 14.dp, vertical = 6.dp)
                .testTag("cancel_optimization_button"),
              contentAlignment = Alignment.Center
            ) {
              Text(
                text = "Batalkan",
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFFFCA5A5)
              )
            }
          }
        }
      }
    }

    is ProcessingState.Uploading -> {
      IOSLiquidGlassCard(
        modifier = modifier.fillMaxWidth(),
        cornerRadius = 24.dp,
        padding = 20.dp
      ) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          CircularProgressIndicator(
            color = ElectricMint,
            strokeWidth = 3.dp,
            modifier = Modifier.size(24.dp)
          )
          Spacer(modifier = Modifier.width(16.dp))
          Text(
            text = processingState.statusText,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = IOSTextPrimary
          )
        }
      }
    }

    is ProcessingState.Saving -> {
      IOSLiquidGlassCard(
        modifier = modifier.fillMaxWidth(),
        cornerRadius = 24.dp,
        padding = 20.dp
      ) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          CircularProgressIndicator(
            color = ElectricMint,
            strokeWidth = 3.dp,
            modifier = Modifier.size(24.dp)
          )
          Spacer(modifier = Modifier.width(16.dp))
          Text(
            text = processingState.statusText,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = IOSTextPrimary
          )
        }
      }
    }

    is ProcessingState.Cancelled -> {
      IOSLiquidGlassCard(
        modifier = modifier.fillMaxWidth(),
        cornerRadius = 24.dp,
        padding = 18.dp
      ) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
              imageVector = Icons.Default.Close,
              contentDescription = null,
              tint = Color(0xFFFCD34D),
              modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Text(
              text = "Proses dibatalkan",
              color = IOSTextPrimary,
              fontWeight = FontWeight.Medium,
              fontSize = 14.sp
            )
          }

          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(12.dp))
              .background(Color(0xFFFFFFFF).copy(alpha = 0.08f))
              .border(
                BorderStroke(1.dp, IOSGlassHighlightTop),
                RoundedCornerShape(12.dp)
              )
              .clickable(onClick = onReset)
              .padding(horizontal = 12.dp, vertical = 6.dp),
            contentAlignment = Alignment.Center
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(imageVector = Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(13.dp), tint = IOSTextPrimary)
              Spacer(modifier = Modifier.width(4.dp))
              Text("Ulangi", fontSize = 12.sp, color = IOSTextPrimary)
            }
          }
        }
      }
    }

    is ProcessingState.Error -> {
      IOSLiquidGlassCard(
        modifier = modifier.fillMaxWidth(),
        cornerRadius = 24.dp,
        padding = 20.dp
      ) {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
              imageVector = Icons.Default.ErrorOutline,
              contentDescription = null,
              tint = Color(0xFFF87171),
              modifier = Modifier.size(22.dp)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Text(
              text = processingState.message,
              color = IOSTextPrimary,
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
              shape = RoundedCornerShape(14.dp),
              colors = ButtonDefaults.buttonColors(
                containerColor = ElectricMint,
                contentColor = Color(0xFF021E14)
              )
            ) {
              Text("Coba Lagi", fontSize = 13.sp, fontWeight = FontWeight.Bold)
            }
          }
        }
      }
    }

    is ProcessingState.Success -> {
      // Handled via SuccessDialog
    }
  }
}
