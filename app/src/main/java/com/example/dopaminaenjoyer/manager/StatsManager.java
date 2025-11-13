package com.example.dopaminaenjoyer.manager;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * StatsManager gestiona las estadísticas del jugador usando SharedPreferences:
 *  - Clics totales
 *  - Nivel máximo alcanzado
 *  - Tiempo total jugado
 *  - Progreso actual de nivel
 *  Permite guardar y recuperar de forma persistente estos datos.
 */
public class StatsManager {

    // Nombre del archivo SharedPreferences
    private static final String PREFS_NAME = "DopaminaStats";

    // Claves para guardar los datos
    private static final String KEY_TOTAL_CLICKS = "total_clicks";
    private static final String KEY_MAX_LEVEL = "max_level";
    private static final String KEY_TIME_PLAYED_MS = "time_played_ms";
    private static final String KEY_CURRENT_LEVEL = "current_level";
    private static final String KEY_CURRENT_PROGRESS = "current_progress";

    private final SharedPreferences prefs;
    private final SharedPreferences.Editor editor;

    /**
     * Constructor: inicializa SharedPreferences y el editor
     * @param context contexto de la app
     */
    public StatsManager(Context context) {
        prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        editor = prefs.edit();
    }

    // ---------------------------------------------------------
    // Guardar el progreso actual del jugador (nivel y barra de progreso)
    public void saveCurrentProgress(int level, int progress) {
        editor.putInt(KEY_CURRENT_LEVEL, level);
        editor.putInt(KEY_CURRENT_PROGRESS, progress);
        editor.apply(); // aplicar cambios de forma asíncrona
    }

    // Cargar el progreso actual: devuelve un array {nivel, progreso}
    public int[] loadCurrentProgress() {
        int level = prefs.getInt(KEY_CURRENT_LEVEL, 0);
        int progress = prefs.getInt(KEY_CURRENT_PROGRESS, 0);
        return new int[]{level, progress};
    }

    // ---------------------------------------------------------------
    // Incrementar el número total de clicks
    public void addClicks(int clicks) {
        int total = prefs.getInt(KEY_TOTAL_CLICKS, 0);
        editor.putInt(KEY_TOTAL_CLICKS, total + clicks).apply();
    }

    // Actualizar el nivel máximo si el nivel actual es mayor
    public void updateMaxLevel(int currentLevel) {
        int max = prefs.getInt(KEY_MAX_LEVEL, 0);
        if (currentLevel > max) {
            editor.putInt(KEY_MAX_LEVEL, currentLevel).apply();
        }
    }

    // Agregar tiempo de juego (en milisegundos)
    public void addPlayTime(long milliseconds) {
        long total = prefs.getLong(KEY_TIME_PLAYED_MS, 0L);
        editor.putLong(KEY_TIME_PLAYED_MS, total + milliseconds).apply();
    }

    // ------------------------------------------------------------
    // GETTERS: recuperar valores persistentes

    /**
     * Devuelve la cantidad total de clicks realizados
     */
    public int getTotalClicks() {
        return prefs.getInt(KEY_TOTAL_CLICKS, 0);
    }

    /**
     * Devuelve el nivel máximo alcanzado
     */
    public int getMaxLevel() {
        return prefs.getInt(KEY_MAX_LEVEL, 0);
    }

    /**
     * Devuelve el tiempo total jugado en milisegundos
     */
    public long getTotalPlayTimeMs() {
        return prefs.getLong(KEY_TIME_PLAYED_MS, 0L);
    }
}