# 👹 YokaiList

**YokaiList** adalah aplikasi Android untuk para penggemar anime yang ingin dengan mudah mencari, melihat anime musiman (seasonal), serta mengatur daftar tontonan mereka secara terorganisir. Dibuat dengan Java dan Android Studio, aplikasi ini bertujuan untuk menggantikan kebiasaan mencatat daftar anime secara manual di notes dan melihat season anime dengan mudah. 

---

## 📌 Fitur Utama

- 🔍 **Pencarian Anime berdasarkan Judul & History Pencarian **
- 🏆 **Top Anime berdasarkan Skor & Popularitas**
- 📅 **Daftar Anime Musiman (Season Ini & Akan Datang)**
- 📂 **Kelola Daftar Anime Pribadi**
- 🌗 **Toggle Light/Dark Mode**
- 🔗 **Detail Lengkap Anime**
- 🧠 **Filter Berdasarkan Genre**

---

## 📱 Cara Penggunaan

1. **Home Page (🏠 HomeFragment):**
   - Menampilkan *Top 10 Anime by Score* yang sedang tayang.
   - Menampilkan *Top 10 Anime dengan skor tertinggi sepanjang masa.*

2. **Discover Page (🔍 DiscoverFragment):**
   - User dapat melakukan pencarian anime berdasarkan **judul**.
   - Riwayat pencarian disimpan otomatis menggunakan **Shared Preferences**.
   - Bisa juga menjelajah anime berdasarkan **genre** pilihan.

3. **Seasonal Page (📅 SeasonalFragment):**
   - Terdapat dua tab layout:
     - **ThisSeasonFragment**: Menampilkan anime yang sedang tayang musim ini.
     - **UpcomingSeasonFragment**: Menampilkan anime yang akan datang.

4. **My List Page (📘 MyListFragment):**
   - Menampilkan daftar anime user lengkap dengan:
     - Tombol **Edit** (ubah status, progress, dsb.)
     - Tombol **Add Episode** (langsung tambah progres episode).
   - Fitur **Filter Status**:
     - Watching
     - Completed
     - Plan to Watch
     - Dropped
     - On Hold

5. **Anime Detail Page (ℹ️ AnimeDetailActivity):**
   - Menampilkan informasi lengkap tentang anime:
     - Skor, Rank, Popularity, Members
     - Tipe, Status, Jumlah episode, Durasi
     - Genre, Sinopsis, Trailer (link ke YouTube)
     - Source, Studio, Rating, Season, Tanggal tayang
     - Cast (gambar + nama karakter)

6. **Add/Edit Anime Page (✍️ AddToMyListActivity):**
   - User dapat menambahkan anime ke daftar pribadi dengan memilih:
     - Status
     - Progress episode
     - Skor anime
     - Tanggal mulai & selesai menonton

---

## 🔧 Implementasi Teknis

- **Bahasa Pemrograman:** Java
- **Platform:** Android (Android Studio)
- **Arsitektur Navigasi:** Fragment + BottomNavigationView
- **Tema:** Light & Dark Mode dengan toggle switch (disimpan dalam SharedPreferences)
- **Penyimpanan Data MyList:** SQLite lokal
- **API:** [Jikan API](https://jikan.moe/)
- **Riwayat Pencarian & Preferensi Tema:** SharedPreferences
- **Navigasi UI** Menggunakan kombinasi Activity, Fragment, dan Nested Fragment

---

## 📁 Struktur Folder 

```
├── activities/
│   ├── MainActivity.java
│   ├── anime/
│   │   ├── AnimeDetailActivity.java
│   │   ├── AddToMyListActivity.java
├── fragments/
│   ├── HomeFragment.java
│   ├── DiscoverFragment.java
│   ├── SeasonalFragment.java
│   ├── ThisSeasonFragment.java
│   ├── UpcomingSeasonFragment.java
│   ├── MyListFragment.java
├── database/
│   ├── AnimeDbHelper.java
│   ├── AnimeModel.java
├── adapters/
│   ├── AnimeAdapter.java
│   ├── CharacterAdapter.java
│   ├── GenreAdapter.java
│   ├── MyListAdapter.java
│   ├── SeasonalPagerAdapter.java
│   ├── SeasonalAnimeAdapter.java
│   ├── TopAiringAnimeAdapter.java
│   ├── TopScoreAnimeAdapter.java
├── data/
│   ├── db/
│   │   ├── DatabaseContract.java
│   │   ├── DatabaseHelper.java
│   │   ├── AnimeStatusHelper.java
│   │   ├── MappingHelper
│   ├── models/
│   │   ├── AnimeStatus.java
│   ├── network/
│   │   ├── ApiConfig.java
│   │   ├── ApiService.java
│   ├── response/
│   │   ├── animedetail/
│   │   │   ├── Aired.java
│   │   │   ├── Anime.java
│   │   │   ├── Broadcast.java
│   │   │   ├── GenresItem.java
│   │   │   ├── Images.java
│   │   │   ├── Jpg.java
│   │   │   ├── LicensorsItem.java
│   │   │   ├── ProducersItem.java
│   │   │   ├── StudiosItem.java
│   │   │   ├── Trailer.java
│   │   │   ├── TrailerImages.java
│   │   │   ├── Webp.java
│   │   ├── character/
│   │   │   ├── Character.java
│   │   │   ├── DataItem.java
│   │   │   ├── Images.java
│   │   │   ├── Webp.java
│   │   ├── genre/
│   │   │   ├── Genre.java
│   │   │   ├── GenreResponse.java
│   │   ├── AnimeDetailResponse.java
│   │   ├── AnimeResponse.java
│   │   ├── CharacterResponse.java
│   │   ├── Items.java
│   │   ├── Pagination.java
```

---
## 📷 ScreenShots

1. **Home Page**
<table>
  <tr>
    <td>
      <img src="https://github.com/user-attachments/assets/ca921348-c3a4-4c34-bf93-16073fe4c306" width="300"/>
    </td>
    <td>
      <img src="https://github.com/user-attachments/assets/cb6ef100-a0bf-457e-87d3-d149314c9743" width="300"/>
    </td>
  </tr>
</table>

2. **Discover Page**
<table>
  <tr>
    <td>
      <img src="https://github.com/user-attachments/assets/ebf097fe-f0db-4d7b-9323-150a744c691b" width="300"/>
    </td>
    <td>
      <img src="https://github.com/user-attachments/assets/8f5a3789-aa71-46e8-b1e8-5d2f98773e16" width="300"/>
    </td>
  </tr>
</table>
  
4. **Seasonal Page**
<table>
  <tr>
    <td>
      <img src="https://github.com/user-attachments/assets/e69d2e4c-eeed-4a49-ba83-7c85bf248787" width="300"/>
    </td>
    <td>
      <img src="https://github.com/user-attachments/assets/de5b7903-e0d5-44fb-85e8-923e7666ea16" width="300"/>
    </td>
  </tr>
</table>

6. **My List Page**
<table>
  <tr>
    <td>
      <img src="https://github.com/user-attachments/assets/38750467-774b-4508-a78d-320e0550579b" width="300"/>
    </td>
    <td>
      <img src="https://github.com/user-attachments/assets/af1316a3-b97b-4d6c-b6c2-c7387a18bed9" width="300"/>
    </td>
  </tr>
</table>

8. **Anime Detail Page**
<table>
  <tr>
    <td>
      <img src="https://github.com/user-attachments/assets/65ac442a-b703-44a4-a3fc-7118a15a01d8" width="300"/>
    </td>
    <td>
      <img src="https://github.com/user-attachments/assets/6cf9ad6a-5d5a-457c-afd7-ceda38aa97a4" width="300"/>
    </td>
  </tr>
  <tr>
    <td>
      <img src="https://github.com/user-attachments/assets/99ec6804-720c-429b-ba7c-5fe400388903" width="300"/>
    </td>
    <td>
      <img src="https://github.com/user-attachments/assets/e27bb53a-1d10-47e5-95cf-873ca52e2a2d" width="300"/>
    </td>
  </tr>
</table>

10. **Add To My List Page**
<table>
  <tr>
    <td>
      <img src="https://github.com/user-attachments/assets/54d61ced-603f-4a41-ba3d-ce0739983cc5" width="300"/>
    </td>
    <td>
      <img src="https://github.com/user-attachments/assets/d18816da-4b3c-463e-854f-b8d57b39fe47" width="300"/>
    </td>
  </tr>
</table>
