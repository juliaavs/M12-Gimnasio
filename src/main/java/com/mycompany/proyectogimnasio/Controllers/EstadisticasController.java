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
    
    @FXML private BarChart<String, Number> ocupacionChart;
    @FXML private CategoryAxis ocupacionX;
    @FXML private BarChart<String, Number> estadoInscripcionesBarChart;
    @FXML private CategoryAxis estadoInscripcionesX;
    @FXML private NumberAxis estadoInscripcionesY;

    @FXML private PieChart clasesPorInstructorChart;
    // --- FIN NUEVOS GRÁFICOS ---
    
    private EstadisticasService estadisticasService;

    @FXML
    public void initialize() {
        estadisticasService = new EstadisticasService();
        loadClasesPorActividadData();
        // Llamada a las nuevas funciones de carga
        loadInscripcionesPorEstadoData();
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
    
    private void loadInscripcionesPorEstadoData() {
    try {
        Map<String, Integer> data = estadisticasService.getInscripcionesPorEstado();
        
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Reservas por Estado");
        
        int total = 0;
        for (Map.Entry<String, Integer> entry : data.entrySet()) {
            String estado = entry.getKey().toUpperCase();
            int count = entry.getValue();
            
            // Creamos los datos del gráfico de barras
            series.getData().add(new XYChart.Data<>(estado, count));
            total += count;
        }

        estadoInscripcionesBarChart.getData().clear();
        estadoInscripcionesBarChart.getData().add(series);
        
        // Título del gráfico de barras
        estadoInscripcionesBarChart.setTitle("Reservas por Estado (Total: " + total + ")");
        
    } catch (SQLException e) {
        System.err.println("Error al cargar datos de Inscripciones por Estado: " + e.getMessage());
    }
}
    }

    
    