module com.mycompany.proyectogimnasio {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
<<<<<<< HEAD
    
=======
    //requires mysql.connector.java; // permite usar el driver JDBC


>>>>>>> 257c544780b67625a2462c5a71be182272865fc7
    opens com.mycompany.proyectogimnasio to javafx.fxml;
    exports com.mycompany.proyectogimnasio;
}
