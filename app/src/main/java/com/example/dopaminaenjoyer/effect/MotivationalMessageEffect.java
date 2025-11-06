package com.example.dopaminaenjoyer.effect;

import java.util.Random;

public class MotivationalMessageEffect {


    private static final Random random = new Random();
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

    // Nuevo método con probabilidad de mostrar clicks
    public static String getMotivationalMessage(int totalClicks) {

        // Probabilidad 1 de cada 5 → mensaje con número
        if (random.nextInt(5) == 0) {
            return "Llevas " + totalClicks + " clics. ¡Nada mal!"; // puedes editar frase
        }

        return ALL_MESSAGES[random.nextInt(ALL_MESSAGES.length)];
    }



    // 🌟 Solo mensajes de subida de nivel (devueltos como String)
    public static String getLevelUpMessage(int level) {

        if (level % 5 == 0) {
            return "¡Nivel " + level + "! ¡Sigue rompiéndolo!";
        }
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
            return "¡Nivel " + level + "! ... Ya no es dopamina. Es fe.";
        }
    }

    // 🌟 Frases existenciales (para la Crisis Existencial)
    private static final String[] EXISTENTIAL_QUOTES = {
            "¿Pero... para qué clickeas?",
            "¿Acaso el clic tiene sentido?",
            "Tu dopamina es finita. ¿Y luego?",
            "¿Estás jugando… o el juego te juega a ti?",
            "¿Qué buscas? ¿Gloria? ¿Niveles? ¿Paz?",
            "Detente un segundo. Respira. ¿Vale la pena?"
    };


    public static String getRandomExistentialQuote() {
        return EXISTENTIAL_QUOTES[random.nextInt(EXISTENTIAL_QUOTES.length)];
    }

    public static final int EXISTENTIAL_CRISIS_INTERVAL = 10;
}