module com.mycompany.proyectogimnasio {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    
    opens com.mycompany.proyectogimnasio to javafx.fxml;
    opens com.mycompany.proyectogimnasio.Controllers to javafx.fxml; // Abrir controladores
    exports com.mycompany.proyectogimnasio;
}
