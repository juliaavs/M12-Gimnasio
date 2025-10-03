
package com.mycompany.proyectogimnasio.Controllers;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import com.mycompany.proyectogimnasio.Models.Instructor;

public class InstructorController {
    
    @FXML private TableView<Instructor> tablaInstructores;
    @FXML private TableColumn<Instructor, Integer> colId;
    @FXML private TableColumn<Instructor, String> colNombre;
    @FXML private TableColumn<Instructor, String> colApellido;
    @FXML private TableColumn<Instructor, String> colTelefono;
    @FXML private TableColumn<Instructor, String> colDni;
    @FXML private TableColumn<Instructor, Boolean> colActivo;

    @FXML private TextField txtId;
    @FXML private TextField txtNombre;
    @FXML private TextField txtApellido;
    @FXML private TextField txtTelefono;
    @FXML private TextField txtDni;
    @FXML private CheckBox chkActivo;

    private InstructorService service = new InstructorService();
    private Instructor selectedInstructor;

    @FXML
    public void initialize() {
        colId.setCellValueFactory(c -> new javafx.beans.property.SimpleIntegerProperty(c.getValue().getIdInstructor()).asObject());
        colNombre.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getNombre()));
        colApellido.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getApellido()));
        colTelefono.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getTelefono()));
        colDni.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getDni()));
        colActivo.setCellValueFactory(c -> new javafx.beans.property.SimpleBooleanProperty(c.getValue().isActivo()).asObject());

        tablaInstructores.setItems(service.getAll());

        // Datos de ejemplo
        service.add(new Instructor(1, "Carlos", "Pérez", "600123456", "12345678A", true));
        service.add(new Instructor(2, "Laura", "Gómez", "600987654", "87654321B", false));

        tablaInstructores.setOnMouseClicked(this::seleccionarInstructor);
    }

    @FXML
    private void agregarInstructor() {
        int id = Integer.parseInt(txtId.getText());
        String nombre = txtNombre.getText();
        String apellido = txtApellido.getText();
        String telefono = txtTelefono.getText();
        String dni = txtDni.getText();
        boolean activo = chkActivo.isSelected();

        service.add(new Instructor(id, nombre, apellido, telefono, dni, activo));
        limpiarCampos();
    }

    @FXML
    private void actualizarInstructor() {
        if (selectedInstructor != null) {
            Instructor nuevo = new Instructor(
                Integer.parseInt(txtId.getText()),
                txtNombre.getText(),
                txtApellido.getText(),
                txtTelefono.getText(),
                txtDni.getText(),
                chkActivo.isSelected()
            );
            service.update(selectedInstructor, nuevo);
            limpiarCampos();
        }
    }

    @FXML
    private void eliminarInstructor() {
        if (selectedInstructor != null) {
            service.delete(selectedInstructor);
            limpiarCampos();
        }
    }

    private void seleccionarInstructor(MouseEvent event) {
        selectedInstructor = tablaInstructores.getSelectionModel().getSelectedItem();
        if (selectedInstructor != null) {
            txtId.setText(String.valueOf(selectedInstructor.getIdInstructor()));
            txtNombre.setText(selectedInstructor.getNombre());
            txtApellido.setText(selectedInstructor.getApellido());
            txtTelefono.setText(selectedInstructor.getTelefono());
            txtDni.setText(selectedInstructor.getDni());
            chkActivo.setSelected(selectedInstructor.isActivo());
        }
    }

    private void limpiarCampos() {
        txtId.clear();
        txtNombre.clear();
        txtApellido.clear();
        txtTelefono.clear();
        txtDni.clear();
        chkActivo.setSelected(false);
        selectedInstructor = null;
    }
}
    

