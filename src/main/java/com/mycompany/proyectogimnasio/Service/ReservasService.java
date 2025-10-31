package com.mycompany.proyectogimnasio.Service;

import com.mycompany.proyectogimnasio.Database; 
import com.mycompany.proyectogimnasio.Models.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;


public class ReservasService {

    /**
     * Obtiene el número de reservas realizadas hoy.
     */
    public int getReservasHoy() {
        LocalDate today = LocalDate.now();
        // Esta consulta cuenta todas las reservas cuya fecha sea igual a la de hoy.
        String sql = "SELECT COUNT(*) FROM inscripciones WHERE DATE(fecha_reserva) = ?";
        return getReservasPorFecha(today, sql);
    }

    /**
     * Obtiene el número de reservas realizadas ayer.
     */
    public int getReservasAyer() {
        LocalDate yesterday = LocalDate.now().minusDays(1);
        String sql = "SELECT COUNT(*) FROM inscripciones WHERE DATE(fecha_reserva) = ?";
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
}
