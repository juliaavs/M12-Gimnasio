package com.mycompany.proyectogimnasio.Models;
import java.io.Serializable;

public class Actividad implements Serializable {
    private int idActividad;
    private String nombre;
    private String descripcion;
    private int duracion;
    private int aforo;

    public Actividad(int idActividad, String nombre, String descripcion, int duracion, int aforo) {
        this.idActividad = idActividad;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.duracion = duracion;
        this.aforo = aforo;
    }

    // --- Getters ---
    public int getIdActividad() { 
        return idActividad; 
    }
    
    public String getNombre() { 
        return nombre; 
    }
    
    public String getDescripcion() { 
        return descripcion; 
    }
    
    public int getDuracion() { 
        return duracion; 
    }
    
    public int getAforo() { 
        return aforo; 
    }

    // --- Setters ---
    public void setIdActividad(int idActividad) { 
        this.idActividad = idActividad; 
    }
    
    public void setNombre(String nombre) { 
        this.nombre = nombre; 
    }
    
    public void setDescripcion(String descripcion) { 
        this.descripcion = descripcion; 
    }
    
    public void setDuracion(int duracion) { 
        this.duracion = duracion; 
    }
    
    public void setAforo(int aforo) { 
        this.aforo = aforo; 
    }
}