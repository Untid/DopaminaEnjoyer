// Archivo: com/example/dopaminaenjoyer/effect/RippleEffect.java

package com.example.dopaminaenjoyer.effect;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;

public class RippleEffect extends View {

    private static float globalAlpha = 1f;
    public static void setGlobalAlpha(float a) { globalAlpha = a; }


    private Paint paint;
    private float radius = 0f;
    private int color = Color.YELLOW;
    private float strokeWidth = 8f; // Grosor del anillo (en píxeles)


    private int currentAlpha = 200;

    public void setAlphaValue(float alpha) {
        this.currentAlpha = (int) alpha;
        setAlpha(currentAlpha); // setAlpha() es un método de View
        invalidate();
    }

    public RippleEffect(Context context) {
        super(context);
        init();
    }

    public RippleEffect(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setStyle(Paint.Style.STROKE); // ¡Esto hace que sea un anillo!
        paint.setStrokeWidth(strokeWidth);
        paint.setColor(color);
        setAlpha(200 / 255f); // ✅ 200 de 255 ≈ 0.78
    }

    public void setColor(int color) {
        this.color = color;
        paint.setColor(color);
        invalidate();
    }

    public void setRadius(float radius) {
        this.radius = radius;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // Aplicar alpha global (modo minimalista)
        int alpha = (int) (currentAlpha * globalAlpha);
        alpha = Math.max(0, Math.min(alpha, 255));
        paint.setAlpha(alpha);

        float centerX = getWidth() / 2f;
        float centerY = getHeight() / 2f;

        float drawRadius = Math.min(radius, Math.min(centerX, centerY) - strokeWidth / 2);
        canvas.drawCircle(centerX, centerY, drawRadius, paint);
    }


    // Método para iniciar la animación
    public void startRippleAnimation(float maxRadius) {
        ObjectAnimator radiusAnim = ObjectAnimator.ofFloat(this, "radius", 0f, maxRadius);
        ObjectAnimator alphaAnim = ObjectAnimator.ofFloat(this, "alphaValue", 200f, 0f);

        radiusAnim.setDuration(700);
        alphaAnim.setDuration(700);

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(radiusAnim, alphaAnim);
        animatorSet.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationEnd(Animator animation) {
                if (getParent() != null) {
                    ((ViewGroup) getParent()).removeView(RippleEffect.this);
                }
            }
            // ... otros métodos (puedes dejarlos vacíos)
            @Override public void onAnimationCancel(Animator animation) { onAnimationEnd(animation); }
            @Override public void onAnimationStart(Animator animation) {}
            @Override public void onAnimationRepeat(Animator animation) {}
        });
        animatorSet.start();
    }
}