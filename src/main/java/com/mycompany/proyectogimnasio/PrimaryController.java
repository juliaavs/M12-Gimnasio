package com.mycompany.proyectogimnasio;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.Node;
import javafx.event.ActionEvent;
import javafx.scene.control.Label;

public class PrimaryController {

    @FXML
    private Label messageLabel;

    @FXML
    private void onHelloButtonClick() {
        messageLabel.setText("¡Hola desde ProyectoGimnasio!");
    }

    @FXML
    private void onNextButtonClick(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(App.class.getResource("/com/mycompany/proyectogimnasio/secondary.fxml"));
            Scene scene = new Scene(loader.load(), 640, 480);

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(scene);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
