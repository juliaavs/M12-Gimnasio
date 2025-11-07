package com.mycompany.proyectogimnasio.Service;

import com.mycompany.proyectogimnasio.Database;
import com.mycompany.proyectogimnasio.Models.Cliente;
import java.sql.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.DayOfWeek;
import java.time.LocalDate;


public class ClienteService {

    public ObservableList<Cliente> getAllClientes() {
        ObservableList<Cliente> clientes = FXCollections.observableArrayList();
        // **ACTUALIZADO**: Añadidos los nuevos campos
        String sql = "SELECT id_cliente, dni, nombre, apellido, password, IBAN, telefono, cod_postal, activo FROM clientes";
        try (Connection conn = Database.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                clientes.add(new Cliente(
                    rs.getInt("id_cliente"),
                    rs.getString("dni"),
                    rs.getString("nombre"),
                    rs.getString("apellido"),
                    rs.getString("password"),
                    rs.getString("IBAN"),
                    rs.getString("telefono"),
                    rs.getString("cod_postal"),
                    rs.getInt("activo") == 0 // 0 = Activo (true)
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return clientes;
    }

    /**
     * Comprueba si un DNI ya existe en la base de datos.
     * @param dni El DNI a comprobar.
     * @return true si el DNI ya existe, false en caso contrario.
     */
    public boolean dniExiste(String dni) {
        String sql = "SELECT COUNT(*) FROM clientes WHERE dni = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, dni);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean agregarCliente(Cliente cliente) {
        String sql = "INSERT INTO clientes(dni, nombre, apellido, password, IBAN, telefono, cod_postal, activo) VALUES(?,?,?,?,?,?,?,0)";
        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, cliente.getDni());
            pstmt.setString(2, cliente.getNombre());
            pstmt.setString(3, cliente.getApellido());
            pstmt.setString(4, cliente.getPassword());
            pstmt.setString(5, cliente.getIban());
            pstmt.setString(6, cliente.getTelefono());
            pstmt.setString(7, cliente.getCodPostal());
            // 'activo' se inserta como 0 (activo) por defecto
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public boolean actualizarCliente(Cliente cliente) {
        String sql = "UPDATE clientes SET dni = ?, nombre = ?, apellido = ?, password = ?, IBAN = ?, telefono = ?, cod_postal = ? WHERE id_cliente = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, cliente.getDni());
            pstmt.setString(2, cliente.getNombre());
            pstmt.setString(3, cliente.getApellido());
            pstmt.setString(4, cliente.getPassword());
            pstmt.setString(5, cliente.getIban());
            pstmt.setString(6, cliente.getTelefono());
            pstmt.setString(7, cliente.getCodPostal());
            pstmt.setInt(8, cliente.getIdCliente());
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * **MÉTODO NUEVO** (Reemplaza a desactivarCliente)
     * Activa (activo=0) o desactiva (activo=1) un cliente.
     * @param idCliente El ID del cliente a modificar.
     * @param activar true para activar (poner a 0), false para desactivar (poner a 1).
     * @return true si tuvo éxito, false si no.
     */
    public boolean setEstadoCliente(int idCliente, boolean activar) {
        // Si 'activar' es true, 'nuevoValorDB' será 0.
        // Si 'activar' es false, 'nuevoValorDB' será 1.
        int nuevoValorDB = activar ? 0 : 1; 
        
        String sql = "UPDATE clientes SET activo = ? WHERE id_cliente = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, nuevoValorDB);
            pstmt.setInt(2, idCliente);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public int getTotalClientes() {
        // Asumiendo que 0 es activo (activo=false en la BD)
        String sql = "SELECT COUNT(*) FROM clientes WHERE activo = 0";
        int total = 0;
        try (Connection conn = Database.getConnection(); 
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            if (rs.next()) {
                total = rs.getInt(1);
            }
        } catch (Exception e) {
            System.err.println("Error al obtener el total de clientes: " + e.getMessage());
        }
        return total;
    }
    
    public int getNuevosClientesEstaSemana() {
        LocalDate today = LocalDate.now();
        LocalDate startOfWeek = today.with(DayOfWeek.MONDAY);

        // Asumiendo que tienes una columna 'fecha_alta'
        String sql = "SELECT COUNT(*) FROM clientes WHERE fecha_alta >= ?";
        int newClients = 0;

        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setDate(1, java.sql.Date.valueOf(startOfWeek)); 
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    newClients = rs.getInt(1);
                }
            }
        } catch (Exception e) {
            System.err.println("Error al obtener nuevos clientes de la semana: " + e.getMessage());
        }
        return newClients;
    }
}