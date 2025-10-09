package com.mycompany.proyectogimnasio.Controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class EstadisticasController {

    @FXML
    private Label welcomeLabel;

    @FXML
    private Label roleLabel;

    public void setUser(String nombre, String rol) {
        welcomeLabel.setText("Bienvenido, " + nombre + "!");
        roleLabel.setText("Rol: " + rol);
    }
}
