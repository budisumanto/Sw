# Prompt Redesain UI & Engine v2: StatusHD (Pure iOS Liquid Glass Experience)

Gunakan seluruh isi dokumen ini sebagai instruksi utama (system & developer prompt) untuk AI desainer UI/UX dan pengembang Jetpack Compose agar mendesain ulang aplikasi **StatusHD** dari awal.

---

## 1. Visi Desain & Eksklusivitas Visual (Pure iOS Liquid Glass)

Desain aplikasi ini mengambil inspirasi langsung dari bahasa desain **iOS / VisionOS (Apple Frosted Glassmorphism)** yang mewah, jernih, dan sangat memanjakan mata.

### A. Apa yang Membuat "Liquid Glass" Ini Otentik iOS?
1. **Bukan Sekadar Kotak Transparan Hijau:**
   - Gunakan kanvas latar belakang gelap mewah (**True Dark Obsidian / Pitch Black `#000000` s.d. `#05080E`**).
   - Di balik kartu kaca, tambahkan **cahaya ambient pendaran warna (aurora mesh glow)** yang lembut dan tembus pandang melalui lapisan kaca (translucent blur).
2. **Kaca Cair Netral (Neutral Frosted Glass):**
   - Latar kartu kaca menggunakan warna putih transparan ultra-lembut: `Color.White.copy(alpha = 0.08f)` hingga `0.12f`.
   - Permukaan kartu diberi efek pantulan cahaya tipis (specular highlight 1 dp) dengan gradasi vertikal: bagian atas putih bersinar halus (`Color.White.copy(alpha = 0.35f)`), bagian bawah memudar lembut (`Color.White.copy(alpha = 0.04f)`).
3. **Bentuk Sudut Super-ellipse (Squircle):**
   - Sudut kartu menggunakan kelengkungan ekstra halus khas Apple (`28.dp` - `32.dp`).
   - Tidak ada sudut tajam, tidak ada garis border tebal kaku berwarna hijau lumpur.

---

## 2. Aturan Tampilan: Bersih, Minimalis & Keren (Zero Technical Jargon)

1. **Hapus Semua Elemen Teknis & Teks AI Slop:**
   - DILARANG menampilkan istilah: `CRF`, `GOP`, `Bitrate`, `Maxrate`, `Bufsize`, `Preset`, `FFmpeg`, `FastStart`, `yuv420p`, `Auto-Split`.
   - Hapus kartu terminal preview (`CommandInspectorCard`) dan matriks teknis (`SmartEngineGlassCard`).
   - Hapus badge teks kaku seperti: badge `LIQUID GLASS`, `Offline`, `BYPASS READY`, `Anti-Bloat Guard`.
   - Hapus tombol demo pengujian emulator (`15s Demo`, `100s Split Demo`).
2. **Hapus Perbandingan Ukuran (MB ke MB):**
   - Pengguna tidak perlu dipusingkan dengan hitungan angka MB atau persentase kompresi. Pengguna hanya butuh tahu videonya sekarang jernih, mulus, dan siap dipakai.
   - Gantikan area perbandingan ukuran dengan **kartu pemutar video langsung (Floating Cinema Video Player)** yang estetik dengan efek kaca mengambang.

---

## 3. Alur Tampilan Layar (State-Driven Screen Experience)

Halaman utama dirancang seringkas mungkin dengan alur dinamis:

### 1. Header Minimalis Khas iOS
- Judul bersih: **StatusHD**
- Subtitle lembut: *"Kualitas jernih untuk Status WhatsApp"*
- Tanpa tombol informasi teknis atau badge status yang berisik.

---

### 2. State 1: Pemilihan Video (Belum Ada Video)
- **Kartu Kaca Utama (Hero Liquid Glass Container):**
  - Kotak kaca melengkung anggun (`30.dp`) dengan kilau halus.
  - Ikon video/galeri melayang di dalam lingkaran kaca bersinar lembut.
  - Teks panduan ramah: *"Pilih Video"*
  - Subteks: *"Video Anda akan otomatis dioptimalkan dalam kualitas HD terbaik tanpa pecah."*
  - **Tombol Kaca Bersinar:** Tombol kapsul penuh (`54.dp`) dengan warna hijau mint neon khas iOS (`#10E599` / `#2DD4BF`) bertuliskan **"Buka Galeri"**.

---

### 3. State 2: Video Terpilih & Siap Dioptimalkan
- **Kartu Pratinjau Kaca Halus:**
  - Thumbnail video melengkung rapi (`16.dp`) dengan durasi di pojok kanan bawah.
  - Nama file video & durasi.
  - Jika video berdurasi lebih dari 90 detik, tampilkan catatan anggun:
    *"Video panjang otomatis dibagi menjadi beberapa bagian rapi."*
  - Tombol kecil bergaya kaca di samping: *"Ganti Video"*.
- **Tombol Aksi Utama:**
  - Tombol kapsul kaca bercahaya hijau mint: **"Mulai Optimalkan"**.

---

### 4. State 3: Proses Berjalan (Minimalist Floating Glass Progress)
- **Tampilan Progres Bersih Tanpa Telemetri Rumit:**
  - Bilah progres lengkung halus dengan animasi denyut cahaya (glowing pulse).
  - Teks proses manusiawi: *"Sedang memproses kualitas terbaik..."* atau *"Menyiapkan bagian 1 dari 2..."*.
  - Angka persentase anggun: `68%`.
  - Tombol outline kaca merah tipis di bawahnya: *"Batalkan"*.

---

### 5. State 4: Hasil Selesai (Floating Cinema Video Player)
*Ini adalah fitur utama yang membedakan tampilan v2:*

1. **Pemutar Video Kaca Langsung (Inline Glass Video Player):**
   - Tepat setelah proses selesai, video yang sudah dioptimalkan **langsung tampil dan dapat diputar di layar utama** dalam bingkai kartu kaca mengambang (Floating Cinema Glass).
   - Dilengkapi tombol Play/Pause kaca transparan dan kontrol durasi yang mulus.
   - Di atas video terdapat badge elegan yang keren: **"HD Ready for Status"** dengan ikon kilau bintang/berlian.
2. **Klip Terpisah (Jika Video > 90 Detik):**
   - Jika video terbagi menjadi beberapa bagian, tampilkan tab/selector kaca horizontal yang anggun:
     `[ Bagian 1 ]  [ Bagian 2 ]  [ Bagian 3 ]`
   - Mengklik bagian akan langsung mengganti video yang sedang diputar di pemutar kaca.
3. **Tombol Aksi Pembagian:**
   - **"Kirim ke Status WhatsApp"** (Tombol utama paling terang dan menonjol).
   - **"Simpan ke Galeri"** (Tombol sekunder bergaya kaca transparan berkilau).
   - Tautan teks halus: *"Pilih video lain"*.

---

## 4. Spesifikasi Engine Encoder FFmpeg (Di Balik Layar)

Engine di background harus menggunakan konfigurasi presisi berikut:

### A. Logika Pemotongan (> 90 Detik)
- Potong video secara otomatis per 90 detik menggunakan argumen `-ss [DETIK_AWAL]` dan `-t [DURASI]`.

### B. Rumus Bitrate Adaptif (Anti-Bloat)
```
Bitrate Asli (kbps) = ((Ukuran File Byte * 8) / Durasi Detik) / 1000

Jika Bitrate Asli > 6000 kbps  ➔  Maxrate = 6000k
Jika Bitrate Asli < 500 kbps   ➔  Maxrate = 500k
Jika di antaranya              ➔  Maxrate = Bitrate Asli (kbps)

Bufsize = Maxrate * 2
```

### C. Konfigurasi Argumen FFmpeg (Preset: MEDIUM, Tanpa GOP)
Setiap bagian video dieksekusi dengan argumen berikut:
```bash
-y
-ss [DETIK_AWAL]
-i [INPUT_PATH]
-t [DURASI]
-c:v libx264
-preset medium
-tune film
-x264-params aq-mode=3
-crf 20
-maxrate [MAXRATE]k
-bufsize [BUFSIZE]k
-vf scale=w=if(gte(iw\,ih)\,-2\,min(1080\,iw)):h=if(gte(iw\,ih)\,min(1080\,ih)\,-2)
-pix_fmt yuv420p
-c:a aac
-b:a 128k
-movflags +faststart
[OUTPUT_PATH]
```
*(PENTING: Parameter `-preset` menggunakan **medium**, dan parameter GOP `-g` **DIHAPUS SEPENUHNYA**).*

---

## 5. Standar Komponen Jetpack Compose (iOS Liquid Glass)

Berikut adalah komponen kaca yang harus diimplementasikan oleh AI:

### A. Kontainer Kaca Otentik (`IOSLiquidGlassSurface`)
```kotlin
package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

// Palet Kaca Netral iOS / VisionOS
val GlassBackgroundWhite = Color.White.copy(alpha = 0.08f)
val GlassBorderTopSpecular = Color.White.copy(alpha = 0.35f)
val GlassBorderBottomSoft = Color.White.copy(alpha = 0.04f)

@Composable
fun IOSLiquidGlassSurface(
    modifier: Modifier = Modifier,
    cornerRadius: Dp = 28.dp,
    contentPadding: Dp = 20.dp,
    content: @Composable BoxScope.() -> Unit
) {
    val squircleShape = RoundedCornerShape(cornerRadius)

    Box(
        modifier = modifier
            .clip(squircleShape)
            .background(GlassBackgroundWhite)
            .border(
                BorderStroke(
                    width = 1.dp,
                    brush = Brush.verticalGradient(
                        listOf(GlassBorderTopSpecular, GlassBorderBottomSoft)
                    )
                ),
                shape = squircleShape
            )
            .padding(contentPadding),
        content = content
    )
}
```

### B. Pratinjau Pemutar Video Kaca Selesai (`GlassVideoPreviewPlayer`)
```kotlin
package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.VideoSegment

@Composable
fun InlineGlassVideoPlayer(
    segment: VideoSegment,
    modifier: Modifier = Modifier
) {
    val playerShape = RoundedCornerShape(24.dp)

    Box(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(16f / 9f)
            .clip(playerShape)
            .background(Color.Black.copy(alpha = 0.6f))
            .border(
                androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.2f)),
                playerShape
            ),
        contentAlignment = Alignment.Center
    ) {
        // Pemutar Video Otomatis / ExoPlayer View di sini
        // Menampilkan video hasil kompresi langsung kepada pengguna

        // Badge Elegan di pojok atas
        Box(
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(12.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(Color.Black.copy(alpha = 0.5f))
                .padding(horizontal = 10.dp, vertical = 4.dp)
        ) {
            Text(
                text = "✨ HD Ready",
                color = Color(0xFF10E599),
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
```

---

## 6. Ringkasan Tugas untuk AI Pengembang

1. **Gunakan Palet iOS Netral:** Canvas hitam pekat/obsidian mewah dengan kaca putih transparan 8-12%, bukan hijau lumut kusam.
2. **Tampilkan Video Langsung Saat Selesai:** Tampilkan pemutar video hasil optimasi di kartu kaca utama agar pengguna bisa langsung menonton hasilnya.
3. **Tanpa Perbandingan Ukuran:** Jangan tampilkan angka kompresi MB ke MB.
4. **Preset FFmpeg = Medium:** Gunakan `-preset medium` untuk hasil lebih maksimal dan stabil.
5. **Tanpa GOP:** Jangan sertakan parameter `-g`.
6. **Bebas Jargon:** Pastikan seluruh teks antarmuka bersih, elegan, dan berbahasa Indonesia yang santun.
