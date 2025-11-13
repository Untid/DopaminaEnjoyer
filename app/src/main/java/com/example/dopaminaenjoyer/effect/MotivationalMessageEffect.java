package com.example.dopaminaenjoyer.effect;

import java.util.Random;

/**
 *  Clase estática que gestiona mensajes motivacionales y frases existencias
 *  para el clicker. Incluye mensajes normales, de subida de nivel y de crisis.
 */
public class MotivationalMessageEffect {

    // Generador de números aleatorios
    private static final Random random = new Random();

    // Mensajes motivacionales generales
    private static final String[] ALL_MESSAGES = {
            "¡Sigue así!",
            "¡Vas por buen camino!",
            "¡No pares ahora!",
            "¡Ya queda poco!",
            "¡La meta está cerca!",
            "¡Esto se pone interesante!",
            "¡Algo ocurrirá pronto...",
            "¡Siente la dopamina crecer!",
            "¡El clímax se acerca!",
            "¡Estás en modo dopamina pura!",
            "¡La dopamina fluye en tus venas!",
            "Eres más fuerte de lo que crees.",
            "Tu viaje importa.",
            "Hoy fue una buena decisión."
    };

    // Mensajes existenciales para la Crisis existencial
    private static final String[] EXISTENTIAL_QUOTES = {
            "¿Pero... para qué clickeas?",
            "¿Acaso el clic tiene sentido?",
            "Tu dopamina es finita. ¿Y luego?",
            "¿Estás jugando… o el juego te juega a ti?",
            "¿Qué buscas? ¿Gloria? ¿Niveles? ¿Paz?",
            "Detente un segundo. Respira. ¿Vale la pena?"
    };

    /**
     * Devuelve un mensaje motivacional aleatorio.
     * A veces muestra el número de clicks totales (1 de cada 5 probabilidades)
     * @param totalClicks clicks acumulados en la sesión
     * @return String con mensaje motivacional
     */
    public static String getMotivationalMessage(int totalClicks) {

        // 1 de cada 5 veces devuelve el mensaje con el número de clicks
        if (random.nextInt(5) == 0) {
            return "Llevas " + totalClicks + " clics. ¡Nada mal!";
        }
        // Si no, devuelve un mensaje aleatorio de ALL_MESSAGES
        return ALL_MESSAGES[random.nextInt(ALL_MESSAGES.length)];
    }

    /**
     * Devuelve un mensaje cuando se sube de nivel.
     * Mensaje depende del nivel actual y crea sensación de progreso
     * @param level nivel actual del jugador
     * @return String con mensaje de subida de nivel
     */
    public static String getLevelUpMessage(int level) {
        // Mensajes especiales cada 5 niveles
        if (level % 5 == 0) {
            return "¡Nivel " + level + "! ¡Sigue rompiéndolo!";
        }

        // Mnesajes según rangos de nivel
        if (level < 10) {
            return "¡Subiste al nivel " + level + "! ¡Vas con todo!";
        } else if (level < 25) {
            return "¡Nivel " + level + "! ¡Eres una máquina!";
        } else if (level < 50) {
            return "¡Nivel " + level + "! ¡Esto ya es otra liga!";
        } else if (level < 100) {
            return "¡Nivel " + level + "! ¡Puro compromiso neuroquímico!";
        } else if (level < 200) {
            return "¡Nivel " + level + "! ¿Estás bien? ¿Necesitas agua?";
        } else {
            // Niveles muy altos
            return "¡Nivel " + level + "! ... Ya no es dopamina. Es fe.";
        }
    }

    /**
     * Devuelve una frase existencial aleatoria.
     * Se usa cuando el total de clicks alcanza ciertos intervalos.
     * @return String con frase existencial
     */
    public static String getRandomExistentialQuote() {
        return EXISTENTIAL_QUOTES[random.nextInt(EXISTENTIAL_QUOTES.length)];
    }

    // Cada cuántos clicks se dispara la Crisis existencial
    public static final int EXISTENTIAL_CRISIS_INTERVAL = 10;
}