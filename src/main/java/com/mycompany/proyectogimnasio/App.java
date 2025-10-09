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

    /** LOGIN */
    public static void showLogin() throws Exception {
        FXMLLoader loader = new FXMLLoader(App.class.getResource("/com/mycompany/proyectogimnasio/login.fxml"));
        Scene scene = new Scene(loader.load(), 900, 600);

        primaryStage.setTitle("Login Administrador");
        primaryStage.setScene(scene);
        primaryStage.setMinWidth(900);
        primaryStage.setMinHeight(800);
        primaryStage.show();
    }

    /** DASHBOARD */
   public static void showDashboard(String nombre, String rol) throws Exception {
    FXMLLoader loader = new FXMLLoader(App.class.getResource("/com/mycompany/proyectogimnasio/dashboard.fxml"));
    Scene scene = new Scene(loader.load(), 1200, 600);

    DashboardController controller = loader.getController();
    if (controller == null) {
        System.out.println("ERROR: DashboardController es null");
    } else {
        controller.setUser(nombre, rol);
        System.out.println("DashboardController cargado correctamente");
    }

    primaryStage.setTitle("Dashboard Admin");
    primaryStage.setScene(scene);
    primaryStage.show();
}


    /** CLIENTES */
    public static void showClientes(String nombre, String rol) throws Exception {
        FXMLLoader loader = new FXMLLoader(App.class.getResource("/com/mycompany/proyectogimnasio/clientes.fxml"));
        Scene scene = new Scene(loader.load(), 900, 600);
        primaryStage.setMinWidth(900);
        primaryStage.setMinHeight(600);

        ClientesController controller = loader.getController();
        controller.setUser(nombre, rol);

        primaryStage.setTitle("Clientes");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    /** INSTRUCTORES */
    public static void showInstructores(String nombre, String rol) throws Exception {
        FXMLLoader loader = new FXMLLoader(App.class.getResource("/com/mycompany/proyectogimnasio/instructores.fxml"));
        Scene scene = new Scene(loader.load(), 900, 600);
        primaryStage.setMinWidth(900);
        primaryStage.setMinHeight(600);

        InstructoresController controller = loader.getController();
        controller.setUser(nombre, rol);

        primaryStage.setTitle("Instructores");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    /** RESERVAS */
    public static void showReservas(String nombre, String rol) throws Exception {
        FXMLLoader loader = new FXMLLoader(App.class.getResource("/com/mycompany/proyectogimnasio/reservas.fxml"));
        Scene scene = new Scene(loader.load(), 900, 600);
        primaryStage.setMinWidth(900);
        primaryStage.setMinHeight(600);

        ReservasController controller = loader.getController();
        controller.setUser(nombre, rol);

        primaryStage.setTitle("Reservas");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    /** ESTADÍSTICAS */
    public static void showEstadisticas(String nombre, String rol) throws Exception {
        FXMLLoader loader = new FXMLLoader(App.class.getResource("/com/mycompany/proyectogimnasio/estadisticas.fxml"));
        Scene scene = new Scene(loader.load(), 900, 600);
        primaryStage.setMinWidth(900);
        primaryStage.setMinHeight(600);

        EstadisticasController controller = loader.getController();
        controller.setUser(nombre, rol);

        primaryStage.setTitle("Estadísticas");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    /** HORARIO */
    public static void showHorario(String nombre, String rol) throws Exception {
        FXMLLoader loader = new FXMLLoader(App.class.getResource("/com/mycompany/proyectogimnasio/horario.fxml"));
        Scene scene = new Scene(loader.load(), 900, 600);
        primaryStage.setMinWidth(900);
        primaryStage.setMinHeight(600);

        HorarioController controller = loader.getController();
        controller.setUser(nombre, rol);

        primaryStage.setTitle("Horario");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static Stage getPrimaryStage() {
        return primaryStage;
    }

    public static void main(String[] args) {
        launch();
    }
}
