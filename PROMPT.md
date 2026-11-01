# Prompt Panduan Redesain UI & Engine: StatusHD (Authentic iOS Liquid Glass Edition)

Gunakan seluruh isi dokumen ini sebagai instruksi utama (system & developer prompt) untuk AI desainer UI/UX dan pengembang Jetpack Compose agar merombak total antarmuka aplikasi **StatusHD**.

---

## 1. Masalah Desain Sebelumnya & Arah Perubahan

Berdasarkan evaluasi tampilan lama:
1. **Warna Kusam & Gelap Pekat (Bukan Karakter iOS):** Warna lama terlalu banyak memakai hijau lumut tua/kehitaman muram yang kaku. iOS Liquid Glass / VisionOS mengandalkan kaca netral transparan (frosted glass) di atas kanvas gelap mewah (Deep Midnight / Obsidian) dengan kilau cahaya putih di tepi atas kaca, serta aksen warna cerah yang sangat bersih dan hidup.
2. **Terlalu Ramai & Kaku (Cluttered Boxy UI):** Banyaknya kotak-kotak bertingkat, matriks teknis, terminal box, switch berlebihan, dan badge kaku merusak estetika minimalis.
3. **Istilah Teknis Rumit:** Istilah seperti CRF, GOP, Bitrate, Scale filter, Preset, dan FFmpeg membuat bingung orang awam.
4. **Permintaan Khusus:** **Hapus GOP sepenuhnya!** Jangan gunakan parameter `-g` di seluruh proses encode maupun UI.

---

## 2. Karakteristik Estetika iOS Liquid Glass (Otentik Apple Style)

Liquid Glass di iOS dan VisionOS bukan sekadar "kotak hijau transparan", melainkan ilusi material kaca cair yang membiaskan cahaya di latar belakang gelap:

### A. Palet Warna Mewah & Bersih
- **Canvas / Background Utama:** Deep Midnight Obsidian (`#06080D` s.d. `#0B0F17`) dengan gradasi radial ambient cahaya ungu/cyan atau emerald yang sangat lembut dan redup di latar belakang (`RadialGradient` beropasitas 10-15%).
- **Permukaan Kaca (Frosted Glass Plate):**
  - Menggunakan putih transparan beropasitas tipis di atas material gelap: `Color(0xFFFFFFFF).copy(alpha = 0.07f)` hingga `0.10f`.
  - Jangan gunakan warna hijau lumpur pekat untuk latar belakang kartu.
- **Specular Highlight Border (Kilau Kaca Tipis 0.75 - 1 dp):**
  - Gradasi vertikal presisi: bagian atas kaca menangkap cahaya putih (`Color(0xFFFFFFFF).copy(alpha = 0.28f)`), lalu memudar ke bagian bawah (`Color(0xFFFFFFFF).copy(alpha = 0.03f)`).
- **Aksen Warna (iOS Vibrancy Accent):**
  - **Electric Cyan / Mint:** `#00F2FE` atau `#10E599` yang terang dan segar untuk tombol aksi utama dan indikator aktif.
  - **Teks:** Judul putih kristal (`#FFFFFF`), teks sekunder abu-abu lembut iOS (`#8E8E93` atau `#98989F`).

### B. Geometri & Tipografi Apple
- **Radius Super-ellipse (Squircle):** Sudut melengkung sangat halus khas iOS (`28.dp` s.d. `32.dp` untuk kontainer besar, `18.dp` s.d. `22.dp` untuk tombol/pil).
- **Hirarki Tipografi Human Interface Guidelines:** Menggunakan bobot SF Pro / modern sans-serif:
  - Header tebal, tegas, dan lapang (*Large Title* / *Title 1*).
  - Jarak antar-elemen (spacing) bernapas lega (`20.dp` - `24.dp`), tidak berdesakan.
- **Micro-Interactions:** Tombol memiliki animasi pantulan halus (spring scale animation) saat disentuh.

---

## 3. Aturan Bersih (Anti-Jargon & Anti-AI Slop)

1. **Hapus Seluruh Elemen Ini dari UI:**
   - Hapus **Command Inspector / Terminal Box** (preview perintah ffmpeg).
   - Hapus **Engine Encoding Matrix** (tabel CRF, GOP, Maxrate, Bufsize).
   - Hapus opsi **Standard Video / High Motion / Jedag-Jedug**.
   - Hapus badge teks klise: `LIQUID GLASS`, `Offline`, `BYPASS READY`, `Anti-Bloat Guard`, `CRF 20`, dll.
   - Hapus tombol demo uji coba (`15s Demo`, `100s Split Demo`).
   - Hapus panel telemetri saat encoding (`Speed 1.2x`, `FPS 30`, `Live FFmpeg Console`).
2. **Dilarang Menampilkan Istilah Kompresi:**
   - Pengguna awam hanya butuh tahu: video diunggah, proses selesai, kualitas jernih maksimal tanpa pecah, siap dikirim ke WhatsApp Status.

---

## 4. Alur Layar Tunggal (Ultra-Minimalist Screen Flow)

Halaman utama hanya memiliki 3 tampilan state yang berganti secara dinamis dan mulus:

### State 1: Pemilihan Video (Idle)
- **Top Header:**
  - Teks minimalis elegan: **StatusHD**
  - Subtitle halus: *"Kualitas jernih untuk Status WhatsApp"*
- **Hero Glass Card (Pusat Sentuh Kaca):**
  - Kartu kaca cair besar berbentuk squircle dengan latar transparan halus.
  - Ikon galeri / video melayang dengan kilau cahaya lembut di tengah.
  - Teks panduan ramah: *"Pilih Video"*
  - Subteks: *"Format dan ukuran disesuaikan otomatis agar tidak buram."*
  - Tombol aksi iOS Kaca Berpendar: **"Buka Galeri"**.

### State 2: Video Terpilih & Siap Dioptimalkan (Selected)
- **Kartu Pratinjau Kaca Elegan:**
  - Thumbnail video melengkung lembut (`16.dp`) dengan durasi di pojok kanan bawah.
  - Nama file video & ukuran file asli (contoh: `Liburan.mp4 • 48 MB`).
  - Jika durasi > 90 detik, muncul keterangan bersahabat tanpa istilah teknis:
    *"Video panjang otomatis dibagi menjadi beberapa bagian rapi."*
  - Tombol teks kaca halus di kanan: *"Ganti Video"*.
- **Tombol Utama (Primary Action):**
  - Tombol kapsul penuh (`56.dp`) berwarna gradasi cerah (Electric Mint `#10E599` ke Emerald `#059669`) dengan teks putih/gelap kontras tebal: **"Optimalkan Kualitas"**.

### State 3: Proses Berjalan (Processing)
- **Animasi Progres Kaca Halus:**
  - Bilah progres melengkung lembut dengan animasi glow.
  - Persentase sederhana: `45%`.
  - Status manusiawi: *"Mengoptimalkan video..."* atau *"Memproses bagian 1 dari 2..."*.
  - Tombol kecil outline kaca tipis: *"Batalkan"*.

### State 4: Selesai & Bagikan (Success)
- **Kartu Hasil Kaca Mewah:**
  - Ikon centang berpendar hijau segar.
  - Judul: *"Video Siap Dibagikan!"*
  - Kartu perbandingan simpel: `48 MB  ➔  14 MB` *(Hemat 70% • Kualitas HD)*.
- **Daftar Bagian Video (Jika lebih dari 90 detik):**
  - Tiap bagian tampil sebagai baris kaca minimalis yang rapi (Bagian 1, Bagian 2) dengan thumbnail mini dan tombol play pratinjau.
- **Dua Tombol Aksi Bersih:**
  - **"Bagikan ke WhatsApp Status"** (Tombol utama paling terang).
  - **"Simpan ke Galeri"** (Tombol sekunder bergaya kaca transparan).
  - Tombol tautan bawah: *"Pilih video lain"*.

---

## 5. Spesifikasi Engine Encoder (Di Balik Layar / Background)

*Catatan: Parameter GOP (`-g`) telah **DIHAPUS SEPENUHNYA** sesuai permintaan.*

### A. Logika Pemotongan (> 90 Detik)
- Jika durasi video > 90 detik, pecah menjadi segmen 90 detik secara berurutan menggunakan `-ss [DETIK_MULAI]` dan `-t [DURASI_SEGMEN]`.

### B. Rumus Bitrate Adaptif (Anti-Bengkak)
```
Bitrate Asli (kbps) = ((Ukuran File Byte * 8) / Durasi Detik) / 1000

Jika Bitrate Asli > 6000 kbps  ➔  Maxrate = 6000k
Jika Bitrate Asli < 500 kbps   ➔  Maxrate = 500k
Jika di antaranya              ➔  Maxrate = Bitrate Asli (kbps)

Bufsize = Maxrate * 2
```

### C. Daftar Argumen FFmpeg Resmi (Tanpa GOP)
Setiap segmen dieksekusi dengan argumen berikut:
```bash
-y
-ss [DETIK_AWAL]
-i [INPUT_FILE]
-t [DURASI]
-c:v libx264
-preset faster
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
[OUTPUT_FILE]
```
*(Catatan: Jangan tambahkan `-g` atau parameter GOP apa pun).*

---

## 6. Contoh Template Komponen Jetpack Compose (iOS Liquid Glass)

Gunakan struktur kode ini sebagai panduan pembuatan komponen kaca otentik:

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

// Palet Kaca Otentik iOS (Neutral Translucent Frosted Glass)
val IOSGlassFill = Color(0xFFFFFFFF).copy(alpha = 0.08f)
val IOSGlassHighlightTop = Color(0xFFFFFFFF).copy(alpha = 0.28f)
val IOSGlassHighlightBottom = Color(0xFFFFFFFF).copy(alpha = 0.03f)

@Composable
fun IOSLiquidGlassCard(
    modifier: Modifier = Modifier,
    cornerRadius: Dp = 28.dp,
    padding: Dp = 20.dp,
    content: @Composable BoxScope.() -> Unit
) {
    val shape = RoundedCornerShape(cornerRadius)
    Box(
        modifier = modifier
            .clip(shape)
            .background(IOSGlassFill)
            .border(
                BorderStroke(
                    width = 1.dp,
                    brush = Brush.verticalGradient(
                        listOf(IOSGlassHighlightTop, IOSGlassHighlightBottom)
                    )
                ),
                shape = shape
            )
            .padding(padding),
        content = content
    )
}
```

---

## 7. Checklist Akhir untuk AI Pengembang

- [ ] Latar belakang aplikasi menggunakan Dark Midnight Obsidian bersih (`#06080D`), bukan hijau lumut kusam.
- [ ] Kartu menggunakan warna kaca transparan netral (`White 8%`) dengan garis pantulan specular putih tipis di atasnya.
- [ ] Seluruh kartu teknis (`CommandInspectorCard`, `SmartEngineGlassCard`, matriks CRF/bitrate, dan tombol demo 15s/100s) **DIHAPUS**.
- [ ] Tidak ada badge tulisan `LIQUID GLASS`, `Offline`, atau teks AI kaku.
- [ ] Parameter `-g` (GOP) **DIHAPUS TOTAL** dari eksekusi FFmpeg dan logika backend.
- [ ] Tampilan terasa lapang, mewah, halus, dan memanjakan mata seperti aplikasi native iOS.
