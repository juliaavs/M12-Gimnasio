package com.mycompany.proyectogimnasio.Controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import com.mycompany.proyectogimnasio.App;

public class SidebarController {

    @FXML private Button dashboardLink;
    @FXML private Button clientesLink;
    @FXML private Button instructoresLink;
    @FXML private Button actividadesLink; // BOTÓN NUEVO
    @FXML private Button clasesLink;
    @FXML private Button reservasLink;
    @FXML private Button estadisticasLink;
    @FXML private Button horarioLink;
    @FXML private Button logoutLink;

    @FXML private Label usuarioLabel;
    @FXML private Label rolLabel;

    private String usuarioActual;
    private String rolActual;

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
        actividadesLink.setOnAction(e -> openActividades()); // ACCIÓN NUEVA
        clasesLink.setOnAction(e -> openClases());
        reservasLink.setOnAction(e -> openReservas());
        estadisticasLink.setOnAction(e -> openEstadisticas());
        horarioLink.setOnAction(e -> openHorario());
        logoutLink.setOnAction(e -> handleLogout());
    }
    
    // --- MÉTODO NUEVO PARA ABRIR LA VISTA DE ACTIVIDADES ---
    private void openActividades() {
        try {
            App.showActividades(usuarioActual, rolActual);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private void openDashboard() {
        try { App.showDashboard(usuarioActual, rolActual); } catch (Exception e) { e.printStackTrace(); }
    }

    private void openClientes() {
        try { App.showClientes(usuarioActual, rolActual); } catch (Exception e) { e.printStackTrace(); }
    }

    private void openInstructores() {
        // Llama al método estático que, a su vez, llama al DashboardController para cambiar el centro
        try { 
            App.showInstructores(usuarioActual, rolActual); 
        } catch (Exception e) { 
            e.printStackTrace(); 
        }
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
    
    private void openClases() {
        try { App.showClases(usuarioActual, rolActual); } catch (Exception e) { e.printStackTrace(); }
    }
}