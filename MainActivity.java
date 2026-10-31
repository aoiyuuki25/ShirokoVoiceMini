
package com.shiroko.voicemin;

import android.app.Activity;
import android.os.Bundle;
import android.media.MediaPlayer;
import android.graphics.Color;
import android.view.Gravity;
import android.widget.*;
import java.util.LinkedHashMap;
import java.util.Map;

public class MainActivity extends Activity {

    private MediaPlayer player;
    private LinearLayout content;
    private TextView status;

    private final Map<String, Integer> sounds =
        new LinkedHashMap<String, Integer>();

    @Override
    protected void onCreate(Bundle state) {
        super.onCreate(state);

        // Daftar suara Shiroko
        sounds.put("Title", R.raw.title);
        sounds.put("Gachaget", R.raw.gachaget);
        sounds.put("Cafe Act 1", R.raw.cafe_act_1);
        sounds.put("Cafe Act 2", R.raw.cafe_act_2);
        sounds.put("Birthday", R.raw.birthday);
        sounds.put("Birthday Player", R.raw.birthday_player);
        sounds.put("NewYear", R.raw.season_newyear);
        sounds.put("Halloween", R.raw.season_halloween);
        sounds.put("Xmas", R.raw.season_xmas);
        sounds.put("Get", R.raw.get);

        buildUI();
        showSoundboard();
    }

    private void buildUI() {
        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(16, 12, 16, 12);
        root.setBackgroundColor(Color.rgb(238, 246, 255));

        TextView title = new TextView(this);
        title.setText("SHIROKO VOICE MINI");
        title.setTextSize(23);
        title.setTextColor(Color.rgb(35, 100, 160));
        title.setGravity(Gravity.CENTER);
        title.setPadding(4, 12, 4, 16);
        root.addView(title);

        LinearLayout nav = new LinearLayout(this);
        nav.setOrientation(LinearLayout.HORIZONTAL);

        Button soundButton = new Button(this);
        soundButton.setText("Soundboard");

        Button aiButton = new Button(this);
        aiButton.setText("AI Voice");

        nav.addView(soundButton,
            new LinearLayout.LayoutParams(0, -2, 1));
        nav.addView(aiButton,
            new LinearLayout.LayoutParams(0, -2, 1));
        root.addView(nav);

        status = new TextView(this);
        status.setText("Siap");
        status.setTextSize(14);
        status.setTextColor(Color.DKGRAY);
        status.setPadding(8, 10, 8, 10);
        root.addView(status);

        ScrollView scroll = new ScrollView(this);
        content = new LinearLayout(this);
        content.setOrientation(LinearLayout.VERTICAL);
        scroll.addView(content);

        root.addView(scroll,
            new LinearLayout.LayoutParams(-1, 0, 1));

        soundButton.setOnClickListener(v -> showSoundboard());
        aiButton.setOnClickListener(v -> showAIPage());

        setContentView(root);
    }

    private void showSoundboard() {
        stopSound();
        content.removeAllViews();

        TextView heading = new TextView(this);
        heading.setText("Shiroko Soundboard");
        heading.setTextSize(20);
        heading.setTextColor(Color.rgb(35, 100, 160));
        content.addView(heading);

        Button stopButton = new Button(this);
        stopButton.setText("STOP AUDIO");
        stopButton.setOnClickListener(v -> stopSound());
        content.addView(stopButton);

        for (Map.Entry<String, Integer> item :
                sounds.entrySet()) {

            final String name = item.getKey();
            final int resource = item.getValue();

            Button button = new Button(this);
            button.setText(name);

            button.setOnClickListener(v ->
                playSound(name, resource));

            content.addView(button);
        }
    }

    private void playSound(String name, int resource) {
        stopSound();

        try {
            player = MediaPlayer.create(this, resource);

            if (player == null) {
                status.setText("Audio gagal dimuat: " + name);
                return;
            }

            final MediaPlayer current = player;

            current.setOnCompletionListener(mp -> {
                mp.release();

                if (player == mp) {
                    player = null;
                }

                status.setText("Selesai: " + name);
            });

            current.start();
            status.setText("Memutar: " + name);

        } catch (Exception e) {
            stopSound();
            status.setText("Gagal memutar audio");
        }
    }

    private void stopSound() {
        if (player != null) {
            MediaPlayer old = player;
            player = null;

            try {
                if (old.isPlaying()) {
                    old.stop();
                }
            } catch (Exception ignored) {
            }

            old.release();
        }

        if (status != null) {
            status.setText("Audio dihentikan");
        }
    }

    private void showAIPage() {
        stopSound();
        content.removeAllViews();

        TextView heading = new TextView(this);
        heading.setText("AI VOICE CHANGER");
        heading.setTextSize(20);
        heading.setTextColor(Color.rgb(35, 100, 160));
        content.addView(heading);

        TextView info = new TextView(this);
        info.setText(
            "Status: Mesin AI belum terpasang.\n\n" +
            "Fitur yang direncanakan:\n" +
            "- Input suara mikrofon\n" +
            "- Konversi suara berbasis AI\n" +
            "- Pemrosesan audio real-time\n" +
            "- Pengujian latensi\n\n" +
            "HP RAM 3 GB:\n" +
            "Gunakan model ringan dan batasi " +
            "pemakaian memori.\n\n" +
            "Catatan:\n" +
            "Halaman ini belum mengubah suara. " +
            "Diperlukan model AI dan mesin inferensi " +
            "yang kompatibel dengan Android."
        );

        info.setTextSize(16);
        info.setPadding(8, 16, 8, 16);
        content.addView(info);

        Button back = new Button(this);
        back.setText("Kembali ke Soundboard");
        back.setOnClickListener(v -> showSoundboard());
        content.addView(back);
    }

    @Override
    protected void onStop() {
        stopSound();
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        stopSound();
        super.onDestroy();
    }
}



        
         
                 
    