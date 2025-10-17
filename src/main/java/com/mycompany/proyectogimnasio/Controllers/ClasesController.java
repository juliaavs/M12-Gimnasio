package com.mycompany.proyectogimnasio.Controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import java.sql.*;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
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
    @FXML private TableColumn<ObservableList<String>, String> colIdInstructor;
    @FXML private TableColumn<ObservableList<String>, String> colStatus;

    // --- Componentes FXML para la Entrada de Datos ---
    @FXML private ComboBox<String> cbDia;
    @FXML private TextField txtHoraInicio;
    @FXML private TextField txtIdClase;
    @FXML private ComboBox<String> cbActividad;
    @FXML private ComboBox<String> cbInstructor;
    @FXML private ComboBox<String> cbStatus;

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
            conn = DriverManager.getConnection("jdbc:mysql://centerbeam.proxy.rlwy.net:23892/railway", "root", "ShlYFjtRmPFlizYSEyizwuhZgYpWHijg");

            cargarActividades();
            cargarInstructores();
            cargarEstadosClase();
            cargarDiasSemana();
            cargarTabla();

            tablaClases.setOnMouseClicked(this::seleccionarClase);

            if (txtIdClase != null) {
                txtIdClase.setDisable(true);
                txtIdClase.setVisible(false);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            mostrarAlerta(Alert.AlertType.ERROR, "Error de Conexión", "No se pudo conectar a la base de datos.");
        }
    }

    // --- Métodos de CRUD (Crear, Leer, Actualizar, Borrar) ---

    @FXML
    private void agregarClase() {
        String nombreActividad = cbActividad.getSelectionModel().getSelectedItem();
        String nombreInstructor = cbInstructor.getSelectionModel().getSelectedItem();
        String status = cbStatus.getSelectionModel().getSelectedItem();
        String dia = cbDia.getSelectionModel().getSelectedItem();
        String horaInicio = txtHoraInicio.getText();

        if (nombreActividad == null || nombreInstructor == null || status == null || dia == null || horaInicio.trim().isEmpty()) {
            mostrarAlerta(Alert.AlertType.WARNING, "Campos Vacíos", "Todos los campos son obligatorios para crear una clase.");
            return;
        }
        
        // Llama a la validación central antes de insertar
        if (!validarHorarioClase(dia, horaInicio, nombreActividad, -1)) {
            return; // La validación falló y ya mostró una alerta
        }

        try {
            int idActividad = actividadesMap.get(nombreActividad);
            int idInstructor = instructoresMap.get(nombreInstructor);

            String sql = "INSERT INTO clases (id_instructor, id_actividad, dia, hora_inicio, status) VALUES (?, ?, ?, ?, ?)";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, idInstructor);
                ps.setInt(2, idActividad);
                ps.setString(3, dia);
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
        if (selectedClaseData == null || txtIdClase == null || txtIdClase.getText().isEmpty()) {
            mostrarAlerta(Alert.AlertType.WARNING, "Advertencia", "Debe seleccionar una clase de la tabla para poder actualizarla.");
            return;
        }

        String nombreActividad = cbActividad.getSelectionModel().getSelectedItem();
        String nombreInstructor = cbInstructor.getSelectionModel().getSelectedItem();
        String status = cbStatus.getSelectionModel().getSelectedItem();
        String dia = cbDia.getSelectionModel().getSelectedItem();
        String horaInicio = txtHoraInicio.getText();

        if (nombreActividad == null || nombreInstructor == null || status == null || dia == null || horaInicio.trim().isEmpty()) {
            mostrarAlerta(Alert.AlertType.WARNING, "Campos Vacíos", "Todos los campos deben estar completos para actualizar.");
            return;
        }

        try {
            int idClase = Integer.parseInt(txtIdClase.getText());

            // Llama a la validación, excluyendo la propia clase de la comprobación
            if (!validarHorarioClase(dia, horaInicio, nombreActividad, idClase)) {
                return; // La validación falló
            }

            int idActividad = actividadesMap.get(nombreActividad);
            int idInstructor = instructoresMap.get(nombreInstructor);

            String sql = "UPDATE clases SET id_instructor = ?, id_actividad = ?, dia = ?, hora_inicio = ?, status = ? WHERE id_clase = ?";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, idInstructor);
                ps.setInt(2, idActividad);
                ps.setString(3, dia);
                ps.setString(4, horaInicio);
                ps.setString(5, status);
                ps.setInt(6, idClase);
                ps.executeUpdate();

                mostrarAlerta(Alert.AlertType.INFORMATION, "Éxito", "Clase actualizada correctamente.");
            }

            cargarTabla();
            limpiarCampos();

        } catch (NumberFormatException e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error de Entrada", "El ID de la clase no es válido.");
        } catch (SQLException e) {
            e.printStackTrace();
            mostrarAlerta(Alert.AlertType.ERROR, "Error SQL", "Error al actualizar la clase: " + e.getMessage());
        }
    }

    @FXML
    private void eliminarClase() {
        if (selectedClaseData == null || txtIdClase == null || txtIdClase.getText().isEmpty()) {
            mostrarAlerta(Alert.AlertType.WARNING, "Advertencia", "Debe seleccionar una clase de la tabla para eliminar.");
            return;
        }

        try {
            int idClase = Integer.parseInt(txtIdClase.getText());
            String sql = "DELETE FROM clases WHERE id_clase = ?";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, idClase);
                int affectedRows = ps.executeUpdate();
                
                if (affectedRows > 0) {
                    mostrarAlerta(Alert.AlertType.INFORMATION, "Éxito", "Clase eliminada correctamente.");
                } else {
                    mostrarAlerta(Alert.AlertType.ERROR, "Error", "No se encontró la clase a eliminar.");
                }
            }

            cargarTabla();
            limpiarCampos();

        } catch (NumberFormatException e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error de Entrada", "El ID de la clase no es válido.");
        } catch (SQLException e) {
            e.printStackTrace();
            mostrarAlerta(Alert.AlertType.ERROR, "Error SQL", "Error al eliminar la clase: " + e.getMessage());
        }
    }

    // --- Lógica de Validación de Horario ---

    /**
     * Valida si el horario para una nueva clase o una actualización es válido.
     * Comprueba: 1. Formato de hora, 2. Horarios del gimnasio, 3. Solapamiento con otras clases.
     * @param idClaseAExcluir ID de la clase a ignorar (para modo actualización). Usar -1 para crear una nueva.
     * @return `true` si el horario es válido, `false` si no lo es.
     */
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
                     "WHERE c.dia = ? AND UPPER(c.status) != 'CANCELADA'";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, dia);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                if (rs.getInt("id_clase") == idClaseAExcluir) {
                    continue; // No nos comparamos con nosotros mismos
                }

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
            mostrarAlerta(Alert.AlertType.ERROR, "Error de Validación", "No se pudo validar la disponibilidad del horario en la base de datos.");
            return false;
        }

        return true;
    }

    // --- Métodos de Carga de Datos y UI ---

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
    
    @FXML
    private void cargarTabla() {
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
    
    private void seleccionarClase(MouseEvent event) {
        selectedClaseData = tablaClases.getSelectionModel().getSelectedItem();
        
        if (selectedClaseData != null) {
            txtIdClase.setVisible(true);
            txtIdClase.setText(selectedClaseData.get(0));
            cbDia.getSelectionModel().select(selectedClaseData.get(1));
            txtHoraInicio.setText(selectedClaseData.get(2));
            cbActividad.getSelectionModel().select(selectedClaseData.get(3));
            cbInstructor.getSelectionModel().select(selectedClaseData.get(4));
            cbStatus.getSelectionModel().select(selectedClaseData.get(5));
        }
    }

    @FXML
    private void limpiarCampos() {
        if (txtIdClase != null) {
            txtIdClase.clear();
            txtIdClase.setVisible(false);
        }
        cbDia.getSelectionModel().clearSelection();
        txtHoraInicio.clear();
        cbActividad.getSelectionModel().clearSelection();
        cbInstructor.getSelectionModel().clearSelection();
        cbStatus.getSelectionModel().clearSelection();
        
        tablaClases.getSelectionModel().clearSelection();
        selectedClaseData = null;
    }

    private void cargarDiasSemana() {
        if (cbDia == null) return;
        ObservableList<String> dias = FXCollections.observableArrayList(
            "Lunes", "Martes", "Miércoles", "Jueves", "Viernes", "Sábado", "Domingo"
        );
        cbDia.setItems(dias);
    }
    
    private void cargarEstadosClase() throws SQLException {
        if (cbStatus == null) return;
        cbStatus.getItems().clear();
        String sql = "SELECT DISTINCT status FROM clases";
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                cbStatus.getItems().add(rs.getString("status"));
            }
        }
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
}