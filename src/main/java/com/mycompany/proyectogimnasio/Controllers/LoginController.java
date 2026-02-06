package com.mycompany.proyectogimnasio.Controllers;

import com.mycompany.proyectogimnasio.*;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException; // <--- ¡Importación necesaria!

public class LoginController {

    @FXML
    private TextField dniField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private void handleLogin() {
        String dni = dniField.getText();
        String password = passwordField.getText();

        try {
            // Intenta la conexión (Aquí es donde falla la red o credenciales de la BD)
            try (Connection conn = Database.getConnection()) {
                
                // Lógica SQL (no modificada)
                String sql = "SELECT nombre, rol FROM administrador WHERE dni=? AND password=? AND activo=1";
                PreparedStatement stmt = conn.prepareStatement(sql);
                stmt.setString(1, dni);
                stmt.setString(2, password);
                ResultSet rs = stmt.executeQuery();

                if (rs.next()) {
                    String nombre = rs.getString("nombre");
                    String rol = rs.getString("rol");
                    
                    // Lógica de éxito (no modificada)
                    App.showDashboard(nombre, rol);
                    
                } else {
                    // Login fallido por credenciales (no modificado)
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Error");
                    alert.setHeaderText("Login fallido");
                    alert.setContentText("DNI o contraseña incorrecta, o usuario inactivo.");
                    alert.showAndWait();
                }
            }
        // **********************************************
        // Bloque CATCH Modificado para Diagnóstico
        // **********************************************
        } catch (SQLException e) { 
            // Esto captura el error si Database.getConnection() falla (timeout/red).
            
            // Imprimir el error en la consola (para tu diagnóstico):
            System.err.println("ERROR CRÍTICO DE CONEXIÓN O SQL: " + e.getMessage());
            e.printStackTrace();

            // Mostrar el mensaje de error explícito al usuario:
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error de Conexión");
            alert.setHeaderText("Fallo al conectar a la Base de Datos");
            alert.setContentText("La aplicación no pudo establecer conexión con Railway.\n\nMensaje: " + e.getMessage());
            alert.showAndWait();
            
        } catch (Exception e) {
            // Captura cualquier otro error (como fallo al cargar el Dashboard)
            System.err.println("Error Inesperado:");
            e.printStackTrace();
            
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error Inesperado");
            alert.setHeaderText("Ocurrió un error general");
            alert.setContentText("Revisa la terminal para el stack trace del error.");
            alert.showAndWait();
        }
    }
}