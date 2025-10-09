<<<<<<< Updated upstream
package com.mycompany.proyectogimnasio.Controllers;

import javafx.fxml.FXML;
import javafx.scene.layout.VBox; // <-- IMPORT NECESARIO
import com.mycompany.proyectogimnasio.Controllers.SidebarController;

public class DashboardController {

    @FXML
    private VBox sidebar; // fx:id del <fx:include>

    private SidebarController sidebarController;

    public void setUser(String nombre, String rol) {
        // Obtener el controller del include
        sidebarController = (SidebarController) sidebar.getProperties().get("fx:controller");
        if (sidebarController != null) {
            sidebarController.setUser(nombre, rol);
        }
    }
}
=======
package com.mycompany.proyectogimnasio.Controllers;

import com.mycompany.proyectogimnasio.App;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import java.io.IOException;
import com.mycompany.proyectogimnasio.Controllers.*;

public class DashboardController {
    
    @FXML
    private BorderPane mainPane; // El panel central donde se cargarán las vistas

    @FXML
    private Label welcomeLabel;

    @FXML
    private Label roleLabel;

    @FXML
    private Button logoutButton;
    
    @FXML
    private Button btnInstructores; // Botón del menú para instructores

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
    @FXML
private void handleDashboard() {
    // Carga el dashboard nuevamente en el mismo Stage
    // Puedes pasar datos del usuario si los tienes
    showDashboardView();
}

// Método para recargar el dashboard (panel central o toda la ventana)
private void showDashboardView() {
    try {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/mycompany/proyectogimnasio/dashboard.fxml"));
        Parent root = loader.load();

        DashboardController controller = loader.getController();
        // Si quieres pasar nombre/rol, lo puedes hacer aquí:
        // controller.setUser(currentUserName, currentUserRole);

        // Reemplaza la escena completa
        App.getPrimaryStage().getScene().setRoot(root);

    } catch (IOException e) {
        e.printStackTrace();
    }
}

    @FXML
    private void initializeInstructores() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/mycompany/proyectogimnasio/InstructorView.fxml"));
            Parent vistaInstructores = loader.load();

            // Obtener el controlador de la vista de instructores
            InstructorController controller = loader.getController();
            controller.initializeInstructores(); // Inicializa columnas y carga tabla

            // Colocar la vista en el panel central del dashboard
            mainPane.setCenter(vistaInstructores);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

>>>>>>> Stashed changes
