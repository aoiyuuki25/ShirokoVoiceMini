# Shiroko Voice Mini — prototype 0.1

Aplikasi Android native sederhana untuk **uji monitor mikrofon lokal** dengan perubahan pitch berbasis kecepatan playback.

## Yang sudah ada
- Tombol mulai/hentikan monitor mikrofon.
- Slider pitch kira-kira 0.75× sampai 1.35×.
- Izin mikrofon Android.
- Tidak memakai model AI besar atau koneksi internet.

## Batasan penting
- Ini **bukan** AI voice conversion dan tidak menghasilkan suara karakter anime tertentu.
- Pitch diubah dengan mengubah playback rate, sehingga kecepatan bicara ikut berubah.
- Audio hanya diputar kembali lewat output HP. Android umumnya tidak menyediakan cara bagi aplikasi biasa untuk memasukkan audio hasil olahan ini sebagai mikrofon virtual ke MLBB.
- Kompatibilitas dengan voice chat MLBB **belum diuji dan tidak dijanjikan**.
- Memutar mikrofon ke speaker HP dapat menimbulkan gema/feedback. Mulai dari volume rendah dan hentikan jika terdengar melengking.
- Belum diuji pada Infinix Smart 8 fisik. Kinerja dan dukungan audio dapat berbeda antarperangkat.

## Build APK
1. Buka folder proyek di Android Studio pada komputer.
2. Biarkan Gradle menyelesaikan sinkronisasi.
3. Pilih **Build > Build Bundle(s) / APK(s) > Build APK(s)**.
4. Pasang APK hasil build di perangkat Android untuk pengujian.

Dibuat sebagai proof-of-concept edukatif. Gunakan mikrofon hanya setelah izin diberikan secara eksplisit.
