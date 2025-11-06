package com.example.dopaminaenjoyer.effect;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.graphics.Color;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.widget.FrameLayout;

import java.util.Random;

public class ParticleExplosion {

    private static float globalAlpha = 1f;
    public static void setGlobalAlpha(float a) { globalAlpha = a; }

    private final FrameLayout container;
    private final Random random = new Random();


    public ParticleExplosion(FrameLayout container) {
        this.container = container;
    }

    public void createExplosionAt(int x, int y) {
        if (container.getWidth() <= 0 || container.getHeight() <= 0) {
            container.post(() -> createExplosionAt(x, y));
            return;
        }

        int particleCount = 25 + random.nextInt(15); // 25-40 partículas → más densidad

        for (int i = 0; i < particleCount; i++) {
            int color = random.nextBoolean() ? Color.YELLOW : Color.RED;
            View particle = new View(container.getContext());
            particle.setBackgroundColor(color);

            FrameLayout.LayoutParams p = new FrameLayout.LayoutParams(8, 8);
            p.leftMargin = x - 4;
            p.topMargin = y - 4;
            particle.setLayoutParams(p);
            container.addView(particle);

            // Dirección aleatoria en 360°
            double angle = random.nextDouble() * 2 * Math.PI;
            // Velocidad aleatoria entre 200 y 600 px/s
            float speed = 200f + random.nextFloat() * 400f;
            float endX = (float) (Math.cos(angle) * speed);
            float endY = (float) (Math.sin(angle) * speed);

            // Animación de movimiento + desvanecimiento + escala
            ObjectAnimator animX = ObjectAnimator.ofFloat(particle, "translationX", 0, endX);
            ObjectAnimator animY = ObjectAnimator.ofFloat(particle, "translationY", 0, endY);
            ObjectAnimator alpha = ObjectAnimator.ofFloat(particle, "alpha", 1f, 0f);

            ValueAnimator scale = ValueAnimator.ofFloat(1f, 1.5f + random.nextFloat()); // escala aleatoria
            scale.addUpdateListener(a -> {
                float s = (Float) a.getAnimatedValue();
                particle.setScaleX(s);
                particle.setScaleY(s);
            });

            long duration = 600 + random.nextInt(400); // 600-1000ms

            // Aceleración inicial + desaceleración final (más natural)
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

            // Limpiar vista al final
            alpha.addListener(new Animator.AnimatorListener() {
                @Override public void onAnimationEnd(Animator animation) { container.removeView(particle); }
                @Override public void onAnimationCancel(Animator animation) { container.removeView(particle); }
                @Override public void onAnimationStart(Animator animation) {}
                @Override public void onAnimationRepeat(Animator animation) {}
            });
        }
    }
}