package ru.rsreu.astrukov.bool;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;


public class BoolApplication extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("/ru.rsreu.astrukov.bool/scene.fxml"));
        
        Scene scene = new Scene(root);
        scene.getStylesheets().add(
                getClass().getResource("/ru.rsreu.astrukov.bool/styles.css").toExternalForm()
        );
        
        stage.setTitle("JavaFX and Gradle");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }

}
