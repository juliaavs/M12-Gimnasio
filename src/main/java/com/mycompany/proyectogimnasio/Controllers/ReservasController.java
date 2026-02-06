
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
import javafx.scene.control.TableCell;
import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;

public class ReservasController implements Initializable {
    
    // Elementos del FXML
   
    @FXML private TableView<Reservas> reservasTable;
    
    // Columnas que muestran los IDs (visibilidad opcional)
    @FXML private TableColumn<Reservas, Number> idClaseColumn;
    @FXML private TableColumn<Reservas, Number> idClienteColumn;
    
    // Columnas de datos visibles
    @FXML private TableColumn<Reservas, String> nombreClaseColumn; // Nueva
    @FXML private TableColumn<Reservas, String> dniClienteColumn; // Nueva
    @FXML private TableColumn<Reservas, String> statusColumn;
    @FXML private TableColumn<Reservas, LocalDate> diaReservaColumn;
    
    
    @FXML private TextField txtFiltro;
    
    @FXML private Button toggleStatusButton;
    private Reservas selectedReserva;

    private final ReservasService reservasService = new ReservasService();
    private ObservableList<Reservas> reservasList;

  
       
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // 1. Configuración de CellValueFactory para las columnas
        nombreClaseColumn.setCellValueFactory(new PropertyValueFactory<>("nombreClase"));
        dniClienteColumn.setCellValueFactory(new PropertyValueFactory<>("dniCliente"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
        statusColumn.setCellFactory(column -> {
        return new TableCell<Reservas, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);

                // Si la celda está vacía o el item es nulo, no mostrar nada
                if (empty || item == null) {
                    setText(null);
                    setStyle(null);
                } else {
                    setText(item.toUpperCase()); // Mostrar el texto del estado

                    // Lógica para aplicar el color
                    if ("confirmado".equalsIgnoreCase(item)) {
                        // Estado Confirmado: Texto Verde
                        setStyle("-fx-text-fill: #2ecc71; -fx-font-weight: bold;"); // #2ecc71 es Verde Esmeralda
                    } else if ("cancelado".equalsIgnoreCase(item)) {
                        // Estado Cancelado: Texto Rojo
                        setStyle("-fx-text-fill: #e74c3c; -fx-font-weight: bold;"); // #e74c3c es Rojo
                    } else if ("usado".equalsIgnoreCase(item)) {
                        // Estado Cancelado: Texto Rojo
                        setStyle("-fx-text-fill: #0A80F7; -fx-font-weight: bold;"); // #e74c3c es Rojo
                    } else {
                        // Otros estados (por defecto)
                        setStyle(null); 
                    }
                }
            }
        };
    });
        diaReservaColumn.setCellValueFactory(new PropertyValueFactory<>("diaReserva"));
        idClaseColumn.setCellValueFactory(new PropertyValueFactory<>("idClase"));
        idClienteColumn.setCellValueFactory(new PropertyValueFactory<>("idCliente"));

        // 2. Cargar datos
        loadReservas(); // Carga la ObservableList<Reservas>
    
        // -----------------------------------------------------------
        // 3. LÓGICA DE FILTRADO Y BÚSQUEDA CON FilteredList
        // -----------------------------------------------------------
    
        // Crear una FilteredList a partir de la ObservableList original
        FilteredList<Reservas> filteredData = new FilteredList<>(reservasList, p -> true);

        // Agregar un Listener al campo de texto de filtro
        txtFiltro.textProperty().addListener((observable, oldValue, newValue) -> {
        filteredData.setPredicate(reserva -> {
            // Si el campo de filtro está vacío, mostrar todas las reservas.
            if (newValue == null || newValue.isEmpty()) {
                return true;
            }

            // Comparar la cadena de filtro (en minúsculas) con campos relevantes
            String lowerCaseFilter = newValue.toLowerCase();

            if (reserva.getDniCliente().toLowerCase().contains(lowerCaseFilter)) {
                return true; // Coincidencia por Nombre de Cliente
            } else if (reserva.getNombreClase().toLowerCase().contains(lowerCaseFilter)) {
                return true; // Coincidencia por Nombre de Clase
            } else if (reserva.getStatus().toLowerCase().contains(lowerCaseFilter)) {
                return true; // Coincidencia por Estado (Status)
            }
            return false; // No hay coincidencias
        });
    });

        // Envolver la FilteredList en una SortedList
        SortedList<Reservas> sortedData = new SortedList<>(filteredData);

        // Vincular el comparador de SortedList con el comparador de la TableView
         sortedData.comparatorProperty().bind(reservasTable.comparatorProperty());

        // Establecer la SortedList como la fuente de datos de la tabla
        reservasTable.setItems(sortedData);
    
        // -----------------------------------------------------------
        // 4. LÓGICA DE SELECCIÓN Y BOTÓN DE ESTADO
        // -----------------------------------------------------------

        // Inicialmente ocultar el botón de estado
        toggleStatusButton.setVisible(false);
        toggleStatusButton.setManaged(false); // No ocupa espacio en el layout

        // Agregar Listener para actualizar el botón cada vez que cambia la selección
        reservasTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            selectedReserva = newSelection;
            updateToggleButton(); // Llama al método de actualización de estilo/visibilidad
    });
}
    
    /**
     * Maneja el clic en el botón para cambiar el estado de la reserva.
     */
    
     private void loadReservas() {
        reservasList = FXCollections.observableArrayList(reservasService.getAll());
        reservasTable.setItems(reservasList);
    }

    @FXML
    private void handleToggleStatus() {
    Reservas selectedReserva = reservasTable.getSelectionModel().getSelectedItem(); // Correcto
    
    if (selectedReserva == null) {
        showAlert("Advertencia", "Selecciona una reserva para modificar su estado.", Alert.AlertType.WARNING);
        return;
    }
    
    // Usamos el status actual del OBJETO para determinar el status y acción FUTUROS
    // Asumimos: Si el status NO es "cancelado", la próxima acción es "cancelar"
    boolean isCurrentlyConfirmed = "confirmado".equalsIgnoreCase(selectedReserva.getStatus());
    
    String newStatus = isCurrentlyConfirmed ? "cancelado" : "confirmado";
    String action = isCurrentlyConfirmed ? "CANCELAR" : "CONFIRMAR";
    
    if (!confirmAction("Confirmación de Cambio", 
                       "¿Deseas realmente " + action + " la reserva de " + selectedReserva.getDniCliente() + "?")) {
        return;
    }
    
    // 1. Actualizar en la Base de Datos (Service)
    boolean success = reservasService.updateStatus(
        selectedReserva.getIdClase(), 
        selectedReserva.getIdCliente(), 
        newStatus
    );

    if (success) {
        // 2. Actualizar el Modelo en JavaFX
        selectedReserva.setStatus(newStatus);
        
        // Forzar a la tabla a refrescar la fila (necesario cuando se modifica el objeto sin reemplazarlo)
        reservasTable.refresh(); 
        
        // 3. Actualizar la UI del botón (llama al método con la nueva lógica)
        updateToggleButton(); 
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
                toggleStatusButton.setText("Cancelar Reserva");
            } else {
                toggleStatusButton.setText("Activar Reserva");
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
    
    
    
    private boolean confirmAction(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        return alert.showAndWait().filter(response -> response == ButtonType.OK).isPresent();
    }
     
    private void updateToggleButton() {
    // Caso 1: No hay selección
    if (selectedReserva == null) {
        toggleStatusButton.setVisible(false);
        toggleStatusButton.setManaged(false);
        return; 
    }
    
    // Caso 2: Hay selección, mostrar el botón y aplicar estilo
    toggleStatusButton.setVisible(true);
    toggleStatusButton.setManaged(true); 

    String status = selectedReserva.getStatus();
    
    // Asumimos que "confirmado" significa que está activa y se puede cancelar.
    if ("confirmado".equalsIgnoreCase(status)) {
        
        toggleStatusButton.setText("Cancelar Reserva");
        
        // 🚨 Estilo Rojo para CANCELAR
        toggleStatusButton.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-font-weight: bold;");
        
    } 
    // Asumimos que "cancelado" significa que está inactiva y se puede reactivar/confirmar.
    else if ("cancelado".equalsIgnoreCase(status)) {
        
        toggleStatusButton.setText("Activar Reserva");
        
        // ✅ Estilo Verde y Texto Blanco para ACTIVAR
        toggleStatusButton.setStyle("-fx-background-color: #2ecc71; -fx-text-fill: white; -fx-font-weight: bold;");
        
    } else {
        // Ocultar si hay otros estados (e.g., "finalizada")
        toggleStatusButton.setVisible(false);
        toggleStatusButton.setManaged(false);
    }
    }
    
    @FXML
    private void handleClearSelection() {
        // 1. Limpiar la selección de la tabla
        reservasTable.getSelectionModel().clearSelection();
    
        // 2. Resetear la referencia local y el botón de estado
        // Ya que el listener de la tabla se activa al llamar a clearSelection(), 
        // y pone selectedReserva = null, updateToggleButton() debería ocultarlo automáticamente.
        // Pero si quieres ser explícito:
        selectedReserva = null;
        updateToggleButton(); 
}
}
