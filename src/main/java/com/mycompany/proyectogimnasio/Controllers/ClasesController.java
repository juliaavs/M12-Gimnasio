package com.mycompany.proyectogimnasio.Controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;
import javafx.beans.property.SimpleStringProperty;

public class ClasesController {

    // --- Componentes FXML para la Tabla (TableView) ---
    @FXML private TableView<ObservableList<String>> tablaClases;
    @FXML private TableColumn<ObservableList<String>, String> colId;
    @FXML private TableColumn<ObservableList<String>, String> colDia;
    @FXML private TableColumn<ObservableList<String>, String> colHoraInicio;
    @FXML private TableColumn<ObservableList<String>, String> colActividad;
    @FXML private TableColumn<ObservableList<String>, String> colIdInstructor; // Muestra el nombre completo
    @FXML private TableColumn<ObservableList<String>, String> colStatus;

    // --- Componentes FXML para la Entrada de Datos ---
    // @FXML private TextField txtDia; // ELIMINADO: Reemplazado por ComboBox cbDia
    @FXML private ComboBox<String> cbDia; // NUEVO: ComboBox para el día de la semana
    @FXML private TextField txtHoraInicio;
    @FXML private TextField txtIdClase; 
    
    @FXML private ComboBox<String> cbActividad; 
    @FXML private ComboBox<String> cbInstructor; // Nuevo ComboBox para seleccionar el instructor por nombre
    @FXML private ComboBox<String> cbStatus;     // Nuevo ComboBox para seleccionar el estado

    // --- Variables de Conexión y Mapeo ---
    private Connection conn;
    // Mapa para traducir Nombre de Actividad a su ID
    private Map<String, Integer> actividadesMap;
    // Nuevo mapa para traducir Nombre Completo de Instructor a su ID
    private Map<String, Integer> instructoresMap;
    // La clase seleccionada en la tabla
    private ObservableList<String> selectedClaseData;

    @FXML
    public void initialize() {
        // Inicializar los mapas
        actividadesMap = new HashMap<>();
        instructoresMap = new HashMap<>();
        
        // El estado de la clase ahora se carga desde la base de datos.
        
        // 1. Configurar las CellValueFactory (indices inalterados)
        colId.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().get(0)));      // ID_CLASE
        colDia.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().get(1)));     // DIA
        colHoraInicio.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().get(2))); // HORA
        colActividad.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().get(3)));  // NOMBRE_ACTIVIDAD
        colIdInstructor.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().get(4))); // NOMBRE COMPLETO INSTRUCTOR
        colStatus.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().get(5)));  // STATUS

        try {
            // 2. Conexión a la Base de Datos
            conn = DriverManager.getConnection("jdbc:mysql://centerbeam.proxy.rlwy.net:23892/railway", "root", "ShlYFjtRmPFlizYSEyizwuhZgYpWHijg");

            // 3. Cargar datos iniciales
            cargarActividades();    // Llenar cbActividad
            cargarInstructores();   // Llenar cbInstructor
            cargarEstadosClase();   // NUEVO: Llenar cbStatus desde la DB
            cargarDiasSemana();     // NUEVO: Llenar cbDia con los días de la semana
            cargarTabla();          // Llenar TableView

            // 4. Configurar el evento de selección en la tabla
            tablaClases.setOnMouseClicked(this::seleccionarClase);
            
            // 5. Deshabilitar y ocultar el campo ID: Es autoincremental y solo de lectura/edición.
            if (txtIdClase != null) {
                txtIdClase.setDisable(true);
                txtIdClase.setVisible(false);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            mostrarAlerta(Alert.AlertType.ERROR, "Error de Conexión", "No se pudo conectar a la base de datos.");
        }
    }

    /**
     * NUEVO: Carga los días de la semana en el ComboBox cbDia.
     */
    private void cargarDiasSemana() {
        if (cbDia == null) return;
        ObservableList<String> dias = FXCollections.observableArrayList(
            "Lunes", "Martes", "Miércoles", "Jueves", "Viernes", "Sábado", "Domingo"
        );
        cbDia.setItems(dias);
    }

    // --- Métodos de Carga de Datos Existentes/Modificados ---
    
    /**
     * Consulta la base de datos para obtener los distintos estados de clase 
     * y los carga en el ComboBox cbStatus.
     */
    private void cargarEstadosClase() throws SQLException {
        if (cbStatus == null) return; // Añadido check de nulidad
        cbStatus.getItems().clear();
        // Consulta los estados ÚNICOS existentes en la tabla de clases
        String sql = "SELECT DISTINCT status FROM clases";
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                // Asume que los únicos estados en la DB serán "Confirmada" y "Cancelada"
                cbStatus.getItems().add(rs.getString("status"));
            }
        }
    }

    private void cargarInstructores() throws SQLException {
        // Carga los nombres completos de instructores y mapea Nombre Completo -> ID
        instructoresMap.clear();
        if (cbInstructor == null) return; // Añadido check de nulidad
        cbInstructor.getItems().clear();
        String sql = "SELECT id_instructor, nombre, apellido FROM instructores";
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                int id = rs.getInt("id_instructor");
                // Concatenar nombre y apellido para el ComboBox
                String nombreCompleto = rs.getString("nombre") + " " + rs.getString("apellido");
                
                cbInstructor.getItems().add(nombreCompleto);
                instructoresMap.put(nombreCompleto, id); // Guardar el mapeo
            }
        }
    }

    private void cargarActividades() throws SQLException {
        // Carga los nombres de actividades en el ComboBox y mapea Nombre -> ID
        actividadesMap.clear();
        if (cbActividad == null) return; // Añadido check de nulidad
        String sql = "SELECT id_actividad, nombre FROM actividades";
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                int id = rs.getInt("id_actividad");
                String nombre = rs.getString("nombre");
                
                cbActividad.getItems().add(nombre);
                actividadesMap.put(nombre, id); 
            }
        }
    }

    @FXML
    private void cargarTabla() {
        ObservableList<ObservableList<String>> data = FXCollections.observableArrayList();
        
        // Consulta SQL para obtener nombre completo del instructor y ID para uso interno (índice 6)
        String sql = "SELECT c.id_clase, c.dia, c.hora_inicio, a.nombre AS nombre_actividad, " +
                     "i.nombre AS instructor_nombre, i.apellido AS instructor_apellido, c.status, c.id_instructor " +
                     "FROM clases c " +
                     "JOIN actividades a ON c.id_actividad = a.id_actividad " +
                     "JOIN instructores i ON c.id_instructor = i.id_instructor";
        
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                ObservableList<String> row = FXCollections.observableArrayList();
                
                String nombreCompletoInstructor = rs.getString("instructor_nombre") + " " + rs.getString("instructor_apellido");
                
                // ORDEN DE DATOS:
                row.add(String.valueOf(rs.getInt("id_clase")));      // 0: ID_CLASE
                row.add(rs.getString("dia"));                        // 1: DIA
                row.add(rs.getString("hora_inicio"));                // 2: HORA
                row.add(rs.getString("nombre_actividad"));           // 3: NOMBRE_ACTIVIDAD
                row.add(nombreCompletoInstructor);                   // 4: NOMBRE COMPLETO INSTRUCTOR (VISIBLE)
                row.add(rs.getString("status"));                     // 5: STATUS
                row.add(String.valueOf(rs.getInt("id_instructor"))); // 6: ID_INSTRUCTOR (OCULTO/INTERNO)
                
                data.add(row);
            }
            if (tablaClases != null) { // Añadido check de nulidad
                tablaClases.setItems(data);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            mostrarAlerta(Alert.AlertType.ERROR, "Error", "No se pudo cargar la tabla de clases.");
        }
    }

    // --- Métodos de CRUD Modificados ---

    @FXML
    private void agregarClase() {
        // Validar que todos los componentes esenciales estén cargados
        if (cbActividad == null || cbInstructor == null || cbStatus == null || cbDia == null || txtHoraInicio == null) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error de Interfaz", "Error al cargar la interfaz. Revise el archivo FXML.");
            return;
        }

        try {
            // 1. Validar y obtener ID de Actividad
            String nombreActividad = cbActividad.getSelectionModel().getSelectedItem();
            if (nombreActividad == null) {
                mostrarAlerta(Alert.AlertType.WARNING, "Advertencia", "Debe seleccionar una actividad.");
                return;
            }
            int idActividad = actividadesMap.get(nombreActividad); 

            // 2. Validar y obtener ID de Instructor (usando el mapa)
            String nombreInstructor = cbInstructor.getSelectionModel().getSelectedItem();
            if (nombreInstructor == null) {
                mostrarAlerta(Alert.AlertType.WARNING, "Advertencia", "Debe seleccionar un instructor.");
                return;
            }
            int idInstructor = instructoresMap.get(nombreInstructor); // Obtener ID real

            // 3. Validar y obtener Status
            String status = cbStatus.getSelectionModel().getSelectedItem();
            if (status == null) {
                mostrarAlerta(Alert.AlertType.WARNING, "Advertencia", "Debe seleccionar un estado.");
                return;
            }
            
            // 4. Validar y obtener Día (desde el ComboBox)
            String dia = cbDia.getSelectionModel().getSelectedItem();
            if (dia == null) {
                mostrarAlerta(Alert.AlertType.WARNING, "Advertencia", "Debe seleccionar un día de la semana.");
                return;
            }

            String horaInicio = txtHoraInicio.getText();

            String sql = "INSERT INTO clases (id_instructor, id_actividad, dia, hora_inicio, status) VALUES (?, ?, ?, ?, ?)";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, idInstructor);
                ps.setInt(2, idActividad);
                ps.setString(3, dia); // Usar el valor del ComboBox
                ps.setString(4, horaInicio);
                ps.setString(5, status);
                ps.executeUpdate();
                
                mostrarAlerta(Alert.AlertType.INFORMATION, "Éxito", "Clase agregada correctamente.");
            }

            cargarTabla();
            limpiarCampos();

        } catch (SQLException e) {
            e.printStackTrace();
            mostrarAlerta(Alert.AlertType.ERROR, "Error SQL", "Error al agregar la clase: " + e.getMessage());
        }
    }

    @FXML
    private void actualizarClase() {
        // Se añade verificación de nulidad de txtIdClase, solucionando el potencial error al editar
        if (selectedClaseData == null || txtIdClase == null || cbActividad == null || cbInstructor == null || cbStatus == null || cbDia == null) {
            mostrarAlerta(Alert.AlertType.WARNING, "Advertencia", "Debe seleccionar una clase y la interfaz debe cargarse correctamente.");
            return;
        }
        
        try {
            int idClase = Integer.parseInt(txtIdClase.getText()); 

            // 1. Validar y obtener ID de Actividad
            String nombreActividad = cbActividad.getSelectionModel().getSelectedItem();
            if (nombreActividad == null) {
                mostrarAlerta(Alert.AlertType.WARNING, "Advertencia", "Debe seleccionar una actividad.");
                return;
            }
            int idActividad = actividadesMap.get(nombreActividad);
            
            // 2. Validar y obtener ID de Instructor (usando el mapa)
            String nombreInstructor = cbInstructor.getSelectionModel().getSelectedItem();
            if (nombreInstructor == null) {
                mostrarAlerta(Alert.AlertType.WARNING, "Advertencia", "Debe seleccionar un instructor.");
                return;
            }
            int idInstructor = instructoresMap.get(nombreInstructor); // Obtener ID real
            
            // 3. Validar y obtener Status
            String status = cbStatus.getSelectionModel().getSelectedItem();
            if (status == null) {
                mostrarAlerta(Alert.AlertType.WARNING, "Advertencia", "Debe seleccionar un estado.");
                return;
            }
            
            // 4. Validar y obtener Día (desde el ComboBox)
            String dia = cbDia.getSelectionModel().getSelectedItem();
            if (dia == null) {
                mostrarAlerta(Alert.AlertType.WARNING, "Advertencia", "Debe seleccionar un día de la semana.");
                return;
            }
            
            if (txtHoraInicio == null) {
                mostrarAlerta(Alert.AlertType.ERROR, "Error de Interfaz", "Faltan campos de texto requeridos.");
                return;
            }

            String horaInicio = txtHoraInicio.getText();

            String sql = "UPDATE clases SET id_instructor = ?, id_actividad = ?, dia = ?, hora_inicio = ?, status = ? WHERE id_clase = ?";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, idInstructor);
                ps.setInt(2, idActividad);
                ps.setString(3, dia); // Usar el valor del ComboBox
                ps.setString(4, horaInicio);
                ps.setString(5, status);
                ps.setInt(6, idClase);
                ps.executeUpdate();

                mostrarAlerta(Alert.AlertType.INFORMATION, "Éxito", "Clase actualizada correctamente.");
            }

            cargarTabla();
            limpiarCampos();

        } catch (NumberFormatException e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error de Entrada", "El ID de Clase debe ser un número válido.");
        } catch (SQLException e) {
            e.printStackTrace();
            mostrarAlerta(Alert.AlertType.ERROR, "Error SQL", "Error al actualizar la clase: " + e.getMessage());
        }
    }

    @FXML
    private void eliminarClase() {
        // Se añade verificación de nulidad de txtIdClase para evitar la NPE reportada
        if (selectedClaseData == null || txtIdClase == null) {
            mostrarAlerta(Alert.AlertType.WARNING, "Advertencia", "Debe seleccionar una clase y la interfaz debe cargarse correctamente.");
            return;
        }

        try {
            // Línea donde ocurría la NPE: Se protege con la verificación anterior.
            int idClase = Integer.parseInt(txtIdClase.getText());
            String sql = "DELETE FROM clases WHERE id_clase = ?";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, idClase);
                ps.executeUpdate();
                
                mostrarAlerta(Alert.AlertType.INFORMATION, "Éxito", "Clase eliminada correctamente.");
            }

            cargarTabla();
            limpiarCampos();

        } catch (NumberFormatException e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error de Entrada", "El campo ID de Clase debe ser un número.");
        } catch (SQLException e) {
            e.printStackTrace();
            mostrarAlerta(Alert.AlertType.ERROR, "Error SQL", "Error al eliminar la clase: " + e.getMessage());
        }
    }

    // --- Métodos de Utilidad Modificados ---

    private void seleccionarClase(MouseEvent event) {
        selectedClaseData = tablaClases.getSelectionModel().getSelectedItem();
        
        // Se añade verificación de nulidad para evitar NPEs si la interfaz no cargó correctamente
        if (selectedClaseData != null && 
            txtIdClase != null && cbDia != null && txtHoraInicio != null &&
            cbActividad != null && cbInstructor != null && cbStatus != null) {
            
            // Mostrar el campo ID cuando se selecciona una clase
            txtIdClase.setVisible(true);

            // Llenar los campos con los datos de la fila seleccionada
            txtIdClase.setText(selectedClaseData.get(0));     // 0: ID_CLASE
            
            // 1: DIA -> Seleccionar en cbDia (Combox para el día)
            cbDia.getSelectionModel().select(selectedClaseData.get(1));
            
            txtHoraInicio.setText(selectedClaseData.get(2));  // 2: HORA
            
            // 3: NOMBRE_ACTIVIDAD -> Seleccionar en cbActividad
            cbActividad.getSelectionModel().select(selectedClaseData.get(3));
            
            // 4: NOMBRE COMPLETO INSTRUCTOR -> Seleccionar en cbInstructor
            cbInstructor.getSelectionModel().select(selectedClaseData.get(4)); 
            
            // 5: STATUS -> Seleccionar en cbStatus
            cbStatus.getSelectionModel().select(selectedClaseData.get(5)); 
        }
    }

    @FXML
    private void limpiarCampos() {
        // Se añaden verificaciones de nulidad para evitar la NPE en la limpieza de campos reportada
        if (txtIdClase != null) {
            txtIdClase.clear();
            // Ocultar el campo ID al limpiar campos (listo para "nueva clase")
            txtIdClase.setVisible(false); 
        }
        
        // Limpiar ComboBoxes y campo de hora
        if (cbDia != null) cbDia.getSelectionModel().clearSelection(); // Limpiar selección del día
        if (txtHoraInicio != null) txtHoraInicio.clear();
        if (cbActividad != null) cbActividad.getSelectionModel().clearSelection();
        if (cbInstructor != null) cbInstructor.getSelectionModel().clearSelection();
        if (cbStatus != null) cbStatus.getSelectionModel().clearSelection();
        
        selectedClaseData = null;
    }

    private void mostrarAlerta(Alert.AlertType tipo, String titulo, String mensaje) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}
