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
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.res.stringResource
import com.example.R
import com.example.ui.theme.LocalGlass

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SuccessDialog(
  message: String,
  onOpenWhatsApp: () -> Unit,
  onDismiss: () -> Unit
) {
  val g = LocalGlass.current
  val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

  ModalBottomSheet(
    onDismissRequest = onDismiss,
    sheetState = sheetState,
    containerColor = Color(0xFF0F131D),
    contentColor = g.text,
    scrimColor = Color.Black.copy(alpha = 0.65f),
    shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp),
    dragHandle = {
      Box(
        modifier = Modifier
          .padding(top = 12.dp, bottom = 4.dp)
          .size(width = 44.dp, height = 4.dp)
          .clip(RoundedCornerShape(999.dp))
          .background(Color.White.copy(alpha = 0.25f))
      )
    },
    modifier = Modifier.testTag("success_dialog")
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .navigationBarsPadding()
        .padding(horizontal = 24.dp, vertical = 16.dp),
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
      // Lingkaran Centang Luminous Liquid Glass
      Box(
        modifier = Modifier
          .size(72.dp)
          .clip(CircleShape)
          .background(
            Brush.radialGradient(
              listOf(g.accent.copy(alpha = 0.28f), g.accent.copy(alpha = 0.08f))
            )
          )
          .border(
            1.dp,
            Brush.linearGradient(
              listOf(g.accent.copy(alpha = 0.65f), g.accent.copy(alpha = 0.20f))
            ),
            CircleShape
          ),
        contentAlignment = Alignment.Center
      ) {
        Icon(
          imageVector = Icons.Rounded.Check,
          contentDescription = null,
          tint = g.accent,
          modifier = Modifier.size(36.dp)
        )
      }

      // Judul & Pesan
      Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(6.dp)
      ) {
        Text(
          text = stringResource(id = R.string.dialog_success_title),
          style = MaterialTheme.typography.titleLarge.copy(
            fontWeight = FontWeight.SemiBold
          ),
          color = Color(0xFFF9FAFB),
          textAlign = TextAlign.Center
        )

        val displayMsg = if (message.isNotBlank()) message else stringResource(id = R.string.dialog_success_desc)
        Text(
          text = displayMsg,
          style = MaterialTheme.typography.bodyMedium,
          color = Color(0xFF9CA3AF),
          textAlign = TextAlign.Center,
          modifier = Modifier.padding(horizontal = 12.dp),
          lineHeight = 22.sp
        )
      }

      Spacer(modifier = Modifier.height(4.dp))

      // Tombol Aksi Buka WhatsApp
      GlassButton(
        text = stringResource(id = R.string.dialog_success_open_wa),
        onClick = {
          onOpenWhatsApp()
          onDismiss()
        },
        primary = true,
        modifier = Modifier
          .fillMaxWidth()
          .testTag("open_whatsapp_button")
      )

      TextButton(onClick = onDismiss) {
        Text(
          text = stringResource(id = R.string.dialog_close),
          style = MaterialTheme.typography.labelMedium,
          color = Color(0xFF9CA3AF)
        )
      }

      Spacer(modifier = Modifier.height(8.dp))
    }
  }
}
