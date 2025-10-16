// Service/ClaseService.java
package com.mycompany.proyectogimnasio.Service;

import com.mycompany.proyectogimnasio.Database;
import com.mycompany.proyectogimnasio.Models.ClaseInfo;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException; // Es buena práctica importar esta
import java.sql.Statement;
import java.sql.Time;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

// ... (tus imports)

public class ClaseService {

    public List<ClaseInfo> getClasesProgramadas() {
        List<ClaseInfo> clases = new ArrayList<>();
        
        // Consulta SQL simplificada SIN la sala
        String sql = "SELECT a.nombre AS nombre_actividad, i.nombre AS nombre_instructor, c.dia, c.hora_inicio, a.duracion " +
                     "FROM clases c " +
                     "JOIN actividades a ON c.id_actividad = a.id_actividad " +
                     "JOIN instructores i ON c.id_instructor = i.id_instructor " +
                     "WHERE c.status = 'confirmado'";

        try (Connection conn = Database.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                String nombreActividad = rs.getString("nombre_actividad");
                String nombreInstructor = rs.getString("nombre_instructor");
                String dia = rs.getString("dia").toLowerCase();
                Time horaSql = rs.getTime("hora_inicio");
                LocalTime horaInicio = horaSql.toLocalTime();
                int duracion = rs.getInt("duracion");

                // Creación del objeto SIN la sala
                clases.add(new ClaseInfo(nombreActividad, nombreInstructor, dia, horaInicio, duracion));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return clases;
    }
}