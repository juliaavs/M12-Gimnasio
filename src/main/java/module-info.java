module com.mycompany.proyectogimnasio {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    
    opens com.mycompany.proyectogimnasio to javafx.fxml;
<<<<<<< HEAD
    opens com.mycompany.proyectogimnasio.Controllers to javafx.fxml;
=======
    opens com.mycompany.proyectogimnasio.Controllers to javafx.fxml; // Abrir controladores
>>>>>>> d31f86cb450ddb77cc83eb12071b6e39bcd26bf2
    exports com.mycompany.proyectogimnasio;
}
