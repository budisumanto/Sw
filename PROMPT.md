# Panduan Prompt Redesain UI: StatusHD (iOS Liquid Glass Edition)

Gunakan seluruh isi dokumen ini sebagai instruksi (system/developer prompt) untuk AI desainer & pengembang Jetpack Compose agar mendesain ulang antarmuka aplikasi **StatusHD** dari awal.

---

## 1. Ringkasan Visi Desain

Ubah antarmuka aplikasi Android **StatusHD** menjadi sangat minimalis, mewah, bersih, dan berorientasi pada kemudahan pengguna awam dengan gaya **Liquid Glass ala iOS**.

Semua istilah teknis rumit yang membingungkan orang awam (seperti CRF, GOP, FFmpeg, bitrate, auto smart engine, command inspector, dan konsol log) harus dihilangkan seluruhnya dari layar utama. Pengguna hanya perlu merasakan pengalaman yang mulus: **pilih video -> proses dengan animasi halus -> langsung bagikan ke Status WhatsApp dengan kualitas terbaik.**

---

## 2. Prinsip & Aturan Desain (Design Rules)

### A. Hilangkan Hal-Hal Teknis & Teks Tidak Perlu (Anti-Clutter)
1. **Dilarang Menampilkan Istilah Kompresi:**
   - Hapus teks/kartu yang menyebutkan: `CRF 20`, `Bitrate Limit 4000k`, `GOP Structure (-g 30)`, `FFmpeg`, `FastStart`, `yuv420p`, dan sejenisnya.
   - Hapus komponen `CommandInspectorCard` (kotak terminal dan perintah FFmpeg). Pengguna awam tidak memerlukan perintah terminal.
2. **Hapus Teks Klise / "AI Slop":**
   - Hapus badge atau label berlebihan seperti badge `LIQUID GLASS`, `Offline`, `BYPASS READY`, `Anti-Bloat Guard`, `Master Clarity`, dsb.
   - Jangan menuliskan nama gaya desain di layar pengguna (cukup tampilkan visual kacanya tanpa harus menulis kata "Liquid Glass").
3. **Bersihkan Informasi Diagnostik & Tombol Uji Coba:**
   - Hapus tombol pengujian demo emulator (`15s Demo`, `100s Split Demo`).
   - Hapus panel telemetri saat proses berjalan (`Speed 1.2x`, `FPS 30`, `Live FFmpeg Console`, dan log baris terminal mentah).
   - Gantilah pesan error teknis panjang dengan bahasa manusiawi yang sopan (misal: *"Video tidak dapat diproses. Coba pilih video lain"*), dilengkapi tombol *"Coba Lagi"*.

### B. Estetika iOS Liquid Glass (Frosted Glassmorphism)
1. **Karakteristik Kaca Cair (Liquid Glass):**
   - **Transparansi Berlapis:** Latar belakang kartu menggunakan warna gelap semi-transparan (`Color(0xFF14241F).copy(alpha = 0.55f)` atau warna obsidian transparan).
   - **Specular Border (Efek Pantulan Cahaya):** Border kartu setebal 1 dp menggunakan gradasi vertikal halus, di mana sisi atas lebih terang (`White.copy(alpha = 0.25f)`) untuk menangkap kilau cahaya dari atas, dan memudar lembut ke arah bawah (`White.copy(alpha = 0.05f)`).
   - **Sudut Melengkung Halus (Super-ellipse / Large Rounded Corners):** Gunakan radius sudut melengkung khas iOS antara `24.dp` hingga `28.dp` untuk kartu utama, dan `16.dp` untuk tombol/kartu kecil.
   - **Kedalaman Visual (Depth & Mesh Glow):** Di balik kartu kaca, buat gradasi latar belakang dinamis yang gelap dan elegan (perpaduan Deep Obsidian `#070D0B`, Forest Teal gelap, dan subtle radial aura warna emerald lembut di bagian atas layar).
2. **Palet Warna Mewah:**
   - **Background Utama:** Dark Obsidian `#070D0B` s.d. `#0B1612`.
   - **Aksen Utama:** Emerald Tint / Soft Mint (`#2DD4BF` atau `#10B981`) untuk tombol aksi utama dan indikator aktif.
   - **Permukaan Kartu Kaca:** Frosted Black-Green Glass (`#0E1B16` dengan opasitas 50% - 65%).
   - **Tipografi:** Teks judul putih bersih (`#FFFFFF`), teks pendukung/subteks abu-abu netral sejuk (`#94A3B8`).

---

## 3. Struktur & Alur Halaman (Screen Flow)

Aplikasi difokuskan menjadi 3 fase sederhana dalam 1 halaman tunggal yang intuitif:

### 1. Header yang Bersih & Elegan
- **Ikon Aplikasi:** Ikon persegi tumpul minimalis dengan aksen kaca lembut.
- **Identitas:** Nama aplikasi `StatusHD` dengan tipografi tebal, rapi, dan modern.
- **Slogan Singkat:** *"Kualitas jernih untuk Status WhatsApp Anda"* (tanpa badge atau status offline/teknis).

### 2. Area Pemilihan Video (State: Belum Ada Video)
- **Tampilan Kotak Kaca Utama:**
  - Area sentuh berbentuk kartu kaca bundar/oval halus dengan ikon galeri/film yang bersinar lembut.
  - Teks panduan ramah: *"Pilih Video dari Galeri"*.
  - Subteks: *"Format dan resolusi akan dioptimalkan otomatis agar jernih dan tidak pecah."*
  - Tombol aksi utama bergaya kaca tebal (Frosted Glass Button) dengan label: **"Buka Galeri"**.

### 3. Informasi Video Terpilih (State: Video Dipilih)
- **Pratinjau Video Elegan:**
  - Thumbnail video dengan sudut melengkung halus dan durasi di pojok bawah.
  - Nama file video (dipotong rapi) dan informasi sederhana yang mudah dipahami: durasi dan ukuran file asli (contoh: `0:45 • 42 MB`).
  - Jika video lebih dari batas status (misalnya di atas 90 detik), berikan pemberitahuan bersahabat:
    *"Video panjang akan otomatis dibagi menjadi bagian-bagian rapi."*
  - Tombol kecil bergaya kaca: *"Ganti Video"*.

### 4. Tombol Aksi Utama (Call to Action)
- Tombol kapsul besar dengan efek kaca bercahaya hijau emerald lembut:
  - Teks: **"Optimalkan Video"**
  - Efek saat ditekan: Animasi klik halus (scale transition).

### 5. Tampilan Pemrosesan (State: Sedang Berjalan)
- Desain minimalis tanpa angka-angka teknis yang membingungkan:
  - Lingkaran progres atau bilah progres halus (smooth animated progress bar) berwarna mint neon.
  - Keterangan status bersahabat: *"Menyiapkan kualitas terbaik..."* atau *"Memproses bagian 1 dari 2..."*.
  - Persentase sederhana: `45%`.
  - Tombol *"Batal"* berdesain outline kaca tipis berwarna merah pudar.

### 6. Tampilan Hasil & Pembagian (State: Selesai)
- Kartu apresiasi kaca yang memuaskan:
  - Ikon centang hijau dengan kilau halus.
  - Judul: *"Video Siap Dibagikan!"*
  - Ringkasan simpel perbandingan ukuran:
    - Ukuran Asli: `45 MB`  ->  Ukuran Optimal: `14 MB` *(Hemat 68% tanpa turun kualitas)*.
- **Daftar Klip (Jika Video Terbagi):**
  - Kartu baris kaca untuk setiap bagian (Part 1, Part 2).
  - Pratinjau mini yang bisa diklik untuk memutar langsung (Video Player Dialog).
  - Tombol cepat simpan dan bagikan per klip.
- **Tombol Utama Hasil:**
  - **"Kirim ke Status WhatsApp"** (Tombol hijau cerah paling menonjol, langsung membuka WhatsApp Status).
  - **"Simpan ke Galeri"** (Tombol kedua beraksen kaca elegan).
  - Teks/tombol kecil: *"Proses video lainnya"*.

---

## 4. Contoh Implementasi Komponen Jetpack Compose

Berikut adalah pola komponen dasar yang harus dijadikan standar penulisan kode oleh AI:

### A. Kontainer Kaca Cair (`LiquidGlassBox`)
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

// Palet Kaca iOS
val GlassSurface = Color(0xFF0F1E19).copy(alpha = 0.55f)
val GlassBorderTopHighlight = Color.White.copy(alpha = 0.22f)
val GlassBorderBottomShadow = Color.White.copy(alpha = 0.04f)

@Composable
fun LiquidGlassBox(
    modifier: Modifier = Modifier,
    cornerRadius: Dp = 26.dp,
    contentPadding: Dp = 18.dp,
    content: @Composable BoxScope.() -> Unit
) {
    val glassShape = RoundedCornerShape(cornerRadius)

    Box(
        modifier = modifier
            .clip(glassShape)
            .background(GlassSurface)
            .border(
                BorderStroke(
                    width = 1.dp,
                    brush = Brush.verticalGradient(
                        listOf(GlassBorderTopHighlight, GlassBorderBottomShadow)
                    )
                ),
                shape = glassShape
            )
            .padding(contentPadding),
        content = content
    )
}
```

### B. Tombol Aksi Kaca (`LiquidGlassButton`)
```kotlin
package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun PrimaryGlassActionButton(
    text: String,
    icon: ImageVector? = null,
    onClick: () -> Unit,
    enabled: Boolean = true,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp),
        shape = RoundedCornerShape(20.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xFF10B981),
            contentColor = Color(0xFF022317),
            disabledContainerColor = Color.White.copy(alpha = 0.06f),
            disabledContentColor = Color(0xFF64748B)
        ),
        border = if (enabled) BorderStroke(1.dp, Color.White.copy(alpha = 0.25f)) else null,
        elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            if (icon != null) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
            }
            Text(
                text = text,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
```

---

## 5. Instruksi Langkah Pengerjaan untuk AI

Ketika Anda (AI) memperbarui kode antarmuka aplikasi ini:
1. **Periksa File Screen Utama:** Sederhanakan `StatusHDOptimizerScreen.kt` agar hanya menampilkan komponen:
   - Header aplikasi bersih.
   - Kartu pemilih video (Gallery Selector).
   - Tombol utama mulai optimasi.
   - Kartu progres minimalis (hanya aktif saat sedang berjalan).
   - Kartu hasil akhir dengan tombol kirim ke WhatsApp Status & Simpan.
2. **Keluarkan Komponen Teknis:** Hapus pemanggilan `CommandInspectorCard` dan `SmartEngineGlassCard` dari tata letak utama. Semua konfigurasi pengkodean (seperti CRF 20, batas resolusi, GOP, preset) tetap aktif di ViewModel/Engine di balik layar secara transparan tanpa perlu ditampilkan ke pengguna.
3. **Pelihara Responsivitas Gestur:** Pastikan transisi antar fase (memilih video, memproses, selesai) menggunakan animasi halus (`AnimatedVisibility`, `fadeIn`, `fadeOut`, `animateContentSize`).
4. **Jaga Kerapian:** Tidak ada teks statis "Liquid Glass", "Offline", atau teks dekorasi kaku yang mengganggu visual. Antarmuka harus terasa lapang, tenang, modern, dan sangat nyaman dipandang mata layaknya aplikasi bawaan sistem operasi modern.
