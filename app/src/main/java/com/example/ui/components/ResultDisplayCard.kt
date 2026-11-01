package com.example.ui.components

import android.widget.VideoView
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.model.LinkedWhatsAppAccount
import com.example.model.OptimizationResult
import com.example.model.VideoSegment
import com.example.ui.theme.ElectricMint
import com.example.ui.theme.ElectricMintDark
import com.example.ui.theme.IOSTextPrimary
import com.example.ui.theme.IOSTextSecondary

@Composable
fun ResultDisplayCard(
  result: OptimizationResult,
  linkedAccount: LinkedWhatsAppAccount = LinkedWhatsAppAccount(),
  onOpenLinkDialog: () -> Unit = {},
  onShareToWhatsApp: (VideoSegment) -> Unit,
  onShareAll: (List<VideoSegment>) -> Unit,
  onSaveToGallery: (VideoSegment) -> Unit,
  onSaveAllToGallery: (List<VideoSegment>) -> Unit,
  onPreviewSegment: (VideoSegment) -> Unit,
  onReset: () -> Unit,
  modifier: Modifier = Modifier
) {
  var selectedPartIndex by remember { mutableIntStateOf(0) }
  val activeSegment = result.segments.getOrNull(selectedPartIndex) ?: result.segments.first()

  // Track video playback state cleanly without external popup MediaController
  var isPlaying by remember { mutableStateOf(true) }
  var videoViewRef by remember { mutableStateOf<VideoView?>(null) }

  // Release player when composable leaves composition
  DisposableEffect(activeSegment.file.absolutePath) {
    onDispose {
      videoViewRef?.stopPlayback()
    }
  }

  IOSLiquidGlassCard(
    modifier = modifier
      .fillMaxWidth()
      .testTag("result_display_card"),
    cornerRadius = 28.dp,
    padding = 20.dp
  ) {
    Column(
      modifier = Modifier.fillMaxWidth(),
      verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
      // Header: Centang hijau cerah + Judul
      Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
      ) {
        Box(
          modifier = Modifier
            .size(42.dp)
            .clip(CircleShape)
            .background(
              Brush.radialGradient(
                listOf(ElectricMint, ElectricMintDark)
              )
            ),
          contentAlignment = Alignment.Center
        ) {
          Icon(
            imageVector = Icons.Default.Check,
            contentDescription = null,
            tint = Color(0xFF022115),
            modifier = Modifier.size(22.dp)
          )
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column {
          Text(
            text = "Video Siap Dibagikan!",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = IOSTextPrimary,
            letterSpacing = (-0.3).sp
          )
          Text(
            text = "Format & kepadatan dioptimalkan untuk WhatsApp HD",
            fontSize = 12.sp,
            color = ElectricMint
          )
        }
      }

      // Inline Video Player Container (Rapi, Terkendali, Tidak Nabrak Desain)
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .height(240.dp)
          .clip(RoundedCornerShape(20.dp))
          .background(Color.Black)
          .border(BorderStroke(1.dp, IOSGlassHighlightTop), RoundedCornerShape(20.dp))
          .clickable {
            videoViewRef?.let { vv ->
              if (vv.isPlaying) {
                vv.pause()
                isPlaying = false
              } else {
                vv.start()
                isPlaying = true
              }
            }
          },
        contentAlignment = Alignment.Center
      ) {
        // Player Surface
        AndroidView(
          modifier = Modifier
            .fillMaxSize()
            .clip(RoundedCornerShape(20.dp))
            .testTag("inline_video_player"),
          factory = { ctx ->
            VideoView(ctx).apply {
              setVideoPath(activeSegment.file.absolutePath)
              setOnPreparedListener { mp ->
                mp.isLooping = true
                start()
                isPlaying = true
              }
              setOnCompletionListener {
                isPlaying = false
              }
              videoViewRef = this
            }
          },
          update = { vv ->
            videoViewRef = vv
            vv.setVideoPath(activeSegment.file.absolutePath)
            vv.start()
            isPlaying = true
          }
        )

        // Overlay Icon Play ketika video di-pause
        if (!isPlaying) {
          Box(
            modifier = Modifier
              .size(54.dp)
              .clip(CircleShape)
              .background(Color.Black.copy(alpha = 0.65f))
              .border(BorderStroke(1.dp, Color.White.copy(alpha = 0.35f)), CircleShape),
            contentAlignment = Alignment.Center
          ) {
            Icon(
              imageVector = Icons.Default.PlayArrow,
              contentDescription = "Putar Video",
              tint = Color.White,
              modifier = Modifier.size(30.dp)
            )
          }
        }

        // Badge Elegan: "✨ HD Ready"
        Box(
          modifier = Modifier
            .align(Alignment.TopStart)
            .padding(10.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFF000000).copy(alpha = 0.75f))
            .border(
              BorderStroke(1.dp, Color(0xFFFFFFFF).copy(alpha = 0.25f)),
              RoundedCornerShape(12.dp)
            )
            .padding(horizontal = 10.dp, vertical = 5.dp)
        ) {
          Text(
            text = "✨ HD Ready",
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = ElectricMint,
            letterSpacing = 0.2.sp
          )
        }
      }

      // Part Selector Pills (jika ada lebih dari 1 bagian)
      if (result.segments.size > 1) {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
          Text(
            text = "Pilih Bagian Video (${result.segments.size} Bagian):",
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold,
            color = IOSTextPrimary
          )

          LazyRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            itemsIndexed(result.segments) { index, seg ->
              val isSelected = index == selectedPartIndex
              val bgModifier = if (isSelected) {
                Modifier.background(Brush.horizontalGradient(listOf(ElectricMint, ElectricMintDark)))
              } else {
                Modifier.background(Color(0xFFFFFFFF).copy(alpha = 0.08f))
              }
              Box(
                modifier = Modifier
                  .clip(RoundedCornerShape(14.dp))
                  .then(bgModifier)
                  .border(
                    BorderStroke(
                      1.dp,
                      if (isSelected) ElectricMint else IOSGlassHighlightTop
                    ),
                    RoundedCornerShape(14.dp)
                  )
                  .clickable { selectedPartIndex = index }
                  .padding(horizontal = 14.dp, vertical = 8.dp)
              ) {
                Text(
                  text = "Bagian ${seg.partIndex} (${seg.formattedDuration})",
                  fontSize = 12.sp,
                  fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                  color = if (isSelected) Color(0xFF021E14) else IOSTextPrimary
                )
              }
            }
          }
        }
      }

      // Tombol Aksi Bersih
      Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        // 1. Kirim ke WhatsApp Pribadi (Perangkat Tertaut)
        Button(
          onClick = {
            if (!linkedAccount.isLinked) {
              onOpenLinkDialog()
            } else {
              if (result.segments.size == 1) {
                onShareToWhatsApp(activeSegment)
              } else {
                onShareAll(result.segments)
              }
            }
          },
          modifier = Modifier
            .fillMaxWidth()
            .height(54.dp)
            .testTag("share_whatsapp_status_button"),
          shape = RoundedCornerShape(20.dp),
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
                shape = RoundedCornerShape(20.dp)
              ),
            contentAlignment = Alignment.Center
          ) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.Center
            ) {
              Icon(
                imageVector = Icons.Default.Share,
                contentDescription = null,
                tint = Color(0xFF022115),
                modifier = Modifier.size(19.dp)
              )
              Spacer(modifier = Modifier.width(8.dp))
              Text(
                text = if (!linkedAccount.isLinked) {
                  "Tautkan Perangkat & Kirim"
                } else if (result.segments.size == 1) {
                  "Kirim Otomatis ke WhatsApp Saya"
                } else {
                  "Kirim Otomatis ke WhatsApp (${result.segments.size} Bagian)"
                },
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                color = Color(0xFF022115)
              )
            }
          }
        }

        // Trik Bebas Kompresi Box
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Color.White.copy(alpha = 0.05f))
            .border(BorderStroke(1.dp, Color.White.copy(alpha = 0.12f)), RoundedCornerShape(16.dp))
            .padding(horizontal = 14.dp, vertical = 10.dp)
        ) {
          Row(verticalAlignment = Alignment.Top) {
            Text(text = "💡", fontSize = 14.sp)
            Spacer(modifier = Modifier.width(8.dp))
            Column {
              Text(
                text = if (linkedAccount.isLinked) {
                  "Perangkat Tertaut aktif (${linkedAccount.formattedDisplayNumber}). Video dikirim langsung ke chat pribadi Anda lalu diteruskan (Forward) ke Status tanpa kompresi."
                } else {
                  "Tautkan WhatsApp Anda (Perangkat Tertaut) agar video dikirim otomatis ke akun Anda tanpa kompresi."
                },
                fontSize = 11.sp,
                color = IOSTextSecondary,
                lineHeight = 16.sp
              )
            }
          }
        }

        // 2. Simpan ke Galeri (Langsung kembali ke awal & hapus pilihan video)
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .height(50.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(Color(0xFFFFFFFF).copy(alpha = 0.08f))
            .border(BorderStroke(1.dp, IOSGlassHighlightTop), RoundedCornerShape(20.dp))
            .clickable {
              if (result.segments.size == 1) {
                onSaveToGallery(activeSegment)
              } else {
                onSaveAllToGallery(result.segments)
              }
            }
            .testTag("save_to_gallery_button"),
          contentAlignment = Alignment.Center
        ) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
          ) {
            Icon(
              imageVector = Icons.Default.Download,
              contentDescription = null,
              modifier = Modifier.size(18.dp),
              tint = IOSTextPrimary
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
              text = if (result.segments.size == 1) "Simpan ke Galeri" else "Simpan Semua ke Galeri",
              fontWeight = FontWeight.SemiBold,
              color = IOSTextPrimary,
              fontSize = 15.sp
            )
          }
        }

        // 3. Tombol tautan bawah: "Proses video lainnya"
        Box(
          modifier = Modifier.fillMaxWidth(),
          contentAlignment = Alignment.Center
        ) {
          TextButton(onClick = onReset) {
            Text(
              text = "Proses video lainnya",
              color = IOSTextSecondary,
              fontSize = 13.sp,
              fontWeight = FontWeight.Medium
            )
          }
        }
      }
    }
  }
}
