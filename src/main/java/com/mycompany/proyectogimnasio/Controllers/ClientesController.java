package com.mycompany.proyectogimnasio.Controllers;

import com.mycompany.proyectogimnasio.Models.Cliente;
import com.mycompany.proyectogimnasio.Service.ClienteService;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import java.util.Optional;

public class ClientesController {

    //<editor-fold desc="FXML Components">
    @FXML private TableView<Cliente> tablaClientes;
    @FXML private TableColumn<Cliente, String> colDni;
    @FXML private TableColumn<Cliente, String> colNombre;
    @FXML private TableColumn<Cliente, String> colApellido;
    @FXML private TableColumn<Cliente, String> colIban;

    @FXML private TextField txtDni;
    @FXML private TextField txtNombre;
    @FXML private TextField txtApellido;
    // <-- **CAMBIO CLAVE**: Se elimina el PasswordField -->
    @FXML private TextField txtIban;

    @FXML private Button btnGuardar;
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

        tablaClientes.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> mostrarDetallesCliente(newValue));
        
        cargarClientes();
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
            // <-- **CAMBIO CLAVE**: La contraseña ya no se muestra -->
            txtIban.setText(cliente.getIban());
            btnEliminar.setDisable(false);
        } else {
            handleLimpiar();
        }
    }

    @FXML
    private void handleGuardar() {
        if (!validarCampos()) {
            return;
        }

        String dni = txtDni.getText().toUpperCase(); // Guardamos el DNI en mayúsculas
        String nombre = txtNombre.getText();
        String apellido = txtApellido.getText();
        // <-- **CAMBIO CLAVE**: La contraseña ahora es el DNI por defecto -->
        String password = dni; 
        String iban = txtIban.getText();
        
        boolean exito;
        
        if (clienteSeleccionado == null) {
            // Crear nuevo cliente
            Cliente nuevoCliente = new Cliente(0, dni, nombre, apellido, password, iban);
            exito = clienteService.agregarCliente(nuevoCliente);
            mostrarAlerta(exito, "Cliente Agregado", "El nuevo cliente se ha añadido correctamente.", "Error al añadir el cliente.");
        } else {
            // Actualizar cliente existente
            Cliente clienteActualizado = new Cliente(clienteSeleccionado.getIdCliente(), dni, nombre, apellido, password, iban);
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
            mostrarAlerta(false, "Error", "", "No hay ningún cliente seleccionado para eliminar.");
            return;
        }
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmar Eliminación");
        alert.setHeaderText("Eliminar Cliente: " + clienteSeleccionado.getNombre() + " " + clienteSeleccionado.getApellido());
        alert.setContentText("¿Estás seguro de que quieres dar de baja a este cliente?");
        
        Optional<ButtonType> resultado = alert.showAndWait();
        if (resultado.isPresent() && resultado.get() == ButtonType.OK) {
            boolean exito = clienteService.eliminarCliente(clienteSeleccionado.getIdCliente());
            mostrarAlerta(exito, "Cliente Eliminado", "El cliente ha sido dado de baja.", "Error al eliminar el cliente.");
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
        // <-- **CAMBIO CLAVE**: Ya no se limpia el campo de contraseña -->
        txtIban.clear();
        btnEliminar.setDisable(true);
        tablaClientes.getSelectionModel().clearSelection();
        this.clienteSeleccionado = null;
    }

    private boolean validarCampos() {
        String mensajeError = "";
        if (txtDni.getText() == null || txtDni.getText().isEmpty()) {
            mensajeError += "El DNI es obligatorio.\n";
        } else if (!esDniValido(txtDni.getText())) {
            mensajeError += "El formato del DNI no es válido.\n";
        }
        if (txtNombre.getText() == null || txtNombre.getText().isEmpty()) mensajeError += "El Nombre es obligatorio.\n";
        if (txtApellido.getText() == null || txtApellido.getText().isEmpty()) mensajeError += "El Apellido es obligatorio.\n";
        // <-- **CAMBIO CLAVE**: Se elimina la validación de la contraseña -->
        if (txtIban.getText() == null || txtIban.getText().isEmpty()) mensajeError += "El IBAN es obligatorio.\n";
        
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
    
    private boolean esDniValido(String dni) {
        if (dni == null || dni.length() != 9) {
            return false;
        }
        String parteNumerica = dni.substring(0, 8);
        char letra = Character.toUpperCase(dni.charAt(8));
        if (!parteNumerica.matches("\\d{8}") || !Character.isLetter(letra)) {
            return false;
        }
        try {
            int numeroDni = Integer.parseInt(parteNumerica);
            String letras = "TRWAGMYFPDXBNJZSQVHLCKE";
            char letraCalculada = letras.charAt(numeroDni % 23);
            return letra == letraCalculada;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private void mostrarAlerta(boolean exito, String titulo, String encabezadoExito, String encabezadoError) {
        if (exito) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle(titulo);
            alert.setHeaderText(encabezadoExito);
            alert.showAndWait();
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(encabezadoError);
            alert.showAndWait();
        }
    }
}