package com.mycompany.proyectogimnasio.Models;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

/**
 * Modelo para un Administrador, ahora con estado 'activo'.
 */
public class Admin {
    private final SimpleIntegerProperty idAdmin;
    private final SimpleStringProperty dni;
    private final SimpleStringProperty nombre;
    private final SimpleStringProperty apellido;
    private final SimpleStringProperty password;
    private final SimpleStringProperty rol;
    private final SimpleBooleanProperty activo; // <-- NUEVO CAMPO

    public Admin(int idAdmin, String dni, String nombre, String apellido, String password, String rol, boolean activo) {
        this.idAdmin = new SimpleIntegerProperty(idAdmin);
        this.dni = new SimpleStringProperty(dni);
        this.nombre = new SimpleStringProperty(nombre);
        this.apellido = new SimpleStringProperty(apellido);
        this.password = new SimpleStringProperty(password);
        this.rol = new SimpleStringProperty(rol);
        this.activo = new SimpleBooleanProperty(activo); // <-- NUEVO CAMPO
    }

    // --- Getters de Propiedades ---
    public SimpleIntegerProperty idAdminProperty() { return idAdmin; }
    public SimpleStringProperty dniProperty() { return dni; }
    public SimpleStringProperty nombreProperty() { return nombre; }
    public SimpleStringProperty apellidoProperty() { return apellido; }
    public SimpleStringProperty rolProperty() { return rol; }
    public SimpleBooleanProperty activoProperty() { return activo; } // <-- NUEVO CAMPO

    // --- Getters/Setters de Valores ---
    public int getIdAdmin() { return idAdmin.get(); }
    public String getDni() { return dni.get(); }
    public String getNombre() { return nombre.get(); }
    public String getApellido() { return apellido.get(); }
    public String getPassword() { return password.get(); }
    public String getRol() { return rol.get(); }
    public boolean isActivo() { return activo.get(); } // <-- NUEVO CAMPO
}