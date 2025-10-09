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
    private static BorderPane root; // BorderPane principal
    private static SidebarController sidebarController; // Sidebar persistente
    private static String usuarioActual; // Nombre del usuario logueado
    private static String rolActual;     // Rol del usuario

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

    /** Inicializar la ventana principal con Sidebar */
    private static void initRootWithSidebar() throws Exception {
        if (root == null) {
            // Cargamos BorderPane principal vacío
            FXMLLoader loader = new FXMLLoader(App.class.getResource("/com/mycompany/proyectogimnasio/dashboard.fxml"));
            root = loader.load();

            // Cargamos Sidebar
            FXMLLoader sidebarLoader = new FXMLLoader(App.class.getResource("/com/mycompany/proyectogimnasio/sidebar.fxml"));
            VBox sidebar = sidebarLoader.load();
            sidebarController = sidebarLoader.getController();
            sidebarController.setUser(usuarioActual, rolActual);

            root.setLeft(sidebar);

            // Escena principal
            Scene scene = new Scene(root, 1200, 600);
            primaryStage.setScene(scene);
            primaryStage.setTitle("Dashboard Admin");
            primaryStage.show();
        }
    }

    /** Mostrar Dashboard */
    public static void showDashboard(String usuario, String rol) throws Exception {
        usuarioActual = usuario;
        rolActual = rol;

        initRootWithSidebar();

        // Cargar contenido central del dashboard
        FXMLLoader loader = new FXMLLoader(App.class.getResource("/com/mycompany/proyectogimnasio/dashboard.fxml"));
        VBox center = loader.load();

        DashboardController controller = loader.getController();
        controller.setUser(usuario, rol);

        root.setCenter(center); // reemplazamos solo el centro
    }

    /** Mostrar Clientes */
    public static void showClientes(String usuario, String rol) throws Exception {
        usuarioActual = usuario;
        rolActual = rol;

        initRootWithSidebar();

        FXMLLoader loader = new FXMLLoader(App.class.getResource("/com/mycompany/proyectogimnasio/clientes.fxml"));
        VBox center = loader.load();

        ClientesController controller = loader.getController();
        controller.setUser(usuario, rol);

        root.setCenter(center);
        primaryStage.setTitle("Clientes");
    }

    /** Mostrar Instructores */
    public static void showInstructores(String usuario, String rol) throws Exception {
        usuarioActual = usuario;
        rolActual = rol;

        initRootWithSidebar();

        FXMLLoader loader = new FXMLLoader(App.class.getResource("/com/mycompany/proyectogimnasio/instructores.fxml"));
        VBox center = loader.load();

        InstructoresController controller = loader.getController();
        controller.setUser(usuario, rol);

        root.setCenter(center);
        primaryStage.setTitle("Instructores");
    }

    /** Mostrar Reservas */
    public static void showReservas(String usuario, String rol) throws Exception {
        usuarioActual = usuario;
        rolActual = rol;

        initRootWithSidebar();

        FXMLLoader loader = new FXMLLoader(App.class.getResource("/com/mycompany/proyectogimnasio/reservas.fxml"));
        VBox center = loader.load();

        ReservasController controller = loader.getController();
        controller.setUser(usuario, rol);

        root.setCenter(center);
        primaryStage.setTitle("Reservas");
    }

    /** Mostrar Estadísticas */
    public static void showEstadisticas(String usuario, String rol) throws Exception {
        usuarioActual = usuario;
        rolActual = rol;

        initRootWithSidebar();

        FXMLLoader loader = new FXMLLoader(App.class.getResource("/com/mycompany/proyectogimnasio/estadisticas.fxml"));
        VBox center = loader.load();

        EstadisticasController controller = loader.getController();
        controller.setUser(usuario, rol);

        root.setCenter(center);
        primaryStage.setTitle("Estadísticas");
    }

    /** Mostrar Horario */
    public static void showHorario(String usuario, String rol) throws Exception {
        usuarioActual = usuario;
        rolActual = rol;

        initRootWithSidebar();

        FXMLLoader loader = new FXMLLoader(App.class.getResource("/com/mycompany/proyectogimnasio/horario.fxml"));
        VBox center = loader.load();

        HorarioController controller = loader.getController();
        controller.setUser(usuario, rol);

        root.setCenter(center);
        primaryStage.setTitle("Horario");
    }

    public static Stage getPrimaryStage() {
        return primaryStage;
    }

    public static void main(String[] args) {
        launch();
    }
}
