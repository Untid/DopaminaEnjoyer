package com.example.dopaminaenjoyer.manager;

import android.content.Context;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;

import androidx.annotation.RawRes;

/**
 * Clase para gestionar sonidos cortos en el juego (efectos de click, inhalar/exhalar, etc.)
 *  - Usa SoundPool para reproducir efectos de manera eficiente
 *  - Permite controlar el volumen
 *  - Compatible con versiones antiguas y nuevas de Android
 */
public class SoundManager {
    private SoundPool soundPool; // gestor de sonidos
    private int soundId; // id del sonido cargado
    private float currentVolume = 1f; // volumen actual (0f a 1f)

    /**
     * Construcotr: recibe contexto y recurso de sonido
     * @param context contexto de la app
     * @param soundResId id del sonido en res/raw
     */
    public SoundManager(Context context, @RawRes int soundResId){
        initSoundPool(context,soundResId);
    }

    // Inicializa SoundPool dependiendo de la versión de Android
    private void initSoundPool(Context context, @RawRes int sounResId){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            // Versiones modernas: AudioAttributes
            AudioAttributes audioAttributes = new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_GAME) // uso: juego
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION) // efecto sonoro
                    .build();
            soundPool = new SoundPool.Builder()
                    .setMaxStreams(3) // máximo 3 sonidos simultáneos
                    .setAudioAttributes(audioAttributes)
                    .build();
        }else {
            // Versiones antiguas: constructor legacy
            soundPool = new SoundPool(3, AudioManager.STREAM_MUSIC,0);
        }
        // Carga el sonido en SoundPool y guarda su ID
        soundId = soundPool.load(context,sounResId,1);
    }

    /**
     * Reproduce el sonido con el volumen actual
     */
    public void play(){
        if (soundPool != null){
            soundPool.play(soundId, currentVolume, currentVolume, 1, 0, 1f);
        }
    }

    /**
     * Libera recursos de SoundPool (cuando se destruye la Activity)
     */
    public void release() {
        if (soundPool != null){
            soundPool.release();
            soundPool = null;
        }
    }

    /**
     * Ajusta el volumen del sonido
     * @param vol volumen (0f = silencio, 1f = máximo)
     */
    public void setVolume(float vol) {
        currentVolume = vol;
    }
}