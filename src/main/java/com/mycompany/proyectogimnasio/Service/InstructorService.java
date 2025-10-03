
package com.mycompany.proyectogimnasio.Service;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import com.mycompany.proyectogimnasio.Models.Instructor;

public class InstructorService {
    private ObservableList<Instructor> instructores = FXCollections.observableArrayList();

    public InstructorService() {
        // Constructor vacío público
    }

    public ObservableList<Instructor> getAll() {
        return instructores;
    }

    public void add(Instructor instructor) {
        instructores.add(instructor);
    }

    public void update(Instructor oldInstructor, Instructor newInstructor) {
        int index = instructores.indexOf(oldInstructor);
        if (index >= 0) {
            instructores.set(index, newInstructor);
        }
    }

    public void delete(Instructor instructor) {
        instructores.remove(instructor);
    }
}

