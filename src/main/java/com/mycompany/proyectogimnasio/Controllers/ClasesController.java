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
    @FXML private TableColumn<ObservableList<String>, String> colIdInstructor;
    @FXML private TableColumn<ObservableList<String>, String> colStatus;

    // --- Componentes FXML para la Entrada de Datos ---
    @FXML private TextField txtDia;
    @FXML private TextField txtHoraInicio;
    @FXML private TextField txtStatus;
    @FXML private TextField txtIdInstructor;
    @FXML private TextField txtIdClase; // Campo para mostrar la ID de la clase seleccionada
    @FXML private ComboBox<String> cbActividad; // Nombre de la actividad

    // --- Variables de Conexión y Mapeo ---
    private Connection conn;
    // Mapa para traducir Nombre de Actividad a su ID (necesario para la inserción)
    private Map<String, Integer> actividadesMap;
    // La clase seleccionada en la tabla
    private ObservableList<String> selectedClaseData;

    @FXML
    public void initialize() {
        // Inicializar el mapa
        actividadesMap = new HashMap<>();
        
        // 1. Configurar las CellValueFactory
        // Usamos una lista observable de Strings (fila) y el índice para cada columna
        colId.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().get(0))); // ID_CLASE
        colDia.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().get(1))); // DIA
        colHoraInicio.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().get(2))); // HORA
        colActividad.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().get(3))); // NOMBRE_ACTIVIDAD
        colIdInstructor.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().get(4))); // ID_INSTRUCTOR
        colStatus.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().get(5))); // STATUS

        try {
            // 2. Conexión a la Base de Datos (Asegúrate de cambiar estos datos)
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/tu_base", "usuario", "password");

            // 3. Cargar datos iniciales
            cargarActividades(); // Llenar ComboBox y el mapa de IDs
            cargarTabla();       // Llenar TableView

            // 4. Configurar el evento de selección en la tabla
            tablaClases.setOnMouseClicked(this::seleccionarClase);

        } catch (SQLException e) {
            e.printStackTrace();
            mostrarAlerta(Alert.AlertType.ERROR, "Error de Conexión", "No se pudo conectar a la base de datos.");
        }
    }

    // --- Métodos de Carga de Datos ---

    private void cargarActividades() throws SQLException {
        // Carga los nombres de actividades en el ComboBox y mapea Nombre -> ID
        actividadesMap.clear();
        String sql = "SELECT id_actividad, nombre FROM actividades";
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                int id = rs.getInt("id_actividad");
                String nombre = rs.getString("nombre");
                
                cbActividad.getItems().add(nombre);
                actividadesMap.put(nombre, id); // Guardar el mapeo
            }
        }
    }

    @FXML
    private void cargarTabla() {
        // Carga la lista de clases con el nombre de la actividad
        ObservableList<ObservableList<String>> data = FXCollections.observableArrayList();
        
        String sql = "SELECT c.id_clase, c.dia, c.hora_inicio, a.nombre AS nombre_actividad, c.id_instructor, c.status " +
                     "FROM clases c " +
                     "JOIN actividades a ON c.id_actividad = a.id_actividad";
        
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                ObservableList<String> row = FXCollections.observableArrayList();
                // Importante: El orden aquí debe coincidir con el orden de las CellValueFactory (get(0), get(1), etc.)
                row.add(String.valueOf(rs.getInt("id_clase")));      // 0
                row.add(rs.getString("dia"));                        // 1
                row.add(rs.getString("hora_inicio"));                // 2
                row.add(rs.getString("nombre_actividad"));           // 3
                row.add(String.valueOf(rs.getInt("id_instructor"))); // 4
                row.add(rs.getString("status"));                     // 5
                data.add(row);
            }
            tablaClases.setItems(data);
        } catch (SQLException e) {
            e.printStackTrace();
            mostrarAlerta(Alert.AlertType.ERROR, "Error", "No se pudo cargar la tabla de clases.");
        }
    }

    // --- Métodos de CRUD ---

    @FXML
    private void agregarClase() {
        try {
            String nombreActividad = cbActividad.getSelectionModel().getSelectedItem();
            if (nombreActividad == null) {
                mostrarAlerta(Alert.AlertType.WARNING, "Advertencia", "Debe seleccionar una actividad.");
                return;
            }
            
            int idActividad = actividadesMap.get(nombreActividad); // Obtener ID real
            int idInstructor = Integer.parseInt(txtIdInstructor.getText());
            String dia = txtDia.getText();
            String horaInicio = txtHoraInicio.getText();
            String status = txtStatus.getText();

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

        } catch (NumberFormatException e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error de Entrada", "ID de Instructor debe ser un número válido.");
        } catch (SQLException e) {
            e.printStackTrace();
            mostrarAlerta(Alert.AlertType.ERROR, "Error SQL", "Error al agregar la clase: " + e.getMessage());
        }
    }

    @FXML
    private void actualizarClase() {
        if (selectedClaseData == null) {
            mostrarAlerta(Alert.AlertType.WARNING, "Advertencia", "Debe seleccionar una clase para actualizar.");
            return;
        }

        try {
            int idClase = Integer.parseInt(txtIdClase.getText()); // Usamos el ID del campo
            String nombreActividad = cbActividad.getSelectionModel().getSelectedItem();
            if (nombreActividad == null) {
                mostrarAlerta(Alert.AlertType.WARNING, "Advertencia", "Debe seleccionar una actividad.");
                return;
            }
            
            int idActividad = actividadesMap.get(nombreActividad);
            int idInstructor = Integer.parseInt(txtIdInstructor.getText());
            String dia = txtDia.getText();
            String horaInicio = txtHoraInicio.getText();
            String status = txtStatus.getText();

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
            mostrarAlerta(Alert.AlertType.ERROR, "Error de Entrada", "ID de Clase e Instructor deben ser números válidos.");
        } catch (SQLException e) {
            e.printStackTrace();
            mostrarAlerta(Alert.AlertType.ERROR, "Error SQL", "Error al actualizar la clase: " + e.getMessage());
        }
    }

    @FXML
    private void eliminarClase() {
        if (selectedClaseData == null) {
            mostrarAlerta(Alert.AlertType.WARNING, "Advertencia", "Debe seleccionar una clase para eliminar.");
            return;
        }

        try {
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

    // --- Métodos de Utilidad ---

    private void seleccionarClase(MouseEvent event) {
        selectedClaseData = tablaClases.getSelectionModel().getSelectedItem();
        if (selectedClaseData != null) {
            // Llenar los campos con los datos de la fila seleccionada
            txtIdClase.setText(selectedClaseData.get(0)); // ID_CLASE
            txtDia.setText(selectedClaseData.get(1)); // DIA
            txtHoraInicio.setText(selectedClaseData.get(2)); // HORA
            // El campo 3 es el NOMBRE_ACTIVIDAD, lo seleccionamos en el ComboBox
            cbActividad.getSelectionModel().select(selectedClaseData.get(3));
            txtIdInstructor.setText(selectedClaseData.get(4)); // ID_INSTRUCTOR
            txtStatus.setText(selectedClaseData.get(5)); // STATUS
        }
    }

    @FXML
    private void limpiarCampos() {
        txtIdClase.clear();
        txtIdInstructor.clear();
        txtDia.clear();
        txtHoraInicio.clear();
        txtStatus.clear();
        cbActividad.getSelectionModel().clearSelection();
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