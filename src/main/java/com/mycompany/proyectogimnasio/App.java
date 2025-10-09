package com.mycompany.proyectogimnasio;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
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
        // Cargar dashboard
        FXMLLoader dashboardLoader = new FXMLLoader(App.class.getResource("/com/mycompany/proyectogimnasio/dashboard.fxml"));
        BorderPane root = dashboardLoader.load();

        // Controlador del dashboard
        DashboardController dashboardController = dashboardLoader.getController();
        dashboardController.setUser(nombre, rol);

        // Cargar sidebar
        FXMLLoader sidebarLoader = new FXMLLoader(App.class.getResource("/com/mycompany/proyectogimnasio/sidebar.fxml"));
        VBox sidebar = sidebarLoader.load();
        SidebarController sidebarController = sidebarLoader.getController();
        sidebarController.setUser(nombre, rol);

        // Insertar sidebar en el BorderPane
        root.setLeft(sidebar);

        // Mostrar escena
        Scene scene = new Scene(root, 1200, 600);
        primaryStage.setTitle("Dashboard Admin");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    /** CLIENTES */
    public static void showClientes(String nombre, String rol) throws Exception {
        FXMLLoader loader = new FXMLLoader(App.class.getResource("/com/mycompany/proyectogimnasio/clientes.fxml"));
        BorderPane root = loader.load();

        ClientesController controller = loader.getController();
        controller.setUser(nombre, rol);

        // Sidebar
        FXMLLoader sidebarLoader = new FXMLLoader(App.class.getResource("/com/mycompany/proyectogimnasio/sidebar.fxml"));
        VBox sidebar = sidebarLoader.load();
        SidebarController sidebarController = sidebarLoader.getController();
        sidebarController.setUser(nombre, rol);

        root.setLeft(sidebar);

        Scene scene = new Scene(root, 1200, 600);
        primaryStage.setTitle("Clientes");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    /** INSTRUCTORES */
    public static void showInstructores(String nombre, String rol) throws Exception {
        FXMLLoader loader = new FXMLLoader(App.class.getResource("/com/mycompany/proyectogimnasio/instructores.fxml"));
        BorderPane root = loader.load();

        InstructoresController controller = loader.getController();
        controller.setUser(nombre, rol);

        FXMLLoader sidebarLoader = new FXMLLoader(App.class.getResource("/com/mycompany/proyectogimnasio/sidebar.fxml"));
        VBox sidebar = sidebarLoader.load();
        SidebarController sidebarController = sidebarLoader.getController();
        sidebarController.setUser(nombre, rol);

        root.setLeft(sidebar);

        Scene scene = new Scene(root, 1200, 600);
        primaryStage.setTitle("Instructores");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    /** RESERVAS */
    public static void showReservas(String nombre, String rol) throws Exception {
        FXMLLoader loader = new FXMLLoader(App.class.getResource("/com/mycompany/proyectogimnasio/reservas.fxml"));
        BorderPane root = loader.load();

        ReservasController controller = loader.getController();
        controller.setUser(nombre, rol);

        FXMLLoader sidebarLoader = new FXMLLoader(App.class.getResource("/com/mycompany/proyectogimnasio/sidebar.fxml"));
        VBox sidebar = sidebarLoader.load();
        SidebarController sidebarController = sidebarLoader.getController();
        sidebarController.setUser(nombre, rol);

        root.setLeft(sidebar);

        Scene scene = new Scene(root, 1200, 600);
        primaryStage.setTitle("Reservas");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    /** ESTADÍSTICAS */
    public static void showEstadisticas(String nombre, String rol) throws Exception {
        FXMLLoader loader = new FXMLLoader(App.class.getResource("/com/mycompany/proyectogimnasio/estadisticas.fxml"));
        BorderPane root = loader.load();

        EstadisticasController controller = loader.getController();
        controller.setUser(nombre, rol);

        FXMLLoader sidebarLoader = new FXMLLoader(App.class.getResource("/com/mycompany/proyectogimnasio/sidebar.fxml"));
        VBox sidebar = sidebarLoader.load();
        SidebarController sidebarController = sidebarLoader.getController();
        sidebarController.setUser(nombre, rol);

        root.setLeft(sidebar);

        Scene scene = new Scene(root, 1200, 600);
        primaryStage.setTitle("Estadísticas");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    /** HORARIO */
    public static void showHorario(String nombre, String rol) throws Exception {
        FXMLLoader loader = new FXMLLoader(App.class.getResource("/com/mycompany/proyectogimnasio/horario.fxml"));
        BorderPane root = loader.load();

        HorarioController controller = loader.getController();
        controller.setUser(nombre, rol);

        FXMLLoader sidebarLoader = new FXMLLoader(App.class.getResource("/com/mycompany/proyectogimnasio/sidebar.fxml"));
        VBox sidebar = sidebarLoader.load();
        SidebarController sidebarController = sidebarLoader.getController();
        sidebarController.setUser(nombre, rol);

        root.setLeft(sidebar);

        Scene scene = new Scene(root, 1200, 600);
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
