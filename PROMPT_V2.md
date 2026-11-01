# Panduan Prompt Redesain UI v2: StatusHD (iOS Liquid Glass & Auto Player)

Gunakan dokumen ini sebagai panduan prompt langsung ke AI Anda untuk mengupdate kode aplikasi **StatusHD**. Panduan ini sudah disaring agar AI fokus hanya pada file yang perlu diubah dan tidak menyentuh file lainnya.

---

## 1. Daftar File yang Diubah (Scope of Changes)

Instruksikan AI Anda untuk **HANYA** memodifikasi file berikut, jangan mengubah file lainnya agar tidak bingung atau merusak arsitektur aplikasi:

### 🗑️ File yang DIHAPUS (Hapus Total):
1. `app/src/main/java/com/example/ui/components/CommandInspectorCard.kt`
2. `app/src/main/java/com/example/ui/components/SmartEngineGlassCard.kt`

### 🔒 File yang JANGAN DIUBAH (Biarkan Asli):
- `MainActivity.kt`
- `ui/components/VideoPlayerDialog.kt`
- `ui/theme/Color.kt`, `ui/theme/Theme.kt`, `ui/theme/Type.kt`
- `util/ExportHelper.kt`, `util/SampleVideoHelper.kt`, `util/VideoMetadataHelper.kt`

### ✏️ File yang DIUBAH (Hanya 9 File Ini):
1. `ui/components/GlassComponents.kt`
2. `ui/components/TopBarHeader.kt`
3. `ui/components/VideoSelectorCard.kt`
4. `ui/components/ExecutionProgressCard.kt`
5. `ui/components/ResultDisplayCard.kt`
6. `ui/screens/StatusHDOptimizerScreen.kt`
7. `model/VideoModels.kt`
8. `engine/StatusHDOptimizerEngine.kt`
9. `viewmodel/OptimizerViewModel.kt`

---

## 2. Aturan Tampilan: Estetika Liquid Glass iOS Otentik

Terapkan gaya visual iOS / VisionOS Frosted Glassmorphism:
- **Latar Belakang Canvas:** Gunakan Dark Obsidian murni (`#000000` s.d. `#070D0B`), hilangkan warna hijau lumut kusam yang kaku.
- **Kartu Kaca Transparan (Liquid Glass):**
  - Warna kaca: Putih transparan netral (`Color.White.copy(alpha = 0.08f)` s.d. `0.12f`) atau permukaan gelap transparan (`#0F1E19` dengan alpha `0.55f`).
  - Border pantulan cahaya (Specular Highlight 1 dp): Bagian atas putih terang (`Color.White.copy(alpha = 0.30f)`), bagian bawah memudar (`Color.White.copy(alpha = 0.04f)`).
  - Sudut lengkung squircle khas Apple (`26.dp` - `28.dp`).
- **Bebas Jargon & Anti-AI Slop:**
  - Hapus badge: `LIQUID GLASS`, `Offline`, `BYPASS READY`, `Anti-Bloat Guard`, `CRF 20`.
  - Hapus tombol pengujian demo (`15s Demo`, `100s Split Demo`).
  - Hapus teks kecepatan, FPS, bitrate live, dan FFmpeg live terminal logs.

---

## 3. Rincian Perubahan per File untuk AI Anda

### 1. `ui/components/GlassComponents.kt`
- Buat wadah utama `LiquidGlassBox` dengan border gradasi specular highlight (`GlassBorderTopHighlight` dan `GlassBorderBottomShadow`).
- Hapus pill / elemen kaku yang tidak diperlukan.

### 2. `ui/components/TopBarHeader.kt`
- Buat header bersih dan anggun:
  - Ikon aplikasi dalam kotak kaca melengkung.
  - Judul: **StatusHD** (Font tebal).
  - Subtitle: *"Kualitas jernih untuk Status WhatsApp"*.
- Hapus badge teks `LIQUID GLASS`, badge `Offline`, dan tombol info spek dialog `(i)`.

### 3. `ui/components/VideoSelectorCard.kt`
- **Sebelum Video Dipilih:**
  - Area kaca elegan dengan ikon film/galeri melayang di tengah.
  - Teks: *"Pilih Video dari Galeri"*.
  - Subteks: *"Format dan ukuran disesuaikan otomatis agar jernih dan tidak pecah."*
  - Tombol kaca: **"Buka Galeri"**.
  - Hapus tombol demo `15s Demo` dan `100s Split Demo`.
- **Setelah Video Dipilih:**
  - Pratinjau thumbnail melengkung lembut dengan durasi.
  - Nama file video dan ukuran file.
  - Jika video > 90 detik, tampilkan teks halus: *"Video panjang akan otomatis dibagi menjadi bagian-bagian rapi."*
  - Tombol outline kaca: *"Ganti Video"*.

### 4. `ui/components/ExecutionProgressCard.kt`
- **State Idle:** Tombol kapsul kaca bercahaya: **"Optimalkan Kualitas"**.
- **State Memproses:**
  - Bilah progres lengkung halus dengan persentase `XX%`.
  - Status teks bersahabat: *"Menyiapkan kualitas terbaik..."* atau *"Memproses bagian 1 dari 2..."*.
  - Tombol outline kaca merah tipis: *"Batal"*.
  - Hapus panel telemetri (Speed, FPS, Bitrate) dan tombol log konsol FFmpeg.
- **State Batal/Error:** Pesan manusiawi *"Pengoptimalan dibatalkan"* / *"Video tidak dapat diproses. Coba pilih video lain"* dengan tombol *"Ulangi"* / *"Coba Lagi"*.

### 5. `ui/components/ResultDisplayCard.kt`
- **TAMPILKAN PEMUTAR VIDEO LANGSUNG (Inline Video Player):**
  - Begitu selesai, tampilkan pemutar video langsung di kartu kaca utama agar pengguna bisa langsung menonton hasilnya di tempat tanpa harus membuka dialog baru.
  - Tambahkan badge keren di atas video: **"✨ HD Ready"**.
- **HAPUS PERBANDINGAN UKURAN:**
  - Hapus kotak perbandingan ukuran MB ke MB karena pengguna awam tidak membutuhkannya.
- **Jika Video Terbagi (> 90 Detik):**
  - Tampilkan pemilih bagian video (`Bagian 1`, `Bagian 2`) untuk beralih video yang sedang diputar.
- **Tombol Aksi Utama:**
  - Tombol besar hijau menyala: **"Kirim ke Status WhatsApp"**.
  - Tombol sekunder kaca transparan: **"Simpan ke Galeri"**.
  - Tombol teks: *"Proses video lainnya"*.

### 6. `ui/screens/StatusHDOptimizerScreen.kt`
- Hapus impor dan pemanggilan `CommandInspectorCard` dan `SmartEngineGlassCard`.
- Sederhanakan alur layar menjadi:
  1. `TopBarHeader()`
  2. `VideoSelectorCard(...)`
  3. `ExecutionProgressCard(...)`
  4. `ResultDisplayCard(...)` saat state sukses.

### 7. `model/VideoModels.kt`
- Hapus kelas `SmartEncodingConfig` (semua parameter dipindahkan langsung ke engine backend).
- Sederhanakan `ProcessingState.Compressing` (hanya membawa `overallProgress`, `statusText`, `activeChunk`, `totalChunks`, tanpa telemetri FPS/speed/logs).
- Sederhanakan `OptimizationResult` tanpa menyimpan log command terminal.

### 8. `engine/StatusHDOptimizerEngine.kt`
Konfigurasikan encoder FFmpeg di balik layar dengan ketentuan berikut:
- **Auto-Split > 90 Detik:** Jika durasi video > 90 detik, potong per 90 detik (`-ss` dan `-t 90`).
- **Rumus Bitrate Anti-Bloat:**
  ```
  Bitrate Asli (kbps) = ((Ukuran File Byte * 8) / Durasi Detik) / 1000
  Maxrate = Bitrate Asli di-clamp antara 500k dan 6000k
  Bufsize = Maxrate * 2
  ```
- **Parameter FFmpeg (Preset MEDIUM, Tanpa GOP):**
  - `-y`
  - `-ss [DETIK_AWAL]`
  - `-i [INPUT_PATH]`
  - `-t [DURASI]`
  - `-c:v libx264`
  - `-preset medium` *(Catatan: Wajib gunakan preset medium)*
  - `-tune film`
  - `-x264-params aq-mode=3`
  - `-crf 20`
  - *(PENTING: JANGAN gunakan `-g` atau parameter GOP apa pun)*
  - `-maxrate [MAXRATE]k`
  - `-bufsize [BUFSIZE]k`
  - `-vf scale=w=if(gte(iw\,ih)\,-2\,min(1080\,iw)):h=if(gte(iw\,ih)\,min(1080\,ih)\,-2)`
  - `-pix_fmt yuv420p`
  - `-c:a aac -b:a 128k`
  - `-movflags +faststart`

### 9. `viewmodel/OptimizerViewModel.kt`
- Bersihkan state Flow yang tidak lagi dipakai (`smartConfig`, `forceAutoSplit`, `isCommandInspectorExpanded`, `isLiveLogsExpanded`, `isGeneratingSample`).
- Jalankan `engine.optimizeVideo(sourceVideo = video)` secara otomatis dan bersih.
