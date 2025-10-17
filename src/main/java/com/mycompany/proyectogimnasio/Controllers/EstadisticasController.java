package com.mycompany.proyectogimnasio.Controllers;

import com.mycompany.proyectogimnasio.Service.EstadisticasService;
import javafx.fxml.FXML;
import javafx.scene.chart.*;
import javafx.scene.control.Label;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.sql.SQLException;
import java.util.Map;

public class EstadisticasController {

    // Referencias FXML originales (Mantenemos el PieChart)
    @FXML private PieChart clasesPorActividadChart;
    @FXML private Label totalClasesLabel; 
   
    // --- NUEVOS GRÁFICOS ---
    @FXML private BarChart<String, Number> aforoChart;
    @FXML private BarChart<String, Number> ocupacionChart;
    @FXML private CategoryAxis aforoX;
    @FXML private NumberAxis aforoY;

    @FXML private PieChart clasesPorInstructorChart;
    // --- FIN NUEVOS GRÁFICOS ---
    
    private EstadisticasService estadisticasService;

    @FXML
    public void initialize() {
        estadisticasService = new EstadisticasService();
        loadClasesPorActividadData();
        // Llamada a las nuevas funciones de carga
        loadAforoPorActividadData();
        loadClasesPorInstructorData();
        loadOcupacionPorClaseData();
        
    }

    // Código de loadClasesPorActividadData (El original, se mantiene)
    private void loadClasesPorActividadData() {
         try {
            Map<String, Integer> data = estadisticasService.getClasesPorActividad();
            ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList();
            
            int total = 0;
            for (Map.Entry<String, Integer> entry : data.entrySet()) {
                pieChartData.add(new PieChart.Data(entry.getKey(), entry.getValue()));
                total += entry.getValue();
            }

            clasesPorActividadChart.setData(pieChartData);
            clasesPorActividadChart.setTitle("Distribución de Clases por Actividad");
            totalClasesLabel.setText("Total de Clases registradas: " + total);

        } catch (SQLException e) {
            System.err.println("Error al cargar datos de Clases por Actividad: " + e.getMessage());
        }
    }
    
    // --- NUEVA FUNCIÓN DE CARGA 1: Aforo por Clase ---
    private void loadAforoPorActividadData() {
        try {
            Map<String, Integer> data = estadisticasService.getAforoPorActividad();
            
            XYChart.Series<String, Number> series = new XYChart.Series<>();
            series.setName("Aforo Máximo");

            for (Map.Entry<String, Integer> entry : data.entrySet()) {
                series.getData().add(new XYChart.Data<>(entry.getKey(), entry.getValue()));
            }

            aforoChart.getData().clear();
            aforoChart.getData().add(series);
            aforoChart.setTitle("Aforo Máximo por Tipo de Clase");

        } catch (SQLException e) {
            System.err.println("Error al cargar datos de Aforo por Actividad: " + e.getMessage());
        }
    }
    
    // --- NUEVA FUNCIÓN DE CARGA 2: Clases por Instructor (Pie Chart) ---
    private void loadClasesPorInstructorData() {
        try {
            Map<String, Integer> data = estadisticasService.getClasesPorInstructor();
            ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList();
            
            for (Map.Entry<String, Integer> entry : data.entrySet()) {
                pieChartData.add(new PieChart.Data(entry.getKey(), entry.getValue()));
            }

            clasesPorInstructorChart.setData(pieChartData);
            clasesPorInstructorChart.setTitle("Clases Impartidas por Instructor");

        } catch (SQLException e) {
            System.err.println("Error al cargar datos de Clases por Instructor: " + e.getMessage());
        }
    
    }
    
    private void loadOcupacionPorClaseData() {
    try {
        Map<String, Map<String, Integer>> data = estadisticasService.getOcupacionPorClase();
        
        // Series para los datos de Aforo Máximo
        XYChart.Series<String, Number> aforoSeries = new XYChart.Series<>();
        aforoSeries.setName("Aforo Máximo");
        
        // Series para los datos de Inscritos
        XYChart.Series<String, Number> inscritosSeries = new XYChart.Series<>();
        inscritosSeries.setName("Inscritos Confirmados");
        
        // Llenar las series
        for (Map.Entry<String, Map<String, Integer>> entry : data.entrySet()) {
            String clase = entry.getKey();
            Map<String, Integer> ocupacion = entry.getValue();
            
            aforoSeries.getData().add(new XYChart.Data<>(clase, ocupacion.get("AFORO")));
            inscritosSeries.getData().add(new XYChart.Data<>(clase, ocupacion.get("INSCRITOS")));
        }

        ocupacionChart.getData().clear();
        ocupacionChart.getData().addAll(aforoSeries, inscritosSeries);
        ocupacionChart.setTitle("Ocupación de Clases");

    } catch (SQLException e) {
        System.err.println("Error al cargar datos de Ocupación por Clase: " + e.getMessage());
    }
    }
}
    
    