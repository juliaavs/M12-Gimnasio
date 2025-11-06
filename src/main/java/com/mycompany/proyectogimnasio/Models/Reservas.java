package com.mycompany.proyectogimnasio.Models;

import javafx.beans.property.*;
import java.time.LocalDate; // Para manejar la fecha de manera más robusta

public class Reservas {
    // Campos que corresponden a las columnas de la tabla
    private final IntegerProperty idClase = new SimpleIntegerProperty();
    private final IntegerProperty idCliente = new SimpleIntegerProperty();
    private final StringProperty status = new SimpleStringProperty();
    private final ObjectProperty<LocalDate> diaReserva = new SimpleObjectProperty<>(); // Usar ObjectProperty para LocalDate


    /**
     * Constructor con todos los campos.
     * @param idClase El ID de la clase reservada.
     * @param idCliente El ID del cliente que realiza la reserva.
     * @param status El estado de la reserva ('confirmado' o 'cancelado').
     * @param diaReserva La fecha de la reserva (YYYY-MM-DD).
     */
    public Reservas(int idClase, int idCliente, String status, LocalDate diaReserva) {
        setIdClase(idClase);
        setIdCliente(idCliente);
        setStatus(status);
        setDiaReserva(diaReserva);
    }
    
        /**
     * Constructor por defecto.
     */
    public Reservas() {}

    // --- Property Getters ---
    public IntegerProperty idClaseProperty() { return idClase; }
    public IntegerProperty idClienteProperty() { return idCliente; }
    public StringProperty statusProperty() { return status; }
    public ObjectProperty<LocalDate> diaReservaProperty() { return diaReserva; }

    // --- Regular Getters and Setters ---

    // idClase
    public int getIdClase() { return idClase.get(); }
    public void setIdClase(int idClase) { this.idClase.set(idClase); }

    // idCliente
    public int getIdCliente() { return idCliente.get(); }
    public void setIdCliente(int idCliente) { this.idCliente.set(idCliente); }

    // status
    public String getStatus() { return status.get(); }
    public void setStatus(String status) { this.status.set(status); }

    // diaReserva
    public LocalDate getDiaReserva() { return diaReserva.get(); }
    public void setDiaReserva(LocalDate diaReserva) { this.diaReserva.set(diaReserva); }

    /**
     * Método útil para verificar si el estado es 'confirmado' (activo).
     * @return true si el estado es 'confirmado', false si es 'cancelado'.
     */
    public boolean isConfirmado() {
        return "confirmado".equalsIgnoreCase(getStatus());
    }
}
