package com.example.dopaminaenjoyer.vista;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

public class ZenRingView extends View {

    private Paint paint;
    private float radius;
    private float baseRadius = 120f;   // Tamaño base del aro en reposo (más grande)
    private float pulseRange = 20f;    // Cuánto se expande al latir (más profundo)

    private int currentAlpha = 230;    // Más visible (0-255)
    private ValueAnimator pulseAnimator;


    public ZenRingView(Context context) {
        super(context);
        init();
    }

    public ZenRingView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        radius = baseRadius;

        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(100f); // ✅ ARO MÁS GORDO
        paint.setColor(Color.WHITE); // Estética Zen fuerte
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        paint.setAlpha(currentAlpha);

        float cx = getWidth() / 2f;
        float cy = getHeight() / 2f;
        canvas.drawCircle(cx, cy, radius, paint);
    }

    // --------------------------------------------------------
    // Seguir dedo
    public void moveTo(float x, float y) {
        setX(x - getWidth() / 2f);
        setY(y - getHeight() / 2f);
    }

    // --------------------------------------------------------
    // LATIDO mientras mantiene pulsado
    public void startPulse() {
        stopPulse(); // asegurarnos de no tener otro animador

        // Animator que va de baseRadius -> baseRadius + pulseRange y vuelve (REVERSE)
        pulseAnimator = ValueAnimator.ofFloat(baseRadius, baseRadius + pulseRange);
        pulseAnimator.setDuration(900);
        pulseAnimator.setRepeatMode(ValueAnimator.REVERSE);
        pulseAnimator.setRepeatCount(ValueAnimator.INFINITE);
        pulseAnimator.setInterpolator(new android.view.animation.AccelerateDecelerateInterpolator());

        pulseAnimator.addUpdateListener(animation -> {
            radius = (float) animation.getAnimatedValue();
            invalidate();
        });

        pulseAnimator.start();
    }
    public void stopPulse() {
        if (pulseAnimator != null) {
            pulseAnimator.cancel();
            pulseAnimator = null;
        }
    }
    // --------------------------------------------------------
    // EXPANSIÓN + DESVANECER durante EXHALACIÓN (8 s)
    public void expandAndDisappear(long durationMs) {
        stopPulse(); // detenemos el pulso al soltar

        float maxRadius = Math.min(getWidth(), getHeight()) * 0.8f; // llega casi a bordes

        ValueAnimator expand = ValueAnimator.ofFloat(radius, maxRadius);
        expand.setDuration(durationMs);
        expand.setInterpolator(new android.view.animation.DecelerateInterpolator());

        ValueAnimator fade = ValueAnimator.ofInt(255, 0);
        fade.setDuration(durationMs);
        fade.setInterpolator(new android.view.animation.DecelerateInterpolator());

        expand.addUpdateListener(anim -> {
            radius = (float) anim.getAnimatedValue();
            invalidate();
        });

        fade.addUpdateListener(anim -> {
            currentAlpha = (int) anim.getAnimatedValue();
            invalidate();
        });

        AnimatorSet set = new AnimatorSet();
        set.playTogether(expand, fade);

        set.addListener(new Animator.AnimatorListener() {
            @Override public void onAnimationEnd(Animator animation) {
                // remover vista al terminar
                ViewGroup parent = (ViewGroup) getParent();
                if (parent != null) parent.removeView(ZenRingView.this);
            }
            @Override public void onAnimationStart(Animator animation) {}
            @Override public void onAnimationCancel(Animator animation) {}
            @Override public void onAnimationRepeat(Animator animation) {}
        });

        set.start();
    }

}
