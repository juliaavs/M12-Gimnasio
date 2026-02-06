package com.mycompany.proyectogimnasio.Service;

import com.mycompany.proyectogimnasio.Models.Instructor;
import com.mycompany.proyectogimnasio.Database;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.time.*;



public class InstructorService {
    
    
   
    public List<Instructor> getAllInstructors() throws SQLException {
        List<Instructor> instructores = new ArrayList<>();
        String SQL_SELECT_ALL = "SELECT id_instructor, nombre, apellido, dni, activo, fecha_alta FROM instructores";

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
                
                
                instructor.setNombresClases(getClasesByInstructorId(instructor.getIdInstructor(), conn));
                instructores.add(instructor);
            }
        }
        return instructores;
    }
    
    
    private List<String> getClasesByInstructorId(int idInstructor, Connection conn) throws SQLException {
        List<String> clases = new ArrayList<>();
        
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

    
    public void addInstructor(Instructor instructor) throws SQLException {
        String SQL_INSERT = "INSERT INTO instructores (nombre, apellido, dni, activo, fecha_alta) VALUES (?, ?, ?, ?, ?)";
        
        try (Connection conn = Database.getConnection();
            PreparedStatement ps = conn.prepareStatement(SQL_INSERT, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, instructor.getNombre());
            ps.setString(2, instructor.getApellido());
            ps.setString(3, instructor.getDni());
            ps.setBoolean(4, instructor.isActivo());
            ps.setDate(5, java.sql.Date.valueOf(LocalDate.now()));
            ps.executeUpdate();
          
        }
    }
    
   
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

   
    public void deleteInstructor(int idInstructor) throws SQLException {
       
        String SQL_DELETE = "DELETE FROM instructores WHERE id_instructor = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL_DELETE)) {
            ps.setInt(1, idInstructor);
            ps.executeUpdate();
        }
    }
    
    public boolean cambiarEstadoActivo(int idInstructor, boolean nuevoEstado) throws SQLException {
        String SQL = "UPDATE instructores SET activo = ? WHERE id_instructor = ?";
        
        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(SQL)) {
            
           
            pstmt.setBoolean(1, nuevoEstado);
            
         
            pstmt.setInt(2, idInstructor);
            
            
            return pstmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            System.err.println("Error al cambiar el estado del instructor ID " + idInstructor + ": " + e.getMessage());
            throw e; 
        }
    }
    
    public boolean existsDni(String dni, Integer instructorId) throws SQLException {
    Connection conn = Database.getConnection(); // Asume que tienes un método para obtener la conexión
    PreparedStatement pstmt = null;
    ResultSet rs = null;
    
    
    String sql;
    
    if (instructorId == null) {
       
        sql = "SELECT COUNT(*) FROM instructores WHERE dni = ?";
    } else {
        
        sql = "SELECT COUNT(*) FROM instructores WHERE dni = ? AND id_Instructor != ?";
    }

    try {
       
        pstmt = conn.prepareStatement(sql);
        
        
        pstmt.setString(1, dni);
        
   
        if (instructorId != null) {
            pstmt.setInt(2, instructorId);
        }
        
        rs = pstmt.executeQuery();
        
        if (rs.next()) {
            int count = rs.getInt(1);
            return count > 0; 
        }
        
        return false; 

    } finally {
        
        if (rs != null) rs.close();
        if (pstmt != null) pstmt.close();
        if (conn != null) conn.close(); 
    }
    }
    public int getTotalInstructores() {
        String sql = "SELECT COUNT(*) FROM instructores WHERE activo = TRUE";
        int total = 0;
        try (Connection conn = Database.getConnection(); 
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            if (rs.next()) {
                total = rs.getInt(1);
            }
        } catch (Exception e) {
            System.err.println("Error al obtener el total de instructores: " + e.getMessage());
        }
        return total;
    }
    
    public int getNuevosInstructoresEsteMes() {
        String sql = "SELECT COUNT(*) FROM instructores WHERE YEAR(fecha_alta) = YEAR(CURDATE()) AND MONTH(fecha_alta) = MONTH(CURDATE())";
        int newInstructors = 0;
        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            if (rs.next()) {
                newInstructors = rs.getInt(1);
            }
        } catch (Exception e) {
            System.err.println("Error al obtener nuevos instructores del mes: " + e.getMessage());
        }
        return newInstructors;
    }
}