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


public class ZenActivity extends AppCompatActivity {
    private Button btnSalir;
    private TextView instructions;
    private View zenTouchArea;
    private MediaPlayer ambientPlayer;

    private SoundManager inhaleSound;
    private SoundManager holdSound;
    private SoundManager exhaleSound;




    private final Handler handler = new Handler();
    private Runnable inhaleTickRunnable;
    private Runnable holdTickRunnable;
    private Runnable exhaleTickRunnable;

    private int inhaleSeconds = 4;   // 4s inhalar
    private int holdSeconds = 7;     // 7s mantener
    private int exhaleSeconds = 8;   // 8s exhalar

    private boolean isPressing = false;   // true mientras el usuario mantenga pulsado
    private boolean isExhaling = false;   // true durante la exhalación (evita reentradas)


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_zen);

        zenTouchArea = findViewById(R.id.zenTouchArea);
        instructions = findViewById(R.id.instructions);

        btnSalir = findViewById(R.id.btnSalirZen);
        btnSalir.setOnClickListener(v -> finish());

        inhaleSound = new SoundManager(this, R.raw.inhale);
        holdSound   = new SoundManager(this, R.raw.hold);
        exhaleSound = new SoundManager(this, R.raw.exhale);

        ambientPlayer = MediaPlayer.create(this, R.raw.zen_ambient);
        ambientPlayer.setLooping(true);
        ambientPlayer.setVolume(0.6f, 0.6f); // suave
        ambientPlayer.start();



        zenTouchArea.setOnTouchListener((v, event) -> {

            // Donde añadiremos el aro Zen (encima de todo)
            ViewGroup root = (ViewGroup) getWindow().getDecorView();

            switch (event.getAction()) {

                case MotionEvent.ACTION_DOWN:
                    if (isExhaling) return true; // No permitir toque si está exhalando

                    isPressing = true;
                    startInhaleSequence(); // Empieza inhalación/retención normal

                    // Crear aro Zen
                    ZenRingView ring = new ZenRingView(ZenActivity.this);

                    // Añadirlo ocupando toda la pantalla
                    FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                            FrameLayout.LayoutParams.MATCH_PARENT,
                            FrameLayout.LayoutParams.MATCH_PARENT
                    );
                    root.addView(ring, params);

                    // Guardar referencia para moverlo y desaparecerlo luego
                    zenTouchArea.setTag(ring);

                    // Posicionar en el punto donde tocó el usuario
                    ring.post(() -> ring.moveTo(event.getRawX(), event.getRawY()));

                    // Comenzar pulso mientras mantenga el dedo presionado
                    ring.startPulse();
                    return true;



                case MotionEvent.ACTION_MOVE:
                    // Hacer que el aro siga al dedo
                    ZenRingView ringMove = (ZenRingView) zenTouchArea.getTag();
                    if (ringMove != null) {
                        ringMove.moveTo(event.getRawX(), event.getRawY());
                    }
                    return true;



                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                    if (isPressing) {
                        isPressing = false;

                        cancelInhaleAndHoldRunnables();
                        startExhaleSequence(); // Aquí exhalas 8s como siempre

                        // Expandir y desvanecer aro durante toda la exhalación
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
    // ---------------------------------------------------
        private void startInhaleSequence () {
            inhaleSeconds = 4;
            holdSeconds = 7;

            inhaleSound.play();
            // Mostrar inicio de inhalación con conteo
            instructions.setText("Inhala... " + inhaleSeconds + "s");

            // Runnable para llevar el conteo de la inhalación (cada 1s)
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

        // ---------------------------------------------------
    private void cancelInhaleAndHoldRunnables() {
        if (inhaleTickRunnable != null) handler.removeCallbacks(inhaleTickRunnable);
        if (holdTickRunnable != null) handler.removeCallbacks(holdTickRunnable);
        inhaleTickRunnable = null;
        holdTickRunnable = null;
    }
    // ---------------------------------------------------
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
                            // Aquí no forzamos nada: esperamos a ACTION_UP
                        } else {
                            // Si soltó durante la retención, arrancamos exhalación
                            startExhaleSequence();
                        }
                    }
                }
            };
            handler.postDelayed(holdTickRunnable, 1000);
        }
    // ---------------------------------------------------
        private void startExhaleSequence() {
            // Evita reentradas si ya está exhalando
            if (isExhaling) return;

            isExhaling = true;
            exhaleSound.play();
            exhaleSeconds = 8;
            instructions.setText("Exhala... " + exhaleSeconds + "s");

            // Si tuvieras vibración o sonido al empezar la exhalación, puedes colocarlo aquí

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
    // ---------------------------------------------------
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
    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

}