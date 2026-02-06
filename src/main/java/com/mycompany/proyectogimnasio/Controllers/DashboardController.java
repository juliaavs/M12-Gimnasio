package com.mycompany.proyectogimnasio.Controllers;

import com.mycompany.proyectogimnasio.App;
import com.mycompany.proyectogimnasio.Models.ClaseInfo;
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
import java.util.List;
import java.util.Map;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableCell;
import javafx.scene.paint.Color;
import javafx.beans.property.SimpleStringProperty;
import java.time.format.DateTimeFormatter;
import java.time.LocalTime;
// Nuevas importaciones para diagnóstico de fecha
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.ZoneId;

public class DashboardController {
    
    // ... (Variables FXML no modificadas) ...
    @FXML private BorderPane mainPane; 
    @FXML private Button logoutButton;
    @FXML private Button btnInstructores; 
    @FXML private PieChart clasesPorActividadChart;
    @FXML private BarChart<String, Number> ocupacionChart;
    @FXML private CategoryAxis ocupacionX;
    
    @FXML private Label totalInstructorsLabel;
    @FXML private Label instructorsChangeLabel;
    @FXML private Label totalClientsLabel;
    @FXML private Label clientsChangeLabel;
    @FXML private Label todayReservationsLabel;
    @FXML private Label reservationsChangeLabel;
    
    @FXML private TableView<ClaseInfo> scheduleTable;
    @FXML private TableColumn<ClaseInfo, String> colHoyHora;
    @FXML private TableColumn<ClaseInfo, String> colHoyActividad;
    @FXML private TableColumn<ClaseInfo, String> colHoyInstructor;
    
    private EstadisticasService estadisticasService;
    private final InstructorService instructorService = new InstructorService();
    private final ClienteService clienteService = new ClienteService();
    private final ReservasService reservasService = new ReservasService(); 
    

    @FXML
    public void initialize() {
        estadisticasService = new EstadisticasService();
        
        setupScheduleTableColumns();
        
        // 1. Cargamos todos los datos una sola vez
        loadDashboardData();
        loadOcupacionPorClaseData();
        loadTodaySchedule(); 
    }
    
    private void setupScheduleTableColumns() {
        // Añadimos una comprobación básica por si acaso, aunque descartamos que sea el problema.
        if (colHoyHora == null || colHoyInstructor == null || colHoyActividad == null) {
            System.err.println("ADVERTENCIA FXML: Alguna columna de la tabla de horario es NULL. Esto puede ser un problema de carga de FXML en el JAR.");
            return;
        }

        colHoyHora.setCellValueFactory(cellData -> {
            LocalTime hora = cellData.getValue().getHoraInicio();
            return new SimpleStringProperty(hora.format(DateTimeFormatter.ofPattern("HH:mm")));
        });

        colHoyInstructor.setCellValueFactory(cellData -> 
            new SimpleStringProperty(cellData.getValue().getNombreInstructor())
        );
        
        colHoyActividad.setCellValueFactory(cellData -> 
            new SimpleStringProperty(cellData.getValue().getNombreActividad())
        );
        
        colHoyActividad.setCellFactory(column -> new TableCell<ClaseInfo, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(item);
                    String activityName = item.toLowerCase();
                    String bgColor = getColor(activityName);
                    String textColor = getTextColor(activityName);
                    setStyle("-fx-background-color: " + bgColor + "; -fx-text-fill: " + textColor + "; -fx-font-weight: bold; -fx-alignment: CENTER;");
                }
            }
        });
    }

    
    private void loadDashboardData() {
        // ... (Tu código existente) ...
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
            System.err.println("Error al cargar datos del Dashboard (etiquetas): " + e.getMessage());
            totalInstructorsLabel.setText("ERROR");
            totalClientsLabel.setText("ERROR");
            todayReservationsLabel.setText("ERROR");
        }
    }

    private void loadTodaySchedule() {
        if (scheduleTable == null) {
            System.err.println("ERROR CRÍTICO: La TableView 'scheduleTable' es NULL. (FALLO DE ENLACE FXML/JAR)");
            return; 
        }
        
        System.out.println("DEBUG: Iniciando carga de horario de clases..."); // DEBUG START
        
        // --- DIAGNÓSTICO DE FECHA Y HORA ---
        DayOfWeek todayDayOfWeek = LocalDate.now().getDayOfWeek();
        ZoneId currentZone = ZoneId.systemDefault();
        System.out.println("DIAGNÓSTICO FECHA: Día Java: " + todayDayOfWeek.name());
        System.out.println("DIAGNÓSTICO ZONA: Zona horaria del sistema: " + currentZone.getId());
        // El log anterior mostraba "DEBUG: Día de la semana calculado para SQL: Fri". Ahora comparamos con Java.
        
        try {
            List<ClaseInfo> todayClasses = estadisticasService.getClasesDeHoy();
            
            if (todayClasses == null || todayClasses.isEmpty()) {
                System.out.println("DEBUG: La consulta SQL DEVOLVIÓ CERO clases. Esto es normal si no hay datos.");
                scheduleTable.setPlaceholder(new Label("No hay clases programadas para hoy."));
            } else {
                System.out.println("DEBUG: Horario cargado con " + todayClasses.size() + " clases.");
                ObservableList<ClaseInfo> items = FXCollections.observableArrayList(todayClasses);
                scheduleTable.setItems(items);
            }
            
            System.out.println("DEBUG: Carga de horario finalizada."); // DEBUG END

        } catch (SQLException e) {
             System.err.println("ERROR SQL CRÍTICO (JAR/DB): Fallo al consultar la base de datos.");
             System.err.println("MENSAJE SQL: " + e.getMessage());
             scheduleTable.setPlaceholder(new Label("ERROR DB: " + e.getMessage()));
             e.printStackTrace();
        } catch (Exception e) {
             System.err.println("ERROR GENERAL (JAR): Fallo inesperado durante la carga del horario.");
             scheduleTable.setPlaceholder(new Label("ERROR GENERAL: " + e.getMessage()));
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
    
    private void showDashboardView() {
        // Mantenemos este método, aunque la navegación principal debe ser a través de App.showDashboard
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
            // mainPane sigue siendo la referencia al BorderPane que contiene el contenido central del dashboard.fxml
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
    
    // ... (El resto de tus métodos de color) ...

    private Color getColorObject(String activityName) {
        switch (activityName.toLowerCase()) {
            case "yoga": return Color.web("#e74c3c");
            case "crossfit": return Color.web("#3498db");
            case "spinning": return Color.web("#f1c40f");
            case "zumba": return Color.web("#9b59b6");
            case "pilates": return Color.web("#1abc9c");
            default:
                int hash = activityName.hashCode();
                double hue = (Math.abs(hash) % 360);
                return Color.hsb(hue, 0.75, 0.85); 
        }
    }

    private String getColor(String activityName) {
        Color color = getColorObject(activityName);
        return String.format("#%02X%02X%02X",
            (int) (color.getRed() * 255),
            (int) (color.getGreen() * 255),
            (int) (color.getBlue() * 255));
    }

    private String getTextColor(String activityName) {
        Color color = getColorObject(activityName);
        double luminance = (0.299 * color.getRed() + 0.587 * color.getGreen() + 0.114 * color.getBlue());
        if (luminance > 0.5) {
            return "#2c3e50"; // Texto oscuro
        } else {
            return "white"; // Texto claro
        }
    }
}