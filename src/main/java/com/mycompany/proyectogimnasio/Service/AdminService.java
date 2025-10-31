package com.mycompany.proyectogimnasio.Service;

import com.mycompany.proyectogimnasio.Database;
import com.mycompany.proyectogimnasio.Models.Admin;
import java.sql.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class AdminService {

    public ObservableList<Admin> getAllAdmins() {
        ObservableList<Admin> admins = FXCollections.observableArrayList();
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
                    rs.getInt("activo") == 1 // **CAMBIO CLAVE: 1 es true (Activo)**
                ));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return admins;
    }

    public boolean agregarAdmin(Admin admin) {
        // **CAMBIO CLAVE: Se inserta '1' (Activo) por defecto**
        String sql = "INSERT INTO administrador(dni, nombre, apellido, password, rol, activo) VALUES(?,?,?,?,?,1)";
        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, admin.getDni());
            pstmt.setString(2, admin.getNombre());
            pstmt.setString(3, admin.getApellido());
            pstmt.setString(4, admin.getPassword());
            pstmt.setString(5, admin.getRol());
            // El 'activo' (campo 6) se pone a 1 (activo) directamente en el SQL
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }
    
    public boolean actualizarAdmin(Admin admin) {
        // **CAMBIO CLAVE: Ya no se actualiza 'activo' aquí**
        String sql = "UPDATE administrador SET dni = ?, nombre = ?, apellido = ?, password = ?, rol = ? WHERE id_admin = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, admin.getDni());
            pstmt.setString(2, admin.getNombre());
            pstmt.setString(3, admin.getApellido());
            pstmt.setString(4, admin.getPassword());
            pstmt.setString(5, admin.getRol());
            pstmt.setInt(6, admin.getIdAdmin());
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    /**
     * **NUEVO MÉTODO**
     * Actualiza el estado de 'activo' de un administrador.
     * @param idAdmin El ID del admin a modificar.
     * @param nuevoEstado El nuevo estado (1 para Activo, 0 para Inactivo).
     */
    public boolean actualizarEstadoAdmin(int idAdmin, int nuevoEstado) {
        String sql = "UPDATE administrador SET activo = ? WHERE id_admin = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, nuevoEstado);
            pstmt.setInt(2, idAdmin);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    public boolean eliminarAdmin(int idAdmin) {
        String sql = "DELETE FROM administrador WHERE id_admin = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, idAdmin);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }
}