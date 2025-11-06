package com.example.dopaminaenjoyer;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.dopaminaenjoyer.effect.MotivationalMessageEffect;
import com.example.dopaminaenjoyer.effect.NumberEffect;
import com.example.dopaminaenjoyer.effect.ParticleExplosion;
import com.example.dopaminaenjoyer.effect.RippleEffect;
import com.example.dopaminaenjoyer.manager.ConfettiManagerWrapper;
import com.example.dopaminaenjoyer.manager.LevelManager;
import com.example.dopaminaenjoyer.manager.SoundManager;
import com.example.dopaminaenjoyer.manager.StatsManager;

import java.util.Random;

public class MainActivity extends AppCompatActivity {
    private FrameLayout effectsContainer;
    private FrameLayout dopamineLayout;
    private View menuInicio;
    //--------------------------------------------------
    private SoundManager soundManager;
    private ConfettiManagerWrapper confettiManager;
    private NumberEffect numberEffect;
    private ParticleExplosion particleExplosion;
    private LevelManager levelManager;
    private StatsManager statsManager;
    //--------------------------------------------------
    private ProgressBar clickProgress;
    private TextView tvNivel;
    private TextView tvLevelUpMessage;
    private TextView tvMotivationalMessage;
    private TextView tvExistentialCrisis;
    private boolean isCrisisActive = false;
    private long sessionStartTime = 0;
    private Handler handler = new Handler();
    private Runnable decayRunnable;
    private final Handler motivationalHandler = new Handler();
    private final Runnable hideMotivationalMessage = () -> tvMotivationalMessage.setVisibility(View.GONE);
    private final Handler existentialHandler = new Handler();
    private final Runnable hideExistentialCrisis = () -> {
        tvExistentialCrisis.setVisibility(View.GONE);
        isCrisisActive = false;
    };
    private final Handler levelUpHandler = new Handler();
    private final Runnable hideLevelUpMessage = () -> tvLevelUpMessage.setVisibility(View.GONE);
    private long lastMotivationTime = 0;
    private static final long MOTIVATION_COOLDOWN_MS = 3000; // 1.5s de cooldown

    //--------------------------------------------------
    private static final int CLICKS_POR_NIVEL = 25;
    private static final long DECAY_DELAY_MS = 2000;
    //--------------------------------------------------

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        effectsContainer = findViewById(R.id.effectsContainer);
        dopamineLayout = findViewById(R.id.dopamineLayout);
        menuInicio = findViewById(R.id.menuInicio);
        clickProgress = findViewById(R.id.clickProgress);
        tvNivel = findViewById(R.id.tvNivel);
        tvLevelUpMessage = findViewById(R.id.tvLevelUpMessage);
        tvMotivationalMessage = findViewById(R.id.tvMotivationalMessage);
        tvExistentialCrisis = findViewById(R.id.tvExistentialCrisis);
        //--------------------------------------------------
        ImageButton btnMenu = findViewById(R.id.btnMenu);
        btnMenu.setOnClickListener(v -> salirModoDopamina());

        // Inicializar LevelManager y StatsManager
        levelManager = new LevelManager(clickProgress, tvNivel, CLICKS_POR_NIVEL);
        statsManager = new StatsManager(this);
        clickProgress.setVisibility(View.GONE);
        //--------------------------------------------------
        // Inicializar managers y efectos
        soundManager = new SoundManager(this, R.raw.blurp);
        confettiManager = new ConfettiManagerWrapper(effectsContainer);
        numberEffect = new NumberEffect(effectsContainer);
        particleExplosion = new ParticleExplosion(effectsContainer);

        findViewById(R.id.btnEmpezar).setOnClickListener(v -> mostrarModoDopamina());
        findViewById(R.id.btnSalir).setOnClickListener(v -> finish());
        findViewById(R.id.btnMinimalista).setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, MinimalistActivity.class));
        });

        findViewById(R.id.btnZen).setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, ZenActivity.class));
        });
        findViewById(R.id.btnEstadisticas).setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, StatsActivity.class));
        });
    }

    //--------------------------------------------------
    @SuppressLint("ClickableViewAccessibility")
    private void mostrarModoDopamina() {
        menuInicio.setVisibility(View.GONE);
        dopamineLayout.setVisibility(View.VISIBLE);
        clickProgress.setVisibility(View.VISIBLE);
        tvLevelUpMessage.setVisibility(View.GONE);
        tvExistentialCrisis.setVisibility(View.GONE);

        int[] saved = statsManager.loadCurrentProgress();
        levelManager.loadProgress(saved[0], saved[1]);

        sessionStartTime = System.currentTimeMillis();

        dopamineLayout.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                float touchX = event.getX();
                float touchY = event.getY();

                // Reiniciar decadencia
                handler.removeCallbacks(decayRunnable);
                decayRunnable = new Runnable() {
                    @Override
                    public void run() {
                        boolean levelDecreased = levelManager.decrementProgress();
                        if (levelManager.getCurrentProgress() > 0 || levelManager.getCurrentLevel() > 0) {
                            handler.postDelayed(this, 100);
                        }
                        if (levelDecreased) {
                            tvLevelUpMessage.setText("¡Nivel BAJADO a " + levelManager.getCurrentLevel() + "!");
                            tvLevelUpMessage.setVisibility(View.VISIBLE);
                            levelUpHandler.postDelayed(hideLevelUpMessage, 2000);
                        }
                    }
                };
                handler.postDelayed(decayRunnable, DECAY_DELAY_MS);

                // Guardar nivel anterior
                int nivelAnterior = levelManager.getCurrentLevel();

                // ✅ SOLO UNA VEZ: incrementar progreso y clics
                levelManager.incrementProgress();
                statsManager.addClicks(1);

                // 🌟 Verificar CRISIS EXISTENCIAL (usando el total actualizado)
                int totalClicks = levelManager.getTotalClicks();
                if (totalClicks >= MotivationalMessageEffect.EXISTENTIAL_CRISIS_INTERVAL &&
                        totalClicks % MotivationalMessageEffect.EXISTENTIAL_CRISIS_INTERVAL == 0 &&
                        !isCrisisActive) {
                    triggerExistentialCrisis();
                }

                long now = System.currentTimeMillis();

                // ✅ Solo mostrar mensaje si pasó el cooldown
                if (now - lastMotivationTime >= MOTIVATION_COOLDOWN_MS) {
                    String motivationalMsg = MotivationalMessageEffect.getMotivationalMessage(totalClicks);
                    if (motivationalMsg != null) {
                        lastMotivationTime = now; // ⏱️ Actualizar cooldown

                        tvMotivationalMessage.setText(motivationalMsg);
                        tvMotivationalMessage.setVisibility(View.VISIBLE);

                        motivationalHandler.removeCallbacks(hideMotivationalMessage);
                        motivationalHandler.postDelayed(hideMotivationalMessage, 2000);
                    }
                }


                // Verificar subida de nivel
                if (levelManager.getCurrentLevel() > nivelAnterior) {
                    showLevelUpMessage(levelManager.getCurrentLevel());
                    statsManager.updateMaxLevel(levelManager.getCurrentLevel());
                    triggerLevelUpEffect(touchX, touchY);
                }

                // Efectos visuales
                numberEffect.showBigYellowNumber(touchX, touchY);
                createRippleEffect(touchX, touchY);
                triggerDopamineEffect(touchX, touchY);

                soundManager.play();
            }
            return true;
        });
    }

    //--------------------------------------------------
    private void triggerExistentialCrisis() {
        isCrisisActive = true;
        String quote = MotivationalMessageEffect.getRandomExistentialQuote();
        tvExistentialCrisis.setText(quote);
        tvExistentialCrisis.setVisibility(View.VISIBLE);

        // Ocultar tras 3.5 segundos
        existentialHandler.removeCallbacks(hideExistentialCrisis);
        existentialHandler.postDelayed(hideExistentialCrisis, 3500);
    }

    //--------------------------------------------------
    private void showLevelUpMessage(int newLevel) {
        levelUpHandler.removeCallbacks(hideLevelUpMessage);
        tvLevelUpMessage.setText(MotivationalMessageEffect.getLevelUpMessage(newLevel));
        tvLevelUpMessage.setVisibility(View.VISIBLE);
        levelUpHandler.postDelayed(hideLevelUpMessage, 2000);
    }

    //--------------------------------------------------
    private void triggerDopamineEffect(float x, float y) {
        int numExplosions = 3 + new Random().nextInt(3);
        for (int i = 0; i < numExplosions; i++) {
            effectsContainer.post(() -> {
                if (effectsContainer.getWidth() <= 0 || effectsContainer.getHeight() <= 0) return;
                int explosionX = (int) x;
                int explosionY = (int) y;
                if (new Random().nextBoolean()) {
                    particleExplosion.createExplosionAt(explosionX, explosionY);
                } else {
                    confettiManager.triggerExplosionAt(explosionX, explosionY);
                }
            });
        }
    }

    //--------------------------------------------------
    private void triggerLevelUpEffect(float x, float y) {
        int numExplosions = 8 + new Random().nextInt(5);
        for (int i = 0; i < numExplosions; i++) {
            effectsContainer.postDelayed(() -> {
                if (effectsContainer.getWidth() <= 0 || effectsContainer.getHeight() <= 0) return;
                particleExplosion.createExplosionAt((int) x, (int) y);
                confettiManager.triggerExplosionAt((int) x, (int) y);
            }, i * 50L);
        }
        confettiManager.triggerRainingAndExplosion();
    }

    //--------------------------------------------------
    private void createRippleEffect(float x, float y) {
        effectsContainer.post(() -> {
            if (effectsContainer.getWidth() <= 0 || effectsContainer.getHeight() <= 0) return;
            int color = getColorByLevel(levelManager.getCurrentLevel());
            RippleEffect ripple = new RippleEffect(this);
            ripple.setColor(color);
            int maxWidth = Math.max(effectsContainer.getWidth(), effectsContainer.getHeight());
            float maxRadius = maxWidth * 0.4f;
            float strokeWidth = 8f;
            int size = (int) (2 * (maxRadius + strokeWidth / 2) + 20);
            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(size, size);
            params.leftMargin = (int) (x - size / 2f);
            params.topMargin = (int) (y - size / 2f);
            ripple.setLayoutParams(params);
            effectsContainer.addView(ripple);
            ripple.startRippleAnimation(maxRadius);
        });
    }

    //-------------------------------------------------------
    private int getColorByLevel(int level) {
        if (level < 5) return Color.YELLOW;
        else if (level < 10) return Color.CYAN;
        else if (level < 15) return Color.MAGENTA;
        else if (level < 20) return Color.GREEN;
        else if (level < 25) return Color.RED;
        else if (level < 30) return Color.BLUE;
        else return Color.rgb(255, 215, 0); // Dorado
    }

    //-------------------------------------------------------
    private void salirModoDopamina() {
        if (sessionStartTime > 0) {
            long elapsedMs = System.currentTimeMillis() - sessionStartTime;
            if (elapsedMs > 1000) {
                statsManager.addPlayTime(elapsedMs);
            }
            statsManager.updateMaxLevel(levelManager.getCurrentLevel());
            statsManager.saveCurrentProgress(
                    levelManager.getCurrentLevel(),
                    levelManager.getCurrentProgress()
            );
            sessionStartTime = 0;
        }
        handler.removeCallbacks(decayRunnable);
        dopamineLayout.setVisibility(View.GONE);
        menuInicio.setVisibility(View.VISIBLE);
    }

    //-------------------------------------------------------
    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacksAndMessages(null);
        levelUpHandler.removeCallbacksAndMessages(null);
        motivationalHandler.removeCallbacksAndMessages(null);
        existentialHandler.removeCallbacksAndMessages(null);
        soundManager.release();
    }
}