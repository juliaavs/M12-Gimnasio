package com.mycompany.proyectogimnasio.Service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import com.mycompany.proyectogimnasio.Database;

public class NotificacionService {

    public boolean enviarNotificacionBienvenida(int idCliente, String nombre) {
        String sql = "INSERT INTO notificaciones (id_cliente, descripcion, fecha_notificacion) VALUES (?, ?, ?)";
        String mensaje = "¡Hola " + nombre + "! Bienvenido/a. Tu contraseña inicial es tu DNI. Te recomendamos cambiarla lo antes posible desde tu perfil por seguridad.";

        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, idCliente);
            pstmt.setString(2, mensaje);
            pstmt.setTimestamp(3, new Timestamp(System.currentTimeMillis()));

            return pstmt.executeUpdate() > 0;
        } catch (Exception e) {
            System.err.println("Error al enviar notificación: " + e.getMessage());
            return false;
        }
    }
    
    public void enviarNotificacionCancelacionMasiva(int idClase, String nombreClase, String dia, String hora) {
        // Buscamos los IDs de los clientes con reserva en esa clase
        String sqlClientes = "SELECT id_cliente FROM inscripciones WHERE id_clase = ? AND status = 'confirmado'";
        String sqlInsert = "INSERT INTO notificaciones (id_cliente, descripcion, fecha_notificacion) VALUES (?, ?, ?)";
        String mensaje = "AVISO: La clase de " + nombreClase + " del " + dia + " a las " + hora + " ha sido cancelada.";

        try (Connection conn = Database.getConnection();
             PreparedStatement psSelect = conn.prepareStatement(sqlClientes)) {

            psSelect.setInt(1, idClase);
            ResultSet rs = psSelect.executeQuery();

            try (PreparedStatement psInsert = conn.prepareStatement(sqlInsert)) {
                while (rs.next()) {
                    psInsert.setInt(1, rs.getInt("id_cliente"));
                    psInsert.setString(2, mensaje);
                    psInsert.setTimestamp(3, new Timestamp(System.currentTimeMillis()));
                    psInsert.addBatch(); // Usamos batch para mayor eficiencia
                }
                psInsert.executeBatch();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    public void enviarNotificacionCancelacionActividad(int idActividad, String nombreActividad) {
        // Buscamos los IDs de los clientes inscritos en CUALQUIER clase de esa actividad
        String sqlClientes = "SELECT DISTINCT i.id_cliente FROM inscripciones i " +
                            "JOIN clases c ON i.id_clase = c.id_clase " +
                            "WHERE c.id_actividad = ? AND i.status = 'confirmado'";

        String sqlInsert = "INSERT INTO notificaciones (id_cliente, descripcion, fecha_notificacion) VALUES (?, ?, ?)";
        String mensaje = "AVISO IMPORTANTE: La actividad '" + nombreActividad + "' ha sido cancelada permanentemente. " +
                         "Tus reservas en las clases de esta actividad han sido anuladas.";

        try (Connection conn = Database.getConnection();
             PreparedStatement psSelect = conn.prepareStatement(sqlClientes)) {

            psSelect.setInt(1, idActividad);
            ResultSet rs = psSelect.executeQuery();

            try (PreparedStatement psInsert = conn.prepareStatement(sqlInsert)) {
                while (rs.next()) {
                    psInsert.setInt(1, rs.getInt("id_cliente"));
                    psInsert.setString(2, mensaje);
                    psInsert.setTimestamp(3, new Timestamp(System.currentTimeMillis()));
                    psInsert.addBatch();
                }
                psInsert.executeBatch();
            }
        } catch (SQLException e) {
            System.err.println("Error al notificar cancelación de actividad: " + e.getMessage());
        }
    }
}
