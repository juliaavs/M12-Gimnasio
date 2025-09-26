package com.mycompany.proyectogimnasio;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class App extends Application {

    private static Stage primaryStage;

    @Override
<<<<<<< HEAD
    public void start(Stage stage) throws Exception {
        primaryStage = stage;
        showLogin();
    }

    public static void showLogin() throws Exception {
        FXMLLoader loader = new FXMLLoader(App.class.getResource("/com/mycompany/proyectogimnasio/login.fxml"));
        Scene scene = new Scene(loader.load(), 400, 300);
        primaryStage.setTitle("Login Administrador");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void showDashboard(String nombre, String rol) throws Exception {
        FXMLLoader loader = new FXMLLoader(App.class.getResource("/com/mycompany/proyectogimnasio/dashboard.fxml"));
        Scene scene = new Scene(loader.load(), 400, 300);

        // Pasar datos al controlador
        DashboardController controller = loader.getController();
        controller.setUser(nombre, rol);

        primaryStage.setTitle("Dashboard Admin");
        primaryStage.setScene(scene);
        primaryStage.show();
=======
    public void start(Stage stage) {
        try {
            ConexionBD.probarConexion();
            FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("/com/mycompany/proyectogimnasio/primary.fxml"));
            Scene scene = new Scene(fxmlLoader.load(), 640, 480);
            stage.setTitle("Proyecto Gimnasio");
            stage.setScene(scene);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
>>>>>>> 257c544780b67625a2462c5a71be182272865fc7
    }

    public static void main(String[] args) {
        launch();
    }
}
