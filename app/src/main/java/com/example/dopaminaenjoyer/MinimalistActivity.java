package com.example.dopaminaenjoyer;

import android.graphics.Color;
import android.os.Bundle;
import android.view.MotionEvent;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.dopaminaenjoyer.effect.NumberEffect;
import com.example.dopaminaenjoyer.effect.ParticleExplosion;
import com.example.dopaminaenjoyer.effect.RippleEffect;
import com.example.dopaminaenjoyer.manager.ConfettiManagerWrapper;
import com.example.dopaminaenjoyer.manager.SoundManager;

public class MinimalistActivity extends AppCompatActivity {

    private FrameLayout minimalistContainer;
    private TextView tvVacuumMessage;

    private SoundManager soundManager;
    private ParticleExplosion particleExplosion;
    private ConfettiManagerWrapper confettiManager;
    private NumberEffect numberEffect;

    private boolean voidAchieved = false;


    private int voidLevel = 0; // 0 = dopamina, 100 = vacío total
    private static final int MAX_VOID = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_minimalist);

        minimalistContainer = findViewById(R.id.minimalistContainer);
        tvVacuumMessage = findViewById(R.id.tvVacuumMessage);

        soundManager = new SoundManager(this, R.raw.blurp);
        particleExplosion = new ParticleExplosion(minimalistContainer);
        confettiManager = new ConfettiManagerWrapper(minimalistContainer);
        numberEffect = new NumberEffect(minimalistContainer);

        minimalistContainer.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {

                // ⛔ No volver a apagar ni animar nada si ya llegamos al vacío
                if (voidAchieved) {
                    return true;
                }

                soundManager.play();

                voidLevel = Math.min(voidLevel + 1, MAX_VOID);
                applyVoidLevel();

                float x = event.getX();
                float y = event.getY();
                triggerMinimalistEffects(x, y);

                if (voidLevel >= MAX_VOID) {
                    voidAchieved = true; // ✅ Bloqueamos más cambios
                    showVoidMessage();
                }
            }
            return true;
        });


    }

    private void applyVoidLevel() {

        float t = voidLevel / 100f; // 0 → 1

        // ✅ Desvanecer fondo sin cambiar color
        if (minimalistContainer.getBackground() != null) {
            minimalistContainer.getBackground().setAlpha((int) (255 * (1f - t)));
        }

        // ✅ El texto se desvanece excepto al final
        tvVacuumMessage.setAlpha(Math.max(0f, 1f - t * 1.1f));

        // ✅ Sonido se atenúa correctamente
        soundManager.setVolume(Math.max(0f, 1f - t));

        // ✅ Apagar visuales globalmente
        float fade = Math.max(0f, 1f - t);
        NumberEffect.setGlobalAlpha(fade);
        RippleEffect.setGlobalAlpha(fade);
        ParticleExplosion.setGlobalAlpha(fade);
        ConfettiManagerWrapper.setGlobalAlpha(fade);
    }

    private void triggerMinimalistEffects(float x, float y) {

        // ✅ Explosiones desaparecen gradualmente
        int reducedExplosions = Math.max(0, 4 - (voidLevel / 25));
        for (int i = 0; i < reducedExplosions; i++) {
            particleExplosion.createExplosionAt((int) x, (int) y);
        }

        // ✅ Confetti desaparece antes de vacío final
        if (voidLevel < 40) confettiManager.triggerExplosionAt((int) x, (int) y);

        // ✅ Números se desvanecen solos gracias a globalAlpha
        numberEffect.showBigYellowNumber(x, y);

        // ✅ Ripple con color en escala de grises, sin amarillear
        if (voidLevel < 75) {
            RippleEffect ripple = new RippleEffect(this);

            // Color desvaneciéndose hacia el gris
            float fade = Math.max(0f, 1f - (voidLevel / 100f));
            int baseColor = Color.YELLOW;
            int desaturatedColor = Color.argb(
                    (int) (255 * fade),
                    (int) (Color.red(baseColor) * fade),
                    (int) (Color.green(baseColor) * fade),
                    (int) (Color.blue(baseColor) * fade)
            );
            ripple.setColor(desaturatedColor);

            // Tamaño dinámico igual que en DopamineMode
            float maxRadius = Math.max(minimalistContainer.getWidth(), minimalistContainer.getHeight()) * 0.4f;
            float strokeWidth = 8f;
            int size = (int) (2 * (maxRadius + strokeWidth / 2) + 20);

            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(size, size);
            params.leftMargin = (int) (x - size / 2f);
            params.topMargin = (int) (y - size / 2f);
            ripple.setLayoutParams(params);

            minimalistContainer.addView(ripple);
            ripple.startRippleAnimation(maxRadius);
        }


    }

    private void showVoidMessage() {
        tvVacuumMessage.setText("He aquí la epifanía del hastío:\n" +
                "habiendo purgado la voluntad y extinguido todo anhelo\n" +
                "—ese motor insensato de la carne—,\n" +
                "la paz del vacío se revela, no como Nirvana,\n" +
                "sino como la glacial constatación de la *nulleza* absoluta.\n" +
                "Es la dicha más desesperante: la certeza\n" +
                "de haber alcanzado el máximo, que es, de hecho, la nada.");

        // Reseteamos visibilidad y tamaño para animación
        tvVacuumMessage.setAlpha(0f);
        tvVacuumMessage.setScaleX(0.85f);
        tvVacuumMessage.setScaleY(0.85f);

        // Animación lenta, impacto emocional
        tvVacuumMessage.animate()
                .alpha(1f)
                .scaleX(1f)
                .scaleY(1f)
                .setDuration(2500) // Lento = más poético
                .setStartDelay(800)
                .start();

        // Fondo negro absoluto
        if (minimalistContainer.getBackground() != null) {
            minimalistContainer.getBackground().setAlpha(0);
        }

        // Silenciar sonido definitivamente
        soundManager.setVolume(0f);
    }
}
