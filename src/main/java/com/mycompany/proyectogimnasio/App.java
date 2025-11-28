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
        Scene scene = new Scene(loader.load(), 900, 900);

        primaryStage.setTitle("FitGym Pro - Login");
        primaryStage.setScene(scene);
        primaryStage.setMinWidth(900);
        primaryStage.setMinHeight(900);
        primaryStage.show();
    }

    /** ================================
     * ADMINISTRADORES (NUEVO MÉTODO)
     * ================================ */
    public static void showAdministradores(String usuario, String rol) throws Exception {
        usuarioActual = usuario;
        rolActual = rol;

        initRootWithSidebar();
        sidebarController.setUser(usuario, rol);

        FXMLLoader loader = new FXMLLoader(App.class.getResource("/com/mycompany/proyectogimnasio/AdminView.fxml"));
        Parent center = loader.load();

        root.setCenter(center);
        primaryStage.setTitle("Administradores - FitGym Pro");
    }
    
    /** ================================
     * Inicializar la ventana principal con Sidebar
     * NOTA: Este método ahora crea el BorderPane principal programáticamente
     * para evitar cargar dashboard.fxml como estructura y como contenido.
     * ================================ */
    private static void initRootWithSidebar() throws Exception {
        if (root == null) {
            // 1. Crear el BorderPane principal (la estructura) sin cargar ningún FXML.
            root = new BorderPane(); 

            // 2. Cargar Sidebar solo una vez
            FXMLLoader sidebarLoader = new FXMLLoader(App.class.getResource("/com/mycompany/proyectogimnasio/sidebar.fxml"));
            VBox sidebar = sidebarLoader.load();
            sidebarController = sidebarLoader.getController();

            // 3. Establecer el Sidebar a la izquierda
            root.setLeft(sidebar);

            // 4. Configurar y mostrar la escena principal
            Scene scene = new Scene(root, 1400, 900);
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

        // 1. Asegura que la estructura principal (root BorderPane) con el sidebar exista.
        initRootWithSidebar();
        // 2. Actualiza los datos del usuario en el sidebar.
        sidebarController.setUser(usuario, rol); 

        // 3. Carga SOLO el contenido del dashboard (esto es lo que contiene las gráficas/tablas).
        // NOTA: Esta es ahora la ÚNICA vez que se carga dashboard.fxml.
        FXMLLoader loader = new FXMLLoader(App.class.getResource("/com/mycompany/proyectogimnasio/dashboard.fxml"));
        Parent center = loader.load();

        // 4. Coloca ese contenido en el centro del BorderPane principal.
        root.setCenter(center);
        primaryStage.setTitle("Dashboard - FitGym Pro");
    }

    /** ================================
     * CLIENTES
     * ================================ */
    public static void showClientes(String usuario, String rol) throws Exception {
        // Guarda los datos del usuario actual
        usuarioActual = usuario;
        rolActual = rol;

        // Asegúrate de que la ventana principal con el sidebar esté inicializada
        initRootWithSidebar();
        
        // Actualiza el nombre y rol que se muestran en el sidebar
        sidebarController.setUser(usuario, rol);

        // Carga el archivo FXML de la vista de clientes
        FXMLLoader loader = new FXMLLoader(App.class.getResource("/com/mycompany/proyectogimnasio/ClientesView.fxml"));
        Parent center = loader.load();

        // Coloca la vista de clientes en el centro de la ventana principal
        root.setCenter(center);
        primaryStage.setTitle("Gestión de Clientes - FitGym Pro");
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

        root.setCenter(center);
        primaryStage.setTitle("Instructores - FitGym Pro");
    }

    /** ================================
     * ACTIVIDADES (NUEVO MÉTODO)
     * ================================ */
    public static void showActividades(String usuario, String rol) throws Exception {
        usuarioActual = usuario;
        rolActual = rol;

        initRootWithSidebar();
        sidebarController.setUser(usuario, rol);

        FXMLLoader loader = new FXMLLoader(App.class.getResource("/com/mycompany/proyectogimnasio/ActividadesView.fxml"));
        Parent center = loader.load();
        
        root.setCenter(center);
        primaryStage.setTitle("Actividades - FitGym Pro");
    }


    
    /** ================================
     * RESERVAS
     * ================================ */
    public static void showReservas(String usuario, String rol) throws Exception {
        usuarioActual = usuario;
        rolActual = rol;

        initRootWithSidebar();
        sidebarController.setUser(usuario, rol);

        FXMLLoader loader = new FXMLLoader(App.class.getResource("/com/mycompany/proyectogimnasio/ReservasView.fxml"));
        Parent center = loader.load();

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

        FXMLLoader loader = new FXMLLoader(App.class.getResource("/com/mycompany/proyectogimnasio/EstadisticasView.fxml"));
        Parent center = loader.load();

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