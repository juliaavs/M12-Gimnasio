package com.mycompany.proyectogimnasio.Models;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

public class Cliente {
    private final SimpleIntegerProperty idCliente;
    private final SimpleStringProperty dni;
    private final SimpleStringProperty nombre;
    private final SimpleStringProperty apellido;
    private final SimpleStringProperty password;
    private final SimpleStringProperty iban;
    private final SimpleStringProperty telefono;   // <-- NUEVO
    private final SimpleStringProperty codPostal;  // <-- NUEVO
    private final SimpleBooleanProperty activo;     // <-- NUEVO (true = 0, false = 1)

    public Cliente(int idCliente, String dni, String nombre, String apellido, String password, String iban, String telefono, String codPostal, boolean activo) {
        this.idCliente = new SimpleIntegerProperty(idCliente);
        this.dni = new SimpleStringProperty(dni);
        this.nombre = new SimpleStringProperty(nombre);
        this.apellido = new SimpleStringProperty(apellido);
        this.password = new SimpleStringProperty(password);
        this.iban = new SimpleStringProperty(iban);
        this.telefono = new SimpleStringProperty(telefono);
        this.codPostal = new SimpleStringProperty(codPostal);
        this.activo = new SimpleBooleanProperty(activo);
    }

    // --- Propiedades ---
    public SimpleIntegerProperty idClienteProperty() { return idCliente; }
    public SimpleStringProperty dniProperty() { return dni; }
    public SimpleStringProperty nombreProperty() { return nombre; }
    public SimpleStringProperty apellidoProperty() { return apellido; }
    public SimpleStringProperty passwordProperty() { return password; }
    public SimpleStringProperty ibanProperty() { return iban; }
    public SimpleStringProperty telefonoProperty() { return telefono; }
    public SimpleStringProperty codPostalProperty() { return codPostal; }
    public SimpleBooleanProperty activoProperty() { return activo; }

    // --- Getters ---
    public int getIdCliente() { return idCliente.get(); }
    public String getDni() { return dni.get(); }
    public String getNombre() { return nombre.get(); }
    public String getApellido() { return apellido.get(); }
    public String getPassword() { return password.get(); }
    public String getIban() { return iban.get(); }
    public String getTelefono() { return telefono.get(); }
    public String getCodPostal() { return codPostal.get(); }
    public boolean isActivo() { return activo.get(); }
    
    // --- Setters (Solo para los campos que pueden cambiar) ---
    public void setDni(String dni) { this.dni.set(dni); }
    public void setNombre(String nombre) { this.nombre.set(nombre); }
    public void setApellido(String apellido) { this.apellido.set(apellido); }
    public void setPassword(String password) { this.password.set(password); }
    public void setIban(String iban) { this.iban.set(iban); }
    public void setTelefono(String telefono) { this.telefono.set(telefono); }
    public void setCodPostal(String codPostal) { this.codPostal.set(codPostal); }
    public void setActivo(boolean activo) { this.activo.set(activo); }
}