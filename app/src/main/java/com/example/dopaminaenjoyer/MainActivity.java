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
    private TextView tvLevelUpMessage; // 🌟 NUEVO: TextView para mensajes de nivel (Alta Prioridad)
    private long sessionStartTime = 0;
    private Handler handler = new Handler();
    private Runnable decayRunnable;

    // 🌟 NUEVOS: Handler y Runnable para ocultar el TextView de Level Up
    private final Handler levelUpHandler = new Handler();
    private final Runnable hideLevelUpMessage = new Runnable() {
        @Override
        public void run() {
            tvLevelUpMessage.setVisibility(View.GONE);
        }
    };
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
        tvLevelUpMessage = findViewById(R.id.tvLevelUpMessage); // 🌟 Inicialización
        //--------------------------------------------------
        ImageButton btnMenu = findViewById(R.id.btnMenu);
        btnMenu.setOnClickListener(v -> salirModoDopamina());


        //--------------------------------------------------


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

        // ✅ Cargar progreso guardado, NO resetear
        int[] saved = statsManager.loadCurrentProgress();
        levelManager.loadProgress(saved[0], saved[1]);

        sessionStartTime = System.currentTimeMillis();

        dopamineLayout.setClickable(true);
        dopamineLayout.setFocusable(true);
        dopamineLayout.setFocusableInTouchMode(true);


        dopamineLayout.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                float touchX = event.getX();
                float touchY = event.getY();

                // Reiniciar decadencia
                handler.removeCallbacks(decayRunnable);
                decayRunnable = new Runnable() {
                    @Override
                    public void run() {
                        // 🌟 Lógica de bajada de nivel: llama al LevelManager modificado
                        boolean levelDecreased = levelManager.decrementProgress();

                        if (levelManager.getCurrentProgress() > 0 || levelManager.getCurrentLevel() > 0) {
                            // Si el progreso es > 0 O el nivel es > 0, seguimos la decadencia.
                            handler.postDelayed(this, 100);
                        }

                        if (levelDecreased) {
                            // 🌟 Mostrar mensaje de bajada de nivel usando el TextView
                            tvLevelUpMessage.setText("¡Nivel BAJADO a " + levelManager.getCurrentLevel() + "!");
                            tvLevelUpMessage.setVisibility(View.VISIBLE);
                            levelUpHandler.postDelayed(hideLevelUpMessage, 2000); // Duración más corta para 'level down'
                        }
                        // NOTA: Si el nivel llega a 0 y el progreso a 0, la decadencia se detiene
                        // y el usuario debe tocar para reiniciar.
                    }
                };
                handler.postDelayed(decayRunnable, DECAY_DELAY_MS);

                // Guardar nivel anterior para detectar cambio
                int nivelAnterior = levelManager.getCurrentLevel();

                // Incrementar progreso
                levelManager.incrementProgress();
                statsManager.addClicks(1);
                // 🌟 Mostrar mensajes de clic (Media Prioridad - Toast con cancelación)
                MotivationalMessageEffect.showMessage(MainActivity.this, levelManager.getTotalClicks());


                // Verificar si hubo cambio de nivel
                if (levelManager.getCurrentLevel() > nivelAnterior) {
                    // 🌟 ALTA PRIORIDAD: Usar el TextView showLevelUpMessage
                    showLevelUpMessage(levelManager.getCurrentLevel());
                    statsManager.updateMaxLevel(levelManager.getCurrentLevel());
                    triggerLevelUpEffect(touchX, touchY);
                } else {
                    // Mostrar mensajes de progreso ocasionales (Baja Prioridad - Toast con cancelación)
                    if (new Random().nextInt(2) == 0) { // 10% de probabilidad
                        MotivationalMessageEffect.showProgressMessage(
                                MainActivity.this,
                                levelManager.getCurrentProgress(),
                                CLICKS_POR_NIVEL
                        );
                    }
                }

                // Mostrar número en la posición del toque
                numberEffect.showBigYellowNumber(touchX, touchY);
                // El anillo
                createRippleEffect(touchX, touchY);
                // Efectos adicionales (explosiones/confeti) en posición aleatoria o en el toque
                triggerDopamineEffect(touchX, touchY);

                soundManager.play();
            }
            return true;
        });
    }
    //--------------------------------------------------

    // 🌟 NUEVO MÉTODO PARA MOSTRAR EL MENSAJE DE NIVEL (ALTA PRIORIDAD)
    private void showLevelUpMessage(int newLevel) {
        // 1. Quitar cualquier temporizador pendiente (Esto es la priorización)
        levelUpHandler.removeCallbacks(hideLevelUpMessage);

        // 2. Obtener el mensaje de nivel
        String message = MotivationalMessageEffect.getLevelUpMessage(newLevel);

        // 3. Mostrar el TextView
        tvLevelUpMessage.setText(message);
        tvLevelUpMessage.setVisibility(View.VISIBLE);

        // 4. Establecer un nuevo temporizador para ocultarlo (ej. 4 segundos)
        levelUpHandler.postDelayed(hideLevelUpMessage, 2000);
    }
    //--------------------------------------------------
    private void triggerDopamineEffect(float x, float y) {
        int numExplosions = 3 + new Random().nextInt(3);
        for (int i = 0; i < numExplosions; i++) {
            effectsContainer.post(() -> {
                if (effectsContainer.getWidth() <= 0 || effectsContainer.getHeight() <= 0) return;

                // Puedes usar x,y o posiciones aleatorias. Aquí uso el toque como centro.
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
        // Más explosiones y confeti centrados en el toque
        int numExplosions = 8 + new Random().nextInt(5);
        for (int i = 0; i < numExplosions; i++) {
            effectsContainer.postDelayed(() -> {
                if (effectsContainer.getWidth() <= 0 || effectsContainer.getHeight() <= 0) return;

                particleExplosion.createExplosionAt((int) x, (int) y);
                confettiManager.triggerExplosionAt((int) x, (int) y);
            }, i * 50L);
        }

        // Efecto de lluvia de confeti adicional (global, no localizado)
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
            float maxRadius = maxWidth * 0.4f; // Puedes hacerlo más grande ahora que es un anillo

            // El tamaño debe ser: 2 * (radio máximo + grosor/2) + margen extra
            float strokeWidth = 8f; // debe coincidir con el de RippleEffect
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
        // Puedes personalizar estos colores como quieras
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
        // ✅ Solo guardar si realmente jugaste
        if (sessionStartTime > 0) {
            long elapsedMs = System.currentTimeMillis() - sessionStartTime;
            if (elapsedMs > 1000) { // más de 1 segundo
                statsManager.addPlayTime(elapsedMs);
            }
            statsManager.updateMaxLevel(levelManager.getCurrentLevel());
            statsManager.saveCurrentProgress(
                    levelManager.getCurrentLevel(),
                    levelManager.getCurrentProgress()
            );
            sessionStartTime = 0; // ✅ Reset para evitar reusos
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
        soundManager.release();
    }
}