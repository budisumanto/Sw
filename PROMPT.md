# Panduan Prompt Redesain UI & Engine: StatusHD (iOS Liquid Glass Edition)

Gunakan seluruh isi dokumen ini sebagai instruksi (system/developer prompt) untuk AI desainer & pengembang aplikasi agar merombak antarmuka aplikasi menjadi **Liquid Glass ala iOS** yang super minimalis, sekaligus menerapkan **spesifikasi engine encoder FFmpeg** resmi yang telah ditentukan di balik layar.

---

## 1. Ringkasan Visi Produk

Aplikasi ini bertujuan menghasilkan video HD, teks tajam, hemat ukuran, lolos kompresi WhatsApp, dan aman untuk CPU HP tanpa overheating.

Prinsip utamanya adalah **pemisahan total antara kompleksitas engine dan kemudahan tampilan**:
- **Di Depan Layar (UI):** Tampilan mewah bergaya **Liquid Glass iOS**, sangat bersih, elegan, dan ramah orang awam. Semua istilah teknis rumit (seperti CRF, GOP, Bitrate, FFmpeg, scale filter, konsol log) **DILARANG MUNCUL** di antarmuka pengguna.
- **Di Balik Layar (Engine/Background):** Menggunakan spesifikasi encoder FFmpeg yang sangat presisi dengan rumus anti-bloat bitrate, auto-split video > 90 detik, dan scale filter adaptif.

---

## 2. Aturan Tampilan Antarmuka (UI Design Rules)

### A. Hilangkan Hal-Hal Teknis & Teks Tidak Perlu (Anti-Clutter)
1. **Dilarang Menampilkan Istilah Kompresi:**
   - Hapus teks/kartu yang menyebutkan: `CRF 20`, `Bitrate`, `Maxrate`, `Bufsize`, `GOP (-g 48)`, `libx264`, `aq-mode`, `FastStart`, `yuv420p`, dan sejenisnya.
   - Hapus komponen `CommandInspectorCard` (kotak terminal dan preview perintah FFmpeg). Pengguna awam tidak memerlukan perintah terminal.
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
   - **Sudut Melengkung Halus (Super-ellipse / Large Rounded Corners):** Gunakan radius sudut melengkung khas iOS antara `24.dp` hingga `28.dp` untuk kartu utama, dan `16.dp` s.d. `20.dp` untuk tombol/kartu kecil.
   - **Kedalaman Visual (Depth & Mesh Glow):** Di balik kartu kaca, buat gradasi latar belakang dinamis yang gelap dan elegan (perpaduan Deep Obsidian `#070D0B`, Forest Teal gelap, dan subtle radial aura warna emerald lembut di bagian atas layar).
2. **Palet Warna Mewah:**
   - **Background Utama:** Dark Obsidian `#070D0B` s.d. `#0B1612`.
   - **Aksen Utama:** Emerald Tint / Soft Mint (`#2DD4BF` atau `#10B981`) untuk tombol aksi utama dan indikator aktif.
   - **Permukaan Kartu Kaca:** Frosted Black-Green Glass (`#0E1B16` dengan opasitas 50% - 65%).
   - **Tipografi:** Teks judul putih bersih (`#FFFFFF`), teks pendukung/subteks abu-abu netral sejuk (`#94A3B8`).

---

## 3. Alur Pengalaman Pengguna (User Flow)

Aplikasi difokuskan menjadi 3 fase sederhana dalam 1 halaman tunggal yang intuitif:

### 1. Header yang Bersih & Elegan
- **Ikon Aplikasi:** Ikon kotak tumpul minimalis dengan aksen kaca lembut.
- **Identitas:** Nama aplikasi `StatusHD` dengan tipografi tebal dan modern.
- **Slogan Singkat:** *"Kualitas jernih untuk Status WhatsApp"* (tanpa badge atau status offline/teknis).

### 2. Area Pemilihan Video (State: Belum Ada Video)
- **Tampilan Kotak Kaca Utama:**
  - Area sentuh berbentuk kartu kaca bundar/oval halus dengan ikon film/galeri yang bersinar lembut.
  - Teks panduan ramah: *"Pilih Video dari Galeri"*.
  - Subteks: *"Format dan resolusi akan dioptimalkan otomatis agar jernih dan tidak pecah."*
  - Tombol aksi utama bergaya kaca tebal: **"Buka Galeri"**.

### 3. Informasi Video Terpilih (State: Video Dipilih)
- **Pratinjau Video Elegan:**
  - Thumbnail video dengan sudut melengkung halus dan durasi di pojok bawah.
  - Nama file video (dipotong rapi) dan informasi sederhana yang mudah dipahami: durasi dan ukuran file asli (contoh: `0:45 • 42 MB`).
  - Jika video lebih dari batas status (di atas 90 detik), berikan pemberitahuan bersahabat:
    *"Video panjang akan otomatis dibagi menjadi bagian-bagian rapi."*
  - Tombol kecil bergaya kaca: *"Ganti Video"*.

### 4. Tombol Aksi Utama (Call to Action)
- Tombol kapsul besar dengan efek kaca bercahaya hijau emerald lembut:
  - Teks: **"Optimalkan Video"**
  - Efek saat ditekan: Animasi klik halus (scale transition).

### 5. Tampilan Pemrosesan (State: Sedang Berjalan)
- Desain minimalis tanpa angka-angka teknis yang membingungkan:
  - Bilah progres halus (smooth animated progress bar) berwarna mint neon.
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
  - Pratinjau mini yang bisa diklik untuk memutar langsung.
  - Tombol cepat simpan dan bagikan per klip.
- **Tombol Utama Hasil:**
  - **"Kirim ke Status WhatsApp"** (Tombol hijau cerah paling menonjol, langsung membuka WhatsApp Status).
  - **"Simpan ke Galeri"** (Tombol kedua beraksen kaca elegan).
  - Teks/tombol kecil: *"Proses video lainnya"*.

---

## 4. Spesifikasi Engine Encoder (Di Balik Layar / Background)

AI pengembang wajib mengonfigurasikan engine pemrosesan video sesuai spesifikasi berikut:

### A. Logika Algoritma Aplikasi
1. **Pemotongan Durasi Otomatis (Jika Video > 90 Detik):**
   - Video dipecah otomatis per 90 detik (1 menit 30 detik).
   - Contoh: Video 4 menit (240 detik) -> Dipecah menjadi 3 bagian:
     - Bagian 1: detik 0 s.d. 90 (`-ss 0 -t 90`)
     - Bagian 2: detik 90 s.d. 180 (`-ss 90 -t 90`)
     - Bagian 3: detik 180 s.d. 240 (`-ss 180 -t 60`)
2. **Rumus Bitrate Asli (Anti-Bloat / Anti-Bengkak):**
   - Hitung bitrate video asli sebelum di-encode:
     ```
     Bitrate Asli (kbps) = ((Ukuran File Byte * 8) / Durasi Detik) / 1000
     ```
   - Tentukan nilai `Maxrate`:
     - Jika `Bitrate Asli > 6000`, maka `Maxrate = 6000`
     - Jika `Bitrate Asli < 500`, maka `Maxrate = 500`
     - Jika di antaranya, `Maxrate = Bitrate Asli`
   - Tentukan nilai `Bufsize`:
     ```
     Bufsize = Maxrate * 2
     ```

### B. Parameter Lengkap FFmpeg
Setiap potongan video dieksekusi dengan argumen berikut:
- `-y` : Overwrite file output jika sudah ada.
- `-ss [DETIK_AWAL]` : Titik mulai potongan (detik).
- `-i [INPUT_PATH]` : File video asli.
- `-t [DURASI_POTONGAN]` : Durasi output (maksimal 90 detik).
- `-c:v libx264` : Codec video standar WhatsApp (H.264).
- `-preset faster` : Optimasi kecepatan untuk HP (stabil, hemat daya, tidak panas).
- `-tune film` : Optimasi khusus untuk video non-animasi.
- `-x264-params aq-mode=3` : Mempertegas detail di area gelap (mencegah artifak kotak-kotak).
- `-crf 20` : Kualitas visual 95% (adaptif menyesuaikan bitrate).
- `-g 48` : GOP Size (keyframe tiap ~2 detik agar mulus saat scrubbing di WhatsApp).
- `-maxrate [MAXRATE]k` : Batas maksimal bitrate (rem darurat anti-bengkak dari rumus 4A).
- `-bufsize [BUFSIZE]k` : Buffer bitrate (2x dari maxrate).
- `-vf scale=w=if(gte(iw\,ih)\,-2\,min(1080\,iw)):h=if(gte(iw\,ih)\,min(1080\,ih)\,-2)` : Filter skala resolusi pintar (menjaga aspek rasio, mencegah video tegak jadi gepeng, batas maksimal 1080p).
  *(Catatan: Karakter koma `,` pada ekspresi filter wajib di-escape dengan `\,` agar tidak memutus filter).*
- `-pix_fmt yuv420p` : Format pixel standar kompatibilitas perangkat mobile.
- `-c:a aac` : Codec audio standar AAC.
- `-b:a 128k` : Kualitas audio 128 kbps (jernih dan efisien).
- `-movflags +faststart` : Struktur file atom MP4 di depan agar video dapat diputar instan tanpa menunggu unduhan penuh.

---

## 5. Pola Komponen Dasar Jetpack Compose

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

---

## 6. Petunjuk Akhir untuk AI Pengembang

1. **Jaga Kebersihan Layar:** Jangan sekali-kali menampilkan argumen FFmpeg, rumus bitrate, atau opsi CRF di tampilan depan. Seluruh logika Bagian 4 berada di lapisan ViewModel/Engine.
2. **Desain Ramah Pengguna:** Pastikan teks di antarmuka sepenuhnya menggunakan bahasa Indonesia yang santun, ringkas, dan modern.
3. **Sentuhan Halus:** Berikan feedback visual yang responsif (haptic feedback saat tombol ditekan, animasi transisi halus antar-state) agar pengalaman Liquid Glass iOS terasa nyata.
