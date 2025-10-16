package com.mycompany.proyectogimnasio.Controllers;

import com.mycompany.proyectogimnasio.App;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import java.io.IOException;

public class DashboardController {
    
    @FXML
    private BorderPane mainPane; // El panel central donde se cargarán las vistas

    @FXML
    private Button logoutButton;
    
    @FXML
    private Button btnInstructores; // Botón del menú para instructores

    // --- Métodos de Navegación del Dashboard ---

    @FXML
    private void handleLogout() {
        try {
            App.showLogin();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    @FXML
    private void handleDashboard() {
        // Método para recargar la vista del dashboard principal
        showDashboardView();
    }

    private void showDashboardView() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/mycompany/proyectogimnasio/dashboard.fxml"));
            Parent root = loader.load();

            // Reemplaza la escena completa
            App.getPrimaryStage().getScene().setRoot(root);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * @FXML
     * Método invocado cuando se presiona el botón "Instructores".
     * Carga la vista InstructorView.fxml y la coloca en el centro del BorderPane.
     */
    @FXML
    public void handleBtnInstructores() {
        try {
            // 1. Cargar el FXML de la vista de instructores
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/mycompany/proyectogimnasio/InstructorView.fxml"));
            Parent vistaInstructores = loader.load(); // Al cargar, se llama automáticamente a InstructorController.initialize()

            // 2. Opcional: Obtener el controlador si necesitas pasar datos *después* de la inicialización
            InstructorController controller = loader.getController();

            // 3. Colocar la vista cargada en el panel central del dashboard
            mainPane.setCenter(vistaInstructores);

        } catch (IOException e) {
            System.err.println("Error al cargar la vista de Instructores.");
            e.printStackTrace();
            // Mostrar un Alert al usuario si la carga falla
        }
    }

}
