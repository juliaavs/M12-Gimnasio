package com.mycompany.proyectogimnasio.Utils;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.Locale;

/**
 * Clase de utilidad para manejar operaciones relacionadas con fechas y zonas horarias.
 */
public class DateUtils {

    // Configura la localización en español para el nombre del día
    // Uso de "es" para el idioma español (ISO 639) y "ES" para la región (España, ISO 3166)
    // Esto asegura que el formato de día sea 'Viernes' o 'Jueves', etc.
    private static final Locale SPANISH_LOCALE = new Locale("es", "ES");
    
    /**
     * Obtiene el nombre del día de la semana actual en español.
     * Ejemplo: "Viernes" para Friday.
     * @return El nombre del día de la semana actual en formato largo y en español.
     */
    public static String getCurrentDayNameInSpanish() {
        DayOfWeek today = LocalDate.now().getDayOfWeek();
        // Usar TextStyle.FULL garantiza que el nombre completo se use (ej: "Viernes" en lugar de "Vie")
        return today.getDisplayName(TextStyle.FULL, SPANISH_LOCALE);
    }
    
    /**
     * Obtiene el nombre del día de la semana especificado en español.
     * @param dayOfWeek El DayOfWeek a convertir.
     * @return El nombre del día en formato largo y en español.
     */
    public static String getDayNameInSpanish(DayOfWeek dayOfWeek) {
        return dayOfWeek.getDisplayName(TextStyle.FULL, SPANISH_LOCALE);
    }
}