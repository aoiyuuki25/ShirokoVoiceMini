
package com.shiroko.voicemin;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaRecorder;
import android.media.audiofx.AcousticEchoCanceler;
import android.os.Bundle;
import android.os.Process;
import android.view.Gravity;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {

    private static final int REQUEST_MIC = 40;
    private static final int SAMPLE_RATE = 22050;

    private volatile boolean running = false;
    private Thread audioThread;
    private AudioRecord recorder;
    private AudioTrack player;
    private AcousticEchoCanceler echoCanceler;

    private SeekBar pitchSeek;
    private TextView pitchLabel, status;
    private Button startButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        buildUi();
    }

    private LinearLayout.LayoutParams matchWrap() {
        return new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
    }

    private void buildUi() {
        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(28, 34, 28, 24);
        root.setGravity(Gravity.CENTER_HORIZONTAL);
        root.setBackgroundColor(0xFFF4F7FC);

        TextView title = new TextView(this);
        title.setText("SHIROKO VOICE MINI");
        title.setTextSize(24);
        title.setTextColor(0xFF20304A);
        title.setGravity(Gravity.CENTER);
        title.setTypeface(null, 1);
        root.addView(title, matchWrap());

        TextView subtitle = new TextView(this);
        subtitle.setText("Prototipe uji suara • versi 0.1");
        subtitle.setTextSize(14);
        subtitle.setTextColor(0xFF61708A);
        subtitle.setGravity(Gravity.CENTER);
        LinearLayout.LayoutParams sp = matchWrap();
        sp.topMargin = 8;
        root.addView(subtitle, sp);

        status = new TextView(this);
        status.setText("Status: berhenti");
        status.setTextSize(16);
        status.setTextColor(0xFF20304A);
        LinearLayout.LayoutParams stp = matchWrap();
        stp.topMargin = 28;
        root.addView(status, stp);

        pitchLabel = new TextView(this);
        pitchLabel.setText("Nada: 1.05×");
        pitchLabel.setTextSize(16);
        pitchLabel.setTextColor(0xFF20304A);
        LinearLayout.LayoutParams lp = matchWrap();
        lp.topMargin = 22;
        root.addView(pitchLabel, lp);

        pitchSeek = new SeekBar(this);
        pitchSeek.setMax(100);
        pitchSeek.setProgress(50);
        LinearLayout.LayoutParams seekLp = matchWrap();
        seekLp.topMargin = 8;
        root.addView(pitchSeek, seekLp);

        pitchSeek.setOnSeekBarChangeListener(
                new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(
                            SeekBar seekBar, int progress, boolean fromUser) {
                        float rate = rateFor(progress);
                        pitchLabel.setText(String.format(
                                java.util.Locale.US, "Nada: %.2f×", rate));

                        if (player != null && running) {
                            try {
                                player.setPlaybackRate((int) (SAMPLE_RATE * rate));
                            } catch (Exception ignored) {
                            }
                        }
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {
                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {
                    }
                });

        TextView note = new TextView(this);
        note.setText(
                "Pengubah nada sederhana. Perubahan nada juga mengubah " +
                "kecepatan suara; ini bukan AI voice conversion."
        );
        note.setTextSize(14);
        note.setTextColor(0xFF61708A);
        LinearLayout.LayoutParams np = matchWrap();
        np.topMargin = 10;
        root.addView(note, np);

        startButton = new Button(this);
        startButton.setText("Mulai monitor mikrofon");
        LinearLayout.LayoutParams bp = matchWrap();
        bp.topMargin = 26;
        root.addView(startButton, bp);

        startButton.setOnClickListener(v -> {
            if (running) {
                stopAudio();
            } else if (checkSelfPermission(Manifest.permission.RECORD_AUDIO)
                    != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(
                        new String[]{Manifest.permission.RECORD_AUDIO},
                        REQUEST_MIC
                );
            } else {
                startAudio();
            }
        });

        TextView warning = new TextView(this);
        warning.setText(
                "Suara diputar melalui speaker HP untuk pengujian. " +
                "Aplikasi ini belum mengirim suara olahan ke mikrofon MLBB. " +
                "Gunakan headset untuk mengurangi gema."
        );
        warning.setTextSize(13);
        warning.setTextColor(0xFF8A4B26);
        LinearLayout.LayoutParams wp = matchWrap();
        wp.topMargin = 22;
        root.addView(warning, wp);

        setContentView(root);
    }

    private float rateFor(int progress) {
        return 0.75f + (progress / 100.0f) * 0.60f;
    }

    private void startAudio() {
        if (running) return;

        int minIn = AudioRecord.getMinBufferSize(
                SAMPLE_RATE,
                AudioFormat.CHANNEL_IN_MONO,
                AudioFormat.ENCODING_PCM_16BIT
        );

        int minOut = AudioTrack.getMinBufferSize(
                SAMPLE_RATE,
                AudioFormat.CHANNEL_OUT_MONO,
                AudioFormat.ENCODING_PCM_16BIT
        );

        if (minIn <= 0 || minOut <= 0) {
            Toast.makeText(
                    this, "Audio tidak didukung di perangkat ini.",
                    Toast.LENGTH_LONG
            ).show();
            return;
        }

        int bufferSize = Math.max(Math.max(minIn, minOut), 4096);

        try {
            recorder = new AudioRecord(
                    MediaRecorder.AudioSource.VOICE_COMMUNICATION,
                    SAMPLE_RATE,
                    AudioFormat.CHANNEL_IN_MONO,
                    AudioFormat.ENCODING_PCM_16BIT,
                    bufferSize * 2
            );

            player = new AudioTrack(
                    android.media.AudioManager.STREAM_MUSIC,
                    SAMPLE_RATE,
                    AudioFormat.CHANNEL_OUT_MONO,
                    AudioFormat.ENCODING_PCM_16BIT,
                    bufferSize * 2,
                    AudioTrack.MODE_STREAM
            );

            if (recorder.getState() != AudioRecord.STATE_INITIALIZED
                    || player.getState() != AudioTrack.STATE_INITIALIZED) {
                releaseAudio();
                Toast.makeText(
                        this, "Gagal menyiapkan audio.",
                        Toast.LENGTH_LONG
                ).show();
                return;
            }

            // AEC hanya bekerja jika tersedia dan didukung perangkat.
            if (AcousticEchoCanceler.isAvailable()) {
                try {
                    echoCanceler = AcousticEchoCanceler.create(
                            recorder.getAudioSessionId()
                    );

                    if (echoCanceler != null) {
                        echoCanceler.setEnabled(true);
                    }
                } catch (Exception ignored) {
                    releaseEchoCanceler();
                }
            }

            player.setPlaybackRate(
                    (int) (SAMPLE_RATE * rateFor(pitchSeek.getProgress()))
            );
            player.play();
            recorder.startRecording();

            running = true;
            startButton.setText("Hentikan");
            status.setText("Status: mikrofon aktif");

            audioThread = new Thread(() -> {
                Process.setThreadPriority(Process.THREAD_PRIORITY_AUDIO);
                short[] buffer = new short[512];

                while (running && !Thread.currentThread().isInterrupted()) {
                    int count;

                    try {
                        count = recorder.read(buffer, 0, buffer.length);
                    } catch (Exception e) {
                        break;
                    }

                    if (count > 0 && running) {
                        try {
                            player.write(buffer, 0, count);
                        } catch (Exception e) {
                            break;
                        }
                    }
                }
            }, "ShirokoAudioLoop");

            audioThread.start();

        } catch (SecurityException e) {
            stopAudio();
            Toast.makeText(
                    this, "Izin mikrofon diperlukan.",
                    Toast.LENGTH_LONG
            ).show();
        } catch (Exception e) {
            stopAudio();
            Toast.makeText(
                    this, "Audio gagal dimulai di perangkat ini.",
                    Toast.LENGTH_LONG
            ).show();
        }
    }

    private void releaseEchoCanceler() {
        try {
            if (echoCanceler != null) {
                echoCanceler.setEnabled(false);
                echoCanceler.release();
            }
        } catch (Exception ignored) {
        }
        echoCanceler = null;
    }

    private void stopAudio() {
        running = false;

        try {
            if (recorder != null) {
                recorder.stop();
            }
        } catch (Exception ignored) {
        }

        if (audioThread != null) {
            try {
                audioThread.join(500);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            audioThread = null;
        }

        releaseAudio();

        if (startButton != null) {
            startButton.setText("Mulai monitor mikrofon");
        }
        if (status != null) {
            status.setText("Status: berhenti");
        }
    }

    private void releaseAudio() {
        releaseEchoCanceler();

        try {
            if (recorder != null) recorder.release();
        } catch (Exception ignored) {
        }

        try {
            if (player != null) {
                player.pause();
                player.flush();
                player.stop();
                player.release();
            }
        } catch (Exception ignored) {
        }

        recorder = null;
        player = null;
    }

    @Override
    public void onRequestPermissionsResult(
            int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(
                requestCode, permissions, grantResults);

        if (requestCode == REQUEST_MIC
                && grantResults.length > 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            startAudio();
        } else if (requestCode == REQUEST_MIC) {
            Toast.makeText(
                    this, "Izin mikrofon ditolak.",
                    Toast.LENGTH_SHORT
            ).show();
        }
    }

    @Override
    protected void onStop() {
        stopAudio();
        super.onStop();
    }
}
