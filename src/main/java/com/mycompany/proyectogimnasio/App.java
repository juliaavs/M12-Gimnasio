package com.mycompany.proyectogimnasio;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import com.mycompany.proyectogimnasio.Controllers.*;




public class App extends Application {

    private static Stage primaryStage;

    @Override
    public void start(Stage stage) throws Exception {
        primaryStage = stage;
        showLogin();
    }

    public static void showLogin() throws Exception {
        FXMLLoader loader = new FXMLLoader(App.class.getResource("/com/mycompany/proyectogimnasio/login.fxml"));
        Scene scene = new Scene(loader.load(), 900, 600);

        primaryStage.setTitle("Login Administrador");
        primaryStage.setScene(scene);
        primaryStage.setMinWidth(900);
        primaryStage.setMinHeight(800); 
        primaryStage.show();
    }


    public static void showDashboard(String nombre, String rol) throws Exception {
        FXMLLoader loader = new FXMLLoader(App.class.getResource("/com/mycompany/proyectogimnasio/dashboard.fxml"));
        Scene scene = new Scene(loader.load(), 900, 600);
        primaryStage.setMinWidth(900);
        primaryStage.setMinHeight(600);

        // Pasar datos al controlador
        DashboardController controller = loader.getController();
        controller.setUser(nombre, rol);

        primaryStage.setTitle("Dashboard Admin");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}