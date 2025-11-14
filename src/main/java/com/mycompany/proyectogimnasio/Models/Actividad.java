package com.mycompany.proyectogimnasio.Models;

import javafx.beans.property.*;
import java.io.Serializable;
import java.util.List;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;

public class Actividad {
    private final IntegerProperty idActividad = new SimpleIntegerProperty();
    private final StringProperty nombre = new SimpleStringProperty();
    private final StringProperty descripcion = new SimpleStringProperty();
    private final IntegerProperty duracion = new SimpleIntegerProperty();
    private final IntegerProperty aforo = new SimpleIntegerProperty();
    private final BooleanProperty activo = new SimpleBooleanProperty();

    public Actividad(int idActividad, String nombre, String descripcion, int duracion, int aforo, boolean activo) {
        setIdActividad(idActividad);
        setNombre(nombre);
        setDescripcion(descripcion);
        setDuracion(duracion);
        setAforo(aforo);
        setActivo(activo);
    }

    public Actividad(String nombre, String descripcion, int duracion, int aforo, boolean activo) {
        this(0, nombre, descripcion, duracion, aforo, activo);
    }
    
    
    public IntegerProperty idActividadProperty() { return idActividad; }
    public StringProperty nombreProperty() { return nombre; }
    public StringProperty descripcionProperty() { return descripcion; }
    public IntegerProperty duracionProperty() { return duracion; }
    public IntegerProperty aforoProperty() { return aforo; }
    public BooleanProperty activoProperty() { return activo; }

    
    public int getIdActividad() { return idActividad.get(); }
    public void setIdActividad(int idActividad) { this.idActividad.set(idActividad); }

    public String getNombre() { return nombre.get(); }
    public void setNombre(String nombre) { this.nombre.set(nombre); }

    public String getDescripcion() { return descripcion.get(); }
    public void setDescripcion(String descripcion) { this.descripcion.set(descripcion); }

    public int getDuracion() { return duracion.get(); }
    public void setDuracion(int duracion) { this.duracion.set(duracion); }

    public int getAforo() { return aforo.get(); }
    public void setAforo(int aforo) { this.aforo.set(aforo); }
    
    public boolean isActivo() { return activo.get(); }
    public void setActivo(boolean activo) { this.activo.set(activo); }
    
    
}
