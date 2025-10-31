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
    @FXML private Button btnCambiarEstado;
    
  
    

    private InstructorService instructorService;
    private ObservableList<Instructor> instructorList;
    private Instructor selectedInstructor;

    @FXML
    public void initialize() {
        instructorService = new InstructorService();
        instructorList = FXCollections.observableArrayList();
        
        
        idColumn.setCellValueFactory(cellData -> cellData.getValue().idInstructorProperty().asObject());
        nombreColumn.setCellValueFactory(cellData -> cellData.getValue().nombreProperty());
        apellidoColumn.setCellValueFactory(cellData -> cellData.getValue().apellidoProperty());
        dniColumn.setCellValueFactory(cellData -> cellData.getValue().dniProperty());
        activoColumn.setCellValueFactory(cellData -> cellData.getValue().activoProperty().asObject());
        
       
        clasesColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getClasesConcatenadas()));
        
        instructorTable.setItems(instructorList);
        
        
        instructorTable.getSelectionModel().selectedItemProperty().addListener(
            (observable, oldValue, newValue) -> showInstructorDetails(newValue));

        loadInstructors();
    }

    
    private void loadInstructors() {
         try {
             instructorList.clear();
             instructorList.addAll(instructorService.getAllInstructors());
         } catch (SQLException e) {
             showAlert("Error de BD", "No se pudieron cargar los instructores: " + e.getMessage(), Alert.AlertType.ERROR);
         }
     }
    
   
     private void showInstructorDetails(Instructor instructor) {
         selectedInstructor = instructor;
         if (instructor != null) {
             nombreField.setText(instructor.getNombre());
             apellidoField.setText(instructor.getApellido());
             dniField.setText(instructor.getDni());
          
             saveButton.setText("Actualizar");
             
         } else {
             clearFields();
             saveButton.setText("Guardar");
             
         }
     }

    
     
    @FXML
   
private void handleSaveInstructor() {
    
    if (!validarCampos()) { 
        return; 
    }
    
    String dni = dniField.getText();
    
    
    Integer currentId = (selectedInstructor != null) ? selectedInstructor.getIdInstructor() : null;

    try {
       
        if (instructorService.existsDni(dni, currentId)) {
           
            showAlert("Error de Validación", 
                      "El DNI/NIE " + dni + " ya está registrado y no puede usarse.", 
                      Alert.AlertType.ERROR);
            return; 
        }
        
        
        if (selectedInstructor != null) {
           
            selectedInstructor.setNombre(nombreField.getText());
            selectedInstructor.setApellido(apellidoField.getText());
            selectedInstructor.setDni(dni); 
            
            instructorService.updateInstructor(selectedInstructor);
            showAlert("Éxito", "Instructor actualizado correctamente.", Alert.AlertType.INFORMATION);
           
            
        } else {
            
            Instructor newInstructor = new Instructor(
                0,
                nombreField.getText(),
                apellidoField.getText(),
                dni,
                true 
            );
            
            instructorService.addInstructor(newInstructor);
            showAlert("Éxito", "Instructor guardado y activado correctamente.", Alert.AlertType.INFORMATION);
           
        }
        
        clearFields();
        loadInstructors();
      
        
    } catch (SQLException e) {
        
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
        showInstructorDetails(null); 
    }
    
    @FXML
    private void handleCambiarEstado() {
    Instructor selectedInstructor = instructorTable.getSelectionModel().getSelectedItem();
    
    if (selectedInstructor != null) {
        
       
        boolean nuevoEstado = !selectedInstructor.isActivo(); 
        String accion = nuevoEstado ? "activado" : "desactivado";
        
        try {
            
            boolean success = instructorService.cambiarEstadoActivo(selectedInstructor.getIdInstructor(), nuevoEstado);
            
            if (success) {
                
                selectedInstructor.setActivo(nuevoEstado);
                instructorTable.refresh(); 
                
               
                showAlert("Éxito", "El instructor ha sido " + accion + " correctamente.", Alert.AlertType.INFORMATION);
            } else {
                
                showAlert("Fallo", "No se pudo " + accion + " el instructor. Inténtelo de nuevo.", Alert.AlertType.WARNING);
            }
            
        } catch (SQLException e) {
           
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
    
        
        String dniLimpio = dni.trim().toUpperCase();

       
        if (dniLimpio.length() != 9) {
            return "El DNI debe tener exactamente 9 caracteres (ej: 12345678A).";
        }

        String parteNumerica = dniLimpio.substring(0, 8);
        char letraRecibida = dniLimpio.charAt(8);

        
        if (!parteNumerica.matches("\\d{8}")) {
            return "Los primeros 8 caracteres del DNI deben ser números.";
        }

        
        if (!Character.isLetter(letraRecibida)) {
            return "El último carácter del DNI debe ser una letra.";
        }

        try {
            int numeroDni = Integer.parseInt(parteNumerica);
            String letras = "TRWAGMYFPDXBNJZSQVHLCKE";
        
            
            char letraCalculada = letras.charAt(numeroDni % 23);

            if (letraRecibida != letraCalculada) {
                return "La letra '" + letraRecibida + "' no es correcta para ese número. La letra válida es '" + letraCalculada + "'.";
            }
        } catch (NumberFormatException e) {
            
            return "Error interno al procesar el número de DNI."; 
        }
    
    
    return null; 
}

    
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