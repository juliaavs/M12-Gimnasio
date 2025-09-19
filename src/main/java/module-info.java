module com.mycompany.proyectogimnasio {
    requires javafx.controls;
    requires javafx.fxml;

    opens com.mycompany.proyectogimnasio to javafx.fxml;
    exports com.mycompany.proyectogimnasio;
}
