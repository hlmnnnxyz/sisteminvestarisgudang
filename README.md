# Sistem Inventaris Barang

Aplikasi **Sistem Inventaris Barang** adalah aplikasi mobile berbasis Android yang digunakan untuk mengelola data inventaris barang, kategori, mutasi stok, manajemen user, dan pelaporan statistik.  
Aplikasi ini dikembangkan sebagai **Tugas 2 Praktikum Mobile Programming**.

---

## Informasi Tugas

- **Mata Kuliah:** Praktikum Mobile Programming  
- **Tugas:** Tugas 2  
- **Nama:** Joice Hielman Abbrori  
- **NIM:** 722520073  

---

## Fitur Utama

- **Manajemen Barang:** Tambah, edit, hapus, dan pencarian barang.
- **Kategori Barang:** Pengelolaan kategori barang.
- **Mutasi Stok:** Penambahan dan pengurangan stok barang beserta log mutasi.
- **Manajemen User:** Admin dapat menambah dan menghapus user dengan role (admin, petugas, viewer).
- **Audit Log:** Pencatatan aktivitas penting (login, tambah/edit/hapus barang, dll).
- **Laporan & Statistik:** Visualisasi data barang dan kategori menggunakan grafik.
- **Import/Export CSV:** Mendukung ekspor dan impor data barang dalam format CSV.
- **Autentikasi:** Login multi-user dengan role-based access.

---

## Instalasi & Build

1. **Clone repository ini:**
    ```sh
    git clone <repo-url>
    ```
2. **Buka di Android Studio atau Visual Studio Code (dengan ekstensi Android).**
3. **Build dan jalankan pada emulator atau perangkat Android.**

---

## Struktur Proyek

- `app/src/main/java/com/joo/sisteminvestarisbarang/`  
  Berisi seluruh source code Java aplikasi.
- `app/src/main/res/`  
  Berisi resource aplikasi (layout, drawable, values, dll).
- `app/build.gradle.kts`  
  Konfigurasi build module aplikasi.
- `README.md`  
  Dokumentasi proyek.

---

## Penggunaan

- **Login:**  
  Username & password default admin: `admin`  
- **Role:**  
  - Admin: akses penuh (semua fitur)
  - Petugas: tambah barang & mutasi stok
  - Viewer: hanya melihat data

---

## Lisensi

MIT License © 2025 Joice Hielman Abbrori

---

## Kontak

Untuk pertanyaan atau saran, silakan hubungi:  
instagram: @hlmnnnn.a
