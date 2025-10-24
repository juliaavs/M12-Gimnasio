package com.mycompany.proyectogimnasio.Service;

import com.mycompany.proyectogimnasio.Database;
import com.mycompany.proyectogimnasio.Models.Admin;
import java.sql.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class AdminService {

    public ObservableList<Admin> getAllAdmins() {
        ObservableList<Admin> admins = FXCollections.observableArrayList();
        // <-- CAMBIO CLAVE: Añadido 'activo' al SELECT -->
        String sql = "SELECT id_admin, dni, nombre, apellido, password, rol, activo FROM administrador";
        try (Connection conn = Database.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                admins.add(new Admin(
                    rs.getInt("id_admin"),
                    rs.getString("dni"),
                    rs.getString("nombre"),
                    rs.getString("apellido"),
                    rs.getString("password"),
                    rs.getString("rol"),
                    rs.getBoolean("activo") // <-- CAMBIO CLAVE
                ));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return admins;
    }

    public boolean agregarAdmin(Admin admin) {
        // <-- CAMBIO CLAVE: Añadido 'activo' al INSERT -->
        String sql = "INSERT INTO administrador (dni, nombre, apellido, password, rol, activo) VALUES(?,?,?,?,?,?)";
        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, admin.getDni());
            pstmt.setString(2, admin.getNombre());
            pstmt.setString(3, admin.getApellido());
            pstmt.setString(4, admin.getPassword());
            pstmt.setString(5, admin.getRol());
            pstmt.setBoolean(6, admin.isActivo()); // <-- CAMBIO CLAVE
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }
    
    public boolean actualizarAdmin(Admin admin) {
        // <-- CAMBIO CLAVE: Añadido 'activo' al UPDATE -->
        String sql = "UPDATE administrador SET dni = ?, nombre = ?, apellido = ?, password = ?, rol = ?, activo = ? WHERE id_admin = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, admin.getDni());
            pstmt.setString(2, admin.getNombre());
            pstmt.setString(3, admin.getApellido());
            pstmt.setString(4, admin.getPassword());
            pstmt.setString(5, admin.getRol());
            pstmt.setBoolean(6, admin.isActivo()); // <-- CAMBIO CLAVE
            pstmt.setInt(7, admin.getIdAdmin());
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    public boolean eliminarAdmin(int idAdmin) {
        String sql = "DELETE FROM administradores WHERE id_admin = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, idAdmin);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }
}