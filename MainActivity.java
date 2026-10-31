
package com.shiroko.voicemini;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import java.util.LinkedHashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private MediaPlayer mp;
    private LinearLayout root;
    private LinearLayout content;
    private TextView status;

    private final Map<String, Integer> data =
        new LinkedHashMap<String, Integer>() {{
            put("Title", R.raw.title);
            put("Gachaget", R.raw.gachaget);
            put("Cafe Act 1", R.raw.cafe_act_1);
            put("Cafe Act 2", R.raw.cafe_act_2);
            put("Birthday", R.raw.birthday);
            put("Birthday Player", R.raw.birthday_player);
            put("NewYear", R.raw.season_newyear);
            put("Halloween", R.raw.season_halloween);
            put("Xmas", R.raw.season_xmas);
            put("Get", R.raw.get);
        }};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(16, 12, 16, 12);

        TextView title = new TextView(this);
        title.setText("SHIROKO VOICE MINI");
        title.setTextSize(23);
        title.setTextColor(Color.rgb(40, 105, 160));
        title.setGravity(Gravity.CENTER);
        title.setPadding(4, 12, 4, 16);
        root.addView(title);

        LinearLayout nav = new LinearLayout(this);
        nav.setOrientation(LinearLayout.HORIZONTAL);

        Button soundTab = new Button(this);
        soundTab.setText("Soundboard");

        Button aiTab = new Button(this);
        aiTab.setText("AI Voice");

        nav.addView(soundTab,
            new LinearLayout.LayoutParams(0, -2, 1));
        nav.addView(aiTab,
            new LinearLayout.LayoutParams(0, -2, 1));

        root.addView(nav);

        status = new TextView(this);
        status.setText("Siap");
        status.setPadding(8, 10, 8, 10);
        root.addView(status);

        ScrollView scroll = new ScrollView(this);
        content = new LinearLayout(this);
        content.setOrientation(LinearLayout.VERTICAL);
        scroll.addView(content);

        root.addView(scroll,
            new LinearLayout.LayoutParams(-1, 0, 1));

        soundTab.setOnClickListener(v -> showSoundboard());
        aiTab.setOnClickListener(v -> showAIInfo());

        setContentView(root);
        showSoundboard();
    }

    private void showSoundboard() {
        content.removeAllViews();

        TextView heading = new TextView(this);
        heading.setText("Shiroko Soundboard");
        heading.setTextSize(19);
        content.addView(heading);

        Button stop = new Button(this);
        stop.setText("STOP AUDIO");
        stop.setOnClickListener(v -> stopAudio());
        content.addView(stop);

        for (Map.Entry<String, Integer> e : data.entrySet()) {
            final String name = e.getKey();
            final int resource = e.getValue();

            Button btn = new Button(this);
            btn.setText(name);
            btn.setOnClickListener(v -> playSound(name, resource));
            content.addView(btn);
        }
    }

    private void playSound(String name, int resource) {
        stopAudio();

        try {
            mp = MediaPlayer.create(this, resource);

            if (mp == null) {
                status.setText("Audio gagal dimuat: " + name);
                return;
            }

            mp.setOnCompletionListener(player -> {
                if (mp == player) {
                    mp = null;
                }
                player.release();
                status.setText("Selesai: " + name);
            });

            mp.start();
            status.setText("Memutar: " + name);

        } catch (Exception ex) {
            stopAudio();
            status.setText("Gagal memutar audio.");
        }
    }

    private void stopAudio() {
        if (mp != null) {
            MediaPlayer old = mp;
            mp = null;

            try {
                if (old.isPlaying()) {
                    old.stop();
                }
            } catch (IllegalStateException ignored) {
            }

            old.release();
        }

        if (status != null) {
            status.setText("Audio dihentikan");
        }
    }

    private void showAIInfo() {
        stopAudio();
        content.removeAllViews();

        TextView heading = new TextView(this);
        heading.setText("AI Voice Changer");
        heading.setTextSize(20);
        content.addView(heading);

        TextView description = new TextView(this);
        description.setText(
            "Status: Mesin AI belum terpasang.\n\n" +
            "Agar suara mikrofon dapat diubah secara real-time, " +
            "aplikasi membutuhkan:\n\n" +
            "1. Model voice conversion yang kompatibel.\n" +
            "2. Mesin inferensi Android, misalnya ONNX Runtime.\n" +
            "3. Pemrosesan mikrofon dengan AudioRecord.\n" +
            "4. Pengolahan audio dengan latensi rendah.\n" +
            "5. Jalur output yang kompatibel dengan aplikasi game.\n\n" +
            "RAM 3 GB: gunakan model ringan dan uji " +
            "pemakaian memori sebelum bermain.\n\n" +
            "Catatan: halaman ini belum mengubah suara."
        );

        description.setTextSize(15);
        description.setPadding(4, 12, 4, 12);
        content.addView(description);

        Button check = new Button(this);
        check.setText("Periksa kebutuhan AI");
        check.setOnClickListener(v ->
            status.setText(
                "Belum ada model AI. Pilih model dan runtime " +
                "yang kompatibel sebelum mengaktifkan mikrofon."
            )
        );
        content.addView(check);
    }

    @Override
    protected void onStop() {
        stopAudio();
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        stopAudio();
        super.onDestroy();
    }
}


            
                 
    