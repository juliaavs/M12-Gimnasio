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
     * **MÉTODO QUE FALTABA**
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
        // **ACTUALIZADO**: Añadidos los nuevos campos
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
        // **ACTUALIZADO**: Añadidos los nuevos campos (sin incluir 'activo')
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
     * **MÉTODO ACTUALIZADO**
     * Desactiva un cliente poniendo su estado 'activo' a 1.
     */
    public boolean desactivarCliente(int idCliente) {
        String sql = "UPDATE clientes SET activo = 1 WHERE id_cliente = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, idCliente);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public int getTotalClientes() {
        String sql = "SELECT COUNT(*) FROM clientes WHERE activo = FALSE";
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
        // En Java: calcular la fecha de inicio de la semana actual (Lunes)
        LocalDate today = LocalDate.now();
        // Encuentra el último lunes (o el lunes de esta semana)
        LocalDate startOfWeek = today.with(DayOfWeek.MONDAY);

        // Consulta SQL para contar clientes registrados desde el inicio de la semana.
        // Usando un marcador de posición (?) para la fecha en la consulta JDBC.
        String sql = "SELECT COUNT(*) FROM clientes WHERE fecha_registro >= ?";
        int newClients = 0;

        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            // Convertir LocalDate a java.sql.Date y establecer el parámetro
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