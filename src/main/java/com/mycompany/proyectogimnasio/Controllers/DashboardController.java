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
import java.util.List; // <-- IMPORTAR List
import java.util.Map;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.control.ListView; // <-- IMPORTAR ListView

public class DashboardController {
    
    @FXML
    private BorderPane mainPane; 

    @FXML
    private Button logoutButton;
    
    @FXML
    private Button btnInstructores; 
    
    @FXML private PieChart clasesPorActividadChart; // No se usa en esta versión, pero estaba
    
    @FXML private BarChart<String, Number> ocupacionChart;
    
    @FXML private CategoryAxis ocupacionX;
    
    
    @FXML private Label totalInstructorsLabel;
    @FXML private Label instructorsChangeLabel;
    @FXML private Label totalClientsLabel;
    @FXML private Label clientsChangeLabel;
    @FXML private Label todayReservationsLabel;
    @FXML private Label reservationsChangeLabel;
    
    @FXML private ListView<String> scheduleList; // <-- Inyección para el horario de hoy

    
    private EstadisticasService estadisticasService;
    private final InstructorService instructorService = new InstructorService();
    private final ClienteService clienteService = new ClienteService();
    private final ReservasService reservasService = new ReservasService(); 

    @FXML
    public void initialize() {
        estadisticasService = new EstadisticasService();
        
        loadDashboardData();
        loadOcupacionPorClaseData();
        loadTodaySchedule(); // <-- AÑADIDA LA LLAMADA
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
            System.err.println("Error al cargar datos del Dashboard: " + e.getMessage());
            totalInstructorsLabel.setText("ERROR");
            totalClientsLabel.setText("ERROR");
            todayReservationsLabel.setText("ERROR");
        }
    }

    /**
     * MÉTODO NUEVO
     * Carga el horario del día de hoy en el ListView.
     */
    private void loadTodaySchedule() {
        try {
            // Asumimos que el servicio tiene este método
            List<String> todayClasses = estadisticasService.getClasesDeHoy();
            
            if (todayClasses.isEmpty()) {
                scheduleList.setPlaceholder(new Label("No hay clases programadas para hoy."));
            } else {
                ObservableList<String> items = FXCollections.observableArrayList(todayClasses);
                scheduleList.setItems(items);
            }

        } catch (Exception e) {
             System.err.println("Error al cargar el horario de hoy: " + e.getMessage());
             scheduleList.setPlaceholder(new Label("Error al cargar el horario."));
             e.printStackTrace();
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
        showDashboardView();
    }

    private void showDashboardView() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/mycompany/proyectogimnasio/dashboard.fxml"));
            Parent root = loader.load();
            App.getPrimaryStage().getScene().setRoot(root);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void handleBtnInstructores() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/mycompany/proyectogimnasio/InstructorView.fxml"));
            Parent vistaInstructores = loader.load(); 
            mainPane.setCenter(vistaInstructores);
        } catch (IOException e) {
            System.err.println("Error al cargar la vista de Instructores.");
            e.printStackTrace();
        }
    }
    
    @FXML
    private void handleBtnEstadisticas() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/mycompany/proyectogimnasio/EstadisticasView.fxml"));
            Parent vistaEstadisticas = loader.load(); 
            mainPane.setCenter(vistaEstadisticas);
        } catch (IOException e) {
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
            
            for (Map.Entry<String, Map<String, Integer>> entry : data.entrySet()) {
                String claveUnica = entry.getKey(); 
                
                String etiquetaLimpia = claveUnica.replaceFirst("\\s*\\(ID:\\s*\\d+\\)$", "");
                
                Map<String, Integer> ocupacion = entry.getValue();
                
                aforoSeries.getData().add(new XYChart.Data<>(etiquetaLimpia, ocupacion.get("AFORO")));
                
                // --- CORRECCIÓN DE TIPEO ---
                inscritosSeries.getData().add(new XYChart.Data<>(etiquetaLimpia, ocupacion.get("INSCRITOS")));
            }

            ocupacionChart.getData().clear();
            ocupacionChart.getData().addAll(aforoSeries, inscritosSeries);
            ocupacionChart.setTitle("Ocupación de Clases (por Instancia)");
            
            ocupacionX.setLabel("Actividad");

        } catch (SQLException e) {
            System.err.println("Error al cargar datos de Ocupación por Clase: " + e.getMessage());
        }
    }
}