package com.mycompany.proyectogimnasio.Controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import com.mycompany.proyectogimnasio.App;

public class SidebarController {

    
    @FXML private Button dashboardLink;
    @FXML private Button clientesLink;
    @FXML private Button instructoresLink;
    @FXML private Button reservasLink;
    @FXML private Button estadisticasLink;
    @FXML private Button horarioLink;
    @FXML private Button logoutLink;

    private String usuarioActual;
    private String rolActual;

    @FXML private Label usuarioLabel;
    @FXML private Label rolLabel;

    public void setUser(String usuario, String rol) {
        this.usuarioActual = usuario;
        this.rolActual = rol;
        usuarioLabel.setText(usuario);
        rolLabel.setText(rol);
    }



    @FXML
    private void initialize() {
        dashboardLink.setOnAction(e -> openDashboard());
        clientesLink.setOnAction(e -> openClientes());
        instructoresLink.setOnAction(e -> openInstructores());
        reservasLink.setOnAction(e -> openReservas());
        estadisticasLink.setOnAction(e -> openEstadisticas());
        horarioLink.setOnAction(e -> openHorario());
        logoutLink.setOnAction(e -> handleLogout());
    }


    
    private void openDashboard() {
        try {
            App.showDashboard(usuarioActual, rolActual); // ahora solo cambia el centro
        } catch (Exception e) { e.printStackTrace(); }
    }


    private void openClientes() {
        try { App.showClientes(usuarioActual, rolActual); } catch (Exception e) { e.printStackTrace(); }
    }

    private void openInstructores() {
        try { App.showInstructores(usuarioActual, rolActual); } catch (Exception e) { e.printStackTrace(); }
    }

    private void openReservas() {
        try { App.showReservas(usuarioActual, rolActual); } catch (Exception e) { e.printStackTrace(); }
    }

    private void openEstadisticas() {
        try { App.showEstadisticas(usuarioActual, rolActual); } catch (Exception e) { e.printStackTrace(); }
    }

    private void openHorario() {
        try { App.showHorario(usuarioActual, rolActual); } catch (Exception e) { e.printStackTrace(); }
    }

    private void handleLogout() {
        try { App.showLogin(); } catch (Exception e) { e.printStackTrace(); }
    }
}
