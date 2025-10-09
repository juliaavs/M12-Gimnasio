<<<<<<< Updated upstream
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

    /** LOGIN */
    public static void showLogin() throws Exception {
        // Limpiar root para permitir re-login
        root = null;
        usuarioActual = null;
        rolActual = null;

        FXMLLoader loader = new FXMLLoader(App.class.getResource("/com/mycompany/proyectogimnasio/login.fxml"));
        Scene scene = new Scene(loader.load(), 900, 800);

        primaryStage.setTitle("Login Administrador");
        primaryStage.setScene(scene);
        primaryStage.setMinWidth(900);
        primaryStage.setMinHeight(800);
        primaryStage.show();
    }

    /** Inicializar la ventana principal con Sidebar */
    private static void initRootWithSidebar() throws Exception {
        if (root == null) {
            FXMLLoader loader = new FXMLLoader(App.class.getResource("/com/mycompany/proyectogimnasio/dashboard.fxml"));
            root = loader.load();

            // Cargamos Sidebar
            FXMLLoader sidebarLoader = new FXMLLoader(App.class.getResource("/com/mycompany/proyectogimnasio/sidebar.fxml"));
            VBox sidebar = sidebarLoader.load();
            sidebarController = sidebarLoader.getController();
            sidebarController.setUser(usuarioActual, rolActual);

            root.setLeft(sidebar);

            Scene scene = new Scene(root, 900, 800);
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

        FXMLLoader loader = new FXMLLoader(App.class.getResource("/com/mycompany/proyectogimnasio/dashboard_center.fxml"));
        Parent center = loader.load();

        DashboardController controller = loader.getController();
        controller.setUser(usuario, rol);

        root.setCenter(center);
    }

    /** Mostrar Clientes */
    public static void showClientes(String usuario, String rol) throws Exception {
        usuarioActual = usuario;
        rolActual = rol;

        initRootWithSidebar();

        FXMLLoader loader = new FXMLLoader(App.class.getResource("/com/mycompany/proyectogimnasio/clientes.fxml"));
        Parent center = loader.load();

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
        Parent center = loader.load();

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
        Parent center = loader.load();

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
        Parent center = loader.load();

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
        Parent center = loader.load();

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
=======
package com.mycompany.proyectogimnasio;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import com.mycompany.proyectogimnasio.Controllers.*;

import java.io.IOException;

public class App extends Application {

    private static Stage primaryStage;

    @Override
    public void start(Stage stage) throws Exception {
        primaryStage = stage;
        showLogin(); // Opcional: abrir dashboard al iniciar
    }

    public static void main(String[] args) {
        launch();
    }
    
    public static Stage getPrimaryStage() {
    return primaryStage;
}


    // 🔹 Función para mostrar cualquier FXML en el stage principal
    public static void showView(String fxmlPath, String title) {
        try {
            FXMLLoader loader = new FXMLLoader(App.class.getResource(fxmlPath));
            Parent root = loader.load();

            Scene scene = new Scene(root);
            primaryStage.setTitle(title);
            primaryStage.setScene(scene);
            primaryStage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // 🔹 Función específica para mostrar el dashboard
    public static void showDashboard(String nombre, String rol) {
        try {
            FXMLLoader loader = new FXMLLoader(App.class.getResource("/com/mycompany/proyectogimnasio/dashboard.fxml"));
            Parent root = loader.load();

            DashboardController controller = loader.getController();
            controller.setUser(nombre, rol);

            Scene scene = new Scene(root);
            primaryStage.setTitle("Dashboard");
            primaryStage.setScene(scene);
            primaryStage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // 🔹 Función para mostrar login
    public static void showLogin() {
        showView("/com/mycompany/proyectogimnasio/login.fxml", "Login");
    }

    // 🔹 Función para mostrar instructores
    public static void showInstructores() {
        showView("/com/mycompany/proyectogimnasio/InstructorView.fxml", "Instructores");
    }
}

>>>>>>> Stashed changes
