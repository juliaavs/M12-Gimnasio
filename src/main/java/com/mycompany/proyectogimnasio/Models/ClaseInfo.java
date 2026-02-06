package com.mycompany.proyectogimnasio.Models;

import java.io.Serializable; // <-- IMPORT THIS
import java.time.LocalTime;
import java.util.Objects;

/**
 * Modelo que representa la información de una clase programada.
 * Contiene todos los datos necesarios para mostrarla en el horario y para actualizarla.
 */
// Add "implements Serializable" to the class definition
public class ClaseInfo implements Serializable { 
    private final int idClase; 
    private String nombreActividad;
    private String nombreInstructor;
    private String dia;
    private LocalTime horaInicio;
    private final int duracionMinutos;

    public ClaseInfo(int idClase, String nombreActividad, String nombreInstructor, String dia,
                     LocalTime horaInicio, int duracionMinutos) {
        this.idClase = idClase;
        this.nombreActividad = nombreActividad;
        this.nombreInstructor = nombreInstructor;
        this.dia = dia;
        this.horaInicio = horaInicio;
        this.duracionMinutos = duracionMinutos;
    }

    // --- Getters ---
    public int getIdClase() { return idClase; }
    public String getNombreActividad() { return nombreActividad; }
    public String getNombreInstructor() { return nombreInstructor; }
    public String getDia() { return dia; }
    public LocalTime getHoraInicio() { return horaInicio; }
    public int getDuracionMinutos() { return duracionMinutos; }

    public LocalTime getHoraFin() {
        return horaInicio.plusMinutes(duracionMinutos);
    }

    // --- Setters ---
    public void setDia(String dia) {
        this.dia = dia;
    }

    public void setHoraInicio(LocalTime horaInicio) {
        this.horaInicio = horaInicio;
    }
    
    // --- Utility Methods ---
    @Override
    public String toString() {
        return nombreActividad + " (" + nombreInstructor + ")";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ClaseInfo claseInfo = (ClaseInfo) o;
        return idClase == claseInfo.idClase;
    }

    @Override
    public int hashCode() {
        return Objects.hash(idClase);
    }
}