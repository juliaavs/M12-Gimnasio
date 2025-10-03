package com.mycompany.proyectogimnasio.Controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import com.mycompany.proyectogimnasio.*;
public class DashboardController {

    @FXML
    private Label welcomeLabel;

    @FXML
    private Label roleLabel;

    @FXML
    private Button logoutButton;

    public void setUser(String nombre, String rol) {
        welcomeLabel.setText("Bienvenido, " + nombre + "!");
        roleLabel.setText("Rol: " + rol);
    }

    @FXML
    private void handleLogout() {
        try {
            App.showLogin();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
