package com.mycompany.proyectogimnasio.Controllers;

import com.mycompany.proyectogimnasio.Models.Admin;
import com.mycompany.proyectogimnasio.Service.AdminService;
import java.util.Optional;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTableCell;

public class AdminController {

    //<editor-fold desc="FXML UI Components">
    @FXML private TableView<Admin> tablaAdmins;
    @FXML private TableColumn<Admin, String> colDni;
    @FXML private TableColumn<Admin, String> colNombre;
    @FXML private TableColumn<Admin, String> colApellido;
    @FXML private TableColumn<Admin, String> colRol;
    @FXML private TableColumn<Admin, Boolean> colActivo;

    @FXML private TextField txtDni;
    @FXML private TextField txtNombre;
    @FXML private TextField txtApellido;
    @FXML private ComboBox<String> comboRol;
    @FXML private CheckBox checkActivo;
    //</editor-fold>

    private final AdminService adminService = new AdminService();
    private Admin adminSeleccionado; // Esta variable controla si estamos editando o creando

    @FXML
    public void initialize() {
        // 1. Configurar las columnas de la tabla
        colDni.setCellValueFactory(cellData -> cellData.getValue().dniProperty());
        colNombre.setCellValueFactory(cellData -> cellData.getValue().nombreProperty());
        colApellido.setCellValueFactory(cellData -> cellData.getValue().apellidoProperty());
        colRol.setCellValueFactory(cellData -> cellData.getValue().rolProperty());
        colActivo.setCellValueFactory(cellData -> cellData.getValue().activoProperty());
        colActivo.setCellFactory(CheckBoxTableCell.forTableColumn(colActivo));

        // 2. Configurar el ComboBox de roles
        comboRol.setItems(FXCollections.observableArrayList("admin", "superadmin"));

        // 3. Listener para cuando se selecciona un admin en la tabla
        tablaAdmins.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldSelection, newSelection) -> mostrarDetallesAdmin(newSelection));
        
        // 4. Cargar los datos iniciales
        cargarAdmins();
        handleLimpiar(); // Limpiar el formulario al iniciar
    }

    private void cargarAdmins() {
        tablaAdmins.setItems(adminService.getAllAdmins());
    }

    private void mostrarDetallesAdmin(Admin admin) {
        this.adminSeleccionado = admin;
        if (admin != null) {
            // Modo "Edición": Rellenar el formulario con los datos del admin seleccionado
            txtDni.setText(admin.getDni());
            txtNombre.setText(admin.getNombre());
            txtApellido.setText(admin.getApellido());
            comboRol.setValue(admin.getRol());
            checkActivo.setSelected(admin.isActivo());
        }
    }

    @FXML
    private void handleGuardar() {
        if (!validarCampos()) {
            return; // Si la validación falla, no hacer nada
        }

        String dni = txtDni.getText().toUpperCase();
        String nombre = txtNombre.getText();
        String apellido = txtApellido.getText();
        String password = dni; // La contraseña por defecto es el DNI
        String rol = comboRol.getValue();
        boolean activo = checkActivo.isSelected();
        
        boolean exito;
        // La lógica es simple: si 'adminSeleccionado' es nulo, creamos uno nuevo.
        if (adminSeleccionado == null) {
            Admin nuevoAdmin = new Admin(0, dni, nombre, apellido, password, rol, activo);
            exito = adminService.agregarAdmin(nuevoAdmin);
            mostrarAlerta(exito, "Administrador Creado", "Nuevo administrador añadido con éxito.");
        } else {
            // Si no es nulo, actualizamos el que ya existe.
            Admin adminActualizado = new Admin(adminSeleccionado.getIdAdmin(), dni, nombre, apellido, password, rol, activo);
            exito = adminService.actualizarAdmin(adminActualizado);
            mostrarAlerta(exito, "Administrador Actualizado", "Datos actualizados correctamente.");
        }
        
        if (exito) {
            cargarAdmins();
            handleLimpiar();
        }
    }

    @FXML
    private void handleEliminar() {
        if (adminSeleccionado == null) {
            mostrarAlerta(false, "Acción Inválida", "Por favor, selecciona un administrador de la tabla para eliminarlo.");
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmar Eliminación");
        alert.setHeaderText("¿Estás seguro de que quieres eliminar a " + adminSeleccionado.getNombre() + "?");
        alert.setContentText("Esta acción no se puede deshacer.");
        
        Optional<ButtonType> resultado = alert.showAndWait();
        if (resultado.isPresent() && resultado.get() == ButtonType.OK) {
            boolean exito = adminService.eliminarAdmin(adminSeleccionado.getIdAdmin());
            mostrarAlerta(exito, "Administrador Eliminado", "El administrador ha sido eliminado.");
            if (exito) {
                cargarAdmins();
                handleLimpiar();
            }
        }
    }

    @FXML
    private void handleLimpiar() {
        // Limpiar pone la aplicación en modo "Crear"
        tablaAdmins.getSelectionModel().clearSelection();
        adminSeleccionado = null; // ¡Esta línea es la más importante!
        
        txtDni.clear();
        txtNombre.clear();
        txtApellido.clear();
        comboRol.getSelectionModel().clearSelection();
        comboRol.setPromptText("Seleccionar rol");
        checkActivo.setSelected(true); // Por defecto, un nuevo admin está activo
    }

    private boolean validarCampos() {
        if (txtDni.getText().isEmpty() || !esDniValido(txtDni.getText())) {
            mostrarAlerta(false, "Error de Validación", "El DNI introducido no es válido.");
            return false;
        }
        if (txtNombre.getText().isEmpty() || txtApellido.getText().isEmpty()) {
            mostrarAlerta(false, "Error de Validación", "El nombre y el apellido son campos obligatorios.");
            return false;
        }
        if (comboRol.getValue() == null) {
            mostrarAlerta(false, "Error de Validación", "Debes seleccionar un rol para el administrador.");
            return false;
        }
        return true;
    }

    private boolean esDniValido(String dni) {
        if (dni == null || dni.length() != 9) return false;
        String parteNumerica = dni.substring(0, 8);
        char letra = Character.toUpperCase(dni.charAt(8));
        if (!parteNumerica.matches("\\d{8}") || !Character.isLetter(letra)) return false;
        try {
            int numDni = Integer.parseInt(parteNumerica);
            return letra == "TRWAGMYFPDXBNJZSQVHLCKE".charAt(numDni % 23);
        } catch (NumberFormatException e) { return false; }
    }

    private void mostrarAlerta(boolean exito, String titulo, String mensaje) {
        Alert.AlertType tipo = exito ? Alert.AlertType.INFORMATION : Alert.AlertType.ERROR;
        Alert alert = new Alert(tipo, mensaje);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.showAndWait();
    }
}