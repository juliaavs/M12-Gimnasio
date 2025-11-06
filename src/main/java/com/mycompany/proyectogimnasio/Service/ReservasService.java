package com.mycompany.proyectogimnasio.Service;

import com.mycompany.proyectogimnasio.Database; 
import com.mycompany.proyectogimnasio.Models.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;



public class ReservasService {
    
    

    /**
     * Obtiene el número de reservas realizadas hoy.
     */
    public int getReservasHoy() {
        LocalDate today = LocalDate.now();
        // Esta consulta cuenta todas las reservas cuya fecha sea igual a la de hoy.
        String sql = "SELECT COUNT(*) FROM inscripciones WHERE DATE(dia_reserva) = ?";
        return getReservasPorFecha(today, sql);
    }

    /**
     * Obtiene el número de reservas realizadas ayer.
     */
    public int getReservasAyer() {
        LocalDate yesterday = LocalDate.now().minusDays(1);
        String sql = "SELECT COUNT(*) FROM inscripciones WHERE DATE(dia_reserva) = ?";
        return getReservasPorFecha(yesterday, sql);
    }

    /**
     * Método helper genérico para ejecutar la consulta de reservas por fecha.
     */
    private int getReservasPorFecha(LocalDate date, String sql) {
        int count = 0;
        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            // Establecer el parámetro de la fecha
            pstmt.setDate(1, java.sql.Date.valueOf(date)); 
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    count = rs.getInt(1);
                }
            }
        } catch (Exception e) {
            System.err.println("Error al obtener reservas para la fecha " + date + ": " + e.getMessage());
        }
        return count;
    }
    
    public List<Reservas> getAll() {
        List<Reservas> reservasList = new ArrayList<>();
        // Sentencia SQL para seleccionar todos los campos relevantes
        String sql = "SELECT * FROM inscripciones"; 
        
        // Uso de try-with-resources para asegurar el cierre automático de los recursos (Connection, Statement, ResultSet)
        try (Connection conn = Database.getConnection(); // Asume que esta línea funciona
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            // 1. Itera sobre cada fila (registro) devuelta por la consulta
            while (rs.next()) {
                
                // 2. Extrae los datos del ResultSet usando los nombres de las columnas
                int idClase = rs.getInt("id_clase");
                int idCliente = rs.getInt("id_cliente");
                String status = rs.getString("status");
                
                // 3. Mapea java.sql.Date a java.time.LocalDate, que es el tipo de tu modelo
                LocalDate diaReserva = rs.getDate("dia_reserva").toLocalDate(); 

                // 4. Crea el objeto Reservas con el constructor completo
                Reservas reserva = new Reservas(idClase, idCliente, status, diaReserva);
                reservasList.add(reserva);
            }
        } catch (SQLException e) {
            System.err.println("Error SQL al obtener todas las reservas: " + e.getMessage());
            e.printStackTrace();
            // Retorna una lista vacía si falla la conexión o la consulta
        }
        return reservasList;
    }
    
    public boolean updateStatus(int idClase, int idCliente, String newStatus) {
    String sql = "UPDATE inscripciones SET status = ? WHERE id_clase = ? AND id_cliente = ?";
    
    // Asumiendo que usas JDBC
    try (Connection conn = Database.getConnection();
         PreparedStatement ps = conn.prepareStatement(sql)) {
        
        ps.setString(1, newStatus);
        ps.setInt(2, idClase);
        ps.setInt(3, idCliente);
        
        int rowsAffected = ps.executeUpdate();
        return rowsAffected > 0;
        
    } catch (Exception e) {
        e.printStackTrace();
        return false;
    }
    }
}
