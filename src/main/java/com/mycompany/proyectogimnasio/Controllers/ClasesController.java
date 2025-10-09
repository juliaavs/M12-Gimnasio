/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.proyectogimnasio.Controllers;

/**
 *
 * @author julia
 */
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;

import java.sql.*;

public class ClasesController {

    @FXML
    private TextField txtDia, txtHoraInicio, txtStatus, txtIdInstructor;

    @FXML
    private ComboBox<String> cbActividad; // seleccionar actividad

    @FXML
    private ListView<String> listaClases;

    private ObservableList<String> clasesObservable;

    private Connection conn;

    @FXML
    public void initialize() {
        clasesObservable = FXCollections.observableArrayList();
        listaClases.setItems(clasesObservable);

        try {
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/tu_base", "usuario", "password");

            cargarActividades(); // llenar ComboBox con actividades
            cargarClases();      // mostrar las clases existentes

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Crear nueva clase
    @FXML
    private void crearClase() {
        try {
            int idInstructor = Integer.parseInt(txtIdInstructor.getText());
            int idActividad = cbActividad.getSelectionModel().getSelectedIndex() + 1; // IDs según orden
            String dia = txtDia.getText();
            String horaInicio = txtHoraInicio.getText();
            String status = txtStatus.getText();

            String sql = "INSERT INTO clases (id_instructor, id_actividad, dia, hora_inicio, status) VALUES (?, ?, ?, ?, ?)";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, idInstructor);
                ps.setInt(2, idActividad);
                ps.setString(3, dia);
                ps.setString(4, horaInicio);
                ps.setString(5, status);
                ps.executeUpdate();
            }

            cargarClases(); // refrescar lista
            limpiarCampos();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Cargar clases en ListView
    private void cargarClases() throws SQLException {
        clasesObservable.clear();
        String sql = "SELECT c.dia, c.hora_inicio, a.nombre " +
                     "FROM clases c " +
                     "JOIN actividades a ON c.id_actividad = a.id_actividad";
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                String item = rs.getString("dia") + " " + rs.getString("hora_inicio") + " - " + rs.getString("nombre");
                clasesObservable.add(item);
            }
        }
    }

    // Cargar actividades en ComboBox
    private void cargarActividades() throws SQLException {
        String sql = "SELECT nombre FROM actividades";
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                cbActividad.getItems().add(rs.getString("nombre"));
            }
        }
    }

    private void limpiarCampos() {
        txtIdInstructor.clear();
        txtDia.clear();
        txtHoraInicio.clear();
        txtStatus.clear();
        cbActividad.getSelectionModel().clearSelection();
    }
}

