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
        String sql = "SELECT COUNT(*) FROM inscripciones WHERE dia_reserva = ?";
        return getReservasPorFecha(today, sql);
    }

    /**
     * Obtiene el número de reservas realizadas ayer.
     */
    public int getReservasAyer() {
        LocalDate yesterday = LocalDate.now().minusDays(1);
        String sql = "SELECT COUNT(*) FROM inscripciones WHERE dia_reserva = ?";
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
    
    // Consulta SQL compleja usando JOIN para obtener:
    // 1. Datos de la Inscripción (i)
    // 2. Nombre de la Actividad (a) a través de la Clase (c)
    // 3. DNI del Cliente (cl) <--- ¡CAMBIADO!
    String sql = "SELECT " +
                    "i.id_clase, i.id_cliente, i.status, i.dia_reserva, " +
                    "a.nombre AS nombre_clase, " + // nombre de la actividad
                    "cl.dni AS dni_cliente " + // DNI del cliente <--- ¡CAMBIADO!
                    "FROM inscripciones i " +
                    // JOIN 1: De inscripciones a Clases
                    "INNER JOIN clases c ON i.id_clase = c.id_clase " +
                    // JOIN 2: De Clases a Actividades (para obtener el nombre)
                    "INNER JOIN actividades a ON c.id_actividad = a.id_actividad " +
                    // JOIN 3: De inscripciones a Clientes
                    "INNER JOIN clientes cl ON i.id_cliente = cl.id_cliente";
    
    try (Connection conn = Database.getConnection(); 
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql)) {

        while (rs.next()) {
            int idClase = rs.getInt("id_clase");
            int idCliente = rs.getInt("id_cliente");
            String status = rs.getString("status");
            // Asegúrate de que el campo 'dia_reserva' en la BD permite obtener un LocalDate.
            // Si es un SQL DATE, está bien. Si es TIMESTAMP, podrías necesitar .toLocalDateTime().toLocalDate()
            LocalDate diaReserva = rs.getDate("dia_reserva").toLocalDate(); 
            
            // Mapeo de los datos obtenidos por el JOIN
            String nombreClase = rs.getString("nombre_clase");
            String dniCliente = rs.getString("dni_cliente"); // <--- ¡CAMBIADO!

            // Asegúrate de que el constructor de Reservas se actualice para aceptar el DNI.
            Reservas reserva = new Reservas(idClase, idCliente, status, diaReserva, 
                                            nombreClase, dniCliente); // <--- ¡CAMBIADO!
            reservasList.add(reserva);
        }
    } catch (SQLException e) {
        System.err.println("Error SQL en getAll() con JOIN: " + e.getMessage());
        e.printStackTrace();
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
