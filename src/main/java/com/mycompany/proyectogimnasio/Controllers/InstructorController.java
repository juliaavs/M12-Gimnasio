package com.mycompany.proyectogimnasio.Controllers;

import com.mycompany.proyectogimnasio.Models.Instructor;
import com.mycompany.proyectogimnasio.Service.InstructorService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import java.sql.SQLException;
import java.util.List;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import java.util.Optional;
import java.text.SimpleDateFormat;
import java.util.Date;

public class InstructorController {

    @FXML private TableView<Instructor> instructorTable;
    @FXML private TableColumn<Instructor, String> colTipoDoc;
    @FXML private TableColumn<Instructor, String> nombreColumn;
    @FXML private TableColumn<Instructor, String> apellidoColumn;
    @FXML private TableColumn<Instructor, String> dniColumn;
    @FXML private TableColumn<Instructor, Boolean> activoColumn;
    @FXML private TableColumn<Instructor, Date> fechaAltaColumn;

    @FXML private TextField txtFiltro;

    @FXML private TextField nombreField;
    @FXML private TextField apellidoField;
    @FXML private Button btnCambiarEstado;
    @FXML private Button btnVerClases;

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

    @FXML private TextField txtIdInstructor;
    @FXML private HBox hboxCrear;
    @FXML private HBox hboxEditar;

    private InstructorService instructorService;
    private ObservableList<Instructor> instructorList;
    private Instructor selectedInstructor;

    private static final String TIPO_DNI = "DNI";
    private static final String TIPO_NIE = "NIE";
    private static final String TIPO_PASAPORTE = "Pasaporte";

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd/MM/yyyy");

    @FXML
    public void initialize() {
        instructorService = new InstructorService();
        instructorList = FXCollections.observableArrayList();

        colTipoDoc.setCellValueFactory(cellData -> {
            String doc = cellData.getValue().getDni();
            String tipo = "N/A";
            if (doc != null) {
                if (doc.matches("\\d{8}[A-Z]")) {
                    tipo = TIPO_DNI;
                } else if (doc.matches("[XYZ]\\d{7}[A-Z]")) {
                    tipo = TIPO_NIE;
                } else {
                    tipo = TIPO_PASAPORTE;
                }
            }
            return new SimpleStringProperty(tipo);
        });

        nombreColumn.setCellValueFactory(cellData -> cellData.getValue().nombreProperty());
        apellidoColumn.setCellValueFactory(cellData -> cellData.getValue().apellidoProperty());
        dniColumn.setCellValueFactory(cellData -> cellData.getValue().dniProperty());
        activoColumn.setCellValueFactory(cellData -> cellData.getValue().activoProperty().asObject());
        
        activoColumn.setCellFactory(col -> new TableCell<Instructor, Boolean>() {
            @Override
            protected void updateItem(Boolean item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty ? null : (item ? "Activo" : "Inactivo"));
                if (!empty) {
                    setStyle(item ? "-fx-text-fill: green;" : "-fx-text-fill: red; -fx-font-weight: bold;");
                }
            }
        });

        instructorTable.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> showInstructorDetails(newValue));

        cbTipoDoc.setItems(FXCollections.observableArrayList(TIPO_DNI, TIPO_NIE, TIPO_PASAPORTE));
        cbNiePrefix.setItems(FXCollections.observableArrayList("X", "Y", "Z"));

        cbTipoDoc.valueProperty().addListener((obs, oldVal, newVal) -> gestionarVisibilidadCamposDoc(newVal));

        txtDni.textProperty().addListener((obs, oldVal, newVal) -> actualizarLetraDni());
        txtNieNum.textProperty().addListener((obs, oldVal, newVal) -> actualizarLetraNie());
        cbNiePrefix.valueProperty().addListener((obs, oldVal, newVal) -> actualizarLetraNie());

        addNumericLimiter(txtDni, 8);
        addNumericLimiter(txtNieNum, 7);
        addPasaporteLimiter(txtPasaporte, 7);

        loadInstructors();
        configurarFiltro();

        handleClearInstructor();
    }

    private void configuringFiltro() {
        FilteredList<Instructor> filteredData = new FilteredList<>(instructorList, p -> true);

        txtFiltro.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(instructor -> {
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }
                String lowerCaseFilter = newValue.toLowerCase();

                if (instructor.getDni() != null && instructor.getDni().toLowerCase().contains(lowerCaseFilter)) {
                    return true;
                } else if (instructor.getNombre() != null && instructor.getNombre().toLowerCase().contains(lowerCaseFilter)) {
                    return true;
                } else if (instructor.getApellido() != null && instructor.getApellido().toLowerCase().contains(lowerCaseFilter)) {
                    return true;
                }

                return false;
            });
        });

        SortedList<Instructor> sortedData = new SortedList<>(filteredData);
        sortedData.comparatorProperty().bind(instructorTable.comparatorProperty());
        instructorTable.setItems(sortedData);
    }

    private void loadInstructors() {
        try {
            instructorList.clear();
            instructorList.addAll(instructorService.getAllInstructors());
        } catch (SQLException e) {
            showAlert("Error de BD", "No se pudieron cargar los instructores: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void showInstructorDetails(Instructor instructor) {
        selectedInstructor = instructor;

        if (instructor != null) {
            nombreField.setText(instructor.getNombre());
            apellidoField.setText(instructor.getApellido());
            txtIdInstructor.setText(String.valueOf(instructor.getIdInstructor()));

            String doc = instructor.getDni();
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

            setEstadoFormulario(true);

        } else {
            clearFields();
        }
    }

    @FXML
    private void handleSaveInstructor() {
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

        Integer currentId = (selectedInstructor != null) ? selectedInstructor.getIdInstructor() : null;

        try {
            if (instructorService.existsDni(dniFinal, currentId)) {
                showAlert("Error de Validación",
                        "El DNI/NIE/Pasaporte " + dniFinal + " ya está registrado y no puede usarse.",
                        Alert.AlertType.ERROR);
                return;
            }

            if (selectedInstructor != null) {
                selectedInstructor.setNombre(nombreField.getText());
                selectedInstructor.setApellido(apellidoField.getText());
                selectedInstructor.setDni(dniFinal);

                instructorService.updateInstructor(selectedInstructor);
                showAlert("Éxito", "Instructor actualizado correctamente.", Alert.AlertType.INFORMATION);

            } else {
                Instructor newInstructor = new Instructor(
                        0,
                        nombreField.getText(),
                        apellidoField.getText(),
                        dniFinal,
                        true
                );

                instructorService.addInstructor(newInstructor);
                showAlert("Éxito", "Instructor guardado y activado correctamente.", Alert.AlertType.INFORMATION);
            }

            loadInstructors();
            handleClearInstructor();

        } catch (SQLException e) {
            showAlert("Error de Base de Datos", "Error al guardar/actualizar el instructor: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void handleClearInstructor() {
        clearFields();
    }

    @FXML
    private void handleCambiarEstado() {
        Instructor selected = instructorTable.getSelectionModel().getSelectedItem();

        if (selected != null) {
            boolean nuevoEstado = !selected.isActivo();
            String accion = nuevoEstado ? "activado" : "desactivado";

            try {
                boolean success = instructorService.cambiarEstadoActivo(selected.getIdInstructor(), nuevoEstado);

                if (success) {
                    selected.setActivo(nuevoEstado);
                    instructorTable.refresh();
                    showAlert("Éxito", "El instructor ha sido " + accion + " correctamente.", Alert.AlertType.INFORMATION);
                } else {
                    showAlert("Fallo", "No se pudo " + accion + " el instructor. Inténtelo de nuevo.", Alert.AlertType.WARNING);
                }

            } catch (SQLException e) {
                showAlert("Error de BD", "Error al comunicarse con la base de datos: " + e.getMessage(), Alert.AlertType.ERROR);
            }

        } else {
            showAlert("Sin Selección", "Por favor, selecciona un instructor de la tabla.", Alert.AlertType.WARNING);
        }
    }

    @FXML
    private void handleVerClases() {
        if (selectedInstructor == null) {
            showAlert("Error", "No hay un instructor seleccionado.", Alert.AlertType.WARNING);
            return;
        }

        List<String> clases = selectedInstructor.getNombresClases();
        String nombreInstructor = selectedInstructor.getNombre() + " " + selectedInstructor.getApellido();

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Clases Asociadas");
        alert.setHeaderText("Clases de: " + nombreInstructor);

        if (clases == null || clases.isEmpty()) {
            alert.setContentText("Este instructor no tiene clases asociadas actualmente.");
        } else {
            String contenido = String.join("\n", clases.stream().map(c -> "• " + c).collect(Collectors.toList()));

            TextArea textArea = new TextArea(contenido);
            textArea.setEditable(false);
            textArea.setWrapText(true);
            alert.getDialogPane().setContent(textArea);
        }

        alert.showAndWait();
    }

    private void configurarFiltro() {
        FilteredList<Instructor> filteredData = new FilteredList<>(instructorList, p -> true);

        txtFiltro.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(instructor -> {
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }
                String lowerCaseFilter = newValue.toLowerCase();

                if (instructor.getDni() != null && instructor.getDni().toLowerCase().contains(lowerCaseFilter)) {
                    return true;
                } else if (instructor.getNombre() != null && instructor.getNombre().toLowerCase().contains(lowerCaseFilter)) {
                    return true;
                } else if (instructor.getApellido() != null && instructor.getApellido().toLowerCase().contains(lowerCaseFilter)) {
                    return true;
                }

                return false;
            });
        });

        SortedList<Instructor> sortedData = new SortedList<>(filteredData);
        sortedData.comparatorProperty().bind(instructorTable.comparatorProperty());
        instructorTable.setItems(sortedData);
    }

    private String getDniValidationError(String dni) {
        if (dni == null || dni.trim().isEmpty()) { return "El DNI es obligatorio."; }
        String dniLimpio = dni.trim().toUpperCase();
        if (dniLimpio.length() != 9) { return "El DNI debe tener exactamente 9 caracteres (ej: 12345678A)."; }
        String parteNumerica = dniLimpio.substring(0, 8);
        char letraRecibida = dniLimpio.charAt(8);
        if (!parteNumerica.matches("\\d{8}")) { return "Los primeros 8 caracteres del DNI deben ser números."; }
        if (!Character.isLetter(letraRecibida)) { return "El último carácter del DNI debe ser una letra."; }
        try {
            int numeroDni = Integer.parseInt(parteNumerica);
            String letras = "TRWAGMYFPDXBNJZSQVHLCKE";
            char letraCalculada = letras.charAt(numeroDni % 23);
            if (letraRecibida != letraCalculada) {
                return "La letra '" + letraRecibida + "' no es correcta. Debería ser '" + letraCalculada + "'.";
            }
        } catch (NumberFormatException e) { return "Error interno al procesar el número de DNI."; }
        return null;
    }

    private String getNieValidationError(String prefix, String numeros, String letra) {
        if (prefix == null) { return "Debe seleccionar un prefijo (X, Y, Z) para el NIE."; }
        if (!numeros.matches("\\d{7}")) { return "El número de NIE debe tener 7 dígitos."; }
        if (letra.isEmpty()) { return "El número de NIE introducido no es válido."; }
        return null;
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
                case TIPO_PASAPORTE:
                    if (txtPasaporte.getText() == null || txtPasaporte.getText().trim().isEmpty()) {
                        mensajeError += "El número de Pasaporte es obligatorio.\n";
                    } else if (!txtPasaporte.getText().trim().toUpperCase().matches("^[A-Z]{3}\\d{4}$")) {
                        mensajeError += "El Pasaporte debe tener 3 letras (mayúsculas) seguidas de 4 números (ej: ABC1234).\n";
                    }
                    break;
            }
        }

        if (nombreField.getText() == null || nombreField.getText().trim().isEmpty()) mensajeError += "El Nombre es obligatorio.\n";
        if (apellidoField.getText() == null || apellidoField.getText().trim().isEmpty()) mensajeError += "El Apellido es obligatorio.\n";

        if (mensajeError.isEmpty()) {
            return true;
        } else {
            showAlert("Campos Inválidos", "Por favor, corrige los siguientes errores:", mensajeError, Alert.AlertType.WARNING);
            return false;
        }
    }

    private void clearFields() {
        selectedInstructor = null;
        nombreField.setText("");
        apellidoField.setText("");
        txtIdInstructor.setText("");
        instructorTable.getSelectionModel().clearSelection();

        cbTipoDoc.setValue(TIPO_DNI);
        gestionarVisibilidadCamposDoc(TIPO_DNI);

        txtDni.clear();
        txtDniLetra.clear();
        cbNiePrefix.setValue(null);
        txtNieNum.clear();
        txtNieLetra.clear();
        txtPasaporte.clear();

        setEstadoFormulario(false);
    }

    private void setEstadoFormulario(boolean isEditing) {
        hboxCrear.setVisible(!isEditing);
        hboxCrear.setManaged(!isEditing);
        hboxEditar.setVisible(isEditing);
        hboxEditar.setManaged(isEditing);

        btnVerClases.setVisible(isEditing);
        btnVerClases.setManaged(isEditing);

        cbTipoDoc.setDisable(isEditing);
    }

    private void showAlert(String title, String content, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private void showAlert(String title, String header, String content, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }

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
        } else {
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