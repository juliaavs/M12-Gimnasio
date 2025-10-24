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

        // **CAMBIO CLAVE**: Nos aseguramos de limpiar y estandarizar el DNI aquí
        String dni = txtDni.getText().trim().toUpperCase(); 
        String nombre = txtNombre.getText();
        String apellido = txtApellido.getText();
        String password = dni; // La contraseña es el DNI limpio y en mayúsculas
        String iban = txtIban.getText();
        
        boolean exito;
        
        if (clienteSeleccionado == null) {
            Cliente nuevoCliente = new Cliente(0, dni, nombre, apellido, password, iban);
            exito = clienteService.agregarCliente(nuevoCliente);
            mostrarAlerta(exito, "Cliente Agregado", "El nuevo cliente se ha añadido correctamente.", "Error al añadir el cliente.");
        } else {
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
        txtIban.clear();
        btnEliminar.setDisable(true);
        tablaClientes.getSelectionModel().clearSelection();
        this.clienteSeleccionado = null;
    }

    /**
     * **MÉTODO VALIDAR CAMPOS (MODIFICADO)**
     * Ahora usa la nueva función de validación de DNI.
     */
    private boolean validarCampos() {
        String mensajeError = "";
        
        // **CAMBIO CLAVE**: Llamamos al nuevo validador que devuelve un mensaje de error
        String errorDni = getDniValidationError(txtDni.getText());
        if (errorDni != null) {
            mensajeError += errorDni + "\n";
        }
        
        if (txtNombre.getText() == null || txtNombre.getText().trim().isEmpty()) mensajeError += "El Nombre es obligatorio.\n";
        if (txtApellido.getText() == null || txtApellido.getText().trim().isEmpty()) mensajeError += "El Apellido es obligatorio.\n";
        if (txtIban.getText() == null || txtIban.getText().trim().isEmpty()) mensajeError += "El IBAN es obligatorio.\n";
        
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
     * **MÉTODO DE VALIDACIÓN DE DNI (MODIFICADO)**
     * Ahora devuelve un String con el error específico, o null si es válido.
     * @param dni El DNI a validar.
     * @return Un String con el mensaje de error, or null si es válido.
     */
    private String getDniValidationError(String dni) {
        if (dni == null || dni.trim().isEmpty()) {
            return "El DNI es obligatorio.";
        }
        
        // Limpiamos y estandarizamos el DNI (quitamos espacios y a mayúsculas)
        String dniLimpio = dni.trim().toUpperCase();

        if (dniLimpio.length() != 9) {
            return "El DNI debe tener 9 caracteres (ej: 12345678A).";
        }

        String parteNumerica = dniLimpio.substring(0, 8);
        char letra = dniLimpio.charAt(8);

        if (!parteNumerica.matches("\\d{8}")) {
            return "Los primeros 8 caracteres del DNI deben ser números.";
        }

        if (!Character.isLetter(letra)) {
            return "El último carácter del DNI debe ser una letra.";
        }

        try {
            int numeroDni = Integer.parseInt(parteNumerica);
            String letras = "TRWAGMYFPDXBNJZSQVHLCKE";
            char letraCalculada = letras.charAt(numeroDni % 23);

            if (letra != letraCalculada) {
                // **AQUÍ ESTÁ EL DIAGNÓSTICO**
                return "La letra '" + letra + "' no es correcta para ese número. Debería ser '" + letraCalculada + "'.";
            }
        } catch (NumberFormatException e) {
            return "Error interno al validar el número de DNI."; // Salvaguarda
        }
        
        return null; // Si llegamos aquí, el DNI es válido
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