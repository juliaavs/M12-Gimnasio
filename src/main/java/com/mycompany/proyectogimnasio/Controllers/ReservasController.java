package com.mycompany.proyectogimnasio.Controllers;

import com.mycompany.proyectogimnasio.Models.Reservas;
import com.mycompany.proyectogimnasio.Service.ReservasService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import java.sql.SQLException;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.layout.HBox;
import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;

public class ReservasController {
    
    @FXML private TableView<Reservas> reservasTable;
    @FXML private TableColumn<Reservas, Number> idClaseColumn;
    @FXML private TableColumn<Reservas, Number> idClienteColumn;
    @FXML private TableColumn<Reservas, String> statusColumn;
    @FXML private TableColumn<Reservas, LocalDate> diaReservaColumn;
    // ... otras columnas

    @FXML private Button toggleStatusButton;

    private ReservasService reservasService = new ReservasService(); // Tu DAO/Service
    private ObservableList<Reservas> reservasList;

    
    public void initialize(URL url, ResourceBundle rb) {
        // 1. **Mapeo de Columnas (Binding)**: Conecta las columnas del TableView con las Properties del modelo.
        idClaseColumn.setCellValueFactory(cellData -> cellData.getValue().idClaseProperty());
        idClienteColumn.setCellValueFactory(cellData -> cellData.getValue().idClienteProperty());
        statusColumn.setCellValueFactory(cellData -> cellData.getValue().statusProperty());
        diaReservaColumn.setCellValueFactory(cellData -> cellData.getValue().diaReservaProperty());

        // Opcional: Renderizar el estado en mayúsculas
        statusColumn.setCellFactory(column -> new javafx.scene.control.TableCell<Reservas, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.toUpperCase());
                }
            }
        });
        
        // 2. **Cargar Datos**: Obtener los datos del DAO y cargarlos en la lista observable.
        reservasList = FXCollections.observableArrayList(reservasService.getAll());
        reservasTable.setItems(reservasList);
        
        // 3. **Listener de Selección**: Habilita el botón y actualiza su texto al seleccionar una fila.
        toggleStatusButton.setDisable(true); // Deshabilitar por defecto
        
        reservasTable.getSelectionModel().selectedItemProperty().addListener(
            (observable, oldValue, newValue) -> updateButtonText(newValue));
    }

    private void updateButtonText(Reservas selected) {
        if (selected != null) {
            if (selected.isConfirmado()) {
                toggleStatusButton.setText("Cambiar a CANCELADO");
            } else {
                toggleStatusButton.setText("Cambiar a CONFIRMADO");
            }
            toggleStatusButton.setDisable(false);
        } else {
            toggleStatusButton.setText("Seleccionar Reserva");
            toggleStatusButton.setDisable(true);
        }
    }
    
    @FXML
    private void handleToggleStatus() {
        Reservas selectedInscripcion = reservasTable.getSelectionModel().getSelectedItem();
        if (selectedInscripcion == null) return;
        
        // Determinar el nuevo estado
        String newStatus = selectedInscripcion.isConfirmado() ? "cancelado" : "confirmado";
        
        // 1. Actualizar en la base de datos
        boolean success = reservasService.updateStatus(
            selectedInscripcion.getIdClase(), 
            selectedInscripcion.getIdCliente(), 
            newStatus
        );

        if (success) {
            // 2. Si la DB se actualizó, actualizamos el modelo en memoria.
            // Gracias a las Properties de JavaFX, la tabla se actualiza sola.
            selectedInscripcion.setStatus(newStatus);
            
            // 3. Actualizar el texto del botón (opcional, se puede dejar que el Listener lo haga)
            updateButtonText(selectedInscripcion); 
        } else {
            // Manejar error
        }
    }
}