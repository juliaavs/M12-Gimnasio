package com.mycompany.proyectogimnasio.Controllers;

import com.mycompany.proyectogimnasio.Service.ReservasService;
import com.mycompany.proyectogimnasio.Models.Reservas;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;

public class ReservasController implements Initializable {
    
    // Elementos del FXML
    @FXML private TableView<Reservas> reservasTable;
    
    // Columnas que muestran los IDs (visibilidad opcional)
    @FXML private TableColumn<Reservas, Number> idClaseColumn;
    @FXML private TableColumn<Reservas, Number> idClienteColumn;
    
    // Columnas de datos visibles
    @FXML private TableColumn<Reservas, String> nombreClaseColumn; // Nueva
    @FXML private TableColumn<Reservas, String> nombreClienteColumn; // Nueva
    @FXML private TableColumn<Reservas, String> statusColumn;
    @FXML private TableColumn<Reservas, LocalDate> diaReservaColumn;
    
    @FXML private Button toggleStatusButton;

    private final ReservasService reservasService = new ReservasService();
    private ObservableList<Reservas> reservasList;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // 1. Mapeo de Columnas (Binding)
        idClaseColumn.setCellValueFactory(cellData -> cellData.getValue().idClaseProperty());
        idClienteColumn.setCellValueFactory(cellData -> cellData.getValue().idClienteProperty());
        statusColumn.setCellValueFactory(cellData -> cellData.getValue().statusProperty());
        diaReservaColumn.setCellValueFactory(cellData -> cellData.getValue().diaReservaProperty());
        
        // Mapeo de los nombres
        nombreClaseColumn.setCellValueFactory(cellData -> cellData.getValue().nombreClaseProperty());
        nombreClienteColumn.setCellValueFactory(cellData -> cellData.getValue().nombreClienteProperty());

        // Configuración visual del Status
       statusColumn.setCellFactory(column -> new javafx.scene.control.TableCell<Reservas, String>() {
    @Override
    protected void updateItem(String item, boolean empty) {
        super.updateItem(item, empty);
        
        // Limpiar estilos anteriores
        setStyle(""); 
        
        if (empty || item == null) {
            setText(null);
        } else {
            setText(item.toUpperCase()); // Mostrar el texto en mayúsculas

            // Aplicar estilo en línea basado en el estado
            if ("confirmado".equalsIgnoreCase(item)) {
                // Fondo verde claro y texto verde oscuro
                setStyle("-fx-text-fill: green;");
            } else if ("cancelado".equalsIgnoreCase(item)) {
                // Fondo rojo claro y texto rojo oscuro
                setStyle("-fx-text-fill: red; -fx-font-weight: bold;");
            }
        }
    }
});
        
        // 2. Cargar Datos
        loadReservas();
        
        // 3. Listener de Selección
        toggleStatusButton.setDisable(true); 
        reservasTable.getSelectionModel().selectedItemProperty().addListener(
            (observable, oldValue, newValue) -> updateButtonState(newValue));
    }

    /**
     * Carga las reservas desde el Service.
     */
    private void loadReservas() {
        reservasList = FXCollections.observableArrayList(reservasService.getAll());
        reservasTable.setItems(reservasList);
    }
    
    /**
     * Maneja el clic en el botón para cambiar el estado de la reserva.
     */
    @FXML
    private void handleToggleStatus() {
        Reservas selectedReserva = reservasTable.getSelectionModel().getSelectedItem();
        
        if (selectedReserva == null) {
            showAlert("Advertencia", "Selecciona una reserva para modificar su estado.", Alert.AlertType.WARNING);
            return;
        }
        
        String newStatus = selectedReserva.isConfirmado() ? "cancelado" : "confirmado";
        String action = selectedReserva.isConfirmado() ? "CANCELAR" : "CONFIRMAR";
        
        if (!confirmAction("Confirmación de Cambio", 
                           "¿Deseas realmente " + action + " la reserva de " + selectedReserva.getNombreCliente() + "?")) {
            return;
        }
        
        // 1. Actualizar en la Base de Datos (Service)
        boolean success = reservasService.updateStatus(
            selectedReserva.getIdClase(), 
            selectedReserva.getIdCliente(), 
            newStatus
        );

        if (success) {
            // 2. Actualizar el Modelo en JavaFX (la tabla se refresca automáticamente)
            selectedReserva.setStatus(newStatus);
            
            // 3. Actualizar la UI
            updateButtonState(selectedReserva); 
            showAlert("Éxito", "El estado de la reserva ha sido cambiado a: " + newStatus.toUpperCase(), Alert.AlertType.INFORMATION);
        } else {
            showAlert("Error", "No se pudo actualizar el estado de la reserva en la base de datos.", Alert.AlertType.ERROR);
        }
    }
    
    /**
     * Actualiza el texto y el estado de habilitación del botón.
     */
    private void updateButtonState(Reservas selected) {
        if (selected != null) {
            toggleStatusButton.setDisable(false);
            if (selected.isConfirmado()) {
                toggleStatusButton.setText("Cambiar a CANCELADO");
            } else {
                toggleStatusButton.setText("Cambiar a CONFIRMADO (Activar)");
            }
        } else {
            toggleStatusButton.setText("Seleccionar Reserva");
            toggleStatusButton.setDisable(true);
        }
    }
    
    /**
     * Método auxiliar para mostrar alertas de información/error.
     */
    private void showAlert(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    /**
     * Método auxiliar para confirmar una acción.
     */
     private boolean confirmAction(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        return alert.showAndWait().filter(response -> response == ButtonType.OK).isPresent();
    }
}