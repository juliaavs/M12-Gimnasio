package com.mycompany.proyectogimnasio.Models;

import javafx.beans.property.*;
import java.time.LocalDate;

public class Reservas {
    // Campos de la tabla 'inscripciones'
    private final IntegerProperty idClase = new SimpleIntegerProperty();
    private final IntegerProperty idCliente = new SimpleIntegerProperty();
    private final StringProperty status = new SimpleStringProperty();
    private final ObjectProperty<LocalDate> diaReserva = new SimpleObjectProperty<>(); 
    
    // CAMPOS ADICIONALES (de las tablas relacionadas)
    private final StringProperty nombreClase = new SimpleStringProperty();
    private final StringProperty nombreCliente = new SimpleStringProperty();

    public Reservas() {}

    // Nuevo constructor con nombres para usar con el DAO (JOIN)
    public Reservas(int idClase, int idCliente, String status, LocalDate diaReserva, 
                    String nombreClase, String nombreCliente) {
        setIdClase(idClase);
        setIdCliente(idCliente);
        setStatus(status);
        setDiaReserva(diaReserva);
        setNombreClase(nombreClase);
        setNombreCliente(nombreCliente);
    }
    
    // --- Property Getters ---
    public IntegerProperty idClaseProperty() { return idClase; }
    public IntegerProperty idClienteProperty() { return idCliente; }
    public StringProperty statusProperty() { return status; }
    public ObjectProperty<LocalDate> diaReservaProperty() { return diaReserva; }
    
    // Propiedades de las columnas nuevas
    public StringProperty nombreClaseProperty() { return nombreClase; }
    public StringProperty nombreClienteProperty() { return nombreCliente; }

    // --- Regular Getters and Setters ---
    public int getIdClase() { return idClase.get(); }
    public void setIdClase(int idClase) { this.idClase.set(idClase); }

    public int getIdCliente() { return idCliente.get(); }
    public void setIdCliente(int idCliente) { this.idCliente.set(idCliente); }

    public String getStatus() { return status.get(); }
    public void setStatus(String status) { this.status.set(status); }

    public LocalDate getDiaReserva() { return diaReserva.get(); }
    public void setDiaReserva(LocalDate diaReserva) { this.diaReserva.set(diaReserva); }
    
    // Getters/Setters para nombres
    public String getNombreClase() { return nombreClase.get(); }
    public void setNombreClase(String nombreClase) { this.nombreClase.set(nombreClase); }

    public String getNombreCliente() { return nombreCliente.get(); }
    public void setNombreCliente(String nombreCliente) { this.nombreCliente.set(nombreCliente); }

    public boolean isConfirmado() {
        return "confirmado".equalsIgnoreCase(getStatus());
    }
}