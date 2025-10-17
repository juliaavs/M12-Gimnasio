package com.mycompany.proyectogimnasio.Controllers;

import com.mycompany.proyectogimnasio.Models.ClaseInfo;
import com.mycompany.proyectogimnasio.Service.ClaseService;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.*;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class HorarioController {

    //<editor-fold desc="FXML UI Components">
    @FXML private TableView<HorarioBloque> tablaHorarioManana;
    @FXML private TableColumn<HorarioBloque, String> colHoraManana;
    @FXML private TableColumn<HorarioBloque, ClaseInfo> colLunesManana, colMartesManana, colMiercolesManana, colJuevesManana, colViernesManana, colSabadoManana, colDomingoManana;

    @FXML private TableView<HorarioBloque> tablaHorarioTarde;
    @FXML private TableColumn<HorarioBloque, String> colHoraTarde;
    @FXML private TableColumn<HorarioBloque, ClaseInfo> colLunesTarde, colMartesTarde, colMiercolesTarde, colJuevesTarde, colViernesTarde, colSabadoTarde, colDomingoTarde;
    
    // --- Button Declarations ---
    @FXML private Button btnEditar;
    @FXML private Button btnDescartar;
    @FXML private Button btnActualizar; // <-- **CAMBIO CLAVE**: Añadido FXML para el botón de actualizar
    //</editor-fold>

    private final ClaseService claseService = new ClaseService();
    private boolean isInEditMode = false;
    private final Map<Integer, ClaseInfo> clasesModificadas = new HashMap<>();
    private static final DataFormat CLASE_INFO_FORMAT = new DataFormat("com.mycompany.proyectogimnasio.Models.ClaseInfo");
    private List<ClaseInfo> clasesProgramadasCache;
    private String usuarioActual;
    private String rolActual;

    @FXML
    public void initialize() {
        setupTableColumns(colHoraManana, colLunesManana, colMartesManana, colMiercolesManana, colJuevesManana, colViernesManana, colSabadoManana, colDomingoManana);
        setupTableColumns(colHoraTarde, colLunesTarde, colMartesTarde, colMiercolesTarde, colJuevesTarde, colViernesTarde, colSabadoTarde, colDomingoTarde);
        
        // --- **CAMBIO CLAVE**: Configuración inicial de los botones ---
        btnDescartar.setVisible(false);
        btnDescartar.setManaged(false); // No ocupa espacio
        
        loadDataFromDatabase();
    }
    
    private void setupTableColumns(TableColumn<HorarioBloque, String> hora, TableColumn<HorarioBloque, ClaseInfo>... dias) {
        hora.setCellValueFactory(cellData -> cellData.getValue().horaProperty());
        dias[0].setCellValueFactory(cellData -> cellData.getValue().lunesProperty());dias[1].setCellValueFactory(cellData -> cellData.getValue().martesProperty());dias[2].setCellValueFactory(cellData -> cellData.getValue().miercolesProperty());dias[3].setCellValueFactory(cellData -> cellData.getValue().juevesProperty());dias[4].setCellValueFactory(cellData -> cellData.getValue().viernesProperty());dias[5].setCellValueFactory(cellData -> cellData.getValue().sabadoProperty());dias[6].setCellValueFactory(cellData -> cellData.getValue().domingoProperty());
        dias[0].setCellFactory(c->new EditableMergedCell<>(HorarioBloque::lunesProperty));dias[1].setCellFactory(c->new EditableMergedCell<>(HorarioBloque::martesProperty));dias[2].setCellFactory(c->new EditableMergedCell<>(HorarioBloque::miercolesProperty));dias[3].setCellFactory(c->new EditableMergedCell<>(HorarioBloque::juevesProperty));dias[4].setCellFactory(c->new EditableMergedCell<>(HorarioBloque::viernesProperty));dias[5].setCellFactory(c->new EditableMergedCell<>(HorarioBloque::sabadoProperty));dias[6].setCellFactory(c->new EditableMergedCell<>(HorarioBloque::domingoProperty));
    }
    
    private void loadDataFromDatabase() {
        this.clasesProgramadasCache = claseService.getClasesProgramadas();
        redrawTablesFromCache();
    }

    private void redrawTablesFromCache() {
        ObservableList<HorarioBloque> dataManana = FXCollections.observableArrayList();
        for (LocalTime t = LocalTime.of(7, 0); t.isBefore(LocalTime.of(13, 0)); t = t.plusMinutes(15)) {
            dataManana.add(crearFilaHorario(t, this.clasesProgramadasCache));
        }
        tablaHorarioManana.setItems(dataManana);
        ObservableList<HorarioBloque> dataTarde = FXCollections.observableArrayList();
        for (LocalTime t = LocalTime.of(15, 0); t.isBefore(LocalTime.of(19, 0)); t = t.plusMinutes(15)) {
            dataTarde.add(crearFilaHorario(t, this.clasesProgramadasCache));
        }
        tablaHorarioTarde.setItems(dataTarde);
    }

    private HorarioBloque crearFilaHorario(LocalTime hora, List<ClaseInfo> clases) {
        ClaseInfo[] clasesDelBloque = new ClaseInfo[7];
        String[] dias = {"lunes", "martes", "miércoles", "jueves", "viernes", "sábado", "domingo"};
        for (int i = 0; i < dias.length; i++) {for (ClaseInfo clase : clases) {if (clase.getDia().equalsIgnoreCase(dias[i]) && !hora.isBefore(clase.getHoraInicio()) && hora.isBefore(clase.getHoraFin())) {clasesDelBloque[i] = clase;break;}}}
        return new HorarioBloque(hora.format(DateTimeFormatter.ofPattern("HH:mm")), clasesDelBloque);
    }
    
    @FXML private void toggleEditMode() {
        isInEditMode = !isInEditMode;
        if (isInEditMode) {
            // --- Entrando en MODO EDICIÓN ---
            btnEditar.setText("Guardar Cambios");
            btnEditar.setStyle("-fx-background-color: #2ecc71; -fx-text-fill: white; -fx-font-weight: bold;");
            
            btnDescartar.setVisible(true);
            btnDescartar.setManaged(true);
            
            btnActualizar.setVisible(false);
            btnActualizar.setManaged(false);
        } else {
            // --- Saliendo de MODO EDICIÓN ---
            btnEditar.setText("Editar Horario");
            btnEditar.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-font-weight: bold;");
            
            btnDescartar.setVisible(false);
            btnDescartar.setManaged(false);
            
            btnActualizar.setVisible(true);
            btnActualizar.setManaged(true);
            
            guardarCambios();
        }
        tablaHorarioManana.refresh();
        tablaHorarioTarde.refresh();
    }
    
    private void guardarCambios() {
        if (clasesModificadas.isEmpty()) return;
        long exitosos = clasesModificadas.values().stream()
            .filter(clase -> claseService.actualizarClase(clase.getIdClase(), clase.getDia(), clase.getHoraInicio()))
            .count();
        new Alert(Alert.AlertType.INFORMATION, exitosos + " de " + clasesModificadas.size() + " cambios fueron guardados.").showAndWait();
        clasesModificadas.clear();
        loadDataFromDatabase();
    }

    @FXML private void handleRefresh() {
        loadDataFromDatabase();
    }

    @FXML
    private void handleDiscardChanges() {
        clasesModificadas.clear();
        loadDataFromDatabase();
        if (isInEditMode) {
            toggleEditMode(); // Llama a toggle para salir del modo edición y resetear los botones
        }
    }

    private String getMoveValidationError(ClaseInfo claseMovida, LocalTime nuevaHora, String nuevoDia) {
        final LocalTime HORA_FIN_MANANA=LocalTime.of(13,0);final LocalTime HORA_FIN_TARDE=LocalTime.of(19,0);LocalTime finNuevaClase=nuevaHora.plusMinutes(claseMovida.getDuracionMinutos());
        if((nuevaHora.isBefore(HORA_FIN_MANANA)&&finNuevaClase.isAfter(HORA_FIN_MANANA))||finNuevaClase.isAfter(HORA_FIN_TARDE)){return"La clase invade la pausa del almuerzo o termina después del cierre.";}
        for(ClaseInfo claseExistente:this.clasesProgramadasCache){if(claseExistente.getIdClase()==claseMovida.getIdClase())continue;if(claseExistente.getDia().equalsIgnoreCase(nuevoDia)){if(nuevaHora.isBefore(claseExistente.getHoraFin())&&finNuevaClase.isAfter(claseExistente.getHoraInicio())){return"El horario se solapa con la clase de '"+claseExistente.getNombreActividad()+"'.";}}}
        return null;
    }

    public void setUser(String u, String r) { this.usuarioActual = u; this.rolActual = r; }

    public static class HorarioBloque {
        private final SimpleObjectProperty<String> hora;private final SimpleObjectProperty<ClaseInfo> lunes, martes, miercoles, jueves, viernes, sabado, domingo;
        public HorarioBloque(String h, ClaseInfo... c){hora=new SimpleObjectProperty<>(h);lunes=new SimpleObjectProperty<>(c[0]);martes=new SimpleObjectProperty<>(c[1]);miercoles=new SimpleObjectProperty<>(c[2]);jueves=new SimpleObjectProperty<>(c[3]);viernes=new SimpleObjectProperty<>(c[4]);sabado=new SimpleObjectProperty<>(c[5]);domingo=new SimpleObjectProperty<>(c[6]);}
        public SimpleObjectProperty<String> horaProperty(){return hora;}public SimpleObjectProperty<ClaseInfo> lunesProperty(){return lunes;}public SimpleObjectProperty<ClaseInfo> martesProperty(){return martes;}public SimpleObjectProperty<ClaseInfo> miercolesProperty(){return miercoles;}public SimpleObjectProperty<ClaseInfo> juevesProperty(){return jueves;}public SimpleObjectProperty<ClaseInfo> viernesProperty(){return viernes;}public SimpleObjectProperty<ClaseInfo> sabadoProperty(){return sabado;}public SimpleObjectProperty<ClaseInfo> domingoProperty(){return domingo;}
    }

    public class EditableMergedCell<S> extends TableCell<S, ClaseInfo> {
        private final Function<S, ObjectProperty<ClaseInfo>> propertyExtractor;
        public EditableMergedCell(Function<S, ObjectProperty<ClaseInfo>> extractor){this.propertyExtractor = extractor;setOnDragDetected(e->{if(getItem()==null||!isInEditMode)return;Dragboard db=startDragAndDrop(TransferMode.MOVE);ClipboardContent c=new ClipboardContent();c.put(CLASE_INFO_FORMAT,getItem());db.setContent(c);db.setDragView(snapshot(null,null));e.consume();});setOnDragOver(e->{if(e.getGestureSource()!=this&&e.getDragboard().hasContent(CLASE_INFO_FORMAT)&&getItem()==null){e.acceptTransferModes(TransferMode.MOVE);setStyle("-fx-background-color:#2ecc71;");}e.consume();});setOnDragExited(e->updateItem(getItem(),isEmpty()));
            setOnDragDropped(event -> {
                Dragboard db = event.getDragboard();
                boolean success = false;
                if (db.hasContent(CLASE_INFO_FORMAT)) {
                    ClaseInfo claseArrastradaCopia = (ClaseInfo) db.getContent(CLASE_INFO_FORMAT);
                    HorarioBloque filaDestino = (HorarioBloque) getTableView().getItems().get(getIndex());
                    LocalTime nuevaHora = LocalTime.parse(filaDestino.horaProperty().get());
                    String nuevoDia = getTableColumn().getText().toLowerCase();
                    String error = getMoveValidationError(claseArrastradaCopia, nuevaHora, nuevoDia);
                    if (error != null) {
                        new Alert(Alert.AlertType.WARNING, error).showAndWait();
                    } else {
                        ClaseInfo claseOriginal = clasesProgramadasCache.stream().filter(c -> c.getIdClase() == claseArrastradaCopia.getIdClase()).findFirst().orElse(null);
                        if (claseOriginal != null) {
                            claseOriginal.setDia(nuevoDia);
                            claseOriginal.setHoraInicio(nuevaHora);
                            clasesModificadas.put(claseOriginal.getIdClase(), claseOriginal);
                            redrawTablesFromCache();
                            success = true;
                        }
                    }
                }
                event.setDropCompleted(success);
                event.consume();
            });
        }

        // --- MÉTODO updateItem CORREGIDO Y FINAL ---
        @Override 
        protected void updateItem(ClaseInfo item, boolean empty) {
            super.updateItem(item, empty);

            if (empty || item == null) {
                setText(null);
                // **CORRECCIÓN 1**: Las celdas vacías SIEMPRE tienen el borde para formar la rejilla.
                setStyle("-fx-background-color: white; -fx-border-color: #E0E0E0; -fx-border-width: 0 1 1 0;");
                return;
            }
            
            int i = getIndex();
            ObservableList<S> items = getTableView().getItems();
            boolean isFirst = i == 0 || !item.equals(propertyExtractor.apply(items.get(i - 1)).get());
            String name = item.getNombreActividad().toLowerCase();
            
            // **CORRECCIÓN 2**: Las celdas con color tienen el borde transparente por defecto.
            String style = "-fx-background-color:" + getColor(name) + "; -fx-border-color: transparent;";

            if (isFirst) {
                setText(item.getNombreActividad() + "\n(" + item.getNombreInstructor() + ")");
                style += "-fx-text-fill:" + getTextColor(name) + ";-fx-font-weight:bold;";
            } else {
                setText(null);
            }

            // El borde de edición se aplica encima del transparente solo cuando es necesario.
            if (isInEditMode) {
                style += "-fx-cursor:hand;-fx-border-color:#2c3e50;-fx-border-style:dashed;-fx-border-width:1.5;";
            }
            
            setStyle(style);
        }

        private String getColor(String n){switch(n){case"yoga":return"#e74c3c";case"crossfit":return"#3498db";case"spinning":return"#f1c40f";case"zumba":return"#9b59b6";default:return"#bdc3c7";}}
        private String getTextColor(String n){return"spinning".equals(n)?"#2c3e50":"white";}
    }
}