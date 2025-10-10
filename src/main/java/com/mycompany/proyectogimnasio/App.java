package com.mycompany.proyectogimnasio;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
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

    /** ================================
     * LOGIN
     * ================================ */
    public static void showLogin() throws Exception {
        // Limpiar todo al volver al login
        root = null;
        sidebarController = null;
        usuarioActual = null;
        rolActual = null;

        FXMLLoader loader = new FXMLLoader(App.class.getResource("/com/mycompany/proyectogimnasio/login.fxml"));
        Scene scene = new Scene(loader.load(), 900, 800);

        primaryStage.setTitle("FitGym Pro - Login");
        primaryStage.setScene(scene);
        primaryStage.setMinWidth(900);
        primaryStage.setMinHeight(800);
        primaryStage.show();
    }

    /** ================================
     * Inicializar la ventana principal con Sidebar
     * ================================ */
    private static void initRootWithSidebar() throws Exception {
        if (root == null) {
            // Cargamos el layout principal (dashboard.fxml con BorderPane vacío)
            FXMLLoader loader = new FXMLLoader(App.class.getResource("/com/mycompany/proyectogimnasio/dashboard.fxml"));
            root = loader.load();

            // Cargar Sidebar solo una vez
            FXMLLoader sidebarLoader = new FXMLLoader(App.class.getResource("/com/mycompany/proyectogimnasio/sidebar.fxml"));
            VBox sidebar = sidebarLoader.load();
            sidebarController = sidebarLoader.getController();

            root.setLeft(sidebar);

            Scene scene = new Scene(root, 900, 800);
            primaryStage.setScene(scene);
            primaryStage.setTitle("FitGym Pro");
            primaryStage.show();
        }
    }

    /** ================================
     * DASHBOARD
     * ================================ */
    public static void showDashboard(String usuario, String rol) throws Exception {
        usuarioActual = usuario;
        rolActual = rol;

        initRootWithSidebar();
        sidebarController.setUser(usuario, rol); // Esto sí actualiza el sidebar

        FXMLLoader loader = new FXMLLoader(App.class.getResource("/com/mycompany/proyectogimnasio/dashboard.fxml"));
        Parent center = loader.load();

        // DashboardController controller = loader.getController();
        // controller.setUser(usuario, rol); ← eliminar esta línea

        root.setCenter(center);
        primaryStage.setTitle("Dashboard - FitGym Pro");
    }


    /** ================================
     * CLIENTES
     * ================================ */
    public static void showClientes(String usuario, String rol) throws Exception {
        usuarioActual = usuario;
        rolActual = rol;

        initRootWithSidebar();
        sidebarController.setUser(usuario, rol);

        FXMLLoader loader = new FXMLLoader(App.class.getResource("/com/mycompany/proyectogimnasio/clientes.fxml"));
        Parent center = loader.load();

        ClientesController controller = loader.getController();
        controller.setUser(usuario, rol);

        root.setCenter(center);
        primaryStage.setTitle("Clientes - FitGym Pro");
    }
    
    public static void showClases(String usuario, String rol) throws Exception{
        usuarioActual=usuario;
        rolActual=rol;
        
        initRootWithSidebar();
        sidebarController.setUser(usuario, rol); // Esto actualiza los labels del sidebar
        
        FXMLLoader loader = new FXMLLoader(App.class.getResource("/com/mycompany/proyectogimnasio/ClasesView.fxml"));
        Parent center = loader.load();
        
        root.setCenter(center);
        primaryStage.setTitle("Clases - FitGym Pro");


        
        
    }
    

    /** ================================
     * INSTRUCTORES
     * ================================ */
    public static void showInstructores(String usuario, String rol) throws Exception {
        usuarioActual = usuario;
        rolActual = rol;

        initRootWithSidebar();
        sidebarController.setUser(usuario, rol); // Esto actualiza los labels del sidebar

        FXMLLoader loader = new FXMLLoader(App.class.getResource("/com/mycompany/proyectogimnasio/InstructorView.fxml"));
        Parent center = loader.load();

        // InstructorController controller = loader.getController();
        // controller.setUser(usuario, rol); // ← Eliminar esta línea

        root.setCenter(center);
        primaryStage.setTitle("Instructores - FitGym Pro");
    }


    /** ================================
     * RESERVAS
     * ================================ */
    public static void showReservas(String usuario, String rol) throws Exception {
        usuarioActual = usuario;
        rolActual = rol;

        initRootWithSidebar();
        sidebarController.setUser(usuario, rol);

        FXMLLoader loader = new FXMLLoader(App.class.getResource("/com/mycompany/proyectogimnasio/reservas.fxml"));
        Parent center = loader.load();

        ReservasController controller = loader.getController();
        controller.setUser(usuario, rol);

        root.setCenter(center);
        primaryStage.setTitle("Reservas - FitGym Pro");
    }

    /** ================================
     * ESTADÍSTICAS
     * ================================ */
    public static void showEstadisticas(String usuario, String rol) throws Exception {
        usuarioActual = usuario;
        rolActual = rol;

        initRootWithSidebar();
        sidebarController.setUser(usuario, rol);

        FXMLLoader loader = new FXMLLoader(App.class.getResource("/com/mycompany/proyectogimnasio/estadisticas.fxml"));
        Parent center = loader.load();

        EstadisticasController controller = loader.getController();
        controller.setUser(usuario, rol);

        root.setCenter(center);
        primaryStage.setTitle("Estadísticas - FitGym Pro");
    }

    /** ================================
     * HORARIO
     * ================================ */
    public static void showHorario(String usuario, String rol) throws Exception {
        usuarioActual = usuario;
        rolActual = rol;

        initRootWithSidebar();
        sidebarController.setUser(usuario, rol);

        FXMLLoader loader = new FXMLLoader(App.class.getResource("/com/mycompany/proyectogimnasio/HorarioView.fxml"));
        Parent center = loader.load();

        HorarioController controller = loader.getController();
        controller.setUser(usuario, rol);

        root.setCenter(center);
        primaryStage.setTitle("Horario - FitGym Pro");
    }

    /** ================================
     * UTILIDADES
     * ================================ */
    public static Stage getPrimaryStage() {
        return primaryStage;
    }

    public static void main(String[] args) {
        launch();
    }
}
