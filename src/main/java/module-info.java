module com.mycompany.proyectogimnasio {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    //requires mysql.connector.java; // permite usar el driver JDBC


    opens com.mycompany.proyectogimnasio to javafx.fxml;
    exports com.mycompany.proyectogimnasio;
}
