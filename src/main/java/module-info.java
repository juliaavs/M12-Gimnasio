module com.mycompany.proyectogimnasio {
    // Requisitos de JavaFX
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    
    // Módulos estándar
    requires java.sql;
    requires java.naming;
    
    // MÓDULO DEL DRIVER MYSQL
    requires mysql.connector.j;
    
    // Indica que el módulo utiliza el servicio JDBC Driver
    uses java.sql.Driver; 

    opens com.mycompany.proyectogimnasio to javafx.fxml;
    opens com.mycompany.proyectogimnasio.Models to javafx.base;
    opens com.mycompany.proyectogimnasio.Controllers to javafx.fxml, javafx.base;

    exports com.mycompany.proyectogimnasio;
}