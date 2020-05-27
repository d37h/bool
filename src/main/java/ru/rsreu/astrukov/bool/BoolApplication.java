package ru.rsreu.astrukov.bool;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import ru.rsreu.astrukov.bool.model.BoolElement;
import ru.rsreu.astrukov.bool.service.EquationSolverKt;

import javax.swing.text.html.HTMLDocument;
import java.util.Arrays;


public class BoolApplication extends Application {

    @Override
    public void start(Stage stage) throws Exception {

        Parent sceneWithLayout = FXMLLoader.load(getClass().getResource("/scene.fxml"));


        Scene scene = new Scene(sceneWithLayout, 800, 600);
        stage.setTitle("BoolApplication");
        stage.setScene(scene);
        stage.show();

        EquationSolverKt eqs = new EquationSolverKt();

//        eqs.tst();

        String fn = "(x1 && !x2 && !x3 && x5) ||" +
                " (!x1 && x3 && !x5) || (x1 && x3 && !x4 && !x5)" +
                " || (x1 && x2 && !x3 && x5)";

        BoolElement root = eqs.solve(
                fn
                ,
                Arrays.asList("x1", "x2", "x3", "x4", "x5"));

        String a = "";
    }


    public static void main(String[] args) {
        launch(args);
    }

}
