
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

    private final Map<String, String> sounds =
            new LinkedHashMap<String, String>();

    private final int bg = Color.rgb(17, 25, 39);
    private final int panel = Color.rgb(29, 41, 59);
    private final int white = Color.WHITE;
    private final int muted = Color.rgb(180, 195, 215);

    @Override
    protected void onCreate(Bundle state) {
        super.onCreate(state);

        sounds.put("Title", "title");
        sounds.put("Gachaget", "gachaget");
        sounds.put("Cafe Act 1", "cafe_act_1");
        sounds.put("Cafe Act 2", "cafe_act_2");
        sounds.put("Birthday", "birthday");
        sounds.put("Birthday Player", "birthday_player");
        sounds.put("New Year", "season_newyear");
        sounds.put("Halloween", "season_halloween");
        sounds.put("Christmas", "season_xmas");
        sounds.put("Get", "get");

        buildHome();
    }

    private GradientDrawable shape(int color, int radius) {
        GradientDrawable d = new GradientDrawable();
        d.setColor(color);
        d.setCornerRadius(radius);
        return d;
    }

    private TextView makeText(String value, int size, int color) {
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

        TextView title = makeText("SHIROKO VOICE MINI", 23, white);
        title.setTypeface(null, Typeface.BOLD);
        root.addView(title);

        root.addView(makeText(
                "Simple Soundboard • Lightweight", 13, muted));

        LinearLayout nav = new LinearLayout(this);
        nav.setOrientation(LinearLayout.HORIZONTAL);

        Button home = makeButton("Soundboard");
        Button info = makeButton("Info");

        nav.addView(home, new LinearLayout.LayoutParams(0, 48, 1));
        nav.addView(info, new LinearLayout.LayoutParams(0, 48, 1));
        root.addView(nav);

        ScrollView scroll = new ScrollView(this);
        content = new LinearLayout(this);
        content.setOrientation(LinearLayout.VERTICAL);
        scroll.addView(content);

        root.addView(scroll, new LinearLayout.LayoutParams(
                -1, 0, 1));

        status = makeText("Status: Siap", 13, muted);
        status.setPadding(0, 12, 0, 4);
        root.addView(status);

        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSoundboard();
            }
        });

        info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showInfo();
            }
        });

        setContentView(root);
        showSoundboard();
    }

    private void showSoundboard() {
        content.removeAllViews();

        TextView heading = makeText("Shiroko Soundboard", 20, white);
        heading.setTypeface(null, Typeface.BOLD);
        content.addView(heading);

        content.addView(makeText(
                "Pilih audio untuk memutarnya.", 14, muted));

        for (final Map.Entry<String, String> entry
                : sounds.entrySet()) {

            Button b = makeButton("▶  " + entry.getKey());

            LinearLayout.LayoutParams lp =
                    new LinearLayout.LayoutParams(-1, 54);
            lp.setMargins(0, 8, 0, 0);
            content.addView(b, lp);

            b.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    playSound(entry.getKey(), entry.getValue());
                }
            });
        }

        Button stop = makeButton("■  Stop Audio");
        LinearLayout.LayoutParams stopLp =
                new LinearLayout.LayoutParams(-1, 50);
        stopLp.setMargins(0, 14, 0, 12);
        content.addView(stop, stopLp);

        stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopSound();
                status.setText("Status: Audio dihentikan");
            }
        });
    }

    private void playSound(String label, String resourceName) {
        stopSound();

        int id = getResources().getIdentifier(
                resourceName, "raw", getPackageName());

        if (id == 0) {
            status.setText("Status: Audio belum tersedia");
            Toast.makeText(this,
                    "Tambahkan " + resourceName + ".mp3 ke res/raw",
                    Toast.LENGTH_LONG).show();
            return;
        }

        try {
            player = MediaPlayer.create(this, id);

            if (player == null) {
                status.setText("Status: Audio gagal dibuka");
                return;
            }

            player.setOnCompletionListener(
                    new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    stopSound();
                    status.setText("Status: Selesai");
                }
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

        TextView heading = makeText("Tentang Aplikasi", 21, white);
        heading.setTypeface(null, Typeface.BOLD);
        content.addView(heading);

        TextView details = makeText(
                "\nShiroko Voice Mini\n\n"
                + "• Soundboard audio lokal\n"
                + "• Tampilan sederhana\n"
                + "• Tidak membutuhkan AppCompat\n"
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
