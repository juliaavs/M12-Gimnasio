package com.mycompany.proyectogimnasio.Models;

public class Clase {
    private int idClase;
    private int idInstructor;
    private int idActividad;
    private String dia;
    private String horaInicio;
    private String status;
    private String nombreActividad;

    public Clase(int idClase, int idInstructor, int idActividad, String dia, String horaInicio, String status, String nombreActividad) {
        this.idClase = idClase;
        this.idInstructor = idInstructor;
        this.idActividad = idActividad;
        this.dia = dia;
        this.horaInicio = horaInicio;
        this.status = status;
        this.nombreActividad = nombreActividad;
    }

    public int getIdClase() { return idClase; }
    public int getIdInstructor() { return idInstructor; }
    public int getIdActividad() { return idActividad; }
    public String getDia() { return dia; }
    public String getHoraInicio() { return horaInicio; }
    public String getStatus() { return status; }
    public String getNombreActividad() { return nombreActividad; }

    public void setIdClase(int idClase) {
        this.idClase = idClase;
    }

    public void setIdInstructor(int idInstructor) {
        this.idInstructor = idInstructor;
    }

    public void setIdActividad(int idActividad) {
        this.idActividad = idActividad;
    }

    public void setDia(String dia) {
        this.dia = dia;
    }

    public void setHoraInicio(String horaInicio) {
        this.horaInicio = horaInicio;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setNombreActividad(String nombreActividad) {
        this.nombreActividad = nombreActividad;
    }
    
    

}