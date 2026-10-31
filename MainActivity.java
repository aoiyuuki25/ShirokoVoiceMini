
package com.shiroko.voicemin;

import android.app.Activity;
import android.os.Bundle;
import android.media.MediaPlayer;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.LinkedHashMap;
import java.util.Map;

public class MainActivity extends Activity {

    private LinearLayout content;
    private TextView status;
    private MediaPlayer player;

    private final Map<String, Integer> sounds =
            new LinkedHashMap<String, Integer>();

    private final int bg = Color.rgb(17, 25, 39);
    private final int panel = Color.rgb(29, 41, 59);
    private final int white = Color.WHITE;
    private final int muted = Color.rgb(180, 195, 215);

    @Override
    protected void onCreate(Bundle state) {
        super.onCreate(state);

        sounds.put("Shiroko Hello", R.raw.shiroko_hello);
        sounds.put("Shiroko Laugh", R.raw.shiroko_laugh);
        sounds.put("Shiroko Battle", R.raw.shiroko_battle);

        sounds.put("Title", R.raw.title);
        sounds.put("Gachaget", R.raw.gachaget);
        sounds.put("Cafe Act 1", R.raw.cafe_act_1);
        sounds.put("Cafe Act 2", R.raw.cafe_act_2);
        sounds.put("Birthday", R.raw.birthday);
        sounds.put("Birthday Player", R.raw.birthday_player);
        sounds.put("New Year", R.raw.season_newyear);
        sounds.put("Halloween", R.raw.season_halloween);
        sounds.put("Christmas", R.raw.season_xmas);
        sounds.put("Get", R.raw.get);

        buildHome();
    }

    private GradientDrawable shape(int color, int radius) {
        GradientDrawable d = new GradientDrawable();
        d.setColor(color);
        d.setCornerRadius(radius);
        return d;
    }

    private TextView makeText(
            String value, int size, int color) {

        TextView t = new TextView(this);
        t.setText(value);
        t.setTextSize(size);
        t.setTextColor(color);
        t.setGravity(Gravity.CENTER_VERTICAL);
        return t;
    }

    private Button makeButton(String label) {
        Button b = new Button(this);
        b.setText(label);
        b.setTextColor(white);
        b.setAllCaps(false);
        b.setTextSize(15);
        b.setBackground(shape(panel, 18));
        return b;
    }

    private void buildHome() {
        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(18, 20, 18, 14);
        root.setBackgroundColor(bg);

        TextView title = makeText(
                "SHIROKO VOICE MINI", 23, white);
        title.setTypeface(null, Typeface.BOLD);
        root.addView(title);

        root.addView(makeText(
                "Simple Soundboard • Lightweight",
                13, muted));

        LinearLayout nav = new LinearLayout(this);
        nav.setOrientation(LinearLayout.HORIZONTAL);

        Button home = makeButton("Soundboard");
        Button info = makeButton("Info");

        nav.addView(home,
                new LinearLayout.LayoutParams(0, 48, 1));
        nav.addView(info,
                new LinearLayout.LayoutParams(0, 48, 1));
        root.addView(nav);

        ScrollView scroll = new ScrollView(this);
        content = new LinearLayout(this);
        content.setOrientation(LinearLayout.VERTICAL);
        scroll.addView(content);

        root.addView(scroll,
                new LinearLayout.LayoutParams(-1, 0, 1));

        status = makeText("Status: Siap", 13, muted);
        status.setPadding(0, 12, 0, 4);
        root.addView(status);

        home.setOnClickListener(v -> showSoundboard());
        info.setOnClickListener(v -> showInfo());

        setContentView(root);
        showSoundboard();
    }

    private void showSoundboard() {
        content.removeAllViews();

        TextView heading = makeText(
                "Shiroko Soundboard", 20, white);
        heading.setTypeface(null, Typeface.BOLD);
        content.addView(heading);

        content.addView(makeText(
                "Pilih audio untuk memutarnya.",
                14, muted));

        for (Map.Entry<String, Integer> entry
                : sounds.entrySet()) {

            final String label = entry.getKey();
            final int audioId = entry.getValue();

            Button b = makeButton("▶  " + label);

            LinearLayout.LayoutParams lp =
                    new LinearLayout.LayoutParams(-1, 54);
            lp.setMargins(0, 8, 0, 0);
            content.addView(b, lp);

            b.setOnClickListener(v ->
                    playSound(label, audioId));
        }

        Button stop = makeButton("■  Stop Audio");

        LinearLayout.LayoutParams stopLp =
                new LinearLayout.LayoutParams(-1, 50);
        stopLp.setMargins(0, 14, 0, 12);
        content.addView(stop, stopLp);

        stop.setOnClickListener(v -> {
            stopSound();
            status.setText("Status: Audio dihentikan");
        });
    }

    private void playSound(String label, int audioId) {
        stopSound();

        try {
            player = MediaPlayer.create(this, audioId);

            if (player == null) {
                status.setText("Status: Audio gagal dibuka");
                return;
            }

            player.setOnCompletionListener(mp -> {
                stopSound();
                status.setText("Status: Selesai");
            });

            player.start();
            status.setText("Status: Memutar " + label);

        } catch (Exception e) {
            stopSound();
            status.setText("Status: Gagal memutar audio");
        }
    }

    private void stopSound() {
        if (player != null) {
            try {
                if (player.isPlaying()) {
                    player.stop();
                }
            } catch (Exception ignored) {
            }

            try {
                player.release();
            } catch (Exception ignored) {
            }

            player = null;
        }
    }

    private void showInfo() {
        content.removeAllViews();

        TextView heading = makeText(
                "Tentang Aplikasi", 21, white);
        heading.setTypeface(null, Typeface.BOLD);
        content.addView(heading);

        TextView details = makeText(
                "\nShiroko Voice Mini\n\n"
                + "• 13 tombol soundboard\n"
                + "• Audio lokal\n"
                + "• Tampilan sederhana\n"
                + "• Tidak membutuhkan izin internet\n\n"
                + "AI voice changer real-time belum tersedia.\n"
                + "Audio soundboard tidak otomatis menjadi "
                + "input mikrofon di dalam game.",
                15, muted);

        content.addView(details);
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
