package com.example.dopaminaenjoyer.effect;

import android.content.Context;
import android.widget.Toast;

public class MotivationalMessageEffect {

    // 🌟 NUEVO: Variable estática para guardar el Toast actualmente visible o en cola (Baja/Media prioridad)
    private static Toast lastToast = null;

    // 🌟 NUEVO: Helper para cancelar el Toast anterior y mostrar uno nuevo, evitando la cola.
    private static void showPrioritizedToast(Context context, String message, int duration) {
        // Cancelar el Toast anterior para evitar la acumulación
        if (lastToast != null) {
            lastToast.cancel();
        }

        Toast newToast = Toast.makeText(context, message, duration);
        lastToast = newToast;
        newToast.show();
    }
    //----------------------------------------------------------------------------------
    // Prioridad Media: Hitos por click (Usa Toast con cancelación)
    public static void showMessage(Context context, int clickCount) {
        // clickCount ahora es totalClicks
        String message = "";
        switch (clickCount) {
            case 20:
                message = "¡Sigue así!";
                break;
            case 40:
                message = "¡Ya queda poco!";
                break;
            case 80:
                message = "¡Algo ocurrirá pronto...";
                break;
            case 100:
                message = "¡Estás en modo dopamina pura!";
                break;
            default:
                // 🌟 Esta lógica ahora usará el contador total, mostrando 50, 100, 150, etc.
                if (clickCount > 0 && clickCount % 50 == 0) {
                    message = "¡" + clickCount + " clics! ¡No pares!";
                }
        }

        if (!message.isEmpty()) {
            showPrioritizedToast(context, message, Toast.LENGTH_SHORT);
        }
    }

    // 🌟 NUEVO: Método para solo DEVOLVER el mensaje de subida de nivel (ALTA prioridad) como String
    // La MainActivity se encargará de mostrarlo en el TextView.
    public static String getLevelUpMessage(int level) {
        String message = "";
        switch (level) {
            case 1:
                message = "¡Nivel 1! ¡Vamos!";
                break;
            case 2:
                message = "¡Nivel 2! ¡Increíble!";
                break;
            case 3:
                message = "¡Nivel 3! ¡Eres una máquina!";
                break;
            case 5:
                message = "¡Nivel 5! ¡Leyenda en progreso!";
                break;
            case 10:
                message = "¡Nivel 10! ¡Dios del click!";
                break;
            default:
                if (level % 5 == 0) {
                    message = "¡Nivel " + level + "! ¡Sigue rompiéndolo!";
                } else {
                    message = "¡Subiste al nivel " + level + "!";
                }
        }
        return message;
    }

    // ❌ ELIMINADO: El antiguo método showLevelMessage (que usaba Toast) fue reemplazado por getLevelUpMessage.

    //----------------------------------------------------------------------------------
    // Prioridad Baja: Progreso por % (Usa Toast con cancelación)
    public static void showProgressMessage(Context context, int progress, int maxProgress) {
        int percentage = (progress * 100) / maxProgress;
        String message = "";

        if (percentage >= 80) {
            message = "¡Casi llegas! " + percentage + "%";
        } else if (percentage >= 50) {
            message = "¡Mitad del camino! " + percentage + "%";
        } else if (percentage >= 25) {
            message = "¡Buen comienzo! " + percentage + "%";
        }

        if (!message.isEmpty()) {
            // 🌟 Usamos el helper con cancelación
            showPrioritizedToast(context, message, Toast.LENGTH_SHORT);
        }
    }
}