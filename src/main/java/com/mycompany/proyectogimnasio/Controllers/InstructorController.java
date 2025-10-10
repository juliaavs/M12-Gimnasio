
package com.mycompany.proyectogimnasio.Controllers;

import com.mycompany.proyectogimnasio.Models.Instructor;
import com.mycompany.proyectogimnasio.Service.InstructorService;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.beans.property.SimpleStringProperty;
import java.util.*;
import java.net.URL;
import javafx.fxml.Initializable;



public class InstructorController {

    @FXML private TableView<Instructor> tablaInstructores;
    @FXML private TableColumn<Instructor, Integer> colId;
    @FXML private TableColumn<Instructor, String> colNombre;
    @FXML private TableColumn<Instructor, String> colApellido;
    @FXML private TableColumn<Instructor, String> colDni;
    @FXML private TableColumn<Instructor, Boolean> colActivo;
    @FXML private TableColumn<Instructor, String> colClase;
    

    @FXML private TextField txtId;
    @FXML private TextField txtNombre;
    @FXML private TextField txtApellido;
    @FXML private TextField txtDni;
    @FXML private CheckBox chkActivo;
    
    @FXML
    private Label welcomeLabel;

    @FXML
    private Label roleLabel;
    
    public void setUser(String nombre, String rol) {
        welcomeLabel.setText("Bienvenido, " + nombre + "!");
        roleLabel.setText("Rol: " + rol);
    }

    private InstructorService service = new InstructorService();
    private Instructor selectedInstructor;

    @FXML
    public void initialize(URL url, ResourceBundle rb) {
    // Configuración de columnas
        colId.setCellValueFactory(c -> new javafx.beans.property.SimpleIntegerProperty(c.getValue().getIdInstructor()).asObject());
        colNombre.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getNombre()));
        colApellido.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getApellido()));
        colDni.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getDni()));
        colActivo.setCellValueFactory(c -> new javafx.beans.property.SimpleBooleanProperty(c.getValue().isActivo()).asObject());
        colClase.setCellValueFactory(cellData -> {
            Instructor instructor = cellData.getValue();
            List<String> clases = service.getClasesPorInstructorId(instructor.getIdInstructor());
            String clasesTexto = String.join(", ", clases); // si está vacía, muestra ""
                if (clasesTexto.isEmpty()) clasesTexto = "—";
                return new SimpleStringProperty(clasesTexto);
    });

    // Cargar datos
    cargarTabla();

    // Selección de fila
    tablaInstructores.setOnMouseClicked(this::seleccionarInstructor);
}

    public void initializeInstructores() {
        colId.setCellValueFactory(c -> new javafx.beans.property.SimpleIntegerProperty(c.getValue().getIdInstructor()).asObject());
        colNombre.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getNombre())); 
        colApellido.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getApellido())); 
        colDni.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getDni())); 
        colActivo.setCellValueFactory(c -> new javafx.beans.property.SimpleBooleanProperty(c.getValue().isActivo()).asObject());
        colClase.setCellValueFactory(cellData -> {
            Instructor instructor = cellData.getValue();
            List<String> clases = service.getClasesPorInstructorId(instructor.getIdInstructor());
            String clasesTexto = String.join(", ", clases); // si está vacía, muestra ""
            if (clasesTexto.isEmpty()) clasesTexto = "—";
                return new SimpleStringProperty(clasesTexto);
        });
        cargarTabla(); 
        tablaInstructores.setOnMouseClicked(this::seleccionarInstructor); 
    }
    
    @FXML

    private void cargarTabla() {
    ObservableList<Instructor> lista = service.getAll();
    tablaInstructores.setItems(lista);
    tablaInstructores.refresh(); // fuerza actualización de columnas calculadas
}


    @FXML
    private void agregarInstructor() {
        Instructor inst = new Instructor(
                Integer.parseInt(txtId.getText()),
                txtNombre.getText(),
                txtApellido.getText(),
                txtDni.getText(),
                chkActivo.isSelected()
        );
        service.add(inst);
        cargarTabla();
        limpiarCampos();
    }

    @FXML
    private void actualizarInstructor() {
        if (selectedInstructor != null) {
            selectedInstructor.setNombre(txtNombre.getText());
            selectedInstructor.setApellido(txtApellido.getText());
            selectedInstructor.setDni(txtDni.getText());
            selectedInstructor.setActivo(chkActivo.isSelected());

            service.update(selectedInstructor);
            cargarTabla();
            limpiarCampos();
        }
    }

    @FXML
    private void eliminarInstructor() {
        if (selectedInstructor != null) {
            service.delete(selectedInstructor.getIdInstructor());
            cargarTabla();
            limpiarCampos();
        }
    }

    private void seleccionarInstructor(MouseEvent event) {
        selectedInstructor = tablaInstructores.getSelectionModel().getSelectedItem();
        if (selectedInstructor != null) {
            txtId.setText(String.valueOf(selectedInstructor.getIdInstructor()));
            txtNombre.setText(selectedInstructor.getNombre());
            txtApellido.setText(selectedInstructor.getApellido());
            txtDni.setText(selectedInstructor.getDni());
            chkActivo.setSelected(selectedInstructor.isActivo());
        }
    }

    private void limpiarCampos() {
        txtId.clear();
        txtNombre.clear();
        txtApellido.clear();
        txtDni.clear();
        chkActivo.setSelected(false);
        selectedInstructor = null;
    }
    
    
}
