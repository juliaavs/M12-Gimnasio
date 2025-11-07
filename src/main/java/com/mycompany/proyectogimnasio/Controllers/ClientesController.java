package com.mycompany.proyectogimnasio.Controllers;

import com.mycompany.proyectogimnasio.Models.Cliente;
import com.mycompany.proyectogimnasio.Service.ClienteService;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import java.util.Optional;
import java.util.function.UnaryOperator;
import javafx.scene.control.TextFormatter;

public class ClientesController {

    //<editor-fold desc="FXML Components">
    @FXML private TableView<Cliente> tablaClientes;
    @FXML private TableColumn<Cliente, String> colTipoDoc;
    @FXML private TableColumn<Cliente, String> colDni;
    @FXML private TableColumn<Cliente, String> colNombre;
    @FXML private TableColumn<Cliente, String> colApellido;
    @FXML private TableColumn<Cliente, String> colIban;
    @FXML private TableColumn<Cliente, String> colTelefono;
    @FXML private TableColumn<Cliente, String> colCodPostal;
    @FXML private TableColumn<Cliente, Boolean> colActivo;
    
    @FXML private TextField txtFiltro;

    // --- Campos de Documento ---
    @FXML private ComboBox<String> cbTipoDoc;
    @FXML private StackPane stackDoc;
    @FXML private HBox paneDni;
    @FXML private TextField txtDni;
    @FXML private TextField txtDniLetra;
    @FXML private HBox paneNie;
    @FXML private ComboBox<String> cbNiePrefix;
    @FXML private TextField txtNieNum;
    @FXML private TextField txtNieLetra;
    @FXML private HBox panePasaporte;
    @FXML private TextField txtPasaporte;
    // --- Fin Campos de Documento ---

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
    
    private ObservableList<Cliente> masterData = FXCollections.observableArrayList();
    
    private static final String TIPO_DNI = "DNI";
    private static final String TIPO_NIE = "NIE";
    private static final String TIPO_PASAPORTE = "Pasaporte";

    @FXML
    public void initialize() {
        
        // --- Configuración de Columnas de la Tabla ---
        colTipoDoc.setCellValueFactory(cellData -> {
            String doc = cellData.getValue().getDni();
            String tipo = "";
            if (doc == null) {
                tipo = "N/A";
            } else if (doc.matches("\\d{8}[A-Z]")) {
                tipo = TIPO_DNI;
            } else if (doc.matches("[XYZ]\\d{7}[A-Z]")) {
                tipo = TIPO_NIE;
            } else {
                tipo = TIPO_PASAPORTE;
            }
            return new SimpleStringProperty(tipo);
        });

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
        
        // --- Configuración de Campos de Documento ---
        cbTipoDoc.setItems(FXCollections.observableArrayList(TIPO_DNI, TIPO_NIE, TIPO_PASAPORTE));
        cbNiePrefix.setItems(FXCollections.observableArrayList("X", "Y", "Z"));
        
        cbTipoDoc.valueProperty().addListener((obs, oldVal, newVal) -> gestionarVisibilidadCamposDoc(newVal));
        
        txtDni.textProperty().addListener((obs, oldVal, newVal) -> actualizarLetraDni());
        txtNieNum.textProperty().addListener((obs, oldVal, newVal) -> actualizarLetraNie());
        cbNiePrefix.valueProperty().addListener((obs, oldVal, newVal) -> actualizarLetraNie());
        
        // --- APLICAR LÍMITES DE CARACTERES ---
        addNumericLimiter(txtDni, 8);
        addNumericLimiter(txtNieNum, 7);
        addNumericLimiter(txtIban, 22);
        addNumericLimiter(txtTelefono, 9);
        addNumericLimiter(txtCodPostal, 5);
        addPasaporteLimiter(txtPasaporte, 7);
        
        // --- Carga inicial y configuración del filtro ---
        cargarClientes(); 
        configurarFiltro(); 
        
        handleLimpiar(); 
    }
    
    /**
     * MÉTODO CORREGIDO
     * Se añaden comprobaciones '!= null' a todos los campos
     * para evitar NullPointerExceptions si un dato falta en la BD.
     */
    private void configurarFiltro() {
        FilteredList<Cliente> filteredData = new FilteredList<>(masterData, p -> true);

        txtFiltro.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(cliente -> {
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }

                String lowerCaseFilter = newValue.toLowerCase();

                // **LA CORRECCIÓN ESTÁ AQUÍ**
                // Comprobar cada campo solo si no es nulo
                
                if (cliente.getDni() != null && cliente.getDni().toLowerCase().contains(lowerCaseFilter)) {
                    return true;
                } else if (cliente.getNombre() != null && cliente.getNombre().toLowerCase().contains(lowerCaseFilter)) {
                    return true;
                } else if (cliente.getApellido() != null && cliente.getApellido().toLowerCase().contains(lowerCaseFilter)) {
                    return true;
                } else if (cliente.getIban() != null && cliente.getIban().toLowerCase().contains(lowerCaseFilter)) {
                    return true;
                } else if (cliente.getTelefono() != null && cliente.getTelefono().toLowerCase().contains(lowerCaseFilter)) { // <-- FIX
                    return true;
                } else if (cliente.getCodPostal() != null && cliente.getCodPostal().toLowerCase().contains(lowerCaseFilter)) {
                    return true;
                }
                
                return false; // No hay coincidencia
            });
        });

        SortedList<Cliente> sortedData = new SortedList<>(filteredData);
        sortedData.comparatorProperty().bind(tablaClientes.comparatorProperty());
        tablaClientes.setItems(sortedData);
    }

    
    private void gestionarVisibilidadCamposDoc(String tipo) {
        paneDni.setVisible(TIPO_DNI.equals(tipo));
        paneDni.setManaged(TIPO_DNI.equals(tipo));
        
        paneNie.setVisible(TIPO_NIE.equals(tipo));
        paneNie.setManaged(TIPO_NIE.equals(tipo));
        
        panePasaporte.setVisible(TIPO_PASAPORTE.equals(tipo));
        panePasaporte.setManaged(TIPO_PASAPORTE.equals(tipo));
    }
    
    private void actualizarLetraDni() {
        String numeros = txtDni.getText().trim();
        String letra = calcularLetraNif(numeros);
        txtDniLetra.setText(letra);
    }
    
    private void actualizarLetraNie() {
        String prefix = cbNiePrefix.getValue();
        String numeros = txtNieNum.getText().trim();
        
        if (prefix == null || !numeros.matches("\\d{7}")) {
            txtNieLetra.setText("");
            return;
        }
        
        String numerosParaCalculo = "";
        if (prefix.equals("X")) {
            numerosParaCalculo = "0" + numeros;
        } else if (prefix.equals("Y")) {
            numerosParaCalculo = "1" + numeros;
        } else { // Z
            numerosParaCalculo = "2" + numeros;
        }
        
        String letra = calcularLetraNif(numerosParaCalculo);
        txtNieLetra.setText(letra);
    }
    
    private String calcularLetraNif(String numeros) {
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
        cbTipoDoc.setDisable(isEditing);
    }
    
    private void cargarClientes() {
        masterData.clear();
        masterData.addAll(clienteService.getAllClientes());
    }

    private void mostrarDetallesCliente(Cliente cliente) {
        this.clienteSeleccionado = cliente;
        if (cliente != null) {
            String doc = cliente.getDni();
            
            if (doc.matches("\\d{8}[A-Z]")) {
                cbTipoDoc.setValue(TIPO_DNI);
                txtDni.setText(doc.substring(0, 8));
                txtDniLetra.setText(doc.substring(8));
            } else if (doc.matches("[XYZ]\\d{7}[A-Z]")) {
                cbTipoDoc.setValue(TIPO_NIE);
                cbNiePrefix.setValue(doc.substring(0, 1));
                txtNieNum.setText(doc.substring(1, 8));
                txtNieLetra.setText(doc.substring(8));
            } else {
                cbTipoDoc.setValue(TIPO_PASAPORTE);
                txtPasaporte.setText(doc);
            }
            
            String iban = cliente.getIban();
            if (iban != null && iban.startsWith("ES") && iban.length() == 24) {
                txtIban.setText(iban.substring(2));
            } else {
                txtIban.setText(iban);
            }
            
            txtNombre.setText(cliente.getNombre());
            txtApellido.setText(cliente.getApellido());
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

        String dniFinal;
        String tipoDoc = cbTipoDoc.getValue();
        
        if (TIPO_DNI.equals(tipoDoc)) {
            dniFinal = txtDni.getText().trim() + txtDniLetra.getText().trim();
        } else if (TIPO_NIE.equals(tipoDoc)) {
            dniFinal = cbNiePrefix.getValue() + txtNieNum.getText().trim() + txtNieLetra.getText().trim();
        } else {
            dniFinal = txtPasaporte.getText().trim().toUpperCase();
        }
        
        String nombre = txtNombre.getText().trim();
        String apellido = txtApellido.getText().trim();
        String password = dniFinal; 
        String iban = "ES" + txtIban.getText().trim().toUpperCase();
        String telefono = txtTelefono.getText().trim();
        String codPostal = txtCodPostal.getText().trim();
        
        boolean exito;
        
        if (clienteSeleccionado == null) {
            if (clienteService.dniExiste(dniFinal)) {
                mostrarAlerta(false, "Error al Crear", "Ya existe un cliente con el documento: " + dniFinal, "");
                return;
            }
            Cliente nuevoCliente = new Cliente(0, dniFinal, nombre, apellido, password, iban, telefono, codPostal, true);
            exito = clienteService.agregarCliente(nuevoCliente);
            mostrarAlerta(exito, "Cliente Agregado", "El nuevo cliente se ha añadido correctamente.", "Error al añadir el cliente.");
        
        } else {
            Cliente clienteActualizado = new Cliente(
                clienteSeleccionado.getIdCliente(), 
                dniFinal, nombre, apellido, password, iban, telefono, codPostal, 
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
        cbTipoDoc.setValue(TIPO_DNI); 
        gestionarVisibilidadCamposDoc(TIPO_DNI);
        txtDni.clear();
        txtDniLetra.clear(); 
        cbNiePrefix.setValue(null);
        txtNieNum.clear();
        txtNieLetra.clear();
        txtPasaporte.clear();
        
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
        String tipoDoc = cbTipoDoc.getValue();

        if (tipoDoc == null) {
            mensajeError += "Debe seleccionar un tipo de documento (DNI, NIE, Pasaporte).\n";
        } else {
            switch (tipoDoc) {
                case TIPO_DNI:
                    String errorDni = getDniNumerosValidationError(txtDni.getText());
                    if (errorDni != null) {
                        mensajeError += errorDni + "\n";
                    } else if (txtDniLetra.getText().isEmpty()) {
                        mensajeError += "El número de DNI introducido no es válido.\n";
                    }
                    break;
                case TIPO_NIE:
                    if (cbNiePrefix.getValue() == null) {
                        mensajeError += "Debe seleccionar un prefijo (X, Y, Z) para el NIE.\n";
                    }
                    if (!txtNieNum.getText().matches("\\d{7}")) {
                        mensajeError += "El número de NIE debe tener 7 dígitos.\n";
                    } else if (txtNieLetra.getText().isEmpty()) {
                        mensajeError += "El número de NIE introducido no es válido.\n";
                    }
                    break;
                case TIPO_PASAPORTE:
                    if (txtPasaporte.getText() == null || txtPasaporte.getText().trim().isEmpty()) {
                        mensajeError += "El número de Pasaporte es obligatorio.\n";
                    } else if (!txtPasaporte.getText().trim().toUpperCase().matches("^[A-Z]{3}\\d{4}$")) {
                        mensajeError += "El Pasaporte debe tener 3 letras (mayúsculas) seguidas de 4 números (ej: ABC1234).\n";
                    }
                    break;
            }
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
    
    private String getDniNumerosValidationError(String dniNumeros) {
        if (dniNumeros == null || dniNumeros.trim().isEmpty()) { 
            return "El DNI es obligatorio."; 
        }
        if (!dniNumeros.trim().matches("\\d{8}")) { 
            return "El DNI debe tener 8 números."; 
        }
        return null;
    }
    
    private String getIbanValidationError(String iban) {
        if (iban == null || iban.trim().isEmpty()) { return "El IBAN es obligatorio."; }
        
        String ibanLimpio = iban.trim().replaceAll("\\s", "");
        if (!ibanLimpio.matches("^\\d{22}$")) { 
            return "El formato del IBAN no es válido. Debe introducir 22 números."; 
        }
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
    
    // --- MÉTODOS HELPER PARA LIMITAR TEXTO ---

    private void addNumericLimiter(TextField textField, int maxLength) {
        UnaryOperator<TextFormatter.Change> filter = change -> {
            String newText = change.getControlNewText();
            if (newText.matches("\\d*") && newText.length() <= maxLength) {
                return change;
            }
            return null; 
        };
        textField.setTextFormatter(new TextFormatter<>(filter));
    }
    
    private void addPasaporteLimiter(TextField textField, int maxLength) {
        UnaryOperator<TextFormatter.Change> filter = change -> {
            String newText = change.getControlNewText();

            if (newText.length() > maxLength) {
                return null;
            }
            
            if (!newText.matches("^[a-zA-Z0-9]*$")) {
                return null;
            }

            if (!change.isDeleted()) {
                String originalText = change.getText();
                String upperText = originalText.toUpperCase();
                if (!originalText.equals(upperText)) {
                    change.setText(upperText);
                    change.selectRange(change.getAnchor(), change.getCaretPosition());
                }
            }
            return change;
        };
        textField.setTextFormatter(new TextFormatter<>(filter));
    }
}