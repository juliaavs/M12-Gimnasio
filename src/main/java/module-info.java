module com.mycompany.proyectogimnasio {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires java.sql;

    opens com.mycompany.proyectogimnasio to javafx.fxml;
    opens com.mycompany.proyectogimnasio.Models to javafx.base;
    opens com.mycompany.proyectogimnasio.Controllers to javafx.fxml, javafx.base;

    exports com.mycompany.proyectogimnasio;
}
