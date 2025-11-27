package com.mycompany.proyectogimnasio.Controllers;

import com.mycompany.proyectogimnasio.Database;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import java.sql.*;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.Map;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.layout.HBox;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;

public class ClasesController {

    @FXML private TableView<ObservableList<String>> tablaClases;
    @FXML private TableColumn<ObservableList<String>, String> colId;
    @FXML private TableColumn<ObservableList<String>, String> colDia;
    @FXML private TableColumn<ObservableList<String>, String> colHoraInicio;
    @FXML private TableColumn<ObservableList<String>, String> colActividad;
    @FXML private TableColumn<ObservableList<String>, String> colIdInstructor;
    @FXML private TableColumn<ObservableList<String>, String> colStatus;
    

    @FXML private ComboBox<String> cbDia;
    @FXML private ComboBox<String> cbHoraInicio;
    @FXML private TextField txtIdClase;
    @FXML private ComboBox<String> cbActividad;
    @FXML private ComboBox<String> cbInstructor;
    
    @FXML private HBox hboxCrear;
    @FXML private HBox hboxEditar;
    
    @FXML private Button btnToggleStatus;
    @FXML private TextField txtFiltro;
    @FXML private Button btnLimpiarSeleccion;
    
    
    private ObservableList<ObservableList<String>> clasesData;
    private Connection conn;
    private Map<String, Integer> actividadesMap;
    private Map<String, Integer> actividadesDuracionMap;
    private Map<String, Integer> instructoresMap;
    private ObservableList<String> selectedClaseData;
    
    private static final LocalTime APERTURA_MANANA = LocalTime.of(7, 0);
    private static final LocalTime CIERRE_MANANA = LocalTime.of(13, 0);
    private static final LocalTime APERTURA_TARDE = LocalTime.of(15, 0);
    private static final LocalTime CIERRE_TARDE = LocalTime.of(19, 0);
    private static final int INTERVALO_MINUTOS = 15;

    @FXML
public void initialize() {
    actividadesMap = new HashMap<>();
    instructoresMap = new HashMap<>();
    actividadesDuracionMap = new HashMap<>(); 

    // ... (Tu configuración de setCellValueFactory para todas las columnas) ...
    colId.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().get(0)));
    colDia.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().get(1)));
    colHoraInicio.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().get(2)));
    colActividad.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().get(3)));
    colIdInstructor.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().get(4)));
    colStatus.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().get(5)));
    
    // ... (Tu configuración de colStatus.setCellFactory para colores) ...
    colStatus.setCellFactory(column -> {
         // ... (código que ya tenías) ...
         return new TableCell<ObservableList<String>, String>() {
             @Override
             protected void updateItem(String item, boolean empty) {
                 super.updateItem(item, empty);
                 this.getStyleClass().removeAll("clase-confirmada", "clase-cancelada");
                 if (empty || item == null) {
                     setText(null);
                     setGraphic(null);
                 } else {
                     setText(item);
                     if (item.equalsIgnoreCase("Confirmado")) {
                         setStyle("-fx-text-fill: green;");
                     } else if (item.equalsIgnoreCase("Cancelado")) {
                         setStyle("-fx-text-fill: red; -fx-font-weight: bold;");
                     }
                 }
             }
         };
    });
    
    try {
        conn = Database.getConnection();

        cargarActividades();
        cargarInstructores();
        cargarDiasSemana();
        cargarHorariosDisponibles();
        
        // **********************************************
        // PASO 1: CARGAR Y ASIGNAR DATOS A clasesData
        // **********************************************
        // Asumiendo que cargarTabla() ahora devuelve List<ObservableList<String>>
        clasesData = FXCollections.observableArrayList(cargarTabla()); 

        // **********************************************
        // PASO 2: CONFIGURAR EL FILTRO
        // **********************************************
        configurarFiltroClases(); 

        tablaClases.getSelectionModel().selectedItemProperty().addListener(
            (obs, oldSelection, newSelection) -> mostrarDetallesClase(newSelection));

        if (txtIdClase != null) {
            txtIdClase.setDisable(true);
            txtIdClase.setVisible(false);
        }
        
        setEstadoFormulario(false);

    } catch (SQLException e) {
        e.printStackTrace();
        mostrarAlerta(Alert.AlertType.ERROR, "Error de Conexión", "No se pudo conectar a la base de datos.");
    }
}
    
    private void configurarFiltroClases() {
    // La lista 'clasesData' debe estar inicializada y contener todos los datos.
    if (clasesData == null) {
        System.err.println("Error: clasesData no ha sido inicializada.");
        return;
    }
    
    // 1. Crear FilteredList basada en la lista observable original de clases
    // Usamos ObservableList<String> para el tipo de elemento
    FilteredList<ObservableList<String>> filteredData = new FilteredList<>(clasesData, p -> true);

    // 2. Listener para el campo de texto de búsqueda
    txtFiltro.textProperty().addListener((observable, oldValue, newValue) -> {
        
        filteredData.setPredicate(claseRow -> {
            // claseRow es una ObservableList<String> que representa una fila
            if (newValue == null || newValue.isEmpty()) {
                return true;
            }
            
            String lowerCaseFilter = newValue.toLowerCase();

            // 3. Lógica de Filtrado usando los índices:
            
            // Índice 3: Nombre de Actividad
            String nombreActividad = claseRow.get(3);
            if (nombreActividad != null && nombreActividad.toLowerCase().contains(lowerCaseFilter)) {
                return true;
            } 
            
            // Índice 0: ID de Clase
            String idClase = claseRow.get(0);
            if (idClase != null && idClase.contains(lowerCaseFilter)) {
                return true;
            }
            
            // Índice 1: Día
            String dia = claseRow.get(1);
            if (dia != null && dia.toLowerCase().contains(lowerCaseFilter)) {
                return true;
            }
            
            // Índice 5: Status
            String status = claseRow.get(5);
            if (status != null && status.toLowerCase().contains(lowerCaseFilter)) {
                return true;
            }
            
            // Si no hay coincidencias con ningún criterio
            return false;
        });
    });
    
    // 4. Envolver en SortedList 
    SortedList<ObservableList<String>> sortedData = new SortedList<>(filteredData);

    // 5. Vincular el comparador de la SortedList al comparador de la TableView
    sortedData.comparatorProperty().bind(tablaClases.comparatorProperty());

    // 6. Asignar la lista ordenada (y filtrada) a la TableView
    tablaClases.setItems(sortedData);
}

    @FXML
    private void handleGuardar() {
        String nombreActividad = cbActividad.getSelectionModel().getSelectedItem();
        String nombreInstructor = cbInstructor.getSelectionModel().getSelectedItem();
        String dia = cbDia.getSelectionModel().getSelectedItem();
        String horaInicio = cbHoraInicio.getSelectionModel().getSelectedItem();

        if (nombreActividad == null || nombreInstructor == null || dia == null || horaInicio == null) {
            mostrarAlerta(Alert.AlertType.WARNING, "Campos Vacíos", "Todos los campos son obligatorios.");
            return;
        }
        
        try {
            int idActividad = actividadesMap.get(nombreActividad);
            int idInstructor = instructoresMap.get(nombreInstructor);

            if (selectedClaseData == null || txtIdClase.getText().isEmpty()) {
                if (!validarHorarioClase(dia, horaInicio, nombreActividad, -1)) return; 
                String sql = "INSERT INTO clases (id_instructor, id_actividad, dia, hora_inicio, status) VALUES (?, ?, ?, ?, 'confirmado')";
                try (PreparedStatement ps = conn.prepareStatement(sql)) {
                    ps.setInt(1, idInstructor);
                    ps.setInt(2, idActividad);
                    ps.setString(3, dia);
                    ps.setString(4, horaInicio);
                    ps.executeUpdate();
                    mostrarAlerta(Alert.AlertType.INFORMATION, "Éxito", "Clase agregada correctamente.");
                }
            } else {
                int idClase = Integer.parseInt(txtIdClase.getText());
                if (!validarHorarioClase(dia, horaInicio, nombreActividad, idClase)) return;
                String sql = "UPDATE clases SET id_instructor = ?, id_actividad = ?, dia = ?, hora_inicio = ? WHERE id_clase = ?";
                try (PreparedStatement ps = conn.prepareStatement(sql)) {
                    ps.setInt(1, idInstructor);
                    ps.setInt(2, idActividad);
                    ps.setString(3, dia);
                    ps.setString(4, horaInicio);
                    ps.setInt(5, idClase);
                    ps.executeUpdate();
                    mostrarAlerta(Alert.AlertType.INFORMATION, "Éxito", "Datos de la clase actualizados.");
                }
            }
            cargarTabla();
            handleLimpiar();
        } catch (SQLException e) {
            e.printStackTrace();
            mostrarAlerta(Alert.AlertType.ERROR, "Error SQL", "Error al guardar la clase: " + e.getMessage());
        } catch (NumberFormatException e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error de Datos", "El ID de la clase no es válido.");
        }
    }


    @FXML
    private void handleEliminar() {
        if (!validarSeleccion()) return;
        try {
            int idClase = Integer.parseInt(txtIdClase.getText());
            String sql = "DELETE FROM clases WHERE id_clase = ?";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, idClase);
                int affectedRows = ps.executeUpdate();
                if (affectedRows > 0) {
                    mostrarAlerta(Alert.AlertType.INFORMATION, "Éxito", "Clase eliminada permanentemente.");
                }
            }
            cargarTabla();
            handleLimpiar();
        } catch (Exception e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error SQL", "Error al eliminar la clase: " + e.getMessage());
        }
    }
    
    @FXML
    private void handleToggleStatus() {
        if (!validarSeleccion()) return;

        String statusActual = selectedClaseData.get(5);
        String nuevoStatus;

        if ("confirmado".equalsIgnoreCase(statusActual)) {
            nuevoStatus = "cancelado";
        } else {
            nuevoStatus = "confirmado";
        }
        
        actualizarStatus(nuevoStatus);
    }
    
    private void actualizarStatus(String nuevoStatus) {
        if (!validarSeleccion()) return;
        try {
            int idClase = Integer.parseInt(txtIdClase.getText());
            String sql = "UPDATE clases SET status = ? WHERE id_clase = ?";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, nuevoStatus);
                ps.setInt(2, idClase);
                ps.executeUpdate();
                mostrarAlerta(Alert.AlertType.INFORMATION, "Éxito", "Estado de la clase actualizado a: " + nuevoStatus);
            }
            cargarTabla();
            handleLimpiar();
        } catch (Exception e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error SQL", "Error al actualizar el estado: " + e.getMessage());
        }
    }
    
    private boolean validarHorarioClase(String dia, String horaInicioStr, String nombreActividad, int idClaseAExcluir) {
        LocalTime nuevaHoraInicio;
        LocalTime nuevaHoraFin;
        try {
            nuevaHoraInicio = LocalTime.parse(horaInicioStr);
            if (!actividadesDuracionMap.containsKey(nombreActividad)) {
                mostrarAlerta(Alert.AlertType.ERROR, "Error de Datos", "No se encontró la duración para la actividad seleccionada.");
                return false;
            }
            int duracion = actividadesDuracionMap.get(nombreActividad);
            nuevaHoraFin = nuevaHoraInicio.plusMinutes(duracion);
        } catch (DateTimeParseException e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Formato de Hora Inválido", "La hora debe tener el formato HH:mm (ej: 09:00 o 18:30).");
            return false;
        }

        boolean enHorarioManana = !nuevaHoraInicio.isBefore(APERTURA_MANANA) && !nuevaHoraFin.isAfter(CIERRE_MANANA);
        boolean enHorarioTarde = !nuevaHoraInicio.isBefore(APERTURA_TARDE) && !nuevaHoraFin.isAfter(CIERRE_TARDE);

        if (!enHorarioManana && !enHorarioTarde) {
            mostrarAlerta(Alert.AlertType.ERROR, "Horario No Permitido",
                "La clase (de " + nuevaHoraInicio + " a " + nuevaHoraFin + ") debe impartirse completamente dentro de los siguientes horarios:\n" +
                "Mañana: de " + APERTURA_MANANA + " a " + CIERRE_MANANA + "\n" +
                "Tarde: de " + APERTURA_TARDE + " a " + CIERRE_TARDE);
            return false;
        }

        String sql = "SELECT c.id_clase, c.hora_inicio, a.duracion, a.nombre AS nombre_actividad_existente " +
                     "FROM clases c " +
                     "JOIN actividades a ON c.id_actividad = a.id_actividad " +
                     "WHERE c.dia = ? AND UPPER(c.status) != 'CANCELADO'";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, dia);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                if (rs.getInt("id_clase") == idClaseAExcluir) continue; 
                LocalTime existenteHoraInicio = rs.getTime("hora_inicio").toLocalTime();
                LocalTime existenteHoraFin = existenteHoraInicio.plusMinutes(rs.getInt("duracion"));
                String nombreActividadExistente = rs.getString("nombre_actividad_existente");
                if (nuevaHoraInicio.isBefore(existenteHoraFin) && nuevaHoraFin.isAfter(existenteHoraInicio)) {
                    mostrarAlerta(Alert.AlertType.ERROR, "Conflicto de Horario",
                        "La sala ya está ocupada por la clase '" + nombreActividadExistente + "' que se imparte de " +
                        existenteHoraInicio + " a " + existenteHoraFin + ".\n\n" +
                        "El horario que intentas registrar (" + nuevaHoraInicio + " - " + nuevaHoraFin + ") se solapa.");
                    return false;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            mostrarAlerta(Alert.AlertType.ERROR, "Error de Validación", "No se pudo validar la disponibilidad del horario.");
            return false;
        }
        return true;
    }

    private void cargarActividades() throws SQLException {
        actividadesMap.clear();
        actividadesDuracionMap.clear();
        if (cbActividad == null) return;
        String sql = "SELECT id_actividad, nombre, duracion FROM actividades";
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                int id = rs.getInt("id_actividad");
                String nombre = rs.getString("nombre");
                int duracion = rs.getInt("duracion");
                cbActividad.getItems().add(nombre);
                actividadesMap.put(nombre, id);
                actividadesDuracionMap.put(nombre, duracion);
            }
        }
    }
    
    private ObservableList<ObservableList<String>> cargarTabla() {
    ObservableList<ObservableList<String>> data = FXCollections.observableArrayList();
    
    // Consulta SQL: id_clase, dia, hora_inicio, nombre_actividad, instructor_completo, status
    String sql = "SELECT c.id_clase, c.dia, c.hora_inicio, a.nombre AS nombre_actividad, " +
                 "CONCAT(i.nombre, ' ', i.apellido) AS instructor_completo, c.status " +
                 "FROM clases c " +
                 "JOIN actividades a ON c.id_actividad = a.id_actividad " +
                 "JOIN instructores i ON c.id_instructor = i.id_instructor";
    
    // El try-with-resources asume que 'conn' está inicializado correctamente en initialize()
    try (Statement stmt = conn.createStatement();
         ResultSet rs = stmt.executeQuery(sql)) {
        
        while (rs.next()) {
            ObservableList<String> row = FXCollections.observableArrayList();
            // Índice 0
            row.add(rs.getString("id_clase"));
            // Índice 1
            row.add(rs.getString("dia"));
            // Índice 2
            row.add(rs.getString("hora_inicio"));
            // Índice 3
            row.add(rs.getString("nombre_actividad"));
            // Índice 4
            row.add(rs.getString("instructor_completo"));
            // Índice 5
            row.add(rs.getString("status"));
            
            data.add(row);
        }
    } catch (SQLException e) {
        e.printStackTrace();
        mostrarAlerta(Alert.AlertType.ERROR, "Error", "No se pudo cargar la tabla de clases.");
    }
    
    // ¡Devolver la lista!
    return data; 
}
    
    private void mostrarDetallesClase(ObservableList<String> claseData) {
        selectedClaseData = claseData;
        
        if (selectedClaseData != null) {
            if (txtIdClase != null) {
                txtIdClase.setVisible(true);
                txtIdClase.setText(selectedClaseData.get(0));
            }
            cbDia.getSelectionModel().select(selectedClaseData.get(1));
            try {
                LocalTime time = LocalTime.parse(selectedClaseData.get(2)); 
                String formattedTime = time.format(DateTimeFormatter.ofPattern("HH:mm"));
                cbHoraInicio.setValue(formattedTime);
            } catch (Exception e) {
                cbHoraInicio.setValue(null);
            }
            cbActividad.getSelectionModel().select(selectedClaseData.get(3));
            cbInstructor.getSelectionModel().select(selectedClaseData.get(4));
          
            String status = selectedClaseData.get(5);
            if ("confirmado".equalsIgnoreCase(status)) {
                btnToggleStatus.setText("Cancelar");
                btnToggleStatus.setStyle("-fx-background-color: #FF9800; -fx-text-fill: white; -fx-font-weight: bold;");
            } else {
                btnToggleStatus.setText("Confirmar");
                btnToggleStatus.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-weight: bold;");
            }
            
            setEstadoFormulario(true);
        }
    }

    @FXML
    private void handleLimpiar() {
        // 1. Limpiar los campos del formulario
    
        // IMPORTANTE: Asegúrate de que 'cbDia' esté inyectado correctamente.
        // Si cbDia está inyectado, esta línea debería funcionar:
        cbDia.getSelectionModel().clearSelection(); 
    
        cbHoraInicio.getSelectionModel().clearSelection();
        cbActividad.getSelectionModel().clearSelection();
        cbInstructor.getSelectionModel().clearSelection();
    
        // Limpiar el ID oculto
        txtIdClase.setText(""); 

        // 2. Limpiar la selección de la tabla
        tablaClases.getSelectionModel().clearSelection(); 

        // 3. Restaurar el formulario al modo "Crear"
        setEstadoFormulario(false); 
    }
    
    private void cargarHorariosDisponibles() {
        if (cbHoraInicio == null) return;
        ObservableList<String> horarios = FXCollections.observableArrayList();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
        LocalTime hora = APERTURA_MANANA;
        while (hora.isBefore(CIERRE_MANANA)) {
            horarios.add(hora.format(formatter));
            hora = hora.plusMinutes(INTERVALO_MINUTOS);
        }
        hora = APERTURA_TARDE;
        while (hora.isBefore(CIERRE_TARDE)) {
            horarios.add(hora.format(formatter));
            hora = hora.plusMinutes(INTERVALO_MINUTOS);
        }
        cbHoraInicio.setItems(horarios);
    }
    
    private void setEstadoFormulario(boolean modoEdicion) {
        // Si la clase que estás editando está inactiva, puedes deshabilitar la edición de campos
        // (Ejemplo: cbDia.setDisable(modoEdicion);) 
    
        // Ocultar/Mostrar HBox de Crear
        hboxCrear.setManaged(!modoEdicion);
        hboxCrear.setVisible(!modoEdicion);

        // Ocultar/Mostrar HBox de Editar (Actualizar, Toggle Status, Limpiar/Eliminar)
        hboxEditar.setManaged(modoEdicion);
        hboxEditar.setVisible(modoEdicion);
    }

    private void cargarDiasSemana() {
        if (cbDia == null) return;
        ObservableList<String> dias = FXCollections.observableArrayList(
            "Lunes", "Martes", "Miércoles", "Jueves", "Viernes", "Sábado", "Domingo"
        );
        cbDia.setItems(dias);
    }

    private void cargarInstructores() throws SQLException {
        instructoresMap.clear();
        if (cbInstructor == null) return;
        cbInstructor.getItems().clear();
        String sql = "SELECT id_instructor, nombre, apellido FROM instructores";
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                int id = rs.getInt("id_instructor");
                String nombreCompleto = rs.getString("nombre") + " " + rs.getString("apellido");
                cbInstructor.getItems().add(nombreCompleto);
                instructoresMap.put(nombreCompleto, id);
            }
        }
    }

    private void mostrarAlerta(Alert.AlertType tipo, String titulo, String mensaje) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
    
    private boolean validarSeleccion() {
        if (selectedClaseData == null || txtIdClase == null || txtIdClase.getText().isEmpty()) {
            mostrarAlerta(Alert.AlertType.WARNING, "Acción Inválida", "Debe seleccionar una clase de la tabla primero.");
            return false;
        }
        return true;
    }
}