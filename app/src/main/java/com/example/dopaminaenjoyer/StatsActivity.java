package com.example.dopaminaenjoyer;

import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.example.dopaminaenjoyer.manager.StatsManager;


public class StatsActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stats);

        StatsManager stats = new StatsManager(this);

        TextView tvClicks = findViewById(R.id.tvTotalClicks);
        TextView tvMaxLevel = findViewById(R.id.tvMaxLevel);
        TextView tvPlayTime = findViewById(R.id.tvPlayTime);

        tvClicks.setText("Clics totales: " + stats.getTotalClicks());
        tvMaxLevel.setText("Nivel máximo: " + stats.getMaxLevel());

        long totalMs = stats.getTotalPlayTimeMs();
        long totalSeconds = totalMs / 1000;
        long minutes = totalSeconds / 60;
        long seconds = totalSeconds % 60;

        String timeText;
        if (minutes > 0) {
            timeText = String.format("%d min %d s", minutes, seconds);
        } else {
            timeText = String.format("%d s", seconds);
        }
        tvPlayTime.setText("Tiempo jugado: " + timeText);

        findViewById(R.id.btnVolver).setOnClickListener(v -> finish());
    }
}