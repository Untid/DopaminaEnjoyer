package com.example.dopaminaenjoyer;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import com.example.dopaminaenjoyer.manager.SoundManager;
import com.example.dopaminaenjoyer.vista.ZenRingView;


/**
 * Actividad Zen: guía una respiración controlada (inhalar, mantener, exhalar)
 * con animaciones visuales (aro Zen) y sonidos sincronizados.
 */
public class ZenActivity extends AppCompatActivity {

    // --------- Elementos de la interfaz -------------------
    private Button btnSalir;
    private TextView instructions; // Muestra mensajes de respiración
    private View zenTouchArea; // Zona táctil para interactuar

    // --------------- Sonido ambiente y efectos -------------
    private MediaPlayer ambientPlayer; // Música de fondo relajante
    private SoundManager inhaleSound; // Sonido de inhalación
    private SoundManager holdSound; // Sonido de retención
    private SoundManager exhaleSound; // Sonido de exhalación

    // -------------- Controladores de tiempo ---------------------
    private final Handler handler = new Handler();
    private Runnable inhaleTickRunnable; // Contador para inhalar
    private Runnable holdTickRunnable; // Contador para mantener
    private Runnable exhaleTickRunnable; // Contador para exhalar

    // ------------- Duraciones de cada fase (segundos) --------------
    private int inhaleSeconds = 4;   // 4s inhalar
    private int holdSeconds = 7;     // 7s mantener
    private int exhaleSeconds = 8;   // 8s exhalar

    // ---------------------- Flags de estado --------------------
    private boolean isPressing = false;   // true mientras el usuario mantenga pulsado
    private boolean isExhaling = false;   // true durante la exhalación (evita reentradas)

    // ---------------------------------------------------------------------------
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_zen);

        // ------------------------- Referencias del layout ----------------------------
        zenTouchArea = findViewById(R.id.zenTouchArea);
        instructions = findViewById(R.id.instructions);
        btnSalir = findViewById(R.id.btnSalirZen);

        // Botñon salir: vuelve al menú anterior
        btnSalir.setOnClickListener(v -> finish());

        // ------------------------- Inicializa sonidos -----------------------------
        inhaleSound = new SoundManager(this, R.raw.inhale);
        holdSound   = new SoundManager(this, R.raw.hold);
        exhaleSound = new SoundManager(this, R.raw.exhale);

        // -------------------- Música ambiente en bucle ------------------------------
        ambientPlayer = MediaPlayer.create(this, R.raw.zen_ambient);
        ambientPlayer.setLooping(true);
        ambientPlayer.setVolume(0.6f, 0.6f); // suave
        ambientPlayer.start();

        // -------------------------- Interacción táctil principal ----------------------
        zenTouchArea.setOnTouchListener((v, event) -> {

            // Root view. Donde añadiremos el aro Zen (encima de todo)
            ViewGroup root = (ViewGroup) getWindow().getDecorView();

            switch (event.getAction()) {

                case MotionEvent.ACTION_DOWN:
                    // No permitir toque si está exhalando
                    if (isExhaling) return true;

                    isPressing = true;
                    startInhaleSequence(); // Comienza secuencia de inhalación

                    // Crear aro Zen Visual
                    ZenRingView ring = new ZenRingView(ZenActivity.this);

                    // Añadirlo ocupando toda la pantalla
                    FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                            FrameLayout.LayoutParams.MATCH_PARENT,
                            FrameLayout.LayoutParams.MATCH_PARENT
                    );
                    root.addView(ring, params);

                    // Guardar el aro como referencia
                    zenTouchArea.setTag(ring);

                    // Posicionar en el punto donde tocó el usuario
                    ring.post(() -> ring.moveTo(event.getRawX(), event.getRawY()));

                    // Inicia animación de pulso mientras se mantiene presionado
                    ring.startPulse();
                    return true;

                case MotionEvent.ACTION_MOVE:
                    // Mueve el aro mientras el usuario arrastra el dedo
                    ZenRingView ringMove = (ZenRingView) zenTouchArea.getTag();
                    if (ringMove != null) {
                        ringMove.moveTo(event.getRawX(), event.getRawY());
                    }
                    return true;

                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                    // Cuando el usuario suelta, termina la fase
                    if (isPressing) {
                        isPressing = false;

                        // Detiene los contadores previos
                        cancelInhaleAndHoldRunnables();

                        // Inicia secuencia de exhalación
                        startExhaleSequence();

                        // Efecto visual de expansión/fade out del aro
                        ZenRingView ringEnd = (ZenRingView) zenTouchArea.getTag();
                        if (ringEnd != null) {
                            ringEnd.expandAndDisappear(exhaleSeconds * 1000L); // 👈 sincronizado a 8s
                        }
                    }
                    return true;
            }
            return false;
        });

    }
    // -------------------------------------------------------------

    /**
     * Inicia la fase de inhalación (4s) y luego la de mantener (7s) si el usuario sigue pulsando.
     */
        private void startInhaleSequence () {
            inhaleSeconds = 4;
            holdSeconds = 7;

            inhaleSound.play();
            // Mostrar inicio de inhalación con conteo
            instructions.setText("Inhala... " + inhaleSeconds + "s");

            // Runnable para el conteo de inhalación (cada segundo)
            inhaleTickRunnable = new Runnable() {
                @Override
                public void run() {
                    inhaleSeconds--;
                    if (inhaleSeconds > 0 && isPressing) {
                        instructions.setText("Inhala... " + inhaleSeconds + "s");
                        handler.postDelayed(this, 1000);
                    } else {
                        // Si el usuario sigue presionando, pasamos a "Mantén"
                        if (isPressing) {
                            instructions.setText("Mantén... " + holdSeconds + "s");
                            // iniciar conteo de retención
                            startHoldCountdown();
                        } else {
                            // Si ya soltó antes de terminar la inhalación, iniciamos exhalación
                            startExhaleSequence();
                        }
                    }
                }
            };
            handler.postDelayed(inhaleTickRunnable, 1000);
        }

        // ---------------------------------------------------------------------

    /**
     *  Detiene los contadores de inhalar y mantener para evitar conflictos.
     */
    private void cancelInhaleAndHoldRunnables() {
        if (inhaleTickRunnable != null) handler.removeCallbacks(inhaleTickRunnable);
        if (holdTickRunnable != null) handler.removeCallbacks(holdTickRunnable);
        inhaleTickRunnable = null;
        holdTickRunnable = null;
    }

    // --------------------------------------------------------------------------

    /**
     *  Fase de mantener el aire durante 7s (si sigue presionando).
     */
        private void startHoldCountdown () {
            holdSound.play();
            holdTickRunnable = new Runnable() {
                @Override
                public void run() {
                    holdSeconds--;
                    if (holdSeconds > 0 && isPressing) {
                        instructions.setText("Mantén... " + holdSeconds + "s");
                        handler.postDelayed(this, 1000);
                    } else {
                        // Si el usuario sigue presionando tras la retención completa,
                        // le indicamos que levante para exhalar.
                        if (isPressing) {
                            instructions.setText("Suelta para exhalar..."); // explícito
                        } else {
                            // Si soltó durante la retención, arrancamos exhalación
                            startExhaleSequence();
                        }
                    }
                }
            };
            handler.postDelayed(holdTickRunnable, 1000);
        }
    // ------------------------------------------------------------------------

    /**
     *  Fase de exhalar (8s) con bloqueo para evitar reinicios simultáneos.
     */
        private void startExhaleSequence() {
            if (isExhaling) return; // Evita reentradas si ya está exhalando

            isExhaling = true;
            exhaleSound.play();
            exhaleSeconds = 8;
            instructions.setText("Exhala... " + exhaleSeconds + "s");

            exhaleTickRunnable = new Runnable() {
                @Override
                public void run() {
                    exhaleSeconds--;
                    if (exhaleSeconds > 0) {
                        instructions.setText("Exhala... " + exhaleSeconds + "s");
                        handler.postDelayed(this, 1000);
                    } else {
                        // Exhalación completada
                        isExhaling = false;
                        instructions.setText("Presiona para inhalar nuevamente 🌿");
                        exhaleTickRunnable = null;
                    }
                }
            };
            handler.postDelayed(exhaleTickRunnable, 1000);
        }

    // -------------------------------------------------------------------

    /**
     *  Libera todos los recursos y detiene la música al destruir la actividad
     */
        @Override
        protected void onDestroy() {
            super.onDestroy();
            handler.removeCallbacksAndMessages(null);
            if (ambientPlayer != null) {
                ambientPlayer.stop();
                ambientPlayer.release();
                ambientPlayer = null;
            }
        }
    // ---------------------------------------------------

    /**
     * Aplica transición suave al cerrar la actividad
     */
    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }
}