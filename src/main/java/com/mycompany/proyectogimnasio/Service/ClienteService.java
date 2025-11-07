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
                    rs.getInt("activo") == 1 
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return clientes;
    }

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
        // ** CAMBIO 1: Añadir 'fecha_alta' al SQL y un '?' más **
        String sql = "INSERT INTO clientes(dni, nombre, apellido, password, IBAN, telefono, cod_postal, activo, fecha_alta) VALUES(?,?,?,?,?,?,?,1,?)";
        
        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, cliente.getDni());
            pstmt.setString(2, cliente.getNombre());
            pstmt.setString(3, cliente.getApellido());
            pstmt.setString(4, cliente.getPassword());
            pstmt.setString(5, cliente.getIban());
            pstmt.setString(6, cliente.getTelefono());
            pstmt.setString(7, cliente.getCodPostal());
            // 'activo' (campo 8) se pone a 1 (activo) directamente en el SQL
            
            // ** CAMBIO 2: Añadir la fecha de hoy como parámetro 8 (que es el 9º '?') **
            pstmt.setDate(8, java.sql.Date.valueOf(LocalDate.now())); 
            
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

    public boolean setEstadoCliente(int idCliente, boolean activar) {
        int nuevoValorDB = activar ? 1 : 0; 
        
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
        String sql = "SELECT COUNT(*) FROM clientes WHERE activo = 1";
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