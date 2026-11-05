# Catatan Harian

Aplikasi Android untuk mencatat catatan harian, dengan login per pengguna dan data yang privat. Dibuat untuk tugas UTS mata kuliah **Pengembangan Aplikasi Bergerak** (Telkom University, Semester 7), Kelompok 1.

Aplikasi berjalan sepenuhnya offline. Data disimpan di SQLite lewat Room, tanpa internet dan tanpa API.

## Fitur

- Register dan login dengan password yang di-hash (SHA-256)
- Sesi login disimpan di SharedPreferences
- Catatan bersifat privat: setiap query ke database difilter dengan `user_id`, sehingga satu pengguna tidak bisa membaca, mengubah, atau menghapus catatan pengguna lain
- CRUD catatan di lapisan data, termasuk pencarian berdasarkan judul dan isi

## Status pengerjaan

Proyek dikerjakan berkelompok, satu bagian per orang.

| # | Bagian | Cakupan | Status |
|---|---|---|---|
| 1 | Auth | Register, login, validasi, hash password | Selesai |
| 2 | Data layer | Entity `Note`, `NoteDao`, `AppDatabase`, test | Selesai |
| 3 | Dashboard dan Logout | Layar utama, sapaan, logout, cek sesi | Belum |
| 4 | List catatan | RecyclerView, adapter, tampilan kosong, search bar | Belum |
| 5 | Form tambah | Layar tambah, validasi input, simpan | Belum |
| 6 | Edit dan Hapus | Layar edit, hapus dengan konfirmasi | Belum |

`MainActivity` saat ini masih layar bawaan template. Setelah login berhasil, pengguna diarahkan ke layar ini, jadi alur Dashboard, List, Form, dan Edit/Hapus belum bisa dicoba dari UI.

## Teknologi

| Komponen | Versi |
|---|---|
| Bahasa | Kotlin |
| Android Gradle Plugin | 9.4.0 |
| KSP | 2.3.13 |
| Room | 2.8.5 |
| Material Components | 1.10.0 |
| compileSdk / targetSdk | 37 |
| minSdk | 24 (Android 7.0) |
| JDK | 11 (source dan target compatibility) |

ViewBinding aktif.

## Struktur proyek

```
app/src/main/java/com/uts/catatanharian/
├── MainActivity.kt              # Layar setelah login (masih template)
├── auth/
│   ├── LoginActivity.kt         # Launcher; langsung ke MainActivity bila sesi aktif
│   ├── RegisterActivity.kt
│   └── AuthValidator064.kt      # Validasi input dan hash password
├── data/
│   ├── AppDatabase.kt           # Room database, versi 2
│   ├── User064.kt               # Tabel users_064
│   ├── UserDao064.kt
│   ├── Note064.kt               # Tabel notes_064
│   └── NoteDao064.kt
└── util/
    └── SessionManager.kt        # Simpan dan baca sesi (SharedPreferences)
```

### Konvensi penamaan

Sesuai ketentuan soal, nama class, tabel, kolom, dan fungsi hasil modifikasi diakhiri **3 digit NIM** pemilik bagiannya. Karena tugas dibagi, tiap bagian memakai NIM-nya sendiri:

- `064`: bagian Auth (`User064`, `UserDao064`, `AuthValidator064`, tabel `users_064`)
- `064`: bagian Data layer (`Note064`, `NoteDao064`, tabel `notes_064`)

## Skema database

Nama database: `catatan_harian.db`, versi 2.

**`users_064`**

| Kolom | Tipe | Keterangan |
|---|---|---|
| `id` | Int | Primary key, auto increment |
| `username_064` | String | Unique index |
| `password_064` | String | Hash SHA-256 |

**`notes_064`**

| Kolom | Tipe | Keterangan |
|---|---|---|
| `id` | Int | Primary key, auto increment |
| `user_id_064` | Int | Foreign key ke `users_064.id`, `ON DELETE CASCADE`, diberi index |
| `judul_064` | String | |
| `isi_064` | String | |
| `tanggal_064` | Long | Epoch millis; format ke teks dilakukan di UI |

### API `NoteDao064`

Semua fungsi `suspend` dan semuanya terfilter `userId`.

| Fungsi | Keterangan |
|---|---|
| `insertNote064(note): Long` | Tambah catatan, mengembalikan id baru |
| `getAllByUser064(userId): List<Note064>` | Semua catatan milik user, tanggal terbaru dulu |
| `getById064(id, userId): Note064?` | `null` bila catatan bukan milik user |
| `updateNote064(id, userId, judul, isi): Int` | Mengembalikan jumlah baris terubah; `tanggal` tidak diubah |
| `deleteNote064(id, userId): Int` | Mengembalikan jumlah baris terhapus |
| `searchByUser064(userId, keyword): List<Note064>` | Cari di judul dan isi |

`userId` diambil dari `SessionManager.getUserId()`.

> **Migrasi database:** `AppDatabase` memakai `fallbackToDestructiveMigration()`. Setiap perubahan skema menghapus data lama. Setelah menarik perubahan yang menaikkan versi database, uninstall aplikasi atau clear data sekali di emulator/perangkat.

## Aturan validasi

- Username dan password tidak boleh kosong
- Password minimal 6 karakter
- Konfirmasi password harus cocok
- Username harus unik

## Cara menjalankan

Prasyarat: Android Studio versi terbaru yang mendukung AGP 9.4.0, dan emulator atau perangkat dengan Android 7.0 (API 24) ke atas.

1. Clone repo, lalu buka foldernya di Android Studio.
2. Tunggu Gradle sync selesai.
3. Pilih konfigurasi `app` dan jalankan di emulator atau perangkat.

Lewat command line:

```bash
./gradlew assembleDebug
```

Di Windows, pakai `gradlew.bat assembleDebug`.

## Pengujian

Test untuk data layer ada di `app/src/androidTest/java/com/uts/catatanharian/data/ExampleInstrumentedTest.kt` (kelas `NoteDao064Test`). Test memakai database in-memory dan butuh emulator atau perangkat yang berjalan.

Yang diuji:

- Insert dan baca catatan, termasuk urutan tanggal terbaru dulu
- Isolasi data antar pengguna pada baca, update, dan delete
- Pencarian terfilter per pengguna

Menjalankan dari command line:

```bash
./gradlew connectedDebugAndroidTest
```

Belum ada test untuk `ON DELETE CASCADE` (hapus user ikut menghapus catatannya).

## Alur Git

- Satu branch per orang, misalnya `feature/register-login` dan `feature/data-layer`
- Digabung ke `master` lewat pull request
- `AppDatabase.kt` hanya diubah oleh pemilik bagian Data layer, untuk menghindari konflik merge. Perubahan dari bagian lain diminta lewat dia.

## Ketentuan tugas

- Deadline pengumpulan: **9 November 2026**
- Alur navigasi yang diminta: Login → Dashboard → List Data (RecyclerView) → Form Tambah → Edit/Hapus
