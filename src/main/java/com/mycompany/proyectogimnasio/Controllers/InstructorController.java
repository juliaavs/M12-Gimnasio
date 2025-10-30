
package com.mycompany.proyectogimnasio.Controllers;

import com.mycompany.proyectogimnasio.Models.Instructor;
import com.mycompany.proyectogimnasio.Service.InstructorService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import java.sql.SQLException;
import javafx.beans.property.SimpleStringProperty;


public class InstructorController {

    @FXML private TableView<Instructor> instructorTable;
    @FXML private TableColumn<Instructor, Integer> idColumn;
    @FXML private TableColumn<Instructor, String> nombreColumn;
    @FXML private TableColumn<Instructor, String> apellidoColumn;
    @FXML private TableColumn<Instructor, String> dniColumn;
    @FXML private TableColumn<Instructor, Boolean> activoColumn;
    @FXML private TableColumn<Instructor, String> clasesColumn;

    @FXML private TextField nombreField;
    @FXML private TextField apellidoField;
    @FXML private TextField dniField;
    @FXML private Button saveButton;
    @FXML private Button deleteButton;
    @FXML private Button btnCambiarEstado;
    
    // **NOTA: SE HA ELIMINADO @FXML private CheckBox activoCheckbox;**
    

    private InstructorService instructorService;
    private ObservableList<Instructor> instructorList;
    private Instructor selectedInstructor;

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
        
        // Mapeo para la columna de clases
        clasesColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getClasesConcatenadas()));
        
        instructorTable.setItems(instructorList);
        
        // --- 2. Listener para selección de tabla ---
        instructorTable.getSelectionModel().selectedItemProperty().addListener(
            (observable, oldValue, newValue) -> showInstructorDetails(newValue));

        loadInstructors();
    }

    // ... (Métodos loadInstructors y showAlert se mantienen igual) ...
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
             // **NOTA: SE HA ELIMINADO la línea del checkbox aquí.**
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
    // 1. **Paso de Validación de Campos (Formato DNI y Obligatorios)**
    if (!validarCampos()) { 
        return; 
    }
    
    String dni = dniField.getText();
    
    // Identificamos el ID actual. Será null si estamos creando. 
    // Si estamos actualizando, será el ID del selectedInstructor.
    Integer currentId = (selectedInstructor != null) ? selectedInstructor.getIdInstructor() : null;

    try {
        // =======================================================
        // 2. NUEVO PASO: Verificación de DNI Duplicado en BD
        // Esta verificación aplica tanto a CREATE (currentId es null) como a UPDATE (currentId no es null)
        // =======================================================
        if (instructorService.existsDni(dni, currentId)) {
            // El servicio verifica DNI = ? y (si currentId no es null) ID != ?
            showAlert("Error de Validación", 
                      "El DNI/NIE " + dni + " ya está registrado y no puede usarse.", 
                      Alert.AlertType.ERROR);
            return; // Detiene el proceso si el DNI está duplicado
        }
        
        
        // =======================================================
        // 3. Ejecución de la Operación (Si el DNI es único)
        // =======================================================
        if (selectedInstructor != null) {
            // UPDATE (Actualizar Instructor Existente)
            selectedInstructor.setNombre(nombreField.getText());
            selectedInstructor.setApellido(apellidoField.getText());
            selectedInstructor.setDni(dni); // Guardamos el nuevo DNI, ya verificado
            
            instructorService.updateInstructor(selectedInstructor);
            showAlert("Éxito", "Instructor actualizado correctamente.", Alert.AlertType.INFORMATION);
            
        } else {
            // CREATE (Crear Nuevo Instructor)
            Instructor newInstructor = new Instructor(
                0,
                nombreField.getText(),
                apellidoField.getText(),
                dni,
                true // Activo por defecto
            );
            
            instructorService.addInstructor(newInstructor);
            showAlert("Éxito", "Instructor guardado y activado correctamente.", Alert.AlertType.INFORMATION);
        }
        
        // 4. Limpieza y Actualización de la Vista
        clearFields();
        loadInstructors();
        
    } catch (SQLException e) {
        // Manejo de errores de la base de datos
        showAlert("Error de Base de Datos", "Error al guardar/actualizar el instructor: " + e.getMessage(), Alert.AlertType.ERROR);
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
    private void handleClearInstructor() {
        showInstructorDetails(null); // Limpia campos y resetea la lógica a "Guardar"
    }
    
    @FXML
    private void handleCambiarEstado() {
    Instructor selectedInstructor = instructorTable.getSelectionModel().getSelectedItem();
    
    if (selectedInstructor != null) {
        
        // El nuevo estado es el opuesto al estado actual
        boolean nuevoEstado = !selectedInstructor.isActivo(); 
        String accion = nuevoEstado ? "activado" : "desactivado";
        
        try {
            // 1. Llamar al servicio para actualizar la BD
            boolean success = instructorService.cambiarEstadoActivo(selectedInstructor.getIdInstructor(), nuevoEstado);
            
            if (success) {
                // 2. Actualizar el modelo en memoria (importante para que se refleje en la tabla)
                selectedInstructor.setActivo(nuevoEstado);
                instructorTable.refresh(); // Refrescar la vista de la tabla
                
                // Llamada a showAlert
                showAlert("Éxito", "El instructor ha sido " + accion + " correctamente.", Alert.AlertType.INFORMATION);
            } else {
                // Llamada a showAlert
                showAlert("Fallo", "No se pudo " + accion + " el instructor. Inténtelo de nuevo.", Alert.AlertType.WARNING);
            }
            
        } catch (SQLException e) {
            // Llamada a showAlert
            showAlert("Error de BD", "Error al comunicarse con la base de datos: " + e.getMessage(), Alert.AlertType.ERROR);
        }
        
    } else {
        showAlert("Sin Selección", "Por favor, selecciona un instructor de la tabla.", Alert.AlertType.WARNING);
    }
}
    
    private String getDniValidationError(String dni) {
        if (dni == null || dni.trim().isEmpty()) {
            return "El DNI es obligatorio.";
        }
    
        // Limpiamos y estandarizamos el DNI (quitamos espacios y a mayúsculas)
        String dniLimpio = dni.trim().toUpperCase();

        // El DNI debe tener estrictamente 9 caracteres (8 números + 1 letra)
        if (dniLimpio.length() != 9) {
            return "El DNI debe tener exactamente 9 caracteres (ej: 12345678A).";
        }

        String parteNumerica = dniLimpio.substring(0, 8);
        char letraRecibida = dniLimpio.charAt(8);

        // 1. **Validación DNI Estricta**: Los primeros 8 caracteres deben ser solo dígitos.
        if (!parteNumerica.matches("\\d{8}")) {
            return "Los primeros 8 caracteres del DNI deben ser números.";
        }

        // 2. Validación de la letra
        if (!Character.isLetter(letraRecibida)) {
            return "El último carácter del DNI debe ser una letra.";
        }

        try {
            int numeroDni = Integer.parseInt(parteNumerica);
            String letras = "TRWAGMYFPDXBNJZSQVHLCKE";
        
            // Algoritmo: Módulo 23
            char letraCalculada = letras.charAt(numeroDni % 23);

            if (letraRecibida != letraCalculada) {
                // Error si la letra no coincide con el cálculo
                return "La letra '" + letraRecibida + "' no es correcta para ese número. La letra válida es '" + letraCalculada + "'.";
            }
        } catch (NumberFormatException e) {
            // Este catch es una salvaguarda, pues el regex ya verifica la numeración.
            return "Error interno al procesar el número de DNI."; 
        }
    
    // Si llegamos aquí, el DNI es válido
    return null; 
}

    /**
    * Método principal de validación de campos, que ahora usa la lógica de DNI/NIE.
    */
    private boolean validarCampos() {
    String mensajeError = "";
    
    
    String errorDni = getDniValidationError(dniField.getText()); // **Usando dniField**
    
    if (errorDni != null) {
        mensajeError += errorDni + "\n";
    }

    if (nombreField.getText() == null || nombreField.getText().trim().isEmpty()) mensajeError += "El Nombre es obligatorio.\n";
    if (apellidoField.getText() == null || apellidoField.getText().trim().isEmpty()) mensajeError += "El Apellido es obligatorio.\n";
   
    if (mensajeError.isEmpty()) {
        return true;
    } else {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Campos Inválidos");
        alert.setHeaderText("Por favor, corrige los siguientes errores:");
        alert.setContentText(mensajeError);
        alert.showAndWait();
        return false;
    }
}
    
    
    
    private void clearFields() {
        selectedInstructor = null;
        nombreField.setText("");
        apellidoField.setText("");
        dniField.setText("");
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