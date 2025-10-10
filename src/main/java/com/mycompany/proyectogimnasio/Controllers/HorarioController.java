package com.mycompany.proyectogimnasio.Controllers;

import com.mycompany.proyectogimnasio.App;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import java.io.IOException;
// Nuevas importaciones para manejar el tiempo
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

/**
 * Controlador para la vista del Horario de Clases.
 */
public class HorarioController {

    // FXML IDs de la tabla y columnas
    @FXML private TableView<HorarioBloque> tablaHorario;
    @FXML private TableColumn<HorarioBloque, String> colHora;
    @FXML private TableColumn<HorarioBloque, String> colLunes;
    @FXML private TableColumn<HorarioBloque, String> colMartes;
    @FXML private TableColumn<HorarioBloque, String> colMiercoles;
    @FXML private TableColumn<HorarioBloque, String> colJueves;
    @FXML private TableColumn<HorarioBloque, String> colViernes;
    @FXML private TableColumn<HorarioBloque, String> colSabado;
    @FXML private TableColumn<HorarioBloque, String> colDomingo;

    private String usuarioActual;
    private String rolActual;

    /**
     * Clase Modelo para cada fila de la tabla (un bloque de hora).
     */
    public static class HorarioBloque {
        private final SimpleStringProperty hora;
        private final SimpleStringProperty lunes;
        private final SimpleStringProperty martes;
        private final SimpleStringProperty miercoles;
        private final SimpleStringProperty jueves;
        private final SimpleStringProperty viernes;
        private final SimpleStringProperty sabado;
        private final SimpleStringProperty domingo;

        public HorarioBloque(String hora, String lunes, String martes, String miercoles, String jueves, String viernes, String sabado, String domingo) {
            this.hora = new SimpleStringProperty(hora);
            this.lunes = new SimpleStringProperty(lunes);
            this.martes = new SimpleStringProperty(martes);
            this.miercoles = new SimpleStringProperty(miercoles);
            this.jueves = new SimpleStringProperty(jueves);
            this.viernes = new SimpleStringProperty(viernes);
            this.sabado = new SimpleStringProperty(sabado);
            this.domingo = new SimpleStringProperty(domingo);
        }

        // Getters para la vinculación (IMPORTANTE: deben coincidir con los nombres de las propiedades)
        public String getHora() { return hora.get(); }
        public String getLunes() { return lunes.get(); }
        public String getMartes() { return martes.get(); }
        public String getMiercoles() { return miercoles.get(); }
        public String getJueves() { return jueves.get(); }
        public String getViernes() { return viernes.get(); }
        public String getSabado() { return sabado.get(); }
        public String getDomingo() { return domingo.get(); }

        // Propiedades (útil si necesitas listeners de cambio)
        public SimpleStringProperty horaProperty() { return hora; }
        public SimpleStringProperty lunesProperty() { return lunes; }
        public SimpleStringProperty martesProperty() { return martes; }
        public SimpleStringProperty miercolesProperty() { return miercoles; }
        public SimpleStringProperty juevesProperty() { return jueves; }
        public SimpleStringProperty viernesProperty() { return viernes; }
        public SimpleStringProperty sabadoProperty() { return sabado; }
        public SimpleStringProperty domingoProperty() { return domingo; }
    }

    /**
     * Método de inicialización llamado automáticamente por FXMLLoader.
     */
    @FXML
    public void initialize() {
        // 1. Configurar la vinculación de datos (data binding) para cada columna
        // MÉTODO RECOMENDADO USANDO LAMBDAS
        colHora.setCellValueFactory(cellData -> cellData.getValue().horaProperty());
        colLunes.setCellValueFactory(cellData -> cellData.getValue().lunesProperty());
        colMartes.setCellValueFactory(cellData -> cellData.getValue().martesProperty());
        colMiercoles.setCellValueFactory(cellData -> cellData.getValue().miercolesProperty());
        colJueves.setCellValueFactory(cellData -> cellData.getValue().juevesProperty());
        colViernes.setCellValueFactory(cellData -> cellData.getValue().viernesProperty());
        colSabado.setCellValueFactory(cellData -> cellData.getValue().sabadoProperty());
        colDomingo.setCellValueFactory(cellData -> cellData.getValue().domingoProperty());

        // 2. Cargar datos de ejemplo
        loadHorarioData();

        // 3. Habilitar la selección de celda individual
        tablaHorario.getSelectionModel().setCellSelectionEnabled(true);
    }

    /**
     * Carga datos de prueba en la tabla.
     * Genera el horario en intervalos de 15 minutos, de 7:00 a 13:00 y de 15:00 a 19:00.
     */
    private void loadHorarioData() {
        ObservableList<HorarioBloque> data = FXCollections.observableArrayList();
        
        // Formateador para mostrar la hora (HH:mm)
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
        
        // --- Bloque de la Mañana: 7:00 a 13:00 (Intervalos de 15 minutos) ---
        LocalTime startMorning = LocalTime.of(7, 0);
        LocalTime endMorning = LocalTime.of(13, 0);
        
        LocalTime currentTime = startMorning;
        while (currentTime.isBefore(endMorning)) {
            LocalTime nextTime = currentTime.plusMinutes(15);
            String horaString = currentTime.format(formatter) + "-" + nextTime.format(formatter);
            
            // Datos de ejemplo simulados (ajustados a los bloques de 15 min)
            String lunes = "";
            String martes = "";
            String jueves = "";
            String viernes = "";

            if (currentTime.equals(LocalTime.of(8, 0)) || currentTime.equals(LocalTime.of(8, 15))) {
                lunes = "Yoga (Ana)"; // Clase de 8:00 a 8:30 (2 bloques de 15 min)
            }
            if (currentTime.equals(LocalTime.of(11, 45))) {
                martes = "Boxeo (Luis)"; // Clase de 11:45 a 12:00
            }
            if (currentTime.equals(LocalTime.of(12, 0)) || currentTime.equals(LocalTime.of(12, 15))) {
                jueves = "Spinning (Javi)"; // Clase de 12:00 a 12:30
            }
            if (currentTime.equals(LocalTime.of(9, 30))) {
                viernes = "Funcional (Pilar)"; // Clase de 9:30 a 9:45
            }

            data.add(new HorarioBloque(
                horaString,
                lunes,
                martes,
                "", // Miércoles
                jueves,
                viernes,
                "", // Sábado
                "Cerrado"
            ));
            
            currentTime = nextTime;
        }

        // --- Franja de descanso (13:00 a 15:00) ---
        // Agregamos una única fila para el periodo de descanso, como franja en blanco
        data.add(new HorarioBloque(
            "13:00-15:00", 
            "ALMUERZO / PAUSA", 
            "ALMUERZO / PAUSA", 
            "ALMUERZO / PAUSA", 
            "ALMUERZO / PAUSA", 
            "ALMUERZO / PAUSA", 
            "ALMUERZO / PAUSA", 
            "Cerrado"
        ));
        
        // --- Bloque de la Tarde: 15:00 a 19:00 (Intervalos de 15 minutos) ---
        LocalTime startAfternoon = LocalTime.of(15, 0);
        LocalTime endAfternoon = LocalTime.of(19, 0);

        currentTime = startAfternoon;
        while (currentTime.isBefore(endAfternoon)) {
            LocalTime nextTime = currentTime.plusMinutes(15);
            String horaString = currentTime.format(formatter) + "-" + nextTime.format(formatter);
            
            // Datos de ejemplo simulados
            String miercoles = "";
            String sabado = "";
            
            if (currentTime.equals(LocalTime.of(16, 30)) || currentTime.equals(LocalTime.of(16, 45))) {
                miercoles = "Zumba (Carla)"; // Clase de 16:30 a 17:00
            }
            if (currentTime.equals(LocalTime.of(18, 0))) {
                sabado = "Pilates (Bea)"; // Clase de 18:00 a 18:15
            }

            data.add(new HorarioBloque(
                horaString,
                "", // Lunes
                "", // Martes
                miercoles,
                "", // Jueves
                "", // Viernes
                sabado, 
                "Cerrado"
            ));
            
            currentTime = nextTime;
        }

        tablaHorario.setItems(data);
    }

    /**
     * Método para recibir la información del usuario logueado.
     * @param usuario Nombre de usuario.
     * @param rol Rol del usuario (ej: Admin, Instructor, Cliente).
     */
    public void setUser(String usuario, String rol) {
        this.usuarioActual = usuario;
        this.rolActual = rol;
        System.out.println("HorarioController inicializado para: " + usuario + " con rol: " + rol);
    }

    /**
     * Maneja el evento del botón "Actualizar Horario".
     */
    @FXML
    private void handleRefresh() {
        // Lógica para recargar los datos del horario desde la fuente
        System.out.println("Actualizando horario...");
        loadHorarioData(); // Recarga los datos con la nueva estructura
    }

    /**
     * Maneja el evento del botón "Agregar Clase".
     * Podría abrir un nuevo Stage (ventana) para el formulario de adición.
     */
    @FXML
    private void handleAddClass() throws IOException {
        System.out.println("Abriendo formulario para agregar una nueva clase.");
        // Ejemplo de lógica: App.showAddClassForm(usuarioActual, rolActual);
    }
}
