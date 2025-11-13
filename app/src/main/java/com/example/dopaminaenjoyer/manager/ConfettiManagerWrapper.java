package com.example.dopaminaenjoyer.manager;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.widget.FrameLayout;

import com.github.jinatonic.confetti.CommonConfetti;
import com.github.jinatonic.confetti.ConfettiManager;
import com.github.jinatonic.confetti.ConfettiSource;
import com.github.jinatonic.confetti.ConfettoGenerator;
import com.github.jinatonic.confetti.Utils;
import com.github.jinatonic.confetti.confetto.BitmapConfetto;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Wrapper para manejar efectos de confeti usando la librería Confetti de GitHub.
 * Permite disparar explosiones, lluvia y confeti personalizado.
 */
public class ConfettiManagerWrapper {

    // Alpha global para desvanecer todos los confetis
    private static float globalAlpha = 1f;
    public static void setGlobalAlpha(float a) { globalAlpha = a; }

    private final FrameLayout container; // Layout donde se dibuja el confeti

    // Constructor
    public ConfettiManagerWrapper(FrameLayout container) {
        this.container = container;
    }

    /**
     * Efecto combinado: lluvia + explosión al centro del container
     */
    public void triggerRainingAndExplosion() {
        int[] colors = {Color.RED,Color.YELLOW,Color.BLUE,Color.GREEN,Color.MAGENTA};
        CommonConfetti.rainingConfetti(container,colors).oneShot().animate();
        CommonConfetti.explosion(container,10,10,colors).oneShot().animate();
    }

    /**
     * Explosión personalizada con bitmaps de diferentes tamaños y colores
     */
    public void triggerCustomExplosion(){
        container.post(()->{
           int centerX = container.getWidth()/2;
           int centerY = container.getHeight()/2;
           ConfettiSource source = new ConfettiSource(centerX,centerY);

           final int[] explosionColors = {
             Color.RED,
             Color.parseColor("#FF6B00"), // naranja
             Color.YELLOW,
             Color.parseColor("#FF3B30") // rojo intenso
           };

           // Generar bitmaps de confeti con varios tamaños
            List<Bitmap> allBitmaps = new ArrayList<>();
            int[] sizes = {8,12,16,20};
            for (int size : sizes){
                allBitmaps.addAll(Utils.generateConfettiBitmaps(explosionColors,size));
            }

            // Generador aleatorio de confetis
            ConfettoGenerator generator = random1 -> {
                Bitmap bitmap = allBitmaps.get(random1.nextInt(allBitmaps.size()));
                return new BitmapConfetto(bitmap);
            };

            // Crear manager de confeti con parámetros de animación
            new ConfettiManager(container.getContext(), generator, source, container)
                    .setEmissionDuration(800) // duración de emisión
                    .setEmissionRate(150) // confetis por segundo
                    .setVelocityX(-300f,300f) // velocidad horizontal aleatoria
                    .setVelocityY(-400f,200f) // velocidad vertical aleatoria
                    .setAccelerationY(800f) // gravedad
                    .setRotationalVelocity(360f,360f) // rotación
                    .enableFadeOut(Utils.getDefaultAlphaInterpolator()) //desvanecimiento
                    .animate();
        });
    }

    /**
     * Explosión en una posición concreta (x,y)
     */
    public void triggerExplosionAt(int x, int y) {
        container.post(() -> {
            if (container.getWidth() <= 0 || container.getHeight() <= 0) return;

            ConfettiSource source = new ConfettiSource(x, y); // posición de explosión

            final int[] explosionColors = {
                    Color.RED,
                    Color.YELLOW,
                    Color.parseColor("#FF6B00"), // naranja
                    Color.parseColor("#FF3B30")  // rojo intenso
            };

            List<Bitmap> bitmaps = new ArrayList<>();
            int[] sizes = {8, 12, 16};
            for (int size : sizes) {
                bitmaps.addAll(Utils.generateConfettiBitmaps(explosionColors, size));
            }

            ConfettoGenerator generator = random -> {
                Bitmap bitmap = bitmaps.get(random.nextInt(bitmaps.size()));
                return new BitmapConfetto(bitmap);
            };

            new ConfettiManager(container.getContext(), generator, source, container)
                    .setEmissionDuration(600)
                    .setEmissionRate(120)
                    .setVelocityX(-250f, 250f)
                    .setVelocityY(-350f, 100f)
                    .setAccelerationY(800f)
                    .setRotationalVelocity(200f, 400f)
                    .enableFadeOut(Utils.getDefaultAlphaInterpolator())
                    .animate();
        });
    }
}