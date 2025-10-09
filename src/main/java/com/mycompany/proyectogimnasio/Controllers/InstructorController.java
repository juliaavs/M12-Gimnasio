
package com.mycompany.proyectogimnasio.Controllers;

import com.mycompany.proyectogimnasio.Models.Instructor;
import com.mycompany.proyectogimnasio.Service.InstructorService;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;


public class InstructorController {

    @FXML private TableView<Instructor> tablaInstructores;
    @FXML private TableColumn<Instructor, Integer> colId;
    @FXML private TableColumn<Instructor, String> colNombre;
    @FXML private TableColumn<Instructor, String> colApellido;
    @FXML private TableColumn<Instructor, String> colDni;
    @FXML private TableColumn<Instructor, Boolean> colActivo;

    @FXML private TextField txtId;
    @FXML private TextField txtNombre;
    @FXML private TextField txtApellido;
    @FXML private TextField txtDni;
    @FXML private CheckBox chkActivo;
    
    

    private InstructorService service = new InstructorService();
    private Instructor selectedInstructor;

    @FXML
    

    public void initializeInstructores() {
        colId.setCellValueFactory(c -> new javafx.beans.property.SimpleIntegerProperty(c.getValue().getIdInstructor()).asObject());
        colNombre.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getNombre()));
        colApellido.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getApellido()));
        colDni.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getDni()));
        colActivo.setCellValueFactory(c -> new javafx.beans.property.SimpleBooleanProperty(c.getValue().isActivo()).asObject());

        cargarTabla();

        tablaInstructores.setOnMouseClicked(this::seleccionarInstructor);
    }

    private void cargarTabla() {
        ObservableList<Instructor> lista = service.getAll();
        System.out.println("Número de instructores: " + lista.size());
        tablaInstructores.setItems(lista);
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
