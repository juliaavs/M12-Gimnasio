package com.mycompany.proyectogimnasio.Controllers;

import com.mycompany.proyectogimnasio.Models.Actividad;
import com.mycompany.proyectogimnasio.Models.ClaseInfo;
import com.mycompany.proyectogimnasio.Models.Instructor;
import com.mycompany.proyectogimnasio.Service.ClaseService;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.*;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

public class HorarioController {

    //<editor-fold desc="FXML UI Components">
    @FXML private TableView<HorarioBloque> tablaHorarioManana;
    @FXML private TableColumn<HorarioBloque, String> colHoraManana;
    @FXML private TableColumn<HorarioBloque, ClaseInfo> colLunesManana, colMartesManana, colMiercolesManana, colJuevesManana, colViernesManana, colSabadoManana, colDomingoManana;

    @FXML private TableView<HorarioBloque> tablaHorarioTarde;
    @FXML private TableColumn<HorarioBloque, String> colHoraTarde;
    @FXML private TableColumn<HorarioBloque, ClaseInfo> colLunesTarde, colMartesTarde, colMiercolesTarde, colJuevesTarde, colViernesTarde, colSabadoTarde, colDomingoTarde;
    
    @FXML private Button btnEditar;
    @FXML private Button btnDescartar;
    @FXML private Button btnActualizar;
    
    @FXML private Button btnAnadirClase;
    @FXML private VBox panelActividades;
    @FXML private ListView<Actividad> listActividadesDisponibles;
    //</editor-fold>

    private final ClaseService claseService = new ClaseService();
    private boolean isInEditMode = false;
    private final Map<Integer, ClaseInfo> clasesModificadas = new HashMap<>();
    
    private static final DataFormat CLASE_INFO_FORMAT = new DataFormat("com.mycompany.proyectogimnasio.Models.ClaseInfo");
    private static final DataFormat ACTIVIDAD_INFO_FORMAT = new DataFormat("com.mycompany.proyectogimnasio.Models.Actividad");
    
    private List<ClaseInfo> clasesProgramadasCache;
    private List<Actividad> cacheActividades;
    private List<Instructor> cacheInstructores;
    
    private String usuarioActual;
    private String rolActual;

    @FXML
    public void initialize() {
        setupTableColumns(colHoraManana, colLunesManana, colMartesManana, colMiercolesManana, colJuevesManana, colViernesManana, colSabadoManana, colDomingoManana);
        setupTableColumns(colHoraTarde, colLunesTarde, colMartesTarde, colMiercolesTarde, colJuevesTarde, colViernesTarde, colSabadoTarde, colDomingoTarde);
        
        btnDescartar.setVisible(false);
        btnDescartar.setManaged(false);
        
        cargarDatosDeServicios();
        setupDragFromActividadesList(); 
        
        loadDataFromDatabase();
    }
    
    private void cargarDatosDeServicios() {
        this.cacheActividades = claseService.getActividadesDisponibles();
        this.cacheInstructores = claseService.getInstructoresActivos();
        
        listActividadesDisponibles.setItems(FXCollections.observableArrayList(this.cacheActividades));
    }
    
    /**
     * MÉTODO ACTUALIZADO
     * Ahora usa un CellFactory para aplicar los colores de la actividad
     * y mantener la lógica de 'drag-and-drop'.
     */
    private void setupDragFromActividadesList() {
        listActividadesDisponibles.setCellFactory(param -> new ListCell<Actividad>() {
            @Override
            protected void updateItem(Actividad item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle(null); 
                    setOnDragDetected(null);
                } else {
                    setText(item.getNombre());
                    
                    // --- Lógica de Estilo AÑADIDA ---
                    String activityName = item.getNombre().toLowerCase();
                    String bgColor = getColor(activityName);
                    String textColor = getTextColor(activityName);
                    setStyle("-fx-background-color: " + bgColor + "; -fx-text-fill: " + textColor + "; -fx-font-weight: bold;");
                    
                    // --- Lógica de Drag (Existente) ---
                    setOnDragDetected(event -> {
                        if (item == null) return;
                        Dragboard db = startDragAndDrop(TransferMode.COPY);
                        ClipboardContent content = new ClipboardContent();
                        content.put(ACTIVIDAD_INFO_FORMAT, item);
                        db.setContent(content);
                        db.setDragView(this.snapshot(null, null)); 
                        event.consume();
                    });
                }
            }
        });
    }

    /**
     * MÉTODO ACTUALIZADO
     * Deshabilita el botón de editar cuando el panel está visible.
     */
    @FXML
    private void handleToggleAddPanel() {
        boolean isVisible = panelActividades.isVisible();
        
        panelActividades.setVisible(!isVisible);
        panelActividades.setManaged(!isVisible);
        
        if (isVisible) { // El panel AHORA ESTÁ OCULTO
            btnAnadirClase.setText("Añadir Clase");
            btnEditar.setDisable(false); // Habilitar Edición
        } else { // El panel AHORA ESTÁ VISIBLE
            btnAnadirClase.setText("Ocultar Panel");
            btnEditar.setDisable(true); // Deshabilitar Edición
        }
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
        for (LocalTime t = LocalTime.of(7, 0); t.isBefore(LocalTime.of(13, 0)); t = t.plusMinutes(15)) {dataManana.add(crearFilaHorario(t, this.clasesProgramadasCache));}
        tablaHorarioManana.setItems(dataManana);
        ObservableList<HorarioBloque> dataTarde = FXCollections.observableArrayList();
        for (LocalTime t = LocalTime.of(15, 0); t.isBefore(LocalTime.of(19, 0)); t = t.plusMinutes(15)) {dataTarde.add(crearFilaHorario(t, this.clasesProgramadasCache));}
        tablaHorarioTarde.setItems(dataTarde);
    }

    private HorarioBloque crearFilaHorario(LocalTime hora, List<ClaseInfo> clases) {
        ClaseInfo[] clasesDelBloque = new ClaseInfo[7];
        String[] dias = {"lunes", "martes", "miércoles", "jueves", "viernes", "sábado", "domingo"};
        for (int i = 0; i < dias.length; i++) {for (ClaseInfo clase : clases) {if (clase.getDia().equalsIgnoreCase(dias[i]) && !hora.isBefore(clase.getHoraInicio()) && hora.isBefore(clase.getHoraFin())) {clasesDelBloque[i] = clase;break;}}}
        return new HorarioBloque(hora.format(DateTimeFormatter.ofPattern("HH:mm")), clasesDelBloque);
    }
    
    /**
     * MÉTODO ACTUALIZADO
     * Deshabilita el botón de añadir clase cuando está en modo edición.
     */
    @FXML private void toggleEditMode() {
        isInEditMode = !isInEditMode;
        if (isInEditMode) {
            btnEditar.setText("Guardar Cambios");
            btnEditar.setStyle("-fx-background-color: #2ecc71; -fx-text-fill: white; -fx-font-weight: bold;");
            btnDescartar.setVisible(true);
            btnDescartar.setManaged(true);
            btnActualizar.setVisible(false);
            btnActualizar.setManaged(false);
            
            btnAnadirClase.setDisable(true); // Deshabilitar Añadir Clase
            
        } else {
            btnEditar.setText("Editar Horario");
            btnEditar.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-font-weight: bold;");
            btnDescartar.setVisible(false);
            btnDescartar.setManaged(false);
            btnActualizar.setVisible(true);
            btnActualizar.setManaged(true);
            
            btnAnadirClase.setDisable(false); // Habilitar Añadir Clase
            
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
        if (isInEditMode && !clasesModificadas.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "¿Descartar cambios no guardados?", ButtonType.YES, ButtonType.NO);
            if (alert.showAndWait().filter(r -> r == ButtonType.YES).isPresent()) {
                handleDiscardChanges();
            }
        } else {
            loadDataFromDatabase();
        }
    }

    @FXML
    private void handleDiscardChanges() {
        clasesModificadas.clear();
        loadDataFromDatabase();
        if (isInEditMode) {
            toggleEditMode();
        }
    }

    private String getMoveValidationError(ClaseInfo claseMovida, LocalTime nuevaHora, String nuevoDia) {
        final LocalTime HORA_FIN_MANANA=LocalTime.of(13,0);final LocalTime HORA_FIN_TARDE=LocalTime.of(19,0);LocalTime finNuevaClase=nuevaHora.plusMinutes(claseMovida.getDuracionMinutos());
        if((nuevaHora.isBefore(HORA_FIN_MANANA)&&finNuevaClase.isAfter(HORA_FIN_MANANA))||finNuevaClase.isAfter(HORA_FIN_TARDE)){return"La clase invade la pausa del almuerzo o termina después del cierre.";}
        for(ClaseInfo claseExistente:this.clasesProgramadasCache){if(claseExistente.getIdClase()==claseMovida.getIdClase())continue;if(claseExistente.getDia().equalsIgnoreCase(nuevoDia)){if(nuevaHora.isBefore(claseExistente.getHoraFin())&&finNuevaClase.isAfter(claseExistente.getHoraInicio())){return"El horario se solapa con la clase de '"+claseExistente.getNombreActividad()+"'.";}}}
        return null;
    }

    private String getCreateValidationError(Actividad nuevaActividad, LocalTime nuevaHora, String nuevoDia) {
        final LocalTime HORA_FIN_MANANA = LocalTime.of(13, 0);
        final LocalTime HORA_FIN_TARDE = LocalTime.of(19, 0);
        LocalTime finNuevaClase = nuevaHora.plusMinutes(nuevaActividad.getDuracion());

        if ((nuevaHora.isBefore(HORA_FIN_MANANA) && finNuevaClase.isAfter(HORA_FIN_MANANA)) || finNuevaClase.isAfter(HORA_FIN_TARDE)) {
            return "La clase ("+ nuevaActividad.getNombre() +") invade la pausa del almuerzo o termina después del cierre.";
        }

        for (ClaseInfo claseExistente : this.clasesProgramadasCache) {
            if (claseExistente.getDia().equalsIgnoreCase(nuevoDia)) {
                if (nuevaHora.isBefore(claseExistente.getHoraFin()) && finNuevaClase.isAfter(claseExistente.getHoraInicio())) {
                    return "El horario se solapa con la clase de '" + claseExistente.getNombreActividad() + "'.";
                }
            }
        }
        return null;
    }


    public void setUser(String u, String r) { this.usuarioActual = u; this.rolActual = r; }
    
    // --- MÉTODOS DE COLOR REFACTORIZADOS (MOVIDOS AQUÍ) ---
    
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
            return "#2c3e50";
        } else {
            return "white";
        }
    }

    // --- CLASES INTERNAS ---

    public static class HorarioBloque {
        private final SimpleObjectProperty<String> hora;private final SimpleObjectProperty<ClaseInfo> lunes, martes, miercoles, jueves, viernes, sabado, domingo;
        public HorarioBloque(String h, ClaseInfo... c){hora=new SimpleObjectProperty<>(h);lunes=new SimpleObjectProperty<>(c[0]);martes=new SimpleObjectProperty<>(c[1]);miercoles=new SimpleObjectProperty<>(c[2]);jueves=new SimpleObjectProperty<>(c[3]);viernes=new SimpleObjectProperty<>(c[4]);sabado=new SimpleObjectProperty<>(c[5]);domingo=new SimpleObjectProperty<>(c[6]);}
        public SimpleObjectProperty<String> horaProperty(){return hora;}public SimpleObjectProperty<ClaseInfo> lunesProperty(){return lunes;}public SimpleObjectProperty<ClaseInfo> martesProperty(){return martes;}public SimpleObjectProperty<ClaseInfo> miercolesProperty(){return miercoles;}public SimpleObjectProperty<ClaseInfo> juevesProperty(){return jueves;}public SimpleObjectProperty<ClaseInfo> viernesProperty(){return viernes;}public SimpleObjectProperty<ClaseInfo> sabadoProperty(){return sabado;}public SimpleObjectProperty<ClaseInfo> domingoProperty(){return domingo;}
    }

    public class EditableMergedCell<S> extends TableCell<S, ClaseInfo> {
        private final Function<S, ObjectProperty<ClaseInfo>> propertyExtractor;
        
        public EditableMergedCell(Function<S, ObjectProperty<ClaseInfo>> extractor){
            this.propertyExtractor = extractor;
            
            setOnDragDetected(e->{
                if(getItem()==null||!isInEditMode)return;
                Dragboard db=startDragAndDrop(TransferMode.MOVE);
                ClipboardContent c=new ClipboardContent();
                c.put(CLASE_INFO_FORMAT,getItem());
                db.setContent(c);
                db.setDragView(this.snapshot(null,null));
                e.consume();
            });
            
            setOnDragOver(e->{
                Dragboard db = e.getDragboard();
                boolean canDrop = false;

                if (db.hasContent(CLASE_INFO_FORMAT) && getItem() == null && isInEditMode) {
                    canDrop = true;
                } else if (db.hasContent(ACTIVIDAD_INFO_FORMAT) && getItem() == null) {
                    canDrop = true;
                }

                if (canDrop) {
                    e.acceptTransferModes(TransferMode.MOVE, TransferMode.COPY);
                    setStyle("-fx-background-color:#2ecc71;");
                }
                e.consume();
            });
            
            setOnDragExited(e->updateItem(getItem(),isEmpty()));
            
            setOnDragDropped(event -> {
                Dragboard db = event.getDragboard();
                boolean success = false;
                
                HorarioBloque filaDestino = (HorarioBloque) getTableView().getItems().get(getIndex());
                LocalTime nuevaHora = LocalTime.parse(filaDestino.horaProperty().get());
                String nuevoDia = getTableColumn().getText().toLowerCase();

                if (db.hasContent(CLASE_INFO_FORMAT)) {
                    ClaseInfo claseArrastradaCopia = (ClaseInfo) db.getContent(CLASE_INFO_FORMAT);
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
                } else if (db.hasContent(ACTIVIDAD_INFO_FORMAT)) {
                    Actividad nuevaActividad = (Actividad) db.getContent(ACTIVIDAD_INFO_FORMAT);
                    String error = getCreateValidationError(nuevaActividad, nuevaHora, nuevoDia);
                    
                    if (error != null) {
                        new Alert(Alert.AlertType.WARNING, error).showAndWait();
                    } else {
                        if (cacheInstructores.isEmpty()) {
                            new Alert(Alert.AlertType.ERROR, "No hay instructores activos cargados.").showAndWait();
                        } else {
                            ChoiceDialog<Instructor> dialog = new ChoiceDialog<>(cacheInstructores.get(0), cacheInstructores);
                            dialog.setTitle("Seleccionar Instructor");
                            dialog.setHeaderText("¿Qué instructor impartirá " + nuevaActividad.getNombre() + "?");
                            dialog.setContentText("Instructor:");

                            Optional<Instructor> result = dialog.showAndWait();
                            if (result.isPresent()) {
                                Instructor instructorElegido = result.get();
                                boolean creada = claseService.crearClase(
                                        nuevaActividad.getIdActividad(),
                                        instructorElegido.getIdInstructor(),
                                        nuevoDia,
                                        nuevaHora
                                );
                                if (creada) {
                                    new Alert(Alert.AlertType.INFORMATION, "Clase creada exitosamente.").show();
                                    loadDataFromDatabase();
                                    success = true;
                                } else {
                                    new Alert(Alert.AlertType.ERROR, "No se pudo crear la clase en la base de datos.").show();
                                }
                            }
                        }
                    }
                }
                event.setDropCompleted(success);
                event.consume();
            });
        }
        
        @Override protected void updateItem(ClaseInfo item,boolean empty){
            super.updateItem(item,empty);
            if(empty||item==null){
                setText(null);
                setStyle("-fx-background-color:white;-fx-border-color:#E0E0E0;-fx-border-width:0 1 1 0;");
                return;
            }
            int i=getIndex();
            ObservableList<S> items=getTableView().getItems();
            boolean isFirst=i==0||!item.equals(propertyExtractor.apply(items.get(i-1)).get());
            String name=item.getNombreActividad().toLowerCase();
            
            // Los métodos de color ahora se llaman desde la clase 'padre' (HorarioController)
            String style="-fx-background-color:"+getColor(name)+";-fx-border-color:transparent;";
            if(isFirst){
                setText(item.getNombreActividad()+"\n("+item.getNombreInstructor()+")");
                style+="-fx-text-fill:"+getTextColor(name)+";-fx-font-weight:bold;";
            }else{
                setText(null);
            }
            if(isInEditMode){
                style+="-fx-cursor:hand;-fx-border-color:#2c3e50;-fx-border-style:dashed;-fx-border-width:1.5;";
            }
            setStyle(style);
        }
    }
}