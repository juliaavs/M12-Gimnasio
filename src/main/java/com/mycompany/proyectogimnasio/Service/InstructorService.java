
package com.mycompany.proyectogimnasio.Service;

import com.mycompany.proyectogimnasio.Models.Instructor;
import com.mycompany.proyectogimnasio.Database;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;



public class InstructorService {
    
    
    // --- R (Read): Listar todos los instructores con sus clases ---
    public List<Instructor> getAllInstructors() throws SQLException {
        List<Instructor> instructores = new ArrayList<>();
        String SQL_SELECT_ALL = "SELECT id_instructor, nombre, apellido, dni, activo FROM instructores";

        try (Connection conn = Database.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(SQL_SELECT_ALL)) {
            
            while (rs.next()) {
                Instructor instructor = new Instructor(
                    rs.getInt("id_instructor"),
                    rs.getString("nombre"),
                    rs.getString("apellido"),
                    rs.getString("dni"),
                    rs.getBoolean("activo")
                );
                
                // Cargar las clases asociadas a este instructor
                instructor.setNombresClases(getClasesByInstructorId(instructor.getIdInstructor(), conn));
                instructores.add(instructor);
            }
        }
        return instructores;
    }
    
    // Método auxiliar para obtener los nombres de las clases de un instructor
    private List<String> getClasesByInstructorId(int idInstructor, Connection conn) throws SQLException {
        List<String> clases = new ArrayList<>();
        // JOIN para obtener la actividad (nombre) a través de la clase (id_actividad)
        String SQL_CLASES = "SELECT a.nombre FROM clases c JOIN actividades a ON c.id_actividad = a.id_actividad WHERE c.id_instructor = ?";

        try (PreparedStatement ps = conn.prepareStatement(SQL_CLASES)) {
            ps.setInt(1, idInstructor);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    clases.add(rs.getString("nombre"));
                }
            }
        }
        return clases;
    }

    // --- C (Create): Insertar un nuevo instructor ---
    public void addInstructor(Instructor instructor) throws SQLException {
        String SQL_INSERT = "INSERT INTO instructores (nombre, apellido, dni, activo) VALUES (?, ?, ?, ?)";
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL_INSERT, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, instructor.getNombre());
            ps.setString(2, instructor.getApellido());
            ps.setString(3, instructor.getDni());
            ps.setBoolean(4, instructor.isActivo());
            ps.executeUpdate();
            
            // Opcional: obtener el ID generado si se necesita
            // try (ResultSet rs = ps.getGeneratedKeys()) {
            //     if (rs.next()) {
            //         instructor.setIdInstructor(rs.getInt(1));
            //     }
            // }
        }
    }
    
    // --- U (Update): Actualizar un instructor existente ---
    public void updateInstructor(Instructor instructor) throws SQLException {
        String SQL_UPDATE = "UPDATE instructores SET nombre = ?, apellido = ?, dni = ?, activo = ? WHERE id_instructor = ?";
        try (Connection conn = Database.getConnection();

            PreparedStatement ps = conn.prepareStatement(SQL_UPDATE)) {
            ps.setString(1, instructor.getNombre());
            ps.setString(2, instructor.getApellido());
            ps.setString(3, instructor.getDni());
            ps.setBoolean(4, instructor.isActivo());
            ps.setInt(5, instructor.getIdInstructor());
            ps.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();

        }
    }

    // --- D (Delete): Eliminar un instructor ---
    // ¡CUIDADO! La tabla `clases` tiene una clave foránea a `instructores`. 
    // Necesitas eliminar o reasignar las clases asociadas antes de eliminar al instructor.
    public void deleteInstructor(int idInstructor) throws SQLException {
        // En un entorno real, primero se manejarían las dependencias (ej. reasignar clases)
        // Por simplicidad, asumiremos que no hay clases asociadas o se han gestionado.
        String SQL_DELETE = "DELETE FROM instructores WHERE id_instructor = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL_DELETE)) {
            ps.setInt(1, idInstructor);
            ps.executeUpdate();
        }
    }
}
