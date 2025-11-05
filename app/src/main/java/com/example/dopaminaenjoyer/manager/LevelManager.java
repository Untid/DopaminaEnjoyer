package com.example.dopaminaenjoyer.manager;

import android.widget.ProgressBar;
import android.widget.TextView;

public class LevelManager {
    private ProgressBar progressBar;
    private TextView levelText;
    private int currentLevel;
    private int currentProgress;
    private int clicksPerLevel;
    private int totalClicks;

    public LevelManager(ProgressBar progressBar, TextView levelText, int clicksPerLevel) {
        this.progressBar = progressBar;
        this.levelText = levelText;
        this.clicksPerLevel = clicksPerLevel;
        this.currentLevel = 0;
        this.currentProgress = 0;
        this.totalClicks = 0;
        setupProgressBar();
    }

    private void setupProgressBar() {
        progressBar.setMax(clicksPerLevel);
        progressBar.setProgress(0);
        updateLevelText();
    }

    public void incrementProgress() {
        currentProgress++;
        totalClicks++; // 🌟 Incrementar el contador total
        if (currentProgress >= clicksPerLevel) {
            levelUp();
        } else {
            progressBar.setProgress(currentProgress);
        }
    }

    // 🌟 MODIFICADO: Añade la lógica de bajada de nivel y devuelve si ocurrió
    public boolean decrementProgress() {
        boolean levelDecreased = false;
        if (currentProgress > 0) {
            currentProgress--;
            progressBar.setProgress(currentProgress);
        } else if (currentLevel > 0) {
            levelDown();
            levelDecreased = true;
        }
        return levelDecreased; // Devuelve true si el nivel bajó
    }

    private void levelUp() {
        currentLevel++;
        currentProgress = 0;
        progressBar.setProgress(0);
        updateLevelText();
    }

    // 🌟 NUEVO: Lógica de bajada de nivel
    private void levelDown() {
        currentLevel--;
        // Cuando bajas de nivel, la barra se llena completamente para que decaiga inmediatamente
        currentProgress = clicksPerLevel;
        progressBar.setProgress(currentProgress);
        updateLevelText();
    }

    private void updateLevelText() {
        if (levelText != null) {
            levelText.setText("Nivel " + currentLevel);
        }
    }

    public int getCurrentLevel() {
        return currentLevel;
    }

    public int getCurrentProgress() {
        return currentProgress;
    }

    // En LevelManager.java

    public void loadProgress(int level, int progress) {
        this.currentLevel = level;
        this.currentProgress = progress;
        this.totalClicks = level * clicksPerLevel + progress; // ✅ ¡importante!
        updateUI();
    }

    private void updateUI() {
        if (progressBar != null) {
            progressBar.setProgress(currentProgress);
            progressBar.setMax(clicksPerLevel);
        }
        if (levelText != null) {
            levelText.setText("Nivel " + currentLevel);
        }
    }
    public void reset() {
        currentLevel = 0;
        currentProgress = 0;
        totalClicks = 0; // 🌟 Reiniciar también el contador total al inicio del modo
        progressBar.setProgress(0);
        updateLevelText();
    }
    public int getTotalClicks() {
        return totalClicks;
    }

    public boolean isLevelUp() {
        return currentProgress == 0 && currentLevel > 0;
    }
}