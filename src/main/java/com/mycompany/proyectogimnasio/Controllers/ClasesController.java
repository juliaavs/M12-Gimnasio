package com.mycompany.proyectogimnasio.Controllers;

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

public class ClasesController {

    // --- Componentes FXML para la Tabla (TableView) ---
    @FXML private TableView<ObservableList<String>> tablaClases;
    @FXML private TableColumn<ObservableList<String>, String> colId;
    @FXML private TableColumn<ObservableList<String>, String> colDia;
    @FXML private TableColumn<ObservableList<String>, String> colHoraInicio;
    @FXML private TableColumn<ObservableList<String>, String> colActividad;
    @FXML private TableColumn<ObservableList<String>, String> colIdInstructor;
    @FXML private TableColumn<ObservableList<String>, String> colStatus;

    // --- Componentes FXML para la Entrada de Datos ---
    @FXML private ComboBox<String> cbDia;
    @FXML private ComboBox<String> cbHoraInicio;
    @FXML private TextField txtIdClase;
    @FXML private ComboBox<String> cbActividad;
    @FXML private ComboBox<String> cbInstructor;
    
    @FXML private HBox hboxCrear;
    @FXML private HBox hboxEditar;
    
    // **CAMBIO CLAVE**: Un solo botón de estado
    @FXML private Button btnToggleStatus; 

    // --- Variables de Conexión y Mapeo ---
    private Connection conn;
    private Map<String, Integer> actividadesMap;
    private Map<String, Integer> actividadesDuracionMap;
    private Map<String, Integer> instructoresMap;
    private ObservableList<String> selectedClaseData;
    
    // --- Constantes para los horarios del gimnasio ---
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

        colId.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().get(0)));
        colDia.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().get(1)));
        colHoraInicio.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().get(2)));
        colActividad.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().get(3)));
        colIdInstructor.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().get(4)));
        colStatus.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().get(5)));

        try {
            conn = DriverManager.getConnection("jdbc:mysql://gondola.proxy.rlwy.net:51831/railway", "root", "dZLeazCTzEKkPnAQFANrKCxyZlNywudL");

            cargarActividades();
            cargarInstructores();
            cargarDiasSemana();
            cargarHorariosDisponibles();
            cargarTabla();

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

    // --- Métodos de CRUD (Crear, Leer, Actualizar, Borrar) ---

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
                // --- MODO CREAR ---
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
                // --- MODO ACTUALIZAR ---
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
    
    /**
     * **NUEVO MÉTODO**
     * Maneja el clic del botón de estado dinámico.
     */
    @FXML
    private void handleToggleStatus() {
        if (!validarSeleccion()) return;

        String statusActual = selectedClaseData.get(5);
        String nuevoStatus;

        // Determina la acción opuesta
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

    // --- Lógica de Validación de Horario ---
    
    private boolean validarHorarioClase(String dia, String horaInicioStr, String nombreActividad, int idClaseAExcluir) {
        // ... (Este método no cambia)
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
                     "WHERE c.dia = ? AND UPPER(c.status) != 'CANCELADO";

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


    // --- Métodos de Carga de Datos y UI ---

    private void cargarActividades() throws SQLException {
        // ... (Este método no cambia)
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
    
    @FXML
    private void cargarTabla() {
        // ... (Este método no cambia)
        ObservableList<ObservableList<String>> data = FXCollections.observableArrayList();
        String sql = "SELECT c.id_clase, c.dia, c.hora_inicio, a.nombre AS nombre_actividad, " +
                         "CONCAT(i.nombre, ' ', i.apellido) AS instructor_completo, c.status " +
                         "FROM clases c " +
                         "JOIN actividades a ON c.id_actividad = a.id_actividad " +
                         "JOIN instructores i ON c.id_instructor = i.id_instructor";
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                ObservableList<String> row = FXCollections.observableArrayList();
                row.add(rs.getString("id_clase"));
                row.add(rs.getString("dia"));
                row.add(rs.getString("hora_inicio"));
                row.add(rs.getString("nombre_actividad"));
                row.add(rs.getString("instructor_completo"));
                row.add(rs.getString("status"));
                data.add(row);
            }
            if (tablaClases != null) {
                tablaClases.setItems(data);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            mostrarAlerta(Alert.AlertType.ERROR, "Error", "No se pudo cargar la tabla de clases.");
        }
    }
    
    /**
     * **MÉTODO MODIFICADO**
     * Actualiza el texto y estilo del botón de estado.
     */
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
            
            // --- **LÓGICA DEL BOTÓN DINÁMICO** ---
            String status = selectedClaseData.get(5);
            if ("confirmado".equalsIgnoreCase(status)) {
                btnToggleStatus.setText("Cancelar");
                btnToggleStatus.setStyle("-fx-background-color: #FF9800; -fx-text-fill: white; -fx-font-weight: bold;"); // Naranja
            } else {
                // Para "cancelada" o cualquier otro estado
                btnToggleStatus.setText("Confirmar");
                btnToggleStatus.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-weight: bold;"); // Verde
            }
            
            setEstadoFormulario(true); // Modo "Editar"
        }
    }

    @FXML
    private void handleLimpiar() {
        if (txtIdClase != null) {
            txtIdClase.clear();
            txtIdClase.setVisible(false);
        }
        cbDia.getSelectionModel().clearSelection();
        cbHoraInicio.getSelectionModel().clearSelection();
        cbActividad.getSelectionModel().clearSelection();
        cbInstructor.getSelectionModel().clearSelection();
        
        tablaClases.getSelectionModel().clearSelection();
        selectedClaseData = null;
        
        setEstadoFormulario(false); // Modo "Crear"
    }
    
    private void cargarHorariosDisponibles() {
        // ... (Este método no cambia)
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
    
    private void setEstadoFormulario(boolean isEditing) {
        if (hboxCrear != null && hboxEditar != null) {
            hboxCrear.setVisible(!isEditing);
            hboxCrear.setManaged(!isEditing);
            hboxEditar.setVisible(isEditing);
            hboxEditar.setManaged(isEditing);
        }
    }

    private void cargarDiasSemana() {
        // ... (Este método no cambia)
        if (cbDia == null) return;
        ObservableList<String> dias = FXCollections.observableArrayList(
            "Lunes", "Martes", "Miércoles", "Jueves", "Viernes", "Sábado", "Domingo"
        );
        cbDia.setItems(dias);
    }

    private void cargarInstructores() throws SQLException {
        // ... (Este método no cambia)
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