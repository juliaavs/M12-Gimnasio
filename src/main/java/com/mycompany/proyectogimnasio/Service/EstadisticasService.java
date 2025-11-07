package com.mycompany.proyectogimnasio.Service;

import com.mycompany.proyectogimnasio.Database;
import java.sql.*;
import com.mycompany.proyectogimnasio.Models.ClaseInfo;
import java.time.LocalDate; // <-- IMPORTAR
import java.time.format.TextStyle; // <-- IMPORTAR
import java.util.ArrayList; // <-- IMPORTAR
import java.util.HashMap;
import java.util.List; // <-- IMPORTAR
import java.util.Locale; // <-- IMPORTAR
import java.util.Map;

public class EstadisticasService {
    
    /**
     * MÉTODO NUEVO
     * Obtiene las clases programadas para el día de hoy.
     */
    public List<ClaseInfo> getClasesDeHoy() throws SQLException {
        List<ClaseInfo> clasesHoy = new ArrayList<>();
        
        LocalDate today = LocalDate.now();
        String diaSemana = today.getDayOfWeek().getDisplayName(TextStyle.FULL, new Locale("es", "ES"));
        diaSemana = diaSemana.substring(0, 1).toUpperCase() + diaSemana.substring(1);

        if ("Miercoles".equals(diaSemana)) {
            diaSemana = "Miércoles";
        }

        String sql = "SELECT c.id_clase, a.nombre AS actividad, i.nombre AS instructor, " +
                     "       c.hora_inicio, a.duracion " +
                     "FROM clases c " +
                     "JOIN actividades a ON c.id_actividad = a.id_actividad " +
                     "JOIN instructores i ON c.id_instructor = i.id_instructor " +
                     "WHERE c.dia = ? AND c.status = 'confirmado' " +
                     "ORDER BY c.hora_inicio";

        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, diaSemana);
            ResultSet rs = ps.executeQuery();
            
            while (rs.next()) {
                clasesHoy.add(new ClaseInfo(
                    rs.getInt("id_clase"),
                    rs.getString("actividad"),
                    rs.getString("instructor"),
                    diaSemana, 
                    rs.getTime("hora_inicio").toLocalTime(),
                    rs.getInt("duracion")
                ));
            }
        }
        return clasesHoy;
    }
    
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

    public Map<String, Integer> getInscripcionesPorEstado() throws SQLException {
    Map<String, Integer> data = new HashMap<>();
    
    String SQL = "SELECT status, COUNT(status) AS count " +
                 "FROM inscripciones " + // Asumiendo que se llama 'inscripciones'
                 "GROUP BY status"; 
    
    try (Connection conn = Database.getConnection();
         Statement stmt = conn.createStatement();
         ResultSet rs = stmt.executeQuery(SQL)) {
        
        while (rs.next()) {
            data.put(rs.getString("status"), rs.getInt("count"));
        }
    }
    return data;
}

    public Map<String, Integer> getClasesPorInstructor() throws SQLException {
        Map<String, Integer> data = new HashMap<>();
        
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
    
    public Map<String, Map<String, Integer>> getOcupacionPorClase() throws SQLException {
        Map<String, Map<String, Integer>> data = new HashMap<>();

        // Esta consulta asume 'inscripciones' y 'status'
        String SQL = "SELECT " +
                     "c.id_clase, " +
                     "a.nombre AS nombre_actividad, " +
                     "a.aforo AS aforo_maximo, " +
                     "COUNT(CASE WHEN i.status = 'confirmado' THEN 1 END) AS inscripciones_confirmadas " +
                     "FROM clases c " +
                     "JOIN actividades a ON c.id_actividad = a.id_actividad " +
                     "LEFT JOIN inscripciones i ON c.id_clase = i.id_clase " +
                     "GROUP BY c.id_clase, a.nombre, a.aforo";

        try (Connection conn = Database.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(SQL)) {

            while (rs.next()) {
                String claveUnica = rs.getString("nombre_actividad") + " (ID: " + rs.getInt("id_clase") + ")";
                
                Map<String, Integer> ocupacionData = new HashMap<>();
                ocupacionData.put("AFORO", rs.getInt("aforo_maximo"));
                ocupacionData.put("INSCRITOS", rs.getInt("inscripciones_confirmadas"));
                
                data.put(claveUnica, ocupacionData);
            }
        }
        return data;
    }
}