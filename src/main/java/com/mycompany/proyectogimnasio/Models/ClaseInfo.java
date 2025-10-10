package com.mycompany.proyectogimnasio.Models;

import java.time.LocalTime;

public class ClaseInfo {
    private String nombreActividad;
    private String nombreInstructor;
    private String dia;
    private LocalTime horaInicio;
    private int duracionMinutos;

    // Constructor limpio, solo asigna valores
    public ClaseInfo(String nombreActividad, String nombreInstructor, String dia,
                     LocalTime horaInicio, int duracionMinutos) {
        this.nombreActividad = nombreActividad;
        this.nombreInstructor = nombreInstructor;
        this.dia = dia;
        this.horaInicio = horaInicio;
        this.duracionMinutos = duracionMinutos;
    }

    // Getters
    public String getNombreActividad() { return nombreActividad; }
    public String getNombreInstructor() { return nombreInstructor; }
    public String getDia() { return dia; }
    public LocalTime getHoraInicio() { return horaInicio; }
    
    // Este método calcula la hora de fin, es correcto que esté aquí
    public LocalTime getHoraFin() {
        return horaInicio.plusMinutes(duracionMinutos);
    }
}