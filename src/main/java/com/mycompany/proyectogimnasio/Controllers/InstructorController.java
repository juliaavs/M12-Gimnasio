
package com.mycompany.proyectogimnasio.Controllers;


import com.mycompany.proyectogimnasio.Models.Instructor;
import com.mycompany.proyectogimnasio.Service.InstructorService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import java.sql.SQLException;
import javafx.beans.property.SimpleStringProperty; // ¡Esta es la que necesitas!


public class InstructorController {

    @FXML private TableView<Instructor> instructorTable;
    @FXML private TableColumn<Instructor, Integer> idColumn;
    @FXML private TableColumn<Instructor, String> nombreColumn;
    @FXML private TableColumn<Instructor, String> apellidoColumn;
    @FXML private TableColumn<Instructor, String> dniColumn;
    @FXML private TableColumn<Instructor, Boolean> activoColumn;
    // Columna especial para mostrar las clases concatenadas
    @FXML private TableColumn<Instructor, String> clasesColumn; 
    
    @FXML private TextField nombreField;
    @FXML private TextField apellidoField;
    @FXML private TextField dniField;
    @FXML private CheckBox activoCheckbox;
    @FXML private Button saveButton;
    @FXML private Button deleteButton;

    private InstructorService instructorService;
    private ObservableList<Instructor> instructorList;
    private Instructor selectedInstructor; // Para operaciones de actualización

    @FXML
    public void initialize() {
        instructorService = new InstructorService();
        instructorList = FXCollections.observableArrayList();
        
        // --- 1. Mapeo de columnas de la tabla ---
        idColumn.setCellValueFactory(cellData -> cellData.getValue().idInstructorProperty().asObject());
        nombreColumn.setCellValueFactory(cellData -> cellData.getValue().nombreProperty());
        apellidoColumn.setCellValueFactory(cellData -> cellData.getValue().apellidoProperty());
        dniColumn.setCellValueFactory(cellData -> cellData.getValue().dniProperty());
        activoColumn.setCellValueFactory(cellData -> cellData.getValue().activoProperty().asObject());
        
        // Mapeo para la columna de clases (usa el método auxiliar del modelo)
        clasesColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getClasesConcatenadas()));
        
        instructorTable.setItems(instructorList);
        
        // --- 2. Listener para selección de tabla (para Update y Delete) ---
        instructorTable.getSelectionModel().selectedItemProperty().addListener(
            (observable, oldValue, newValue) -> showInstructorDetails(newValue));

        loadInstructors();
    }

    // Cargar datos al iniciar el controlador
    private void loadInstructors() {
        try {
            instructorList.clear();
            instructorList.addAll(instructorService.getAllInstructors());
        } catch (SQLException e) {
            showAlert("Error de BD", "No se pudieron cargar los instructores: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }
    
    // Mostrar detalles del instructor seleccionado en los campos de texto
    private void showInstructorDetails(Instructor instructor) {
        selectedInstructor = instructor;
        if (instructor != null) {
            nombreField.setText(instructor.getNombre());
            apellidoField.setText(instructor.getApellido());
            dniField.setText(instructor.getDni());
            activoCheckbox.setSelected(instructor.isActivo());
            saveButton.setText("Actualizar");
            deleteButton.setDisable(false);
        } else {
            clearFields();
            saveButton.setText("Guardar");
            deleteButton.setDisable(true);
        }
    }

    // --- Operaciones CRUD ---

    @FXML
    private void handleSaveInstructor() {
        // Validación básica
        if (nombreField.getText().isEmpty() || dniField.getText().isEmpty()) {
            showAlert("Error de Validación", "El nombre y el DNI son obligatorios.", Alert.AlertType.WARNING);
            return;
        }

        try {
            if (selectedInstructor != null) {
                // UPDATE (Actualizar)
                selectedInstructor.setNombre(nombreField.getText());
                selectedInstructor.setApellido(apellidoField.getText());
                selectedInstructor.setDni(dniField.getText());
                selectedInstructor.setActivo(activoCheckbox.isSelected());
                instructorService.updateInstructor(selectedInstructor);
                showAlert("Éxito", "Instructor actualizado.", Alert.AlertType.INFORMATION);
            } else {
                // CREATE (Crear)
                Instructor newInstructor = new Instructor(
                    0, 
                    nombreField.getText(), 
                    apellidoField.getText(), 
                    dniField.getText(), 
                    activoCheckbox.isSelected()
                );
                instructorService.addInstructor(newInstructor);
                showAlert("Éxito", "Instructor guardado.", Alert.AlertType.INFORMATION);
            }
            clearFields();
            loadInstructors(); // Recargar la tabla para mostrar cambios
        } catch (SQLException e) {
            showAlert("Error de BD", "Error al guardar/actualizar el instructor: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void handleDeleteInstructor() {
        if (selectedInstructor != null) {
            try {
                instructorService.deleteInstructor(selectedInstructor.getIdInstructor());
                showAlert("Éxito", "Instructor eliminado.", Alert.AlertType.INFORMATION);
                clearFields();
                loadInstructors();
            } catch (SQLException e) {
                 showAlert("Error de BD", "Error al eliminar el instructor. Asegúrate de que no tenga clases asociadas. " + e.getMessage(), Alert.AlertType.ERROR);
            }
        }
    }
    
    @FXML
    private void handleNewInstructor() {
        showInstructorDetails(null); // Limpia campos y resetea la lógica a "Guardar"
    }
    
    private void clearFields() {
        selectedInstructor = null;
        nombreField.setText("");
        apellidoField.setText("");
        dniField.setText("");
        activoCheckbox.setSelected(false);
        saveButton.setText("Guardar");
        deleteButton.setDisable(true);
        instructorTable.getSelectionModel().clearSelection();
    }
    
    private void showAlert(String title, String content, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}