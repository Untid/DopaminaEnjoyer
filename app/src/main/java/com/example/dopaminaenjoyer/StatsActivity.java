package com.example.dopaminaenjoyer;

import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.example.dopaminaenjoyer.manager.StatsManager;

/**
 * Actividad encargada de mostrar las estadísticas del jugador:
 * clics totales, nivel máximo alcanzado y tiempo total de juego.
 */
public class StatsActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stats);

        // -------------- Inicializa el gestor de estadísticas --------------------
        StatsManager stats = new StatsManager(this); // Recupera datos guardados

        // ---------------- Referencias a los TextView del layout ----------------------
        TextView tvClicks = findViewById(R.id.tvTotalClicks); // Muestra total de clics
        TextView tvMaxLevel = findViewById(R.id.tvMaxLevel); // Muestra nivel máximo alcanzado
        TextView tvPlayTime = findViewById(R.id.tvPlayTime); // Muestra tiempo total de juego

        // ---------- Asigna valores obtenidos del StatsManager ------------------
        tvClicks.setText("Clics totales: " + stats.getTotalClicks());
        tvMaxLevel.setText("Nivel máximo: " + stats.getMaxLevel());

        // ------------- Conversión del tiempo total jugado ----------------------
        long totalMs = stats.getTotalPlayTimeMs(); // Tiempo total en milisegundos
        long totalSeconds = totalMs / 1000; // Convertimos a segundos
        long minutes = totalSeconds / 60; // Minutos completos
        long seconds = totalSeconds % 60; // Segundos restantes

        // ----------- Formatea el tiempo en texto legible ----------------
        String timeText;
        if (minutes > 0) {
            timeText = String.format("%d min %d s", minutes, seconds);
        } else {
            timeText = String.format("%d s", seconds);
        }
        tvPlayTime.setText("Tiempo jugado: " + timeText);

        // ------------ Botón de volver al menú anterior ---------------------
        findViewById(R.id.btnVolver).setOnClickListener(v -> finish());
        // finish() cierra la actividad actual y regresa a la anterior
    }
}