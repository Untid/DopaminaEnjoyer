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

/**
 * Actividad alternativa con estilo minimalista
 * Representa una versión más "vacía" del clicker,
 * donde las interacciones van reduciendo los estímulos visuales y sonoros
 * hasta llegar a un "vacio total"
 */
public class MinimalistActivity extends AppCompatActivity {

    // -------------- Elementos de interfaz --------------------
    private FrameLayout minimalistContainer; // Contenedor principal para efectos visuales
    private TextView tvVacuumMessage; // Texto final que se muestra al alcanzar el vacío

    // ------------------ Gestores y efectos ---------------------
    private SoundManager soundManager; // Reproduce sonidos en cada toque
    private ParticleExplosion particleExplosion; // Maneja las partículas
    private ConfettiManagerWrapper confettiManager; // Maneja el confeti
    private NumberEffect numberEffect; // Muestra números flotantes

    // -------------- Control de estado --------------------------
    private boolean voidAchieved = false; // Indica si se alcanzó el vacío total
    private int voidLevel = 0; // Nivel de vacío (0 = dopamina, 100 = vacío total)
    private static final int MAX_VOID = 100; // Límite máximo del vacío

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_minimalist);

        // ------------- Referencias UI ----------------------------
        minimalistContainer = findViewById(R.id.minimalistContainer);
        tvVacuumMessage = findViewById(R.id.tvVacuumMessage);

        // ------------- Inicialización de managers ----------------------
        soundManager = new SoundManager(this, R.raw.blurp);
        particleExplosion = new ParticleExplosion(minimalistContainer);
        confettiManager = new ConfettiManagerWrapper(minimalistContainer);
        numberEffect = new NumberEffect(minimalistContainer);

        // ----------------- Evento táctil principal -----------------------
        minimalistContainer.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {

                // Si ya alcanzamos el vacío, se bloquea toda interacción
                if (voidAchieved) {
                    return true;
                }

                soundManager.play(); // Reproduce sonido al tocar

                // Incrementar el nivel de vacío (hasta 100)
                voidLevel = Math.min(voidLevel + 1, MAX_VOID);
                applyVoidLevel(); // Actualiza efectos según el nivel

                float x = event.getX();
                float y = event.getY();
                triggerMinimalistEffects(x, y); // Genera efectos visuales

                // Si se llega al vacío completo, mostrar mensaje final
                if (voidLevel >= MAX_VOID) {
                    voidAchieved = true; // Bloqueamos más cambios
                    showVoidMessage();
                }
            }
            return true;
        });


    }

    /**
     * Ajusta los efectos visuales, sonoros y de UI
     * según el nivel de vacío actual (voidLevel)
     */
    private void applyVoidLevel() {
        float t = voidLevel / 100f; // Normaliza el valor ( 0 -> 1)

        // Desvanecer fondo progresivamente
        if (minimalistContainer.getBackground() != null) {
            minimalistContainer.getBackground().setAlpha((int) (255 * (1f - t)));
        }

        // Reducir visibilidad del texto a medida que aumenta el vacío
        tvVacuumMessage.setAlpha(Math.max(0f, 1f - t * 1.1f));

        // Atenuar volumen de sonido
        soundManager.setVolume(Math.max(0f, 1f - t));

        // Disminuar opacidad global de todos los efectos visuales
        float fade = Math.max(0f, 1f - t);
        NumberEffect.setGlobalAlpha(fade);
        RippleEffect.setGlobalAlpha(fade);
        ParticleExplosion.setGlobalAlpha(fade);
        ConfettiManagerWrapper.setGlobalAlpha(fade);
    }

    /**
     * Crea los efectos visuales en pantalla.
     * Se van reduciendo conforme aumenta el nivel de vacío.
     */
    private void triggerMinimalistEffects(float x, float y) {

        // Menos explosiones cuanto más vacío hay
        int reducedExplosions = Math.max(0, 4 - (voidLevel / 25));
        for (int i = 0; i < reducedExplosions; i++) {
            particleExplosion.createExplosionAt((int) x, (int) y);
        }

        // El confeti desaparece antes del vacío total
        if (voidLevel < 40) confettiManager.triggerExplosionAt((int) x, (int) y);

        // Números amarillos (desvanecen por alpha global)
        numberEffect.showBigYellowNumber(x, y);

        // Efecto de ondas (ripple) hasta cierto nivel
        if (voidLevel < 75) {
            RippleEffect ripple = new RippleEffect(this);

            // Color progresivamente gris (desaturado)
            float fade = Math.max(0f, 1f - (voidLevel / 100f));
            int baseColor = Color.YELLOW;
            int desaturatedColor = Color.argb(
                    (int) (255 * fade),
                    (int) (Color.red(baseColor) * fade),
                    (int) (Color.green(baseColor) * fade),
                    (int) (Color.blue(baseColor) * fade)
            );
            ripple.setColor(desaturatedColor);

            // Configuración del tamaño y posición del ripple
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

    /**
     * Muestra el mensaje final cuando se alcanza el vacío absoluto.
     * Incluye una animación lenta y silenciamiento total del sonido.
     */
    private void showVoidMessage() {
        tvVacuumMessage.setText("He aquí la epifanía del hastío:\n" +
                "habiendo purgado la voluntad y extinguido todo anhelo\n" +
                "—ese motor insensato de la carne—,\n" +
                "la paz del vacío se revela, no como Nirvana,\n" +
                "sino como la glacial constatación de la *nulleza* absoluta.\n" +
                "Es la dicha más desesperante: la certeza\n" +
                "de haber alcanzado el máximo, que es, de hecho, la nada.");

        // Configuración inicial para animación de aparición
        tvVacuumMessage.setAlpha(0f);
        tvVacuumMessage.setScaleX(0.85f);
        tvVacuumMessage.setScaleY(0.85f);

        // Animación lenta de aparición (alpha + escala), impacto emocional
        tvVacuumMessage.animate()
                .alpha(1f)
                .scaleX(1f)
                .scaleY(1f)
                .setDuration(2500) // Lento = más poético
                .setStartDelay(800)
                .start();

        // Fondo negro absoluto (sin alpha)
        if (minimalistContainer.getBackground() != null) {
            minimalistContainer.getBackground().setAlpha(0);
        }

        // Silenciar audio definitivamente
        soundManager.setVolume(0f);
    }
}