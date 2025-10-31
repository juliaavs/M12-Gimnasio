
package com.mycompany.proyectogimnasio.Models;

import javafx.beans.property.*;
import java.util.List;

public class Instructor {
    private final IntegerProperty idInstructor = new SimpleIntegerProperty();
    private final StringProperty nombre = new SimpleStringProperty();
    private final StringProperty apellido = new SimpleStringProperty();
    private final StringProperty dni = new SimpleStringProperty();
    private final BooleanProperty activo = new SimpleBooleanProperty();
   
    private List<String> nombresClases;

    public Instructor() {}

    public Instructor(int idInstructor, String nombre, String apellido, String dni, boolean activo) {
        setIdInstructor(idInstructor);
        setNombre(nombre);
        setApellido(apellido);
        setDni(dni);
        setActivo(activo);
    }
   
    public IntegerProperty idInstructorProperty() { return idInstructor; }
    public StringProperty nombreProperty() { return nombre; }
    public StringProperty apellidoProperty() { return apellido; }
    public StringProperty dniProperty() { return dni; }
    public BooleanProperty activoProperty() { return activo; }
   
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
   
    public List<String> getNombresClases() { return nombresClases; }
    public void setNombresClases(List<String> nombresClases) { this.nombresClases = nombresClases; }
   
    public String getClasesConcatenadas() {
        return nombresClases != null ? String.join(", ", nombresClases) : "N/A";
    }
}
