package com.mycompany.proyectogimnasio.Controllers;

import com.mycompany.proyectogimnasio.Models.Actividad;
import com.mycompany.proyectogimnasio.Service.ActividadService; // Asegúrate de tener este import
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

public class ActividadesController implements Initializable {

    // --- FXML INYECCIÓN (Necesaria para tu vista) ---
    @FXML private TableView<Actividad> tablaActividades;
    @FXML private TableColumn<Actividad, Integer> colId;
    @FXML private TableColumn<Actividad, String> colNombre;
    @FXML private TableColumn<Actividad, Integer> colDuracion;
    @FXML private TableColumn<Actividad, String> colDescripcion;
    @FXML private TableColumn<Actividad, Integer> colAforo;
    @FXML private TableColumn<Actividad, Boolean> colActivo;
    
    // Campos de formulario
    @FXML private TextField txtFiltro; // Campo de búsqueda
    @FXML private TextField txtIdActividad;
    @FXML private TextField txtNombre;
    @FXML private ComboBox<Integer> cbDuracion;
    @FXML private TextField txtAforo;
    @FXML private TextArea txtDescripcion;
    
    // Botones
    @FXML private Button btnActualizar;
    @FXML private Button btnAgregar;
    @FXML private ToggleButton tglActivo;
   

    // --- CONTROL DE DATOS ---
    private ActividadService actividadService;
    private ObservableList<Actividad> actividadList;
    
    // Lista de duraciones permitidas (Ejemplo)
    private final List<Integer> DURACIONES_VALIDAS = Arrays.asList(30, 45, 50, 60, 90);

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        actividadService = new ActividadService();
        
        // 1. Configurar ComboBox
        inicializarComboBoxDuracion();

        // 2. Cargar datos iniciales y configurar la lista observable
        loadActividades();
        
        // 3. Configurar TableView y filtro
        configurarTabla();
        configurarFiltro();
        
        colActivo.setCellValueFactory(cellData -> cellData.getValue().activoProperty().asObject());
        
        colActivo.setCellFactory(col -> new TableCell<Actividad, Boolean>() {
    @Override
    protected void updateItem(Boolean item, boolean empty) {
        super.updateItem(item, empty);
        
        // Limpiar la celda si está vacía
        if (empty || item == null) {
            setText(null);
            setStyle(null); // Limpiar estilos
        } else {
            // Si es TRUE, el texto es "Activo"
            if (item) {
                setText("Activo");
                // Estilo verde y negrita
                setStyle("-fx-text-fill: green; -fx-font-weight: bold;");
            } 
            // Si es FALSE, el texto es "Inactivo"
            else {
                setText("Inactivo");
                // Estilo rojo y negrita
                setStyle("-fx-text-fill: red; -fx-font-weight: bold;");
            }
        }
    }
    });
        
        // 4. Configurar listeners de la tabla
        tablaActividades.getSelectionModel().selectedItemProperty().addListener(
            (obs, oldSelection, newSelection) -> {
                seleccionarActividad(newSelection);
                cambiarEstadoBotones(newSelection != null);
            }
        );
        
        // Inicializar botones
        cambiarEstadoBotones(false);
        limpiarCampos(); // Limpia al inicio
    }
    
    // ---------------------- MÉTODOS DE INICIALIZACIÓN Y CONFIGURACIÓN ----------------------

    private void inicializarComboBoxDuracion() {
        cbDuracion.setItems(FXCollections.observableArrayList(DURACIONES_VALIDAS));
    }
    
    private void configurarTabla() {
        // La configuración de cellValueFactory se hace mejor en FXML,
        // pero si lo necesitas en código:
        // colNombre.setCellValueFactory(cellData -> cellData.getValue().nombreProperty());
        // ... (etc.)
    }

    private void loadActividades() {
        // Obtiene datos del servicio
        List<Actividad> data = actividadService.obtenerTodasActividades();
        
        // Inicializa la ObservableList
        actividadList = FXCollections.observableArrayList(data);
    }
    
    /**
     * Configura el mecanismo de filtrado (tal como lo corregimos).
     */
    private void configurarFiltro() {
        // 1. Crear FilteredList basada en actividadList (que ya no es null)
        FilteredList<Actividad> filteredData = new FilteredList<>(actividadList, p -> true);

        // 2. Listener para el campo de texto
        txtFiltro.textProperty().addListener((observable, oldValue, newValue) -> {
            
            filteredData.setPredicate(actividad -> {
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }
                
                String lowerCaseFilter = newValue.toLowerCase();

                // Criterio 1: Filtrar por Nombre
                if (actividad.getNombre() != null && actividad.getNombre().toLowerCase().contains(lowerCaseFilter)) {
                    return true;
                } 
                
                // Criterio 2: Filtrar por Aforo (Número a String)
                else if (String.valueOf(actividad.getAforo()).contains(lowerCaseFilter)) {
                    return true;
                }
                
                return false;
            });
        });
        
        // 3. Envolver en SortedList y vincular
        SortedList<Actividad> sortedData = new SortedList<>(filteredData);
        sortedData.comparatorProperty().bind(tablaActividades.comparatorProperty());

        // 4. Asignar la lista filtrada/ordenada a la tabla
        tablaActividades.setItems(sortedData);
    }

    // ---------------------- MÉTODOS CRUD ----------------------

    @FXML
    public void agregarActividad() {
        Actividad nuevaActividad = obtenerActividadDeCampos(0); // ID 0 o temporal

        if (validarCampos(nuevaActividad)) {
            if (actividadService.agregarActividad(nuevaActividad)) {
                mostrarAlerta(Alert.AlertType.INFORMATION, "Éxito", "Actividad agregada correctamente.");
                
                // Recargar y actualizar la lista observable
                actividadList.clear();
                actividadList.addAll(actividadService.obtenerTodasActividades());
                limpiarCampos();
            } else {
                mostrarAlerta(Alert.AlertType.ERROR, "Error", "No se pudo agregar la actividad en la BD.");
            }
        }
    }

    @FXML
    public void actualizarActividad() {
        int id = Integer.parseInt(txtIdActividad.getText());
        Actividad actividadActualizada = obtenerActividadDeCampos(id);

        if (validarCampos(actividadActualizada)) {
            if (actividadService.actualizarActividad(actividadActualizada)) {
                mostrarAlerta(Alert.AlertType.INFORMATION, "Éxito", "Actividad actualizada correctamente.");
                
                // Actualizar la lista observable
                actividadList.set(
                    tablaActividades.getSelectionModel().getSelectedIndex(), 
                    actividadActualizada
                );
                limpiarCampos();
            } else {
                mostrarAlerta(Alert.AlertType.ERROR, "Error", "No se pudo actualizar la actividad en la BD.");
            }
        }
    }

    @FXML
    public void eliminarActividad() {
        Actividad selectedItem = tablaActividades.getSelectionModel().getSelectedItem();
        if (selectedItem != null) {
            Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION, "¿Está seguro de que desea eliminar la actividad: " + selectedItem.getNombre() + "?", ButtonType.YES, ButtonType.NO);
            Optional<ButtonType> result = confirmAlert.showAndWait();

            if (result.isPresent() && result.get() == ButtonType.YES) {
                if (actividadService.eliminarActividad(selectedItem.getIdActividad())) {
                    mostrarAlerta(Alert.AlertType.INFORMATION, "Éxito", "Actividad eliminada correctamente.");
                    actividadList.remove(selectedItem);
                    limpiarCampos();
                } else {
                    mostrarAlerta(Alert.AlertType.ERROR, "Error", "No se pudo eliminar la actividad en la BD.");
                }
            }
        } else {
            mostrarAlerta(Alert.AlertType.WARNING, "Advertencia", "Seleccione una actividad para eliminar.");
        }
    }

    @FXML
    public void limpiarCampos() {
        txtIdActividad.setText("");
        txtNombre.setText("");
        cbDuracion.getSelectionModel().clearSelection();
        txtAforo.setText("");
        txtDescripcion.setText("");
        tablaActividades.getSelectionModel().clearSelection();
    
        // ESTADO POR DEFECTO: ACTIVO (para nuevas actividades)
        tglActivo.setSelected(true); 
        tglActivo.setText("Activo");
    
        cambiarEstadoBotones(false);
    }
    
    // ---------------------- MÉTODOS AUXILIARES ----------------------

    
    private void seleccionarActividad(Actividad actividad) {
    if (actividad != null) {
        txtIdActividad.setText(String.valueOf(actividad.getIdActividad()));
        txtNombre.setText(actividad.getNombre());
        cbDuracion.getSelectionModel().select(Integer.valueOf(actividad.getDuracion()));
        txtAforo.setText(String.valueOf(actividad.getAforo()));
        txtDescripcion.setText(actividad.getDescripcion());
        
        // Cargar el estado 'activo'
        boolean estaActivo = actividad.isActivo();
        tglActivo.setSelected(estaActivo); // True si está activo, false si inactivo
        
        // El texto debe ser la ACCIÓN que realiza al pulsar:
        // Si la actividad está ACTIVA (true), el botón debe decir DESACTIVAR
        // Si la actividad está INACTIVA (false), el botón debe decir ACTIVAR
        tglActivo.setText(estaActivo ? "Desactivar" : "Activar");
        
        // Opcional: Estilo de botón rojo/verde
        String style = estaActivo 
            ? "-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-font-weight: bold;" // Rojo (para Desactivar)
            : "-fx-background-color: #2ecc71; -fx-text-fill: white; -fx-font-weight: bold;"; // Verde (para Activar)
        tglActivo.setStyle(style);
    }
    }
    
    private Actividad obtenerActividadDeCampos(int id) {
        int duracion = cbDuracion.getValue() != null ? cbDuracion.getValue() : 0;
        int aforo = 0;
    
        // Obtener el estado del ToggleButton
        boolean activo = tglActivo.isSelected(); 
    
        try {
            aforo = Integer.parseInt(txtAforo.getText());
        } catch (NumberFormatException e) {
            // Manejado en la validación
        }
    
        return new Actividad(
            id,
            txtNombre.getText(),
            txtDescripcion.getText(),
            duracion,
            aforo,
            activo // <-- Usamos el estado del Toggle
        );
    }
    
    private boolean validarCampos(Actividad actividad) {
        String mensaje = "";

        if (actividad.getNombre() == null || actividad.getNombre().trim().isEmpty()) {
            mensaje += "El nombre de la actividad es requerido.\n";
        }
        if (actividad.getDuracion() <= 0) {
             mensaje += "Debe seleccionar una duración válida.\n";
        }
        if (actividad.getAforo() <= 0) {
             mensaje += "El aforo debe ser un número positivo.\n";
        }
        
        if (mensaje.isEmpty()) {
            return true;
        } else {
            mostrarAlerta(Alert.AlertType.WARNING, "Campos Incompletos", mensaje);
            return false;
        }
    }

    private void cambiarEstadoBotones(boolean modoEdicion) {
        btnAgregar.setManaged(!modoEdicion);
        btnAgregar.setVisible(!modoEdicion);
    
        btnActualizar.setManaged(modoEdicion);
        btnActualizar.setVisible(modoEdicion);
    
        // El ToggleButton solo es visible en modo edición
        tglActivo.setManaged(modoEdicion);
        tglActivo.setVisible(modoEdicion);
    }
    
    private void mostrarAlerta(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
    
    @FXML
    private void manejarToggleActivo() {
        Actividad actividadSeleccionada = tablaActividades.getSelectionModel().getSelectedItem();
        if (actividadSeleccionada != null) {
            // El nuevo estado es el opuesto al actual de la actividad (ya que estamos en modo toggle)
            boolean nuevoEstado = !actividadSeleccionada.isActivo();
        
            // Persistir el cambio en la base de datos
            actividadSeleccionada.setActivo(nuevoEstado);
        
            if (actividadService.actualizarActividad(actividadSeleccionada)) {
                mostrarAlerta(Alert.AlertType.INFORMATION, "Éxito", "Estado cambiado a " + (nuevoEstado ? "ACTIVO" : "INACTIVO") + ".");
            
                // Actualizar la interfaz después de la BD:
            
                // 1. Cambiar texto y estilo para reflejar la *nueva* acción a realizar
                String nuevoTexto = nuevoEstado ? "Desactivar" : "Activar";
                String nuevoStyle = nuevoEstado 
                    ? "-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-font-weight: bold;" 
                    : "-fx-background-color: #2ecc71; -fx-text-fill: white; -fx-font-weight: bold;";
            
                tglActivo.setText(nuevoTexto);
                tglActivo.setStyle(nuevoStyle);
            
                // 2. Refrescar la tabla para que la columna 'activo' muestre el cambio
                tablaActividades.refresh(); 
            } else {
                // Si la actualización falló, revertir el estado del modelo y del ToggleButton en la UI
                actividadSeleccionada.setActivo(!nuevoEstado); 
                // Revertir el toggle button (si se presionó, debe volver a su estado anterior)
                tglActivo.setSelected(!nuevoEstado);
                mostrarAlerta(Alert.AlertType.ERROR, "Error", "No se pudo actualizar el estado en la BD.");
            }
        }
    }   
    
}