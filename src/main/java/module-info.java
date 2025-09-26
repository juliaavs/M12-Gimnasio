module com.mycompany.proyectogimnasio {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    
    opens com.mycompany.proyectogimnasio to javafx.fxml;
    exports com.mycompany.proyectogimnasio;
}
