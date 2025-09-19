package com.mycompany.proyectogimnasio;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.event.ActionEvent;
import javafx.scene.Node;

public class SecondaryController {

    @FXML
    private void onBackButtonClick(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(App.class.getResource("/com/mycompany/proyectogimnasio/primary.fxml"));
            Scene scene = new Scene(loader.load(), 640, 480);

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(scene);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
