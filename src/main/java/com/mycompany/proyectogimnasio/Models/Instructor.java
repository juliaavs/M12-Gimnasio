
package com.mycompany.proyectogimnasio.Models;



public class Instructor {

    private int idInstructor;
    private String nombre;
    private String apellido;
    private String telefono;
    private String dni;
    private boolean activo;

    public Instructor(int idInstructor, String nombre, String apellido, String telefono, String dni, boolean activo) {
        this.idInstructor = idInstructor;
        this.nombre = nombre;
        this.apellido = apellido;
        this.telefono = telefono;
        this.dni = dni;
        this.activo = activo;
    }
    

    // Getters y setters
    public int getIdInstructor() { return idInstructor; }
    public void setIdInstructor(int idInstructor) { this.idInstructor = idInstructor; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getApellido() { return apellido; }
    public void setApellido(String apellido) { this.apellido = apellido; }

    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }

    public String getDni() { return dni; }
    public void setDni(String dni) { this.dni = dni; }

    public boolean isActivo() { return activo; }
    public void setActivo(boolean activo) { this.activo = activo; }
}


