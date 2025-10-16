package com.mycompany.proyectogimnasio.Controllers;

import com.mycompany.proyectogimnasio.Models.ClaseInfo;
import com.mycompany.proyectogimnasio.Service.ClaseService;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import java.io.IOException;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.function.Function;

public class HorarioController {

    //<editor-fold desc="FXML UI Components - TABLA MAÑANA">
    @FXML private TableView<HorarioBloque> tablaHorarioManana;
    @FXML private TableColumn<HorarioBloque, String> colHoraManana;
    @FXML private TableColumn<HorarioBloque, String> colLunesManana;
    @FXML private TableColumn<HorarioBloque, String> colMartesManana;
    @FXML private TableColumn<HorarioBloque, String> colMiercolesManana;
    @FXML private TableColumn<HorarioBloque, String> colJuevesManana;
    @FXML private TableColumn<HorarioBloque, String> colViernesManana;
    @FXML private TableColumn<HorarioBloque, String> colSabadoManana;
    @FXML private TableColumn<HorarioBloque, String> colDomingoManana;
    //</editor-fold>

    //<editor-fold desc="FXML UI Components - TABLA TARDE">
    @FXML private TableView<HorarioBloque> tablaHorarioTarde;
    @FXML private TableColumn<HorarioBloque, String> colHoraTarde;
    @FXML private TableColumn<HorarioBloque, String> colLunesTarde;
    @FXML private TableColumn<HorarioBloque, String> colMartesTarde;
    @FXML private TableColumn<HorarioBloque, String> colMiercolesTarde;
    @FXML private TableColumn<HorarioBloque, String> colJuevesTarde;
    @FXML private TableColumn<HorarioBloque, String> colViernesTarde;
    @FXML private TableColumn<HorarioBloque, String> colSabadoTarde;
    @FXML private TableColumn<HorarioBloque, String> colDomingoTarde;
    //</editor-fold>

    private final ClaseService claseService = new ClaseService();
    private String usuarioActual;
    private String rolActual;

    @FXML
    public void initialize() {
        // --- CONFIGURACIÓN TABLA DE MAÑANA ---
        setupTableColumns(
            colHoraManana, colLunesManana, colMartesManana, colMiercolesManana, colJuevesManana,
            colViernesManana, colSabadoManana, colDomingoManana
        );
        tablaHorarioManana.getSelectionModel().setCellSelectionEnabled(true);

        // --- CONFIGURACIÓN TABLA DE TARDE ---
        setupTableColumns(
            colHoraTarde, colLunesTarde, colMartesTarde, colMiercolesTarde, colJuevesTarde,
            colViernesTarde, colSabadoTarde, colDomingoTarde
        );
        tablaHorarioTarde.getSelectionModel().setCellSelectionEnabled(true);
        
        // Cargar datos en ambas tablas
        loadHorarioData();
    }

    private void setupTableColumns(TableColumn<HorarioBloque, String> hora, TableColumn<HorarioBloque, String> lunes,
                                   TableColumn<HorarioBloque, String> martes, TableColumn<HorarioBloque, String> miercoles,
                                   TableColumn<HorarioBloque, String> jueves, TableColumn<HorarioBloque, String> viernes,
                                   TableColumn<HorarioBloque, String> sabado, TableColumn<HorarioBloque, String> domingo) {
        
        hora.setCellValueFactory(cellData -> cellData.getValue().horaProperty());
        lunes.setCellValueFactory(cellData -> cellData.getValue().lunesProperty());
        martes.setCellValueFactory(cellData -> cellData.getValue().martesProperty());
        miercoles.setCellValueFactory(cellData -> cellData.getValue().miercolesProperty());
        jueves.setCellValueFactory(cellData -> cellData.getValue().juevesProperty());
        viernes.setCellValueFactory(cellData -> cellData.getValue().viernesProperty());
        sabado.setCellValueFactory(cellData -> cellData.getValue().sabadoProperty());
        domingo.setCellValueFactory(cellData -> cellData.getValue().domingoProperty());

        lunes.setCellFactory(col -> new MergedCell<>(HorarioBloque::lunesProperty));
        martes.setCellFactory(col -> new MergedCell<>(HorarioBloque::martesProperty));
        miercoles.setCellFactory(col -> new MergedCell<>(HorarioBloque::miercolesProperty));
        jueves.setCellFactory(col -> new MergedCell<>(HorarioBloque::juevesProperty));
        viernes.setCellFactory(col -> new MergedCell<>(HorarioBloque::viernesProperty));
        sabado.setCellFactory(col -> new MergedCell<>(HorarioBloque::sabadoProperty));
        domingo.setCellFactory(col -> new MergedCell<>(HorarioBloque::domingoProperty));
    }

    private void loadHorarioData() {
        List<ClaseInfo> clasesProgramadas = claseService.getClasesProgramadas();
        
        // Datos para la tabla de la mañana
        ObservableList<HorarioBloque> dataManana = FXCollections.observableArrayList();
        LocalTime currentTime = LocalTime.of(7, 0);
        while (currentTime.isBefore(LocalTime.of(13, 0))) {
            dataManana.add(crearFilaHorario(currentTime, clasesProgramadas));
            currentTime = currentTime.plusMinutes(15);
        }
        tablaHorarioManana.setItems(dataManana);

        // Datos para la tabla de la tarde
        ObservableList<HorarioBloque> dataTarde = FXCollections.observableArrayList();
        currentTime = LocalTime.of(15, 0);
        while (currentTime.isBefore(LocalTime.of(19, 0))) {
            dataTarde.add(crearFilaHorario(currentTime, clasesProgramadas));
            currentTime = currentTime.plusMinutes(15);
        }
        tablaHorarioTarde.setItems(dataTarde);
    }

    private HorarioBloque crearFilaHorario(LocalTime horaActual, List<ClaseInfo> clasesProgramadas) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
        String[] clasesDelBloque = new String[7];
        for (int i = 0; i < 7; i++) {
            String diaActual = getDiaDeLaSemana(i);
            String textoCelda = "";
            for (ClaseInfo clase : clasesProgramadas) {
                if (clase.getDia().equalsIgnoreCase(diaActual)) {
                    if (!horaActual.isBefore(clase.getHoraInicio()) && horaActual.isBefore(clase.getHoraFin())) {
                        textoCelda = clase.getNombreActividad() + " (" + clase.getNombreInstructor() + ")";
                        break;
                    }
                }
            }
            clasesDelBloque[i] = textoCelda;
        }
        return new HorarioBloque(horaActual.format(formatter), clasesDelBloque[0], clasesDelBloque[1],
                clasesDelBloque[2], clasesDelBloque[3], clasesDelBloque[4], clasesDelBloque[5], clasesDelBloque[6]);
    }

    @FXML private void handleRefresh() { loadHorarioData(); }
    @FXML private void handleAddClass() throws IOException { /* Tu lógica aquí */ }
    public void setUser(String usuario, String rol) { this.usuarioActual = usuario; this.rolActual = rol; }
    private String getDiaDeLaSemana(int index) {
        switch (index) { case 0: return "lunes"; case 1: return "martes"; case 2: return "miércoles"; case 3: return "jueves"; case 4: return "viernes"; case 5: return "sábado"; case 6: return "domingo"; default: return ""; }
    }

    // --- Clases Internas (HorarioBloque y MergedCell no cambian) ---
    public static class HorarioBloque {
        private final SimpleStringProperty hora, lunes, martes, miercoles, jueves, viernes, sabado, domingo;
        public HorarioBloque(String h, String l, String m, String mi, String j, String v, String s, String d) { hora = new SimpleStringProperty(h); lunes = new SimpleStringProperty(l); martes = new SimpleStringProperty(m); miercoles = new SimpleStringProperty(mi); jueves = new SimpleStringProperty(j); viernes = new SimpleStringProperty(v); sabado = new SimpleStringProperty(s); domingo = new SimpleStringProperty(d); }
        public SimpleStringProperty horaProperty() { return hora; } public SimpleStringProperty lunesProperty() { return lunes; } public SimpleStringProperty martesProperty() { return martes; } public SimpleStringProperty miercolesProperty() { return miercoles; } public SimpleStringProperty juevesProperty() { return jueves; } public SimpleStringProperty viernesProperty() { return viernes; } public SimpleStringProperty sabadoProperty() { return sabado; } public SimpleStringProperty domingoProperty() { return domingo; }
    }

    public static class MergedCell<S, T> extends TableCell<S, T> {
        private final Function<S, StringProperty> propertyExtractor;
        private static final String STYLE_EMPTY = "-fx-background-color: white; -fx-border-color: #E0E0E0; -fx-border-width: 0 1 1 0;";
        private static final String TEXT_STYLE_LIGHT = "-fx-text-fill: white; -fx-font-weight: bold; -fx-alignment: CENTER;";
        private static final String TEXT_STYLE_DARK = "-fx-text-fill: #2c3e50; -fx-font-weight: bold; -fx-alignment: CENTER;";
        public MergedCell(Function<S, StringProperty> propertyExtractor) { this.propertyExtractor = propertyExtractor; }
        @Override
        protected void updateItem(T item, boolean empty) {
            super.updateItem(item, empty);
            setText(null);
            if (item == null || empty || item.toString().isEmpty()) { setStyle(STYLE_EMPTY); return; }
            String currentText = item.toString(); int currentIndex = getIndex(); ObservableList<S> items = getTableView().getItems(); int maxIndex = items.size() - 1;
            boolean sameAsPrevious = currentIndex > 0 && currentText.equals(propertyExtractor.apply(items.get(currentIndex - 1)).get());
            boolean sameAsNext = currentIndex < maxIndex && currentText.equals(propertyExtractor.apply(items.get(currentIndex + 1)).get());
            String activityName = currentText.split(" ")[0].toLowerCase(); String colorStyle = getColorStyleForActivity(activityName); String textStyle = getTextStyleForActivity(activityName);
            String finalStyle = colorStyle + "-fx-border-color: transparent;";
            if (!sameAsPrevious && sameAsNext) { setText(currentText); finalStyle += textStyle + "-fx-background-radius: 6px 6px 0 0;";
            } else if (sameAsPrevious && sameAsNext) { finalStyle += "-fx-background-radius: 0;";
            } else if (sameAsPrevious && !sameAsNext) { finalStyle += "-fx-background-radius: 0 0 6px 6px;";
            } else { setText(currentText); finalStyle += textStyle + "-fx-background-radius: 6px;"; }
            setStyle(finalStyle);
        }
        private String getColorStyleForActivity(String activityName) {
            switch (activityName) { case "yoga": return "-fx-background-color: #e74c3c;"; case "crossfit": return "-fx-background-color: #3498db;"; case "spinning": return "-fx-background-color: #f1c40f;"; case "zumba": return "-fx-background-color: #9b59b6;"; default: return "-fx-background-color: #bdc3c7;"; }
        }
        private String getTextStyleForActivity(String activityName) {
            switch (activityName) { case "spinning": return TEXT_STYLE_DARK; default: return TEXT_STYLE_LIGHT; }
        }
    }
}