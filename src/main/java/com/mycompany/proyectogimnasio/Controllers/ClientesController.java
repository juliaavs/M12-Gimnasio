package com.mycompany.proyectogimnasio.Controllers;

import com.mycompany.proyectogimnasio.Models.Cliente;
import com.mycompany.proyectogimnasio.Service.ClienteService;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import java.util.Optional;

public class ClientesController {

    //<editor-fold desc="FXML Components">
    @FXML private TableView<Cliente> tablaClientes;
    @FXML private TableColumn<Cliente, String> colDni;
    @FXML private TableColumn<Cliente, String> colNombre;
    @FXML private TableColumn<Cliente, String> colApellido;
    @FXML private TableColumn<Cliente, String> colIban;
    @FXML private TableColumn<Cliente, String> colTelefono;   // <-- NUEVO
    @FXML private TableColumn<Cliente, String> colCodPostal;  // <-- NUEVO
    @FXML private TableColumn<Cliente, Boolean> colActivo;     // <-- NUEVO

    @FXML private TextField txtDni;
    @FXML private TextField txtNombre;
    @FXML private TextField txtApellido;
    @FXML private TextField txtIban;
    @FXML private TextField txtTelefono;   // <-- NUEVO
    @FXML private TextField txtCodPostal;  // <-- NUEVO

    @FXML private HBox hboxEditar;
    @FXML private HBox hboxCrear;
    @FXML private Button btnCrear;
    @FXML private Button btnActualizar;
    @FXML private Button btnEliminar;
    @FXML private Button btnLimpiar;
    //</editor-fold>

    private final ClienteService clienteService = new ClienteService();
    private Cliente clienteSeleccionado;

    @FXML
    public void initialize() {
        colDni.setCellValueFactory(cellData -> cellData.getValue().dniProperty());
        colNombre.setCellValueFactory(cellData -> cellData.getValue().nombreProperty());
        colApellido.setCellValueFactory(cellData -> cellData.getValue().apellidoProperty());
        colIban.setCellValueFactory(cellData -> cellData.getValue().ibanProperty());        
        // **CAMBIO CLAVE**: Mapear nuevas columnas
        colTelefono.setCellValueFactory(cellData -> cellData.getValue().telefonoProperty());
        colCodPostal.setCellValueFactory(cellData -> cellData.getValue().codPostalProperty());
        
        // Columna 'Activo' con formato personalizado (CheckBox visual)
        colActivo.setCellValueFactory(cellData -> cellData.getValue().activoProperty());
        colActivo.setCellFactory(col -> new TableCell<Cliente, Boolean>() {
            @Override
            protected void updateItem(Boolean item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty ? null : (item ? "Activo" : "Inactivo"));
                // Opcional: añadir estilo
                if (!empty) {
                    setStyle(item ? "-fx-text-fill: green;" : "-fx-text-fill: red; -fx-font-weight: bold;");
                }
            }
        });


        tablaClientes.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> mostrarDetallesCliente(newValue));
        
        cargarClientes();
        setEstadoFormulario(false);
    }
    
    private void setEstadoFormulario(boolean isEditing) {
        hboxEditar.setVisible(isEditing);
        hboxEditar.setManaged(isEditing);
        hboxCrear.setVisible(!isEditing);
        hboxCrear.setManaged(!isEditing);
    }
    
    private void cargarClientes() {
        ObservableList<Cliente> clientes = clienteService.getAllClientes();
        tablaClientes.setItems(clientes);
    }

    private void mostrarDetallesCliente(Cliente cliente) {
        this.clienteSeleccionado = cliente;
        if (cliente != null) {
            txtDni.setText(cliente.getDni());
            txtNombre.setText(cliente.getNombre());
            txtApellido.setText(cliente.getApellido());
            txtIban.setText(cliente.getIban());
            txtTelefono.setText(cliente.getTelefono());   // <-- NUEVO
            txtCodPostal.setText(cliente.getCodPostal()); // <-- NUEVO
            setEstadoFormulario(true);
        } else {
            handleLimpiar();
        }
    }

    @FXML
    private void handleGuardar() {
        if (!validarCampos()) {
            return;
        }

        String dni = txtDni.getText().trim().toUpperCase();
        String nombre = txtNombre.getText().trim();
        String apellido = txtApellido.getText().trim();
        String password = dni;
        String iban = txtIban.getText().trim().toUpperCase();
        String telefono = txtTelefono.getText().trim();     // <-- NUEVO
        String codPostal = txtCodPostal.getText().trim(); // <-- NUEVO
        
        boolean exito;
        
        if (clienteSeleccionado == null) {
            if (clienteService.dniExiste(dni)) {
                mostrarAlerta(false, "Error al Crear", "Ya existe un cliente con el DNI: " + dni, "");
                return;
            }
            // Creamos el cliente (el servicio lo pondrá como activo=0 por defecto)
            Cliente nuevoCliente = new Cliente(0, dni, nombre, apellido, password, iban, telefono, codPostal, true);
            exito = clienteService.agregarCliente(nuevoCliente);
            mostrarAlerta(exito, "Cliente Agregado", "El nuevo cliente se ha añadido correctamente.", "Error al añadir el cliente.");
        
        } else {
            // Actualizamos el cliente
            Cliente clienteActualizado = new Cliente(
                clienteSeleccionado.getIdCliente(), 
                dni, nombre, apellido, password, iban, telefono, codPostal, 
                clienteSeleccionado.isActivo() // Mantenemos su estado de actividad
            );
            exito = clienteService.actualizarCliente(clienteActualizado);
            mostrarAlerta(exito, "Cliente Actualizado", "Los datos del cliente se han actualizado.", "Error al actualizar el cliente.");
        }

        if (exito) {
            cargarClientes();
            handleLimpiar();
        }
    }

    @FXML
    private void handleEliminar() {
        if (clienteSeleccionado == null) {
            mostrarAlerta(false, "Error", "No hay ningún cliente seleccionado.", "");
            return;
        }
        
        // **CAMBIO CLAVE**: Lógica de "Dar de Baja"
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmar Baja");
        alert.setHeaderText("Dar de baja a: " + clienteSeleccionado.getNombre() + " " + clienteSeleccionado.getApellido());
        alert.setContentText("¿Estás seguro de que quieres marcar a este cliente como 'Inactivo'?");
        
        Optional<ButtonType> resultado = alert.showAndWait();
        if (resultado.isPresent() && resultado.get() == ButtonType.OK) {
            // Llamamos al nuevo método del servicio
            boolean exito = clienteService.desactivarCliente(clienteSeleccionado.getIdCliente());
            mostrarAlerta(exito, "Cliente Dado de Baja", "El cliente ha sido marcado como 'Inactivo'.", "Error al dar de baja al cliente.");
            if (exito) {
                cargarClientes();
                handleLimpiar();
            }
        }
    }

    @FXML
    private void handleLimpiar() {
        txtDni.clear();
        txtNombre.clear();
        txtApellido.clear();
        txtIban.clear();
        txtTelefono.clear();   // <-- NUEVO
        txtCodPostal.clear();  // <-- NUEVO
        tablaClientes.getSelectionModel().clearSelection();
        this.clienteSeleccionado = null;
        setEstadoFormulario(false);
    }

    private boolean validarCampos() {
        String mensajeError = "";
        
        String errorDni = getDniValidationError(txtDni.getText());
        if (errorDni != null) mensajeError += errorDni + "\n";
        
        String errorIban = getIbanValidationError(txtIban.getText());
        if (errorIban != null) mensajeError += errorIban + "\n";
        
        // **CAMBIO CLAVE**: Validación de nuevos campos
        if (txtTelefono.getText() == null || !txtTelefono.getText().trim().matches("^\\d{9}$")) {
            mensajeError += "El teléfono debe tener 9 dígitos.\n";
        }
        if (txtCodPostal.getText() == null || !txtCodPostal.getText().trim().matches("^\\d{5}$")) {
            mensajeError += "El código postal debe tener 5 dígitos.\n";
        }
        
        if (txtNombre.getText() == null || txtNombre.getText().trim().isEmpty()) mensajeError += "El Nombre es obligatorio.\n";
        if (txtApellido.getText() == null || txtApellido.getText().trim().isEmpty()) mensajeError += "El Apellido es obligatorio.\n";
        
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
    
    private String getIbanValidationError(String iban) {
        if (iban == null || iban.trim().isEmpty()) { return "El IBAN es obligatorio."; }
        String ibanLimpio = iban.trim().toUpperCase().replaceAll("\\s", "");
        if (!ibanLimpio.matches("^ES\\d{22}$")) { return "El formato del IBAN no es válido. Debe ser 'ES' + 22 números."; }
        return null;
    }

    private void mostrarAlerta(boolean exito, String titulo, String encabezadoExito, String encabezadoError) {
        Alert alert;
        if (exito) {
            alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle(titulo);
            alert.setHeaderText(encabezadoExito);
        } else {
            alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(encabezadoError);
        }
        alert.showAndWait();
    }
}