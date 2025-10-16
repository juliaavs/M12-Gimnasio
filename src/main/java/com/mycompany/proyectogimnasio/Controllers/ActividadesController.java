package com.mycompany.proyectogimnasio.Controllers;

import com.mycompany.proyectogimnasio.Models.Actividad;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import java.sql.*;

public class ActividadesController {

    // --- Componentes FXML ---
    @FXML private TableView<Actividad> tablaActividades;
    @FXML private TableColumn<Actividad, Integer> colId;
    @FXML private TableColumn<Actividad, String> colNombre;
    @FXML private TableColumn<Actividad, String> colDescripcion;
    @FXML private TableColumn<Actividad, Integer> colDuracion;
    @FXML private TableColumn<Actividad, Integer> colAforo;

    @FXML private TextField txtIdActividad;
    @FXML private TextField txtNombre;
    @FXML private TextArea txtDescripcion;
    @FXML private ComboBox<Integer> cbDuracion;
    @FXML private TextField txtAforo;

    private Connection conn;

    @FXML
    public void initialize() {
        // Llama al método para configurar el ComboBox
        inicializarComboBoxDuracion();

        try {
            conn = DriverManager.getConnection("jdbc:mysql://centerbeam.proxy.rlwy.net:23892/railway", "root", "ShlYFjtRmPFlizYSEyizwuhZgYpWHijg");
            
            cargarTablaActividades();
            
            tablaActividades.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldSelection, newSelection) -> {
                    if (newSelection != null) {
                        seleccionarActividad(newSelection);
                    }
                }
            );

        } catch (SQLException e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error de Conexión", "No se pudo conectar a la base de datos.");
            e.printStackTrace();
        }
    }
    
    /**
     * MÉTODO NUEVO Y CLAVE:
     * Se encarga de inicializar y poblar el ComboBox de duraciones.
     * Esto asegura que el componente FXML no sea nulo.
     */
    private void inicializarComboBoxDuracion() {
        // Comprobación de seguridad: si cbDuracion es null, significa que el fx:id en el FXML está mal.
        if (cbDuracion == null) {
            System.err.println("Error Crítico: El ComboBox 'cbDuracion' no fue inyectado. Revisa el fx:id en ActividadesView.fxml.");
            return;
        }
        
        // Crea la lista de opciones
        ObservableList<Integer> duraciones = FXCollections.observableArrayList(15, 30, 45, 60);
        
        // Asigna las opciones al ComboBox
        cbDuracion.setItems(duraciones);
    }

    private void cargarTablaActividades() {
        ObservableList<Actividad> listaActividades = FXCollections.observableArrayList();
        String sql = "SELECT id_actividad, nombre, descripcion, duracion, aforo FROM actividades";

        try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                listaActividades.add(new Actividad(
                    rs.getInt("id_actividad"),
                    rs.getString("nombre"),
                    rs.getString("descripcion"),
                    rs.getInt("duracion"),
                    rs.getInt("aforo")
                ));
            }
            tablaActividades.setItems(listaActividades);
        } catch (SQLException e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error al Cargar", "No se pudieron cargar las actividades.");
            e.printStackTrace();
        }
    }
    
    @FXML
    private void agregarActividad() {
        Integer duracionSeleccionada = cbDuracion.getValue();
        
        if (txtNombre.getText().isEmpty() || txtAforo.getText().isEmpty() || duracionSeleccionada == null) {
            mostrarAlerta(Alert.AlertType.WARNING, "Campos Vacíos", "El nombre, la duración y el aforo son obligatorios.");
            return;
        }

        String sql = "INSERT INTO actividades (nombre, descripcion, duracion, aforo) VALUES (?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, txtNombre.getText());
            ps.setString(2, txtDescripcion.getText());
            ps.setInt(3, duracionSeleccionada);
            ps.setInt(4, Integer.parseInt(txtAforo.getText()));
            
            ps.executeUpdate();
            mostrarAlerta(Alert.AlertType.INFORMATION, "Éxito", "Actividad agregada correctamente.");
            
            cargarTablaActividades();
            limpiarCampos();

        } catch (NumberFormatException e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error de Formato", "El aforo debe ser un número entero válido.");
        } catch (SQLException e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error SQL", "No se pudo agregar la actividad.");
            e.printStackTrace();
        }
    }

    @FXML
    private void actualizarActividad() {
        Integer duracionSeleccionada = cbDuracion.getValue();

        if (txtIdActividad.getText().isEmpty()) {
            mostrarAlerta(Alert.AlertType.WARNING, "Sin Selección", "Por favor, selecciona una actividad para actualizar.");
            return;
        }
        if (duracionSeleccionada == null) {
            mostrarAlerta(Alert.AlertType.WARNING, "Campo Vacío", "Debes seleccionar una duración.");
            return;
        }

        String sql = "UPDATE actividades SET nombre = ?, descripcion = ?, duracion = ?, aforo = ? WHERE id_actividad = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, txtNombre.getText());
            ps.setString(2, txtDescripcion.getText());
            ps.setInt(3, duracionSeleccionada);
            ps.setInt(4, Integer.parseInt(txtAforo.getText()));
            ps.setInt(5, Integer.parseInt(txtIdActividad.getText()));

            ps.executeUpdate();
            mostrarAlerta(Alert.AlertType.INFORMATION, "Éxito", "Actividad actualizada correctamente.");
            
            cargarTablaActividades();
            limpiarCampos();

        } catch (NumberFormatException e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error de Formato", "El aforo y el ID deben ser números válidos.");
        } catch (SQLException e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error SQL", "No se pudo actualizar la actividad.");
            e.printStackTrace();
        }
    }

    @FXML
    private void eliminarActividad() {
        if (txtIdActividad.getText().isEmpty()) {
            mostrarAlerta(Alert.AlertType.WARNING, "Sin Selección", "Por favor, selecciona una actividad para eliminar.");
            return;
        }
        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION, "¿Estás seguro?", ButtonType.YES, ButtonType.NO);
        if (confirmacion.showAndWait().orElse(ButtonType.NO) != ButtonType.YES) return;
        String sql = "DELETE FROM actividades WHERE id_actividad = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, Integer.parseInt(txtIdActividad.getText()));
            ps.executeUpdate();
            mostrarAlerta(Alert.AlertType.INFORMATION, "Éxito", "Actividad eliminada.");
            cargarTablaActividades();
            limpiarCampos();
        } catch (SQLException e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error SQL", "No se pudo eliminar la actividad.");
            e.printStackTrace();
        }
    }
    
    private void seleccionarActividad(Actividad actividad) {
        txtIdActividad.setText(String.valueOf(actividad.getIdActividad()));
        txtNombre.setText(actividad.getNombre());
        txtDescripcion.setText(actividad.getDescripcion());
        cbDuracion.setValue(actividad.getDuracion());
        txtAforo.setText(String.valueOf(actividad.getAforo()));
    }

    @FXML
    private void limpiarCampos() {
        txtIdActividad.clear();
        txtNombre.clear();
        txtDescripcion.clear();
        cbDuracion.getSelectionModel().clearSelection();
        cbDuracion.setPromptText("Seleccionar duración");
        txtAforo.clear();
        tablaActividades.getSelectionModel().clearSelection();
    }

    private void mostrarAlerta(Alert.AlertType tipo, String titulo, String mensaje) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}