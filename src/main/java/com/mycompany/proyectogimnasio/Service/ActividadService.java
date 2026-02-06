
package com.mycompany.proyectogimnasio.Service;

import com.mycompany.proyectogimnasio.Models.Actividad;
import com.mycompany.proyectogimnasio.Database;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ActividadService {
    
    public List<Actividad> obtenerTodasActividades() {
        List<Actividad> actividades = new ArrayList<>();
        String sql = "SELECT id_actividad, nombre, descripcion, duracion, aforo, activo FROM actividades";

        try (Connection conn = Database.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                int id = rs.getInt("id_actividad");
                String nombre = rs.getString("nombre");
                String descripcion = rs.getString("descripcion");
                int duracion = rs.getInt("duracion");
                int aforo = rs.getInt("aforo");
                boolean activo = rs.getBoolean("activo");
                
                
                // Creamos el objeto Actividad y lo añadimos a la lista
                actividades.add(new Actividad(id, nombre, descripcion, duracion, aforo, activo));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return actividades;
    }

    /**
     * Crea una nueva actividad en la base de datos.
     */
    public boolean agregarActividad(Actividad actividad) {
        String sql = "INSERT INTO actividades (nombre, descripcion, duracion, aforo, activo) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = Database.getConnection();
             // Añadimos Statement.RETURN_GENERATED_KEYS si quieres recuperar el ID generado
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, actividad.getNombre());
            pstmt.setString(2, actividad.getDescripcion());
            pstmt.setInt(3, actividad.getDuracion());
            pstmt.setInt(4, actividad.getAforo());
            pstmt.setBoolean(5, actividad.isActivo()); // Guarda el booleano

            int affectedRows = pstmt.executeUpdate();
            
            return affectedRows > 0;
            
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Actualiza una actividad existente.
     */
    public boolean actualizarActividad(Actividad actividad) {
        String sql = "UPDATE actividades SET nombre = ?, descripcion = ?, duracion = ?, aforo = ?, activo = ? WHERE id_actividad = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, actividad.getNombre());
            pstmt.setString(2, actividad.getDescripcion());
            pstmt.setInt(3, actividad.getDuracion());
            pstmt.setInt(4, actividad.getAforo());
            pstmt.setBoolean(5, actividad.isActivo()); // Actualiza el booleano
            pstmt.setInt(6, actividad.getIdActividad()); // ID para el WHERE
            
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
            
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Elimina una actividad por su ID.
     */
    public boolean eliminarActividad(int idActividad) {
        String sql = "DELETE FROM actividades WHERE id_actividad = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, idActividad);
            
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
            
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
    

