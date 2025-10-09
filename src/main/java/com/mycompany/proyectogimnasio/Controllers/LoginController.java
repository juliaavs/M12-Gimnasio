package com.mycompany.proyectogimnasio.Controllers;

import com.mycompany.proyectogimnasio.*;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class LoginController {

    @FXML
    private TextField dniField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private void handleLogin() {
        String dni = dniField.getText();
        String password = passwordField.getText();

        try (Connection conn = Database.getConnection()) {
            String sql = "SELECT nombre, rol FROM administrador WHERE dni=? AND password=? AND activo=1";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, dni);
            stmt.setString(2, password);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                String nombre = rs.getString("nombre");
                String rol = rs.getString("rol");
                App.showDashboard(nombre, rol);
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText("Login fallido");
                alert.setContentText("DNI o contraseña incorrecta, o usuario inactivo.");
                alert.showAndWait();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
