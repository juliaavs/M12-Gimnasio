package com.mycompany.proyectogimnasio.Service;

import com.mycompany.proyectogimnasio.Database; // Asegúrate de tener tu clase de conexión
import com.mycompany.proyectogimnasio.Models.ClaseInfo;

import java.sql.*;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Servicio para gestionar las operaciones de las clases en la base de datos.
 */
public class ClaseService {

    /**
     * Obtiene una lista de todas las clases confirmadas desde la base de datos.
     * @return Una lista de objetos ClaseInfo.
     */
    public List<ClaseInfo> getClasesProgramadas() {
        List<ClaseInfo> clases = new ArrayList<>();
        // La consulta ahora debe traer el id_clase y la duración
        String sql = "SELECT c.id_clase, a.nombre AS nombre_actividad, i.nombre AS nombre_instructor, " +
                     "c.dia, c.hora_inicio, a.duracion " +
                     "FROM clases c " +
                     "JOIN actividades a ON c.id_actividad = a.id_actividad " +
                     "JOIN instructores i ON c.id_instructor = i.id_instructor " +
                     "WHERE c.status = 'confirmado'";

        try (Connection conn = Database.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                clases.add(new ClaseInfo(
                    rs.getInt("id_clase"),
                    rs.getString("nombre_actividad"),
                    rs.getString("nombre_instructor"),
                    rs.getString("dia").toLowerCase(),
                    rs.getTime("hora_inicio").toLocalTime(),
                    rs.getInt("duracion") // Asumiendo que la duración está en minutos
                ));
            }
        } catch (SQLException e) {
            System.err.println("Error al cargar las clases programadas: " + e.getMessage());
            e.printStackTrace();
        }
        return clases;
    }

    /**
     * Actualiza el día y la hora de inicio de una clase específica en la base de datos.
     * @param idClase El ID de la clase a modificar.
     * @param nuevoDia El nuevo día para la clase (ej. "lunes").
     * @param nuevaHora La nueva hora de inicio.
     * @return true si la actualización fue exitosa, false en caso contrario.
     */
    public boolean actualizarClase(int idClase, String nuevoDia, LocalTime nuevaHora) {
        String sql = "UPDATE clases SET dia = ?, hora_inicio = ? WHERE id_clase = ?";
        
        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, nuevoDia);
            pstmt.setTime(2, Time.valueOf(nuevaHora));
            pstmt.setInt(3, idClase);

            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0; // Si se afectó al menos una fila, fue exitoso

        } catch (SQLException e) {
            System.err.println("Error al actualizar la clase " + idClase + ": " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
}