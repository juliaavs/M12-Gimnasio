/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.proyectogimnasio.Models;

/**
 *
 * @author julia
 */
public class Clase {
    private int idClase;
    private int idInstructor;
    private int idActividad;
    private String dia;
    private String horaInicio;
    private String status;
    private String nombreActividad; // opcional, para mostrar en la UI

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

    @Override
    public String toString() {
        return dia + " " + horaInicio + " - " + nombreActividad;
    }
}

