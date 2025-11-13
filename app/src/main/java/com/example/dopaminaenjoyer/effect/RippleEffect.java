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

/**
 * Clase para crear un efecto de "ripple" u onda expansiva.
 * Se usa en el clicker para el efecto visual de dopamina al tocar.
 */
public class RippleEffect extends View {

    // Alpha global para desvanecer todos los ripples
    private static float globalAlpha = 1f;
    public static void setGlobalAlpha(float a) { globalAlpha = a; }

    // Pintura para dibujar el anillo
    private Paint paint;
    private float radius = 0f; // Radio actual del anillo
    private int color = Color.YELLOW; // Color del anillo
    private float strokeWidth = 8f; // Grosor del anillo en píxeles

    private int currentAlpha = 200; // Alpha local (0-255)


    // Setter para alpha de animación
    public void setAlphaValue(float alpha) {
        this.currentAlpha = (int) alpha;
        setAlpha(currentAlpha);  // setAlpha() de View
        invalidate();            // Fuerza redraw
    }


    // ------------------------- Constructores ------------------------------
    public RippleEffect(Context context) {
        super(context);
        init();
    }

    public RippleEffect(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    // --------------------- Inicialización común --------------------------
    private void init() {
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setStyle(Paint.Style.STROKE); // Dibuja solo contorno (anillo)
        paint.setStrokeWidth(strokeWidth);
        paint.setColor(color);
        setAlpha(200 / 255f); // Alpha inicial 0.78
    }

    // Cambiar color del anillo
    public void setColor(int color) {
        this.color = color;
        paint.setColor(color);
        invalidate();
    }

    // Cambiar radio actual del anillo
    public void setRadius(float radius) {
        this.radius = radius;
        invalidate();
    }

    // Dibujar el anillo en canvas
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // Aplicar alpha global (modo minimalista)
        int alpha = (int) (currentAlpha * globalAlpha);
        alpha = Math.max(0, Math.min(alpha, 255));
        paint.setAlpha(alpha);

        // Centro del ripple
        float centerX = getWidth() / 2f;
        float centerY = getHeight() / 2f;

        // Radio limitado al tamaño del view
        float drawRadius = Math.min(radius, Math.min(centerX, centerY) - strokeWidth / 2);
        canvas.drawCircle(centerX, centerY, drawRadius, paint);
    }


    /**
     * Inicia la animación de expansión del ripple
     * @param maxRadius radio máximo que alcanzará
     */
    public void startRippleAnimation(float maxRadius) {
        // Animación de radio: 0 -> maxRadius
        ObjectAnimator radiusAnim = ObjectAnimator.ofFloat(this, "radius", 0f, maxRadius);
        // Animación de alpha: 200 -> (desvanecimiento)
        ObjectAnimator alphaAnim = ObjectAnimator.ofFloat(this, "alphaValue", 200f, 0f);

        radiusAnim.setDuration(700);
        alphaAnim.setDuration(700);

        // Ejecutar ambas animaciones al mismo tiempo
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(radiusAnim, alphaAnim);

        // Listener para limpiar view al terminar animación
        animatorSet.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationEnd(Animator animation) {
                if (getParent() != null) {
                    ((ViewGroup) getParent()).removeView(RippleEffect.this);
                }
            }
            // Otros métodos necesarios, pero vacíos
            @Override public void onAnimationCancel(Animator animation) { onAnimationEnd(animation); }
            @Override public void onAnimationStart(Animator animation) {}
            @Override public void onAnimationRepeat(Animator animation) {}
        });
        animatorSet.start();
    }
}