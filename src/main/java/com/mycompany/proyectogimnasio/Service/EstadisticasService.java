package com.mycompany.proyectogimnasio.Service;

import com.mycompany.proyectogimnasio.Database; // Asumiendo tu clase de conexión
import java.sql.*;
import java.util.HashMap;
import java.util.Map;

public class EstadisticasService {
    
    // Función original: Consulta 1. Contar Clases por Actividad
    public Map<String, Integer> getClasesPorActividad() throws SQLException {
        Map<String, Integer> data = new HashMap<>();
        String SQL = "SELECT a.nombre, COUNT(c.id_clase) AS count FROM clases c JOIN actividades a ON c.id_actividad = a.id_actividad GROUP BY a.nombre";

        try (Connection conn = Database.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(SQL)) {
            
            while (rs.next()) {
                data.put(rs.getString("nombre"), rs.getInt("count"));
            }
        }
        return data;
    }

    // --- NUEVA FUNCIÓN 1: Aforo por Nombre de Clase (Actividad) ---
    // Muestra el aforo máximo de cada tipo de actividad
    public Map<String, Integer> getAforoPorActividad() throws SQLException {
        Map<String, Integer> data = new HashMap<>();
        // Consulta la tabla 'actividades' que contiene el nombre y el aforo
        String SQL = "SELECT nombre, aforo FROM actividades";

        try (Connection conn = Database.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(SQL)) {
            
            while (rs.next()) {
                data.put(rs.getString("nombre"), rs.getInt("aforo"));
            }
        }
        return data;
    }

    // --- NUEVA FUNCIÓN 2: Cantidad de Clases por Instructor ---
    // Muestra cuántas clases imparte cada instructor (activo o inactivo)
    public Map<String, Integer> getClasesPorInstructor() throws SQLException {
        Map<String, Integer> data = new HashMap<>();
        
        // JOIN entre clases e instructores y agrupar por el nombre completo del instructor
        String SQL = "SELECT i.nombre, i.apellido, COUNT(c.id_clase) AS count " +
                     "FROM instructores i LEFT JOIN clases c ON i.id_instructor = c.id_instructor " +
                     "GROUP BY i.id_instructor, i.nombre, i.apellido";

        try (Connection conn = Database.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(SQL)) {
            
            while (rs.next()) {
                String nombreCompleto = rs.getString("nombre") + " " + rs.getString("apellido");
                data.put(nombreCompleto, rs.getInt("count"));
            }
        }
        return data;
    }
    
    // --- NUEVA FUNCIÓN 3: Ocupación por Clase (Inscripciones vs. Aforo) ---
public Map<String, Map<String, Integer>> getOcupacionPorClase() throws SQLException {
    // Usaremos un Map anidado: NombreClase -> { "INSCRITOS": N, "AFORO": M }
    Map<String, Map<String, Integer>> data = new HashMap<>();

    String SQL = "SELECT " +
                 "c.id_clase, " +
                 "a.nombre AS nombre_actividad, " +
                 "a.aforo AS aforo_maximo, " +
                 "COUNT(CASE WHEN i.status = 'confirmado' THEN 1 END) AS inscripciones_confirmadas " +
                 "FROM clases c " +
                 "JOIN actividades a ON c.id_actividad = a.id_actividad " +
                 "LEFT JOIN inscripciones i ON c.id_clase = i.id_clase " +
                 "GROUP BY c.id_clase, a.nombre, a.aforo";

    try (Connection conn = Database.getConnection(); // Usando tu conector
         Statement stmt = conn.createStatement();
         ResultSet rs = stmt.executeQuery(SQL)) {

        while (rs.next()) {
            // Generamos una clave única para la clase: "Actividad (Día Hora)"
            // Nota: Aquí solo estoy usando el nombre de la actividad para simplificar la clave, pero es mejor usar el día y la hora también si hay múltiples clases de la misma actividad
            String claveClase = rs.getString("nombre_actividad") + " (ID: " + rs.getInt("id_clase") + ")";
            
            Map<String, Integer> ocupacionData = new HashMap<>();
            ocupacionData.put("AFORO", rs.getInt("aforo_maximo"));
            ocupacionData.put("INSCRITOS", rs.getInt("inscripciones_confirmadas"));
            
            data.put(claveClase, ocupacionData);
        }
    }
    return data;
}
}
