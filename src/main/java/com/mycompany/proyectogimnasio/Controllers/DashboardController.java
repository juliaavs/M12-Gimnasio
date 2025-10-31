
package com.mycompany.proyectogimnasio.Controllers;

import com.mycompany.proyectogimnasio.App;
import com.mycompany.proyectogimnasio.Service.EstadisticasService;
import com.mycompany.proyectogimnasio.Service.InstructorService;
import com.mycompany.proyectogimnasio.Service.ClienteService; 
import com.mycompany.proyectogimnasio.Service.ReservasService;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Map;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;

public class DashboardController {
    
    @FXML
    private BorderPane mainPane; // El panel central donde se cargarán las vistas

    @FXML
    private Button logoutButton;
    
    @FXML
    private Button btnInstructores; // Botón del menú para instructores
    
    @FXML private PieChart clasesPorActividadChart;
    
    @FXML private Label totalClasesLabel; 
    
    @FXML private BarChart<String, Number> ocupacionChart;
    
    @FXML private CategoryAxis ocupacionX;
    
    
    @FXML private Label totalInstructorsLabel;
    @FXML private Label instructorsChangeLabel;
    @FXML private Label totalClientsLabel;
    @FXML private Label clientsChangeLabel;
    @FXML private Label todayReservationsLabel;
    @FXML private Label reservationsChangeLabel;
    
    
    private EstadisticasService estadisticasService;
    private final InstructorService instructorService = new InstructorService(); // Instanciado
    private final ClienteService clienteService = new ClienteService();             // Instanciado
    private final ReservasService reservasService = new ReservasService(); // Instanciado

 
    
    @FXML
    public void initialize() {
        estadisticasService = new EstadisticasService();
        // Llamada a las nuevas funciones de carga
        loadDashboardData();
        loadOcupacionPorClaseData();
        
        
        
        
    }
    
    private void loadDashboardData() {
        try {
            // --- 1. Instructores ---
            int totalInstructores = instructorService.getTotalInstructores();
            int newInstructors = instructorService.getNuevosInstructoresEsteMes();
            
            totalInstructorsLabel.setText(String.valueOf(totalInstructores));
            instructorsChangeLabel.setText("+" + newInstructors + " este mes");
            instructorsChangeLabel.setStyle("-fx-text-fill: green;");

            // --- 2. Clientes ---
            int totalClients = clienteService.getTotalClientes();
            int newClients = clienteService.getNuevosClientesEstaSemana();

            totalClientsLabel.setText(String.valueOf(totalClients));
            clientsChangeLabel.setText("+" + newClients + " esta semana");
            clientsChangeLabel.setStyle("-fx-text-fill: green;");


            // --- 3. Reservas ---
            int todayReservations = reservasService.getReservasHoy();
            int yesterdayReservations = reservasService.getReservasAyer();
            int change = todayReservations - yesterdayReservations;
            
            todayReservationsLabel.setText(String.valueOf(todayReservations));
            
            // Lógica para el texto y el color del cambio
            String changeText = (change >= 0 ? "+" : "") + change + " vs ayer";
            reservationsChangeLabel.setText(changeText);
            
            if (change > 0) {
                reservationsChangeLabel.setStyle("-fx-text-fill: green;");
            } else if (change < 0) {
                reservationsChangeLabel.setStyle("-fx-text-fill: red;");
            } else {
                 reservationsChangeLabel.setStyle("-fx-text-fill: black;");
            }
        } catch (Exception e) {
            // Manejo de errores genérico para la carga de datos del dashboard
            System.err.println("Error al cargar datos del Dashboard: " + e.getMessage());
            // Opcional: Mostrar "Error" en los labels si falla la DB
            totalInstructorsLabel.setText("ERROR");
            totalClientsLabel.setText("ERROR");
            todayReservationsLabel.setText("ERROR");
        }
    }

    @FXML
    private void handleLogout() {
        try {
            App.showLogin();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    @FXML
    private void handleDashboard() {
        // Método para recargar la vista del dashboard principal
        showDashboardView();
    }

    private void showDashboardView() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/mycompany/proyectogimnasio/dashboard.fxml"));
            Parent root = loader.load();

            // Reemplaza la escena completa
            App.getPrimaryStage().getScene().setRoot(root);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * @FXML
     * Método invocado cuando se presiona el botón "Instructores".
     * Carga la vista InstructorView.fxml y la coloca en el centro del BorderPane.
     */
    @FXML
    public void handleBtnInstructores() {
        try {
            // 1. Cargar el FXML de la vista de instructores
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/mycompany/proyectogimnasio/InstructorView.fxml"));
            Parent vistaInstructores = loader.load(); // Al cargar, se llama automáticamente a InstructorController.initialize()

            // 2. Opcional: Obtener el controlador si necesitas pasar datos *después* de la inicialización
            InstructorController controller = loader.getController();

            // 3. Colocar la vista cargada en el panel central del dashboard
            mainPane.setCenter(vistaInstructores);

        } catch (IOException e) {
            System.err.println("Error al cargar la vista de Instructores.");
            e.printStackTrace();
            // Mostrar un Alert al usuario si la carga falla
        }
    }
    
    @FXML
    private void handleBtnEstadisticas() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/mycompany/proyectogimnasio/EstadisticasView.fxml"));
            Parent vistaEstadisticas = loader.load(); 
            mainPane.setCenter(vistaEstadisticas);
        } catch (IOException e) {
            // Manejo de error
            e.printStackTrace();
        }
    }
        
    private void loadOcupacionPorClaseData() {
    try {
        Map<String, Map<String, Integer>> data = estadisticasService.getOcupacionPorClase();
        
        XYChart.Series<String, Number> aforoSeries = new XYChart.Series<>();
        aforoSeries.setName("Aforo Máximo");
        
        XYChart.Series<String, Number> inscritosSeries = new XYChart.Series<>();
        inscritosSeries.setName("Inscritos Confirmados");
        
        // Llenar las series
        for (Map.Entry<String, Map<String, Integer>> entry : data.entrySet()) {
            String claveUnica = entry.getKey(); 
            
            // 🚨 LIMPIEZA DE LA ETIQUETA: Muestra solo el nombre de la actividad
            // Busca la primera aparición de "(ID: X)" y lo reemplaza por una cadena vacía.
            String etiquetaLimpia = claveUnica.replaceFirst("\\s*\\(ID:\\s*\\d+\\)$", "");
            
            Map<String, Integer> ocupacion = entry.getValue();
            
            aforoSeries.getData().add(new XYChart.Data<>(etiquetaLimpia, ocupacion.get("AFORO")));
            inscritosSeries.getData().add(new XYChart.Data<>(etiquetaLimpia, ocupacion.get("INSCRITOS")));
        }

        ocupacionChart.getData().clear();
        ocupacionChart.getData().addAll(aforoSeries, inscritosSeries);
        ocupacionChart.setTitle("Ocupación de Clases (por Instancia)");
        
        // Ajustamos la etiqueta del eje X para reflejar la simplificación
        ocupacionX.setLabel("Actividad");

    } catch (SQLException e) {
        System.err.println("Error al cargar datos de Ocupación por Clase: " + e.getMessage());
    }
        }
    
}


