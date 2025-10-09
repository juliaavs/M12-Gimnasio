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
