package com.mycompany.proyectogimnasio.Controllers;

import com.mycompany.proyectogimnasio.Models.Admin;
import com.mycompany.proyectogimnasio.Service.AdminService;
import java.util.Optional;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox; // <-- Importar HBox

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
    // @FXML private CheckBox checkActivo; // <-- ELIMINADO

    // **CAMBIO CLAVE**: Referencias a los HBox y botones
    @FXML private HBox hboxCrear;
    @FXML private HBox hboxEditar;
    @FXML private Button btnToggleActivo;
    @FXML private Button btnEliminar;
    @FXML private Button btnActualizar;
    @FXML private Button btnLimpiar;
    @FXML private Button btnCrear;
    //</editor-fold>

    private final AdminService adminService = new AdminService();
    private Admin adminSeleccionado;

    @FXML
    public void initialize() {
        colDni.setCellValueFactory(cellData -> cellData.getValue().dniProperty());
        colNombre.setCellValueFactory(cellData -> cellData.getValue().nombreProperty());
        colApellido.setCellValueFactory(cellData -> cellData.getValue().apellidoProperty());
        colRol.setCellValueFactory(cellData -> cellData.getValue().rolProperty());
        
        // **CAMBIO CLAVE**: Mostrar "Activo" / "Inactivo" en la columna
        colActivo.setCellValueFactory(cellData -> cellData.getValue().activoProperty());
        colActivo.setCellFactory(col -> new TableCell<Admin, Boolean>() {
            @Override
            protected void updateItem(Boolean item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty ? null : (item ? "Activo" : "Inactivo"));
                if (!empty) {
                    setStyle(item ? "-fx-text-fill: green;" : "-fx-text-fill: red; -fx-font-weight: bold;");
                }
            }
        });

        comboRol.setItems(FXCollections.observableArrayList("admin", "superadmin"));

        tablaAdmins.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldSelection, newSelection) -> mostrarDetallesAdmin(newSelection));
        
        cargarAdmins();
        setEstadoFormulario(false); // Iniciar en modo "Crear"
    }

    private void cargarAdmins() {
        tablaAdmins.setItems(adminService.getAllAdmins());
    }

    private void mostrarDetallesAdmin(Admin admin) {
        this.adminSeleccionado = admin;
        if (admin != null) {
            txtDni.setText(admin.getDni());
            txtNombre.setText(admin.getNombre());
            txtApellido.setText(admin.getApellido());
            comboRol.setValue(admin.getRol());
            // checkActivo.setSelected(admin.isActivo()); // <-- ELIMINADO
            
            // **CAMBIO CLAVE**: Lógica del botón de estado dinámico
            if (admin.isActivo()) {
                btnToggleActivo.setText("Desactivar");
                btnToggleActivo.setStyle("-fx-background-color: #FF9800; -fx-text-fill: white; -fx-font-weight: bold;"); // Naranja
            } else {
                btnToggleActivo.setText("Activar");
                btnToggleActivo.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-weight: bold;"); // Verde
            }
            
            setEstadoFormulario(true); // Modo "Editar"
        }
    }

    @FXML
    private void handleGuardar() {
        if (!validarCampos()) return;

        String dni = txtDni.getText().trim().toUpperCase();
        String nombre = txtNombre.getText().trim();
        String apellido = txtApellido.getText().trim();
        String password = dni;
        String rol = comboRol.getValue();
        // boolean activo = checkActivo.isSelected(); // <-- ELIMINADO
        
        boolean exito;
        if (adminSeleccionado == null) {
            // **CAMBIO CLAVE**: Al crear, se asume activo=true (el servicio inserta 1)
            Admin nuevoAdmin = new Admin(0, dni, nombre, apellido, password, rol, true); 
            exito = adminService.agregarAdmin(nuevoAdmin);
            mostrarAlerta(exito, "Administrador Creado", "Nuevo administrador añadido con éxito.");
        } else {
            // **CAMBIO CLAVE**: Al actualizar, solo se actualizan los datos, no el estado
            Admin adminActualizado = new Admin(adminSeleccionado.getIdAdmin(), dni, nombre, apellido, password, rol, adminSeleccionado.isActivo());
            exito = adminService.actualizarAdmin(adminActualizado);
            mostrarAlerta(exito, "Administrador Actualizado", "Datos actualizados correctamente.");
        }
        
        if (exito) {
            cargarAdmins();
            handleLimpiar();
        }
    }
    
    /**
     * **NUEVO MÉTODO**
     * Maneja el botón de activar/desactivar.
     */
    @FXML
    private void handleToggleActivo() {
        if (adminSeleccionado == null) return;

        boolean estadoActual = adminSeleccionado.isActivo();
        int nuevoEstadoDB = estadoActual ? 0 : 1; // 1=Activo, 0=Inactivo
        String accion = estadoActual ? "desactivado" : "activado";

        boolean exito = adminService.actualizarEstadoAdmin(adminSeleccionado.getIdAdmin(), nuevoEstadoDB);
        mostrarAlerta(exito, "Estado Actualizado", "El administrador ha sido " + accion + ".");
        
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
        alert.setHeaderText("¿Estás seguro de que quieres ELIMINAR a " + adminSeleccionado.getNombre() + "?");
        alert.setContentText("Esta acción borrará al administrador permanentemente de la base de datos.");
        
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
        tablaAdmins.getSelectionModel().clearSelection();
        adminSeleccionado = null;
        
        txtDni.clear();
        txtNombre.clear();
        txtApellido.clear();
        comboRol.getSelectionModel().clearSelection();
        comboRol.setPromptText("Seleccionar rol");
        // checkActivo.setSelected(true); // <-- ELIMINADO
        
        setEstadoFormulario(false); // Poner en modo "Crear"
    }

    /**
     * **NUEVO MÉTODO**
     * Cambia la visibilidad de los HBox de botones.
     */
    private void setEstadoFormulario(boolean isEditing) {
        if (hboxCrear != null && hboxEditar != null) {
            hboxCrear.setVisible(!isEditing);
            hboxCrear.setManaged(!isEditing);
            hboxEditar.setVisible(isEditing);
            hboxEditar.setManaged(isEditing);
        }
    }

    private boolean validarCampos() {
        String mensajeError = "";
        
        String errorDni = getDniValidationError(txtDni.getText());
        if (errorDni != null) mensajeError += errorDni + "\n";
        
        if (txtNombre.getText() == null || txtNombre.getText().trim().isEmpty()) mensajeError += "El Nombre es obligatorio.\n";
        if (txtApellido.getText() == null || txtApellido.getText().trim().isEmpty()) mensajeError += "El Apellido es obligatorio.\n";
        if (comboRol.getValue() == null) mensajeError += "Debes seleccionar un rol.\n";
        
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
    
    private String getDniValidationError(String dni) {
        if (dni == null || dni.trim().isEmpty()) { return "El DNI es obligatorio."; }
        String dniLimpio = dni.trim().toUpperCase();
        if (dniLimpio.length() != 9) { return "El DNI debe tener 9 caracteres (ej: 12345678A)."; }
        String parteNumerica = dniLimpio.substring(0, 8);
        char letra = dniLimpio.charAt(8);
        if (!parteNumerica.matches("\\d{8}")) { return "Los primeros 8 caracteres del DNI deben ser números."; }
        if (!Character.isLetter(letra)) { return "El último carácter del DNI debe ser una letra."; }
        try {
            int numeroDni = Integer.parseInt(parteNumerica);
            String letras = "TRWAGMYFPDXBNJZSQVHLCKE";
            char letraCalculada = letras.charAt(numeroDni % 23);
            if (letra != letraCalculada) { return "La letra '" + letra + "' no es correcta. Debería ser '" + letraCalculada + "'."; }
        } catch (NumberFormatException e) { return "Error interno al validar el número de DNI."; }
        return null;
    }

    private void mostrarAlerta(boolean exito, String titulo, String mensaje) {
        Alert.AlertType tipo = exito ? Alert.AlertType.INFORMATION : Alert.AlertType.ERROR;
        Alert alert = new Alert(tipo, mensaje);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.showAndWait();
    }
}