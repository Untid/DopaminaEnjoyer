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

public class NumberEffect {

    private final FrameLayout container;
    private final Random random = new Random();

    public NumberEffect(FrameLayout container) {
        this.container = container;
    }

    // Nuevo método que recibe coordenadas del toque
    public void showBigYellowNumber(float x, float y) {
        TextView numberView = new TextView(container.getContext());
        int randomNumber = random.nextInt(1_000_000) + 1;
        numberView.setText(String.valueOf(randomNumber));
        numberView.setTextColor(Color.YELLOW);
        numberView.setTextColor(Color.YELLOW);
        numberView.setTextSize(120);
        numberView.setTypeface(Typeface.DEFAULT_BOLD);

        // Convertimos float a int (márgenes usan enteros)
        int left = (int) x;
        int top = (int) y;

        // Aseguramos que el número no se salga de la pantalla
        int maxWidth = container.getWidth();
        int maxHeight = container.getHeight();

        // Ajustamos para que el centro del texto esté en (x, y)
        // Puedes omitir esto si quieres que la esquina superior izquierda esté en (x, y)
        numberView.measure(
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
        );
        int textWidth = numberView.getMeasuredWidth();
        int textHeight = numberView.getMeasuredHeight();

        left = Math.max(0, Math.min(left - textWidth / 2, maxWidth - textWidth));
        top = Math.max(0, Math.min(top - textHeight / 2, maxHeight - textHeight));

        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        params.leftMargin = left;
        params.topMargin = top;
        numberView.setLayoutParams(params);

        container.addView(numberView);

        // Iniciar invisible y pequeño
        numberView.setAlpha(0f);
        numberView.setScaleX(0.2f);
        numberView.setScaleY(0.2f);

        // Primero: aparecer (alpha 0 → 1, escala 0.2 → 1)
        numberView.animate()
                .alpha(1f)
                .scaleX(1f)
                .scaleY(1f)
                .setDuration(300)
                .withEndAction(() -> {
                    // Luego: desvanecerse después de un breve tiempo
                    numberView.animate()
                            .alpha(0f)
                            .setDuration(700)
                            .setListener(new Animator.AnimatorListener() {
                                @Override
                                public void onAnimationEnd(@NonNull Animator animation) {
                                    container.removeView(numberView);
                                }

                                @Override
                                public void onAnimationCancel(@NonNull Animator animation) {
                                    container.removeView(numberView);
                                }

                                @Override
                                public void onAnimationStart(@NonNull Animator animation) {}

                                @Override
                                public void onAnimationRepeat(@NonNull Animator animation) {}
                            })
                            .start();
                })
                .start();
    }
}