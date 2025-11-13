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

/**
 * ZenRingView genera un aro visual para la actividad de respiración Zen.
 * Funciona como un View personalizado que:
 *  - Late mientras mantienes pulsado
 *  - Sigue el dedo
 *  - Se expande y desvanece al soltar (exhalación)
 */
public class ZenRingView extends View {

    // ---------------------------------------------------------------
    // Propiedades de dibujo
    private Paint paint;
    private float radius;
    private float baseRadius = 120f;   // Tamaño base del aro
    private float pulseRange = 20f;    // amplitud del latido

    private int currentAlpha = 230;    // visibilidad inicial (0-255)
    private ValueAnimator pulseAnimator;

    //------------------------------------------------------------------
    // Constructores
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
        paint.setStyle(Paint.Style.STROKE); // Dibuja solo el contorno
        paint.setStrokeWidth(100f); // aro grueso
        paint.setColor(Color.WHITE); // Estética Zen fuerte
    }

    // --------------------------------------------------------------
    // Dibujar el aro
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
    // LATIDO: mientras el usuario mantiene pulsado
    public void startPulse() {
        stopPulse(); // cancelar cualquier animador existente

        // Animator que va de baseRadius -> baseRadius + pulseRange y vuelve (REVERSE)
        pulseAnimator = ValueAnimator.ofFloat(baseRadius, baseRadius + pulseRange);
        pulseAnimator.setDuration(900); // duración de un ciclo
        pulseAnimator.setRepeatMode(ValueAnimator.REVERSE);
        pulseAnimator.setRepeatCount(ValueAnimator.INFINITE);
        pulseAnimator.setInterpolator(new android.view.animation.AccelerateDecelerateInterpolator());

        pulseAnimator.addUpdateListener(animation -> {
            radius = (float) animation.getAnimatedValue();
            invalidate(); // redibuja el aro
        });

        pulseAnimator.start();
    }

    // Detiene la animación
    public void stopPulse() {
        if (pulseAnimator != null) {
            pulseAnimator.cancel();
            pulseAnimator = null;
        }
    }
    // --------------------------------------------------------
    // EXPANSIÓN + DESVANECER: al soltar (exhalación)
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
                // Elimitar la vista del layout al terminar la animación
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