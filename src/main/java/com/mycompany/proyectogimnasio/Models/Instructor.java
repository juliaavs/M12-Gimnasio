
package com.mycompany.proyectogimnasio.Models;

import javafx.beans.property.*;
import java.util.List;

public class Instructor {
    private final IntegerProperty idInstructor = new SimpleIntegerProperty();
    private final StringProperty nombre = new SimpleStringProperty();
    private final StringProperty apellido = new SimpleStringProperty();
    private final StringProperty dni = new SimpleStringProperty();
    private final BooleanProperty activo = new SimpleBooleanProperty();
   
    // Campo para guardar los nombres de las clases asociadas
    private List<String> nombresClases;

    // Constructor vacío (necesario para algunas operaciones y librerías)
    public Instructor() {}

    // Constructor completo
    public Instructor(int idInstructor, String nombre, String apellido, String dni, boolean activo) {
        setIdInstructor(idInstructor);
        setNombre(nombre);
        setApellido(apellido);
        setDni(dni);
        setActivo(activo);
    }
   
    // --- Métodos Property para JavaFX ---
    public IntegerProperty idInstructorProperty() { return idInstructor; }
    public StringProperty nombreProperty() { return nombre; }
    public StringProperty apellidoProperty() { return apellido; }
    public StringProperty dniProperty() { return dni; }
    public BooleanProperty activoProperty() { return activo; }
   
    // --- Getters y Setters Estándar ---
    public int getIdInstructor() { return idInstructor.get(); }
    public void setIdInstructor(int idInstructor) { this.idInstructor.set(idInstructor); }

    public String getNombre() { return nombre.get(); }
    public void setNombre(String nombre) { this.nombre.set(nombre); }

    public String getApellido() { return apellido.get(); }
    public void setApellido(String apellido) { this.apellido.set(apellido); }

    public String getDni() { return dni.get(); }
    public void setDni(String dni) { this.dni.set(dni); }

    public boolean isActivo() { return activo.get(); }
    public void setActivo(boolean activo) { this.activo.set(activo); }
   
    // --- Getter y Setter para la lista de clases ---
    public List<String> getNombresClases() { return nombresClases; }
    public void setNombresClases(List<String> nombresClases) { this.nombresClases = nombresClases; }
   
    // Método auxiliar para mostrar las clases en la vista
    public String getClasesConcatenadas() {
        return nombresClases != null ? String.join(", ", nombresClases) : "N/A";
    }
}
