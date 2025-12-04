package com.mycompany.proyectogimnasio.Models;

import javafx.beans.property.*;
import java.io.*;

public class Actividad implements Serializable {

    private static final long serialVersionUID = 1L;

    // 1. Quitamos 'final' y añadimos 'transient'.
    // 'transient' significa: "Java, ignora esto al serializar, daría error".
    private transient IntegerProperty idActividad;
    private transient StringProperty nombre;
    private transient StringProperty descripcion;
    private transient IntegerProperty duracion;
    private transient IntegerProperty aforo;
    private transient BooleanProperty activo;

    // Constructor completo
    public Actividad(int idActividad, String nombre, String descripcion, int duracion, int aforo, boolean activo) {
        // Inicializamos las propiedades aquí
        this.idActividad = new SimpleIntegerProperty(idActividad);
        this.nombre = new SimpleStringProperty(nombre);
        this.descripcion = new SimpleStringProperty(descripcion);
        this.duracion = new SimpleIntegerProperty(duracion);
        this.aforo = new SimpleIntegerProperty(aforo);
        this.activo = new SimpleBooleanProperty(activo);
    }

    // Constructor auxiliar
    public Actividad(String nombre, String descripcion, int duracion, int aforo, boolean activo) {
        this(0, nombre, descripcion, duracion, aforo, activo);
    }
    
    // --- Getters y Setters de Propiedades (JavaFX) ---
    public IntegerProperty idActividadProperty() { return idActividad; }
    public StringProperty nombreProperty() { return nombre; }
    public StringProperty descripcionProperty() { return descripcion; }
    public IntegerProperty duracionProperty() { return duracion; }
    public IntegerProperty aforoProperty() { return aforo; }
    public BooleanProperty activoProperty() { return activo; }

    // --- Getters y Setters Normales ---
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
    
    // =========================================================================
    // MÉTODOS MÁGICOS DE SERIALIZACIÓN MANUAL
    // =========================================================================

    // Se ejecuta al arrastrar (Copiar al portapapeles)
    private void writeObject(ObjectOutputStream s) throws IOException {
        s.defaultWriteObject(); // Guarda cualquier campo no-transient (si lo hubiera)

        // Guardamos MANUALMENTE el valor primitivo (int, String, boolean)
        // Usamos getters seguros por si (getName() == null)
        s.writeInt(getIdActividad());
        s.writeUTF(getNombre() != null ? getNombre() : "");
        s.writeUTF(getDescripcion() != null ? getDescripcion() : "");
        s.writeInt(getDuracion());
        s.writeInt(getAforo());
        s.writeBoolean(isActivo());
    }

    // Se ejecuta al soltar (Pegar desde el portapapeles)
    private void readObject(ObjectInputStream s) throws IOException, ClassNotFoundException {
        s.defaultReadObject(); 

        // Leemos los valores en el MISMO ORDEN que los escribimos
        int id = s.readInt();
        String nom = s.readUTF();
        String desc = s.readUTF();
        int dur = s.readInt();
        int af = s.readInt();
        boolean act = s.readBoolean();

        // RECONSTRUIMOS las propiedades de JavaFX
        this.idActividad = new SimpleIntegerProperty(id);
        this.nombre = new SimpleStringProperty(nom);
        this.descripcion = new SimpleStringProperty(desc);
        this.duracion = new SimpleIntegerProperty(dur);
        this.aforo = new SimpleIntegerProperty(af);
        this.activo = new SimpleBooleanProperty(act);
    }
}