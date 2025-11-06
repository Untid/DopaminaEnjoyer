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

    private static float globalAlpha = 1f;
    public static void setGlobalAlpha(float a) { globalAlpha = Math.max(0f, Math.min(1f, a)); }

    public NumberEffect(FrameLayout container) {
        this.container = container;
    }

    public void showBigYellowNumber(float x, float y) {

        TextView numberView = new TextView(container.getContext());
        int randomNumber = random.nextInt(1_000_000) + 1;
        numberView.setText(String.valueOf(randomNumber));
        numberView.setTextColor(Color.YELLOW);
        numberView.setTextSize(120);
        numberView.setTypeface(Typeface.DEFAULT_BOLD);

        // CALCULAR POSICIÓN
        numberView.measure(
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
        );
        int textWidth = numberView.getMeasuredWidth();
        int textHeight = numberView.getMeasuredHeight();

        int left = (int) x - textWidth / 2;
        int top = (int) y - textHeight / 2;

        left = Math.max(0, Math.min(left, container.getWidth() - textWidth));
        top = Math.max(0, Math.min(top, container.getHeight() - textHeight));

        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        params.leftMargin = left;
        params.topMargin = top;
        numberView.setLayoutParams(params);

        container.addView(numberView);

        // 🌫️ USAR globalAlpha
        numberView.setAlpha(0f * globalAlpha);
        numberView.setScaleX(0.2f);
        numberView.setScaleY(0.2f);

        // ⬆️ APARECE → PERO NO LLEGA A 1.0, LLEGA A globalAlpha
        numberView.animate()
                .alpha(globalAlpha)
                .scaleX(1f)
                .scaleY(1f)
                .setDuration(250)
                .withEndAction(() -> {

                    // ⬇️ SE DESVANECE → PERO SE MULTIPLICA POR globalAlpha
                    numberView.animate()
                            .alpha(0f)
                            .setDuration((long)(600 * globalAlpha)) // mientras más vacío, más rápido desaparece
                            .setListener(new Animator.AnimatorListener() {
                                @Override
                                public void onAnimationEnd(@NonNull Animator animation) {
                                    container.removeView(numberView);
                                }

                                @Override
                                public void onAnimationCancel(@NonNull Animator animation) {
                                    container.removeView(numberView);
                                }

                                @Override public void onAnimationStart(@NonNull Animator animation) {}
                                @Override public void onAnimationRepeat(@NonNull Animator animation) {}
                            })
                            .start();
                })
                .start();
    }
}
