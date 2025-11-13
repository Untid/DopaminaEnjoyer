package com.example.dopaminaenjoyer.effect;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.graphics.Color;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.widget.FrameLayout;

import java.util.Random;

/**
 * Clase para generar explosiones de partículas visuales.
 * Se usan en el clicker para el efecto de dopamina y celebraciones.
 */
public class ParticleExplosion {

    // Alpha global para desvanecer todas las partículas
    private static float globalAlpha = 1f;
    public static void setGlobalAlpha(float a) { globalAlpha = a; }

    // Contenedor donde mostrarán las partículas
    private final FrameLayout container;

    // Generador de números aleatorios
    private final Random random = new Random();

    // Constructor
    public ParticleExplosion(FrameLayout container) {
        this.container = container;
    }

    /**
     * Crea una explosión de partículas centrada en (x,y)
     */
    public void createExplosionAt(int x, int y) {
        // Si el contenedor aún no tiene tamaño, esperar un frame
        if (container.getWidth() <= 0 || container.getHeight() <= 0) {
            container.post(() -> createExplosionAt(x, y));
            return;
        }

        // Cantidad de partículas: 25 a 40
        int particleCount = 25 + random.nextInt(15);

        for (int i = 0; i < particleCount; i++) {
            // Color aleatorio: amarillo o rojo
            int color = random.nextBoolean() ? Color.YELLOW : Color.RED;

            // Crear la vista en la partícula
            View particle = new View(container.getContext());
            particle.setBackgroundColor(color);

            // Posicionar partícula centrada en (x,y)
            FrameLayout.LayoutParams p = new FrameLayout.LayoutParams(8, 8);
            p.leftMargin = x - 4;
            p.topMargin = y - 4;
            particle.setLayoutParams(p);
            container.addView(particle);

            // ---------------- DIRECCIÓN Y VELOCIDAD ---------------------------
            double angle = random.nextDouble() * 2 * Math.PI; // 360º
            float speed = 200f + random.nextFloat() * 400f;// Velocidad aleatoria entre 200 y 600 px/s
            float endX = (float) (Math.cos(angle) * speed);
            float endY = (float) (Math.sin(angle) * speed);

            // -------------------------- ANIMACIONES ----------------------------
            ObjectAnimator animX = ObjectAnimator.ofFloat(particle, "translationX", 0, endX);
            ObjectAnimator animY = ObjectAnimator.ofFloat(particle, "translationY", 0, endY);
            ObjectAnimator alpha = ObjectAnimator.ofFloat(particle, "alpha", 1f, 0f);

            // Escala aleatoria de la partícula
            ValueAnimator scale = ValueAnimator.ofFloat(1f, 1.5f + random.nextFloat());
            scale.addUpdateListener(a -> {
                float s = (Float) a.getAnimatedValue();
                particle.setScaleX(s);
                particle.setScaleY(s);
            });

            // Duración total de animación: 600-1000ms
            long duration = 600 + random.nextInt(400);

            // Interpolador: acelera al inicio y desacelera al final
            animX.setInterpolator(new AccelerateInterpolator(1.5f));
            animY.setInterpolator(new AccelerateInterpolator(1.5f));
            alpha.setInterpolator(new AccelerateInterpolator(1.5f));

            animX.setDuration(duration);
            animY.setDuration(duration);
            alpha.setDuration(duration);
            scale.setDuration(duration);

            // Iniciar animaciones
            animX.start();
            animY.start();
            alpha.start();
            scale.start();

            // Limpiar vista al terminar la animación
            alpha.addListener(new Animator.AnimatorListener() {
                @Override public void onAnimationEnd(Animator animation) { container.removeView(particle); }
                @Override public void onAnimationCancel(Animator animation) { container.removeView(particle); }
                @Override public void onAnimationStart(Animator animation) {}
                @Override public void onAnimationRepeat(Animator animation) {}
            });
        }
    }
}