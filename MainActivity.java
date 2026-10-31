package com.shiroko.voicemini;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private MediaPlayer mp;
    private LinearLayout daftarAudio;
    private TextView status;
    private EditText pencarian;
    private final List<AudioItem> audioList = new ArrayList<>();

    static class AudioItem {
        String nama;
        int id;

        AudioItem(String nama, int id) {
            this.nama = nama;
            this.id = id;
        }
    }

    @Override
    protected void onCreate(Bundle b) {
        super.onCreate(b);

        ScrollView scroll = new ScrollView(this);
        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(24, 24, 24, 24);
        scroll.addView(root);

        TextView judul = new TextView(this);
        judul.setText("SHIROKO VOICE MINI");
        judul.setTextSize(24);
        root.addView(judul);

        TextView jumlah = new TextView(this);
        jumlah.setText("Memuat daftar suara...");
        root.addView(jumlah);

        status = new TextView(this);
        status.setText("Pilih suara untuk memutar audio.");
        root.addView(status);

        pencarian = new EditText(this);
        pencarian.setSingleLine(true);
        pencarian.setHint("Cari suara Shiroko...");
        root.addView(pencarian);

        Button berhenti = new Button(this);
        berhenti.setText("■ Berhenti");
        berhenti.setOnClickListener(v -> stopAudio());
        root.addView(berhenti);

        daftarAudio = new LinearLayout(this);
        daftarAudio.setOrientation(LinearLayout.VERTICAL);
        root.addView(daftarAudio);

        muatAudio();

        jumlah.setText("Audio tersedia: " + audioList.size());

        tampilkanAudio("");

        pencarian.addTextChangedListener(new android.text.TextWatcher() {
            @Override
            public void beforeTextChanged(
                    CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(
                    CharSequence s, int start, int before, int count) {
                tampilkanAudio(s.toString());
            }

            @Override
            public void afterTextChanged(
                    android.text.Editable s) {}
        });

        setContentView(scroll);
    }

    private void muatAudio() {
        try {
            Field[] fields = R.raw.class.getFields();

            for (Field field : fields) {
                try {
                    int id = field.getInt(null);
                    String nama = field.getName();

                    audioList.add(new AudioItem(nama, id));
                } catch (Exception ignored) {
                    // Lewati resource yang tidak bisa dibaca.
                }
            }

            Collections.sort(audioList, Comparator.comparing(
                    item -> item.nama.toLowerCase(Locale.ROOT)
            ));

        } catch (Exception e) {
            status.setText("Gagal membaca daftar audio.");
        }
    }

    private void tampilkanAudio(String kataKunci) {
        daftarAudio.removeAllViews();

        String filter = kataKunci.toLowerCase(Locale.ROOT);
        int jumlahTampil = 0;

        for (AudioItem item : audioList) {
            if (!item.nama.toLowerCase(Locale.ROOT).contains(filter)) {
                continue;
            }

            Button tombol = new Button(this);
            tombol.setText("▶ " + item.nama);
            tombol.setOnClickListener(
                    v -> putarAudio(item.id, item.nama)
            );

            daftarAudio.addView(tombol);
            jumlahTampil++;
        }

        if (jumlahTampil == 0) {
            TextView kosong = new TextView(this);
            kosong.setText(audioList.isEmpty()
                    ? "Belum ada audio di res/raw."
                    : "Audio tidak ditemukan.");
            daftarAudio.addView(kosong);
        }
    }

    private void putarAudio(int id, String nama) {
        stopAudio();

        try {
            mp = MediaPlayer.create(this, id);

            if (mp == null) {
                status.setText("Gagal memuat audio: " + nama);
                return;
            }

            mp.setOnCompletionListener(player -> {
                player.release();
                if (mp == player) {
                    mp = null;
                }
                status.setText("Selesai: " + nama);
            });

            mp.start();
            status.setText("Sedang memutar: " + nama);

        } catch (Exception e) {
            stopAudio();
            status.setText("Audio gagal diputar: " + nama);
        }
    }

    private void stopAudio() {
        if (mp != null) {
            try {
                if (mp.isPlaying()) {
                    mp.stop();
                }
            } catch (Exception ignored) {}

            mp.release();
            mp = null;
        }
    }

    @Override
    protected void onDestroy() {
        stopAudio();
        super.onDestroy();
    }
}
