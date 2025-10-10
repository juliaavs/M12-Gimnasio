
package com.mycompany.proyectogimnasio.Service;

import com.mycompany.proyectogimnasio.Database;
import com.mycompany.proyectogimnasio.Models.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;


public class InstructorService {
    
    private List<Clase> listaClases = new ArrayList<>();
    public ObservableList<Instructor> getAll() {
        ObservableList<Instructor> lista = FXCollections.observableArrayList();
        String sql = "SELECT * FROM instructores";
    

        try (Connection conn = Database.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                lista.add(new Instructor(
                        rs.getInt("id_instructor"),
                        rs.getString("nombre"),
                        rs.getString("apellido"),
                        rs.getString("dni"),
                        rs.getBoolean("activo")
                ));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return lista;
    }

    public void add(Instructor instructor) {
        String sql = "INSERT INTO instructores (id_instructor, nombre, apellido, dni, activo) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, instructor.getIdInstructor());
            stmt.setString(2, instructor.getNombre());
            stmt.setString(3, instructor.getApellido());
            stmt.setString(4, instructor.getDni());
            stmt.setBoolean(5, instructor.isActivo());
            stmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void update(Instructor instructor) {
        String sql = "UPDATE instructores SET nombre=?, apellido=?, dni=?, activo=? WHERE id_instructor=?";
        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, instructor.getNombre());
            stmt.setString(2, instructor.getApellido());
            stmt.setString(3, instructor.getDni());
            stmt.setBoolean(4, instructor.isActivo());
            stmt.setInt(5, instructor.getIdInstructor());
            stmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void delete(int idInstructor) {
        String sql = "DELETE FROM instructores WHERE id_instructor=?";
        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idInstructor);
            stmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    
    }
    
    public List<String> getClasesPorInstructorId(int idInstructor) {
    List<String> clases = new ArrayList<>();
    for (Clase c : listaClases) { // listaClases es tu lista de todas las clases
        if (c.getIdInstructor() == idInstructor) {
            // Opción 1: solo mostrar el nombre de la actividad
            clases.add(c.getNombreActividad());

            // Opción 2: mostrar día, hora y nombre
            // clases.add(c.getDia() + " " + c.getHoraInicio() + " - " + c.getNombreActividad());
        }
    }
    return clases;
}
}