package com.mycompany.proyectogimnasio.Service;

import com.mycompany.proyectogimnasio.Database;
import com.mycompany.proyectogimnasio.Models.ClaseInfo;
import com.mycompany.proyectogimnasio.Models.Actividad;
import com.mycompany.proyectogimnasio.Models.Instructor;

import java.sql.*;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class ClaseService {

    public List<ClaseInfo> getClasesProgramadas() {
        List<ClaseInfo> clases = new ArrayList<>();
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
                        rs.getInt("duracion")
                ));
            }
        } catch (SQLException e) {
            System.err.println("Error al cargar las clases programadas: " + e.getMessage());
            e.printStackTrace();
        }
        return clases;
    }

    public boolean actualizarClase(int idClase, String nuevoDia, LocalTime nuevaHora) {
        String sql = "UPDATE clases SET dia = ?, hora_inicio = ? WHERE id_clase = ?";
        
        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, nuevoDia);
            pstmt.setTime(2, Time.valueOf(nuevaHora));
            pstmt.setInt(3, idClase);

            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;

        } catch (SQLException e) {
            System.err.println("Error al actualizar la clase " + idClase + ": " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public List<Actividad> getActividadesDisponibles() {
        List<Actividad> actividades = new ArrayList<>();
        String sql = "SELECT id_actividad, nombre, descripcion, duracion, aforo, activo FROM actividades";
        try (Connection conn = Database.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                actividades.add(new Actividad(
                        rs.getInt("id_actividad"),
                        rs.getString("nombre"),
                        rs.getString("descripcion"),
                        rs.getInt("duracion"),
                        rs.getInt("aforo"),
                        rs.getBoolean("activo")
                ));
            }
        } catch (SQLException e) {
            System.err.println("Error al cargar actividades: " + e.getMessage());
        }
        return actividades;
    }
    
    public List<Instructor> getInstructoresActivos() {
        List<Instructor> instructores = new ArrayList<>();
        String sql = "SELECT id_instructor, nombre, apellido, dni, activo FROM instructores WHERE activo = 1";
        try (Connection conn = Database.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                instructores.add(new Instructor(
                        rs.getInt("id_instructor"),
                        rs.getString("nombre"),
                        rs.getString("apellido"),
                        rs.getString("dni"),
                        rs.getBoolean("activo")
                        
                ));
            }
        } catch (SQLException e) {
            System.err.println("Error al cargar instructores: " + e.getMessage());
        }
        return instructores;
    }

    public boolean crearClase(int idActividad, int idInstructor, String dia, LocalTime horaInicio) {
        String sql = "INSERT INTO clases (id_actividad, id_instructor, dia, hora_inicio, status) VALUES (?, ?, ?, ?, 'confirmado')";
        
        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, idActividad);
            pstmt.setInt(2, idInstructor);
            pstmt.setString(3, dia);
            pstmt.setTime(4, Time.valueOf(horaInicio));
            
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;

        } catch (SQLException e) {
            System.err.println("Error al crear la clase: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
}