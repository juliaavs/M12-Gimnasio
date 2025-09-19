package com.mycompany.proyectogimnasio;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class App extends Application {

    @Override
    public void start(Stage stage) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("/com/mycompany/proyectogimnasio/primary.fxml"));
            Scene scene = new Scene(fxmlLoader.load(), 640, 480);
            stage.setTitle("Proyecto Gimnasio");
            stage.setScene(scene);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch();
    }
}
