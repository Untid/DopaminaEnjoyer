package com.example.dopaminaenjoyer.manager;

import android.content.Context;
import android.content.SharedPreferences;

public class StatsManager {
    private static final String PREFS_NAME = "DopaminaStats";
    private static final String KEY_TOTAL_CLICKS = "total_clicks";
    private static final String KEY_MAX_LEVEL = "max_level";
    private static final String KEY_TIME_PLAYED_MS = "time_played_ms";

    private final SharedPreferences prefs;
    private final SharedPreferences.Editor editor;
    private static final String KEY_CURRENT_LEVEL = "current_level";
    private static final String KEY_CURRENT_PROGRESS = "current_progress";

    public void saveCurrentProgress(int level, int progress) {
        editor.putInt(KEY_CURRENT_LEVEL, level);
        editor.putInt(KEY_CURRENT_PROGRESS, progress);
        editor.apply();
    }

    public int[] loadCurrentProgress() {
        int level = prefs.getInt(KEY_CURRENT_LEVEL, 0);
        int progress = prefs.getInt(KEY_CURRENT_PROGRESS, 0);
        return new int[]{level, progress};
    }

    public StatsManager(Context context) {
        prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        editor = prefs.edit();
    }

    public void addClicks(int clicks) {
        int total = prefs.getInt(KEY_TOTAL_CLICKS, 0);
        editor.putInt(KEY_TOTAL_CLICKS, total + clicks).apply();
    }

    public void updateMaxLevel(int currentLevel) {
        int max = prefs.getInt(KEY_MAX_LEVEL, 0);
        if (currentLevel > max) {
            editor.putInt(KEY_MAX_LEVEL, currentLevel).apply();
        }
    }

    public void addPlayTime(long milliseconds) {
        long total = prefs.getLong(KEY_TIME_PLAYED_MS, 0L);
        editor.putLong(KEY_TIME_PLAYED_MS, total + milliseconds).apply();
    }

    // Getters
    public int getTotalClicks() {
        return prefs.getInt(KEY_TOTAL_CLICKS, 0);
    }

    public int getMaxLevel() {
        return prefs.getInt(KEY_MAX_LEVEL, 0);
    }

    public long getTotalPlayTimeMs() {
        return prefs.getLong(KEY_TIME_PLAYED_MS, 0L);
    }
}