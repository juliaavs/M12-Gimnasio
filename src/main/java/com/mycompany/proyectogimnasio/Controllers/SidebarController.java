package com.mycompany.proyectogimnasio.Controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import com.mycompany.proyectogimnasio.App;

public class SidebarController {

    @FXML private Button dashboardLink;
    @FXML private Button clientesLink;
    @FXML private Button instructoresLink;
    @FXML private Button actividadesLink;
    @FXML private Button clasesLink;
    @FXML private Button reservasLink;
    @FXML private Button estadisticasLink;
    @FXML private Button horarioLink;
    @FXML private Button administradoresLink; // Botón para la nueva sección
    @FXML private Button logoutLink;

    @FXML private Label usuarioLabel;
    @FXML private Label rolLabel;

    private String usuarioActual;
    private String rolActual;

    /**
     * Recibe los datos del usuario y actualiza la UI, incluyendo la visibilidad de los botones.
     */
    public void setUser(String usuario, String rol) {
        this.usuarioActual = usuario;
        this.rolActual = rol;
        usuarioLabel.setText(usuario);
        rolLabel.setText(rol);

        // --- **LA CORRECCIÓN DEFINITIVA** ---
        // 1. Comprueba si el rol es exactamente "superadmin" (ignorando mayúsculas/minúsculas).
        boolean esSuperAdmin = "superadmin".equalsIgnoreCase(rol);
        
        // 2. Establece la visibilidad del botón basándose en el resultado.
        administradoresLink.setVisible(esSuperAdmin);
        administradoresLink.setManaged(esSuperAdmin); // Importante para que no ocupe espacio si está oculto
    }

    @FXML
    private void initialize() {
        dashboardLink.setOnAction(e -> openDashboard());
        clientesLink.setOnAction(e -> openClientes());
        instructoresLink.setOnAction(e -> openInstructores());
        actividadesLink.setOnAction(e -> openActividades());
        clasesLink.setOnAction(e -> openClases());
        reservasLink.setOnAction(e -> openReservas());
        estadisticasLink.setOnAction(e -> openEstadisticas());
        horarioLink.setOnAction(e -> openHorario());
        administradoresLink.setOnAction(e -> openAdministradores());
        logoutLink.setOnAction(e -> handleLogout());
    }
    
    // --- Métodos para abrir las diferentes vistas ---
    
    private void openActividades() {
        try { App.showActividades(usuarioActual, rolActual); } catch (Exception e) { e.printStackTrace(); }
    }
    
    private void openDashboard() {
        try { App.showDashboard(usuarioActual, rolActual); } catch (Exception e) { e.printStackTrace(); }
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
    
    private void openClases() {
        try { App.showClases(usuarioActual, rolActual); } catch (Exception e) { e.printStackTrace(); }
    }

    private void openAdministradores() {
        try { App.showAdministradores(usuarioActual, rolActual); } catch (Exception e) { e.printStackTrace(); }
    }
}