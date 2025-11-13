package com.example.dopaminaenjoyer.effect;

import android.animation.Animator;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import java.util.Random;

/**
 * Clase para mostrar números grandes y amarillos flotando en pantalla
 * como efecto visual de "dopamina" en el clicker.
 */
public class NumberEffect {

    // Contenedor donde se añaden los TextViews
    private final FrameLayout container;

    // Generador de números aleatorios
    private final Random random = new Random();

    // Alfa global para desvanecer todos los números (0 = invisible, 1 = opaco)
    private static float globalAlpha = 1f;
    public static void setGlobalAlpha(float a) {
        // Asegura que globalAlpha esté entre 0 y 1
        globalAlpha = Math.max(0f, Math.min(1f, a));
    }

    // Constructor: recibe el FrameLayout donde se mostrará el efecto
    public NumberEffect(FrameLayout container) {
        this.container = container;
    }

    /**
     * Crea y muestra un número amarillo grande en la posición (x,y)
     * con animación de entrada y salida.
     */
    public void showBigYellowNumber(float x, float y) {

        // Crear TextView con un número aleatorio
        TextView numberView = new TextView(container.getContext());
        int randomNumber = random.nextInt(1_000_000) + 1; // 1 - 1 millón
        numberView.setText(String.valueOf(randomNumber));
        numberView.setTextColor(Color.YELLOW);
        numberView.setTextSize(120); // tamaño grande
        numberView.setTypeface(Typeface.DEFAULT_BOLD); // negrita

        // ---------------------------- CALCULAR POSICIÓN -----------------------------
        numberView.measure(
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
        );
        int textWidth = numberView.getMeasuredWidth();
        int textHeight = numberView.getMeasuredHeight();

        // Centrar el texto sobre (x,y)
        int left = (int) x - textWidth / 2;
        int top = (int) y - textHeight / 2;

        // Evitar que salga del contenedor
        left = Math.max(0, Math.min(left, container.getWidth() - textWidth));
        top = Math.max(0, Math.min(top, container.getHeight() - textHeight));

        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        params.leftMargin = left;
        params.topMargin = top;
        numberView.setLayoutParams(params);

        // Añadir a la pantalla
        container.addView(numberView);

        // ------------------- ANIMACIÓN ----------------------------
        // Inicial: invisible y pequeño
        numberView.setAlpha(0f * globalAlpha);
        numberView.setScaleX(0.2f);
        numberView.setScaleY(0.2f);

        // Animación de entrada (crece y aparece)
        numberView.animate()
                .alpha(globalAlpha) // llega a opacidad global
                .scaleX(1f)
                .scaleY(1f)
                .setDuration(250) // rápido
                .withEndAction(() -> {

                    // Animación de salida (desvanece)
                    numberView.animate()
                            .alpha(0f) // se desvanece
                            .setDuration((long)(600 * globalAlpha)) // duración proporcional a alpha
                            .setListener(new Animator.AnimatorListener() {
                                @Override
                                public void onAnimationEnd(@NonNull Animator animation) {
                                    container.removeView(numberView); // limpiar View
                                }

                                @Override
                                public void onAnimationCancel(@NonNull Animator animation) {
                                    container.removeView(numberView); // limpiar si se cancela
                                }

                                @Override public void onAnimationStart(@NonNull Animator animation) {}
                                @Override public void onAnimationRepeat(@NonNull Animator animation) {}
                            })
                            .start();
                })
                .start();
    }
}