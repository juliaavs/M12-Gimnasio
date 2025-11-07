package com.mycompany.proyectogimnasio.Controllers;

import com.mycompany.proyectogimnasio.Models.Admin;
import com.mycompany.proyectogimnasio.Service.AdminService;
import java.util.Optional;
import java.util.function.UnaryOperator;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import java.util.List;
import java.util.stream.Collectors;

public class AdminController {

    //<editor-fold desc="FXML UI Components">
    @FXML private TableView<Admin> tablaAdmins;
    @FXML private TableColumn<Admin, String> colTipoDoc; 
    @FXML private TableColumn<Admin, String> colDni;
    @FXML private TableColumn<Admin, String> colNombre;
    @FXML private TableColumn<Admin, String> colApellido;
    @FXML private TableColumn<Admin, String> colRol;
    @FXML private TableColumn<Admin, Boolean> colActivo;
    
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
    // --- Fin Campos de Documento ---

    @FXML private TextField txtNombre;
    @FXML private TextField txtApellido;
    @FXML private ComboBox<String> comboRol;
    
    @FXML private Label lblEstado; 

    @FXML private HBox hboxCrear;
    @FXML private HBox hboxEditar;
    @FXML private Button btnToggleActivo;
    @FXML private Button btnEliminar;
    @FXML private Button btnActualizar;
    @FXML private Button btnLimpiar;
    @FXML private Button btnCrear;
    //</editor-fold>

    private final AdminService adminService = new AdminService();
    private ObservableList<Admin> masterData = FXCollections.observableArrayList();
    private Admin adminSeleccionado;
    
    private static final String TIPO_DNI = "DNI";
    private static final String TIPO_NIE = "NIE";

    @FXML
    public void initialize() {
        
        // --- Configuración de Columnas ---
        colTipoDoc.setCellValueFactory(cellData -> {
            String doc = cellData.getValue().getDni();
            String tipo = (doc.matches("[XYZ]\\d{7}[A-Z]")) ? TIPO_NIE : TIPO_DNI;
            return new SimpleStringProperty(tipo);
        });
        
        colDni.setCellValueFactory(cellData -> cellData.getValue().dniProperty());
        colNombre.setCellValueFactory(cellData -> cellData.getValue().nombreProperty());
        colApellido.setCellValueFactory(cellData -> cellData.getValue().apellidoProperty());
        colRol.setCellValueFactory(cellData -> cellData.getValue().rolProperty());
        
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
        
        // --- Configuración de Campos de Documento ---
        cbTipoDoc.setItems(FXCollections.observableArrayList(TIPO_DNI, TIPO_NIE));
        cbNiePrefix.setItems(FXCollections.observableArrayList("X", "Y", "Z"));
        
        cbTipoDoc.valueProperty().addListener((obs, oldVal, newVal) -> gestionarVisibilidadCamposDoc(newVal));
        
        txtDni.textProperty().addListener((obs, oldVal, newVal) -> actualizarLetraDni());
        txtNieNum.textProperty().addListener((obs, oldVal, newVal) -> actualizarLetraNie());
        cbNiePrefix.valueProperty().addListener((obs, oldVal, newVal) -> actualizarLetraNie());

        // --- Aplicar Límites ---
        addNumericLimiter(txtDni, 8);
        addNumericLimiter(txtNieNum, 7);
        
        // --- Carga y Filtro ---
        cargarAdmins();
        configurarFiltro();
        setEstadoFormulario(false);
    }
    
    private void configurarFiltro() {
        FilteredList<Admin> filteredData = new FilteredList<>(masterData, p -> true);

        txtFiltro.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(admin -> {
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }
                String lowerCaseFilter = newValue.toLowerCase();

                if (admin.getDni() != null && admin.getDni().toLowerCase().contains(lowerCaseFilter)) {
                    return true;
                } else if (admin.getNombre() != null && admin.getNombre().toLowerCase().contains(lowerCaseFilter)) {
                    return true;
                } else if (admin.getApellido() != null && admin.getApellido().toLowerCase().contains(lowerCaseFilter)) {
                    return true;
                } else if (admin.getRol() != null && admin.getRol().toLowerCase().contains(lowerCaseFilter)) {
                    return true;
                }
                
                return false;
            });
        });

        SortedList<Admin> sortedData = new SortedList<>(filteredData);
        sortedData.comparatorProperty().bind(tablaAdmins.comparatorProperty());
        tablaAdmins.setItems(sortedData);
    }

    private void cargarAdmins() {
        masterData.clear();
        masterData.addAll(adminService.getAllAdmins());
    }

    private void mostrarDetallesAdmin(Admin admin) {
        this.adminSeleccionado = admin;
        if (admin != null) {
            // Rellenar campos
            txtNombre.setText(admin.getNombre());
            txtApellido.setText(admin.getApellido());
            comboRol.setValue(admin.getRol());
            
            // Lógica DNI/NIE
            String doc = admin.getDni();
            if (doc.matches("[XYZ]\\d{7}[A-Z]")) {
                cbTipoDoc.setValue(TIPO_NIE);
                cbNiePrefix.setValue(doc.substring(0, 1));
                txtNieNum.setText(doc.substring(1, 8));
                txtNieLetra.setText(doc.substring(8));
            } else {
                cbTipoDoc.setValue(TIPO_DNI);
                if (doc.matches("\\d{8}[A-Z]")) {
                    txtDni.setText(doc.substring(0, 8));
                    txtDniLetra.setText(doc.substring(8));
                } else {
                    txtDni.setText(doc);
                    txtDniLetra.clear();
                }
            }
            
            // Lógica de Estado
            if (admin.isActivo()) {
                lblEstado.setText("Activo");
                lblEstado.setStyle("-fx-text-fill: green; -fx-font-weight: bold;");
                btnToggleActivo.setText("Desactivar");
                btnToggleActivo.setStyle("-fx-background-color: #FF9800; -fx-text-fill: white; -fx-font-weight: bold;");
            } else {
                lblEstado.setText("Inactivo");
                lblEstado.setStyle("-fx-text-fill: red; -fx-font-weight: bold;");
                btnToggleActivo.setText("Activar");
                btnToggleActivo.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-weight: bold;");
            }
            
            setEstadoFormulario(true); // Modo "Editar"
        } else {
            handleLimpiar();
        }
    }

    @FXML
    private void handleGuardar() {
        if (!validarCampos()) return;

        String dniFinal;
        String tipoDoc = cbTipoDoc.getValue();
        
        if (TIPO_DNI.equals(tipoDoc)) {
            dniFinal = txtDni.getText().trim() + txtDniLetra.getText().trim();
        } else {
            dniFinal = cbNiePrefix.getValue() + txtNieNum.getText().trim() + txtNieLetra.getText().trim();
        }
        
        String nombre = txtNombre.getText().trim();
        String apellido = txtApellido.getText().trim();
        String password = dniFinal; // Contraseña es el documento
        String rol = comboRol.getValue();
        
        boolean exito;
        if (adminSeleccionado == null) {
            // CREAR
            // Aquí puedes añadir una comprobación de DNI existente si quieres
            Admin nuevoAdmin = new Admin(0, dniFinal, nombre, apellido, password, rol, true); 
            exito = adminService.agregarAdmin(nuevoAdmin);
            mostrarAlerta(exito, "Administrador Creado", "Nuevo administrador añadido con éxito.");
        } else {
            // ACTUALIZAR
            Admin adminActualizado = new Admin(adminSeleccionado.getIdAdmin(), dniFinal, nombre, apellido, password, rol, adminSeleccionado.isActivo());
            exito = adminService.actualizarAdmin(adminActualizado);
            mostrarAlerta(exito, "Administrador Actualizado", "Datos actualizados correctamente.");
        }
        
        if (exito) {
            cargarAdmins();
            handleLimpiar();
        }
    }
    
    @FXML
    private void handleToggleActivo() {
        if (adminSeleccionado == null) return;

        boolean estadoActual = adminSeleccionado.isActivo();
        int nuevoEstadoDB = estadoActual ? 0 : 1; // 1=Activo, 0=Inactivo (Tu lógica de DB)
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
        
        txtNombre.clear();
        txtApellido.clear();
        comboRol.getSelectionModel().clearSelection();
        comboRol.setPromptText("Seleccionar rol");
        
        // Limpiar DNI/NIE
        cbTipoDoc.setValue(TIPO_DNI);
        gestionarVisibilidadCamposDoc(TIPO_DNI);
        txtDni.clear();
        txtDniLetra.clear();
        cbNiePrefix.setValue(null);
        txtNieNum.clear();
        txtNieLetra.clear();
        
        setEstadoFormulario(false); // Poner en modo "Crear"
    }

    private void setEstadoFormulario(boolean isEditing) {
        if (hboxCrear != null && hboxEditar != null) {
            hboxCrear.setVisible(!isEditing);
            hboxCrear.setManaged(!isEditing);
            hboxEditar.setVisible(isEditing);
            hboxEditar.setManaged(isEditing);
            
            lblEstado.setVisible(isEditing); 
        }
        cbTipoDoc.setDisable(isEditing); 
    }

    private boolean validarCampos() {
        String mensajeError = "";
        String tipoDoc = cbTipoDoc.getValue();

        if (tipoDoc == null) {
            mensajeError += "Debe seleccionar un tipo de documento.\n";
        } else {
            switch (tipoDoc) {
                case TIPO_DNI:
                    String errorDni = getDniValidationError(txtDni.getText() + txtDniLetra.getText());
                    if (errorDni != null) mensajeError += errorDni + "\n";
                    break;
                case TIPO_NIE:
                    String errorNie = getNieValidationError(cbNiePrefix.getValue(), txtNieNum.getText(), txtNieLetra.getText());
                    if (errorNie != null) mensajeError += errorNie + "\n";
                    break;
            }
        }
        
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
    
    // --- Métodos de Validación DNI/NIE y Alertas ---
    
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
    
    private String getNieValidationError(String prefix, String numeros, String letra) {
        if (prefix == null) { return "Debe seleccionar un prefijo (X, Y, Z) para el NIE."; }
        if (!numeros.matches("\\d{7}")) { return "El número de NIE debe tener 7 dígitos."; }
        if (letra.isEmpty()) { return "El número de NIE introducido no es válido."; }
        return null; 
    }

    private void mostrarAlerta(boolean exito, String titulo, String mensaje) {
        Alert.AlertType tipo = exito ? Alert.AlertType.INFORMATION : Alert.AlertType.ERROR;
        Alert alert = new Alert(tipo, mensaje);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.showAndWait();
    }
    
    // --- Métodos Helper para limitar texto ---

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
    
    // --- Métodos de gestión de DNI/NIE ---
    
    private void gestionarVisibilidadCamposDoc(String tipo) {
        paneDni.setVisible(TIPO_DNI.equals(tipo));
        paneDni.setManaged(TIPO_DNI.equals(tipo));
        
        paneNie.setVisible(TIPO_NIE.equals(tipo));
        paneNie.setManaged(TIPO_NIE.equals(tipo));
    }
    
    private void actualizarLetraDni() {
        String numeros = txtDni.getText().trim();
        String letra = calcularLetraNif(numeros);
        txtDniLetra.setText(letra);
    }
    
    /**
     * MÉTODO CORREGIDO
     * Corregido el error de tipeo 'numerosParaCalculado'.
     */
    private void actualizarLetraNie() {
        String prefix = cbNiePrefix.getValue();
        String numeros = txtNieNum.getText().trim();
        
        if (prefix == null || !numeros.matches("\\d{7}")) {
            txtNieLetra.setText("");
            return;
        }
        
        // **LA CORRECCIÓN ESTÁ AQUÍ**
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
}