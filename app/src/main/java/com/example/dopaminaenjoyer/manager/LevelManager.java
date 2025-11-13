package com.example.dopaminaenjoyer.manager;

import android.widget.ProgressBar;
import android.widget.TextView;

/**
 * Clase para gestionar niveles y progreso de clics en el juego.
 * Controla:
 *  - Nivel actual
 *  - Progreso dentro del nivel
 *  - Total de clics acumulados
 *  - Subida y bajada de nivel
 */
public class LevelManager {
    private ProgressBar progressBar; // barra que muestra progreso del nivel
    private TextView levelText; // TextView que muestra el nivel actual
    private int currentLevel; // nivel actual
    private int currentProgress; // progreso dentro del nivel
    private int clicksPerLevel; // clicks necesarios para subir un nivel
    private int totalClicks; // contador total de clicks acumulados

    // Constructor: Inicializa variables y configura barra
    public LevelManager(ProgressBar progressBar, TextView levelText, int clicksPerLevel) {
        this.progressBar = progressBar;
        this.levelText = levelText;
        this.clicksPerLevel = clicksPerLevel;
        this.currentLevel = 0;
        this.currentProgress = 0;
        this.totalClicks = 0;
        setupProgressBar();
    }

    // Configura la ProgressBar y el texto de nivel
    private void setupProgressBar() {
        progressBar.setMax(clicksPerLevel);
        progressBar.setProgress(0);
        updateLevelText();
    }

    /**
     * Incrementa el progreso (cuando el usuario hace click)
     *  - Aumenta currentProgress y totalClicks
     *  - Si se completa el nivel, llama a levelUp()
     */
    public void incrementProgress() {
        currentProgress++;
        totalClicks++; // contador global de clics
        if (currentProgress >= clicksPerLevel) {
            levelUp(); // subir nivel si se completa
        } else {
            progressBar.setProgress(currentProgress);
        }
    }

    /**
     * Decrementa el progreso (decadencia de nivel)
     *  - Si currentProgress > 0 -> resta 1
     *  - Si currentProgress = 0 y currentLevel > 0 -> baja nivel
     * @return true si hubo bajada de nivel
     */
    public boolean decrementProgress() {
        boolean levelDecreased = false;
        if (currentProgress > 0) {
            currentProgress--;
            progressBar.setProgress(currentProgress);
        } else if (currentLevel > 0) {
            levelDown(); // bajar nivel
            levelDecreased = true;
        }
        return levelDecreased; // Devuelve true si el nivel bajó
    }

    // Subida de nivel: resetea progreso y actualiza UI
    private void levelUp() {
        currentLevel++;
        currentProgress = 0;
        progressBar.setProgress(0);
        updateLevelText();
    }

    // Bajada de nivel: decrementa nivel y llena barra para decaer rápido
    private void levelDown() {
        currentLevel--;
        // Cuando bajas de nivel, la barra se llena completamente para que decaiga inmediatamente
        currentProgress = clicksPerLevel;
        progressBar.setProgress(currentProgress);
        updateLevelText();
    }

    // Actualiza el texto de nivel
    private void updateLevelText() {
        if (levelText != null) {
            levelText.setText("Nivel " + currentLevel);
        }
    }

    // ------------------------ GETTERS ---------------------
    public int getCurrentLevel() {
        return currentLevel;
    }
    public int getCurrentProgress() {
        return currentProgress;
    }
    public int getTotalClicks() {
        return totalClicks;
    }

    // Indica si acaba de subir de nivel (barra en 0)
    public boolean isLevelUp() {
        return currentProgress == 0 && currentLevel > 0;
    }

    // --------------------- MÉTODOS DE CARGA / RESET --------------------------

    // Cargar progreso desde StatsManager o almacenamiento
    public void loadProgress(int level, int progress) {
        this.currentLevel = level;
        this.currentProgress = progress;
        this.totalClicks = level * clicksPerLevel + progress; // total acumulado
        updateUI();
    }

    // Actualiza barra y texto
    private void updateUI() {
        if (progressBar != null) {
            progressBar.setProgress(currentProgress);
            progressBar.setMax(clicksPerLevel);
        }
        if (levelText != null) {
            levelText.setText("Nivel " + currentLevel);
        }
    }

    // Reinicia todo a 0 (nuevo juego o sesión
    public void reset() {
        currentLevel = 0;
        currentProgress = 0;
        totalClicks = 0; // Reiniciar también el contador total al inicio del modo
        progressBar.setProgress(0);
        updateLevelText();
    }
}