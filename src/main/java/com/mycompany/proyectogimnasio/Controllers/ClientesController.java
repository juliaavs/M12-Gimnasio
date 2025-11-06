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
    @FXML private TableColumn<Cliente, String> colTelefono;
    @FXML private TableColumn<Cliente, String> colCodPostal;
    @FXML private TableColumn<Cliente, Boolean> colActivo;

    @FXML private TextField txtDni;
    @FXML private TextField txtDniLetra; // <-- NUEVO
    @FXML private TextField txtNombre;
    @FXML private TextField txtApellido;
    @FXML private TextField txtIban;
    @FXML private TextField txtTelefono;
    @FXML private TextField txtCodPostal;

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
        colTelefono.setCellValueFactory(cellData -> cellData.getValue().telefonoProperty());
        colCodPostal.setCellValueFactory(cellData -> cellData.getValue().codPostalProperty());
        
        colActivo.setCellValueFactory(cellData -> cellData.getValue().activoProperty());
        colActivo.setCellFactory(col -> new TableCell<Cliente, Boolean>() {
            @Override
            protected void updateItem(Boolean item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty ? null : (item ? "Activo" : "Inactivo"));
                if (!empty) {
                    setStyle(item ? "-fx-text-fill: green;" : "-fx-text-fill: red; -fx-font-weight: bold;");
                }
            }
        });

        tablaClientes.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> mostrarDetallesCliente(newValue));
        
        // **CAMBIO CLAVE**: Listener para calcular la letra del DNI automáticamente
        txtDni.textProperty().addListener((obs, oldVal, newVal) -> {
            actualizarLetraDni();
        });
        
        cargarClientes();
        setEstadoFormulario(false);
    }
    
    /**
     * NUEVO: Método que se llama cada vez que cambia el texto en txtDni.
     */
    private void actualizarLetraDni() {
        String numeros = txtDni.getText().trim();
        String letra = calcularLetraDni(numeros);
        txtDniLetra.setText(letra);
    }
    
    /**
     * NUEVO: Lógica de cálculo de la letra extraída.
     * @param numeros Los 8 números del DNI.
     * @return La letra correspondiente, o "" si la entrada no es válida.
     */
    private String calcularLetraDni(String numeros) {
        if (numeros == null || !numeros.matches("\\d{8}")) {
            return "";
        }
        try {
            int numeroDni = Integer.parseInt(numeros);
            String letras = "TRWAGMYFPDXBNJZSQVHLCKE";
            char letraCalculada = letras.charAt(numeroDni % 23);
            return String.valueOf(letraCalculada);
        } catch (NumberFormatException e) {
            return "";
        }
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
            // **CAMBIO CLAVE**: Separar DNI en números y letra
            String dniCompleto = cliente.getDni();
            if (dniCompleto != null && dniCompleto.length() == 9) {
                txtDni.setText(dniCompleto.substring(0, 8));
                txtDniLetra.setText(dniCompleto.substring(8));
            } else {
                txtDni.setText(dniCompleto); // Cargar datos aunque estén mal
                txtDniLetra.clear();
            }
            
            txtNombre.setText(cliente.getNombre());
            txtApellido.setText(cliente.getApellido());
            txtIban.setText(cliente.getIban());
            txtTelefono.setText(cliente.getTelefono());
            txtCodPostal.setText(cliente.getCodPostal());
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

        // **CAMBIO CLAVE**: Construir el DNI completo
        String dniNumeros = txtDni.getText().trim();
        String dniLetra = txtDniLetra.getText().trim().toUpperCase();
        String dni = dniNumeros + dniLetra; // DNI completo
        
        String nombre = txtNombre.getText().trim();
        String apellido = txtApellido.getText().trim();
        String password = dni; // La contraseña es el DNI completo
        String iban = txtIban.getText().trim().toUpperCase();
        String telefono = txtTelefono.getText().trim();
        String codPostal = txtCodPostal.getText().trim();
        
        boolean exito;
        
        if (clienteSeleccionado == null) {
            if (clienteService.dniExiste(dni)) {
                mostrarAlerta(false, "Error al Crear", "Ya existe un cliente con el DNI: " + dni, "");
                return;
            }
            Cliente nuevoCliente = new Cliente(0, dni, nombre, apellido, password, iban, telefono, codPostal, true);
            exito = clienteService.agregarCliente(nuevoCliente);
            mostrarAlerta(exito, "Cliente Agregado", "El nuevo cliente se ha añadido correctamente.", "Error al añadir el cliente.");
        
        } else {
            Cliente clienteActualizado = new Cliente(
                clienteSeleccionado.getIdCliente(), 
                dni, nombre, apellido, password, iban, telefono, codPostal, 
                clienteSeleccionado.isActivo()
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
        
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmar Baja");
        alert.setHeaderText("Dar de baja a: " + clienteSeleccionado.getNombre() + " " + clienteSeleccionado.getApellido());
        alert.setContentText("¿Estás seguro de que quieres marcar a este cliente como 'Inactivo'?");
        
        Optional<ButtonType> resultado = alert.showAndWait();
        if (resultado.isPresent() && resultado.get() == ButtonType.OK) {
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
        txtDniLetra.clear(); // <-- NUEVO
        txtNombre.clear();
        txtApellido.clear();
        txtIban.clear();
        txtTelefono.clear();
        txtCodPostal.clear();
        tablaClientes.getSelectionModel().clearSelection();
        this.clienteSeleccionado = null;
        setEstadoFormulario(false);
    }

    private boolean validarCampos() {
        String mensajeError = "";
        
        // **CAMBIO CLAVE**: Validar solo los 8 números del DNI
        String errorDni = getDniNumerosValidationError(txtDni.getText());
        if (errorDni != null) {
            mensajeError += errorDni + "\n";
        } else if (txtDniLetra.getText().isEmpty()) {
            // Si los números son válidos pero la letra está vacía (no debería pasar, pero por si acaso)
            mensajeError += "El número de DNI introducido no es válido.\n";
        }
        
        String errorIban = getIbanValidationError(txtIban.getText());
        if (errorIban != null) mensajeError += errorIban + "\n";
        
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
    
    /**
     * MÉTODO ACTUALIZADO
     * Valida solo los 8 números del DNI.
     */
    private String getDniNumerosValidationError(String dniNumeros) {
        if (dniNumeros == null || dniNumeros.trim().isEmpty()) { 
            return "El DNI es obligatorio."; 
        }
        if (!dniNumeros.trim().matches("\\d{8}")) { 
            return "El DNI debe tener 8 números."; 
        }
        // Si los números son correctos, la letra se calcula sola.
        return null;
    }
    
    // El método getDniValidationError original se ha reemplazado/eliminado.
    
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