package com.example.dopaminaenjoyer;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.animation.ScaleAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.animation.ValueAnimator;
import android.view.animation.LinearInterpolator;

import androidx.appcompat.app.AppCompatActivity;

public class ZenActivity extends AppCompatActivity {
    private Button btnSalir;
    private TextView instructions;
    private View zenTouchArea;

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


        zenTouchArea.setOnTouchListener((v, event) -> {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    if (isExhaling) {
                        // Si está exhalando, ignoramos toques hasta que termine
                        return true;
                    }
                    // Usuario empieza a presionar: iniciar inhalación/retención guiada
                    isPressing = true;
                    startInhaleSequence();
                    return true;

                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                    // Usuario levanta el dedo: iniciar exhalación (si no está ya)
                    if (isPressing) {
                        isPressing = false;
                        // cancelar inhalación/retención si estaban en curso
                        cancelInhaleAndHoldRunnables();
                        startExhaleSequence();
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
        }
    // ---------------------------------------------------
    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

}