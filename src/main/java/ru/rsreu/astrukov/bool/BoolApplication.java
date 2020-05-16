package ru.rsreu.astrukov.bool;

import de.tesis.dynaware.grapheditor.Commands;
import de.tesis.dynaware.grapheditor.GraphEditor;
import de.tesis.dynaware.grapheditor.core.DefaultGraphEditor;
import de.tesis.dynaware.grapheditor.model.GConnector;
import de.tesis.dynaware.grapheditor.model.GModel;
import de.tesis.dynaware.grapheditor.model.GNode;
import de.tesis.dynaware.grapheditor.model.GraphFactory;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;


public class BoolApplication extends Application {

    @Override
    public void start(Stage stage) throws Exception {

        BorderPane rootPane = new BorderPane();

        Parent sceneWithLayout = FXMLLoader.load(getClass().getResource("/ru.rsreu.astrukov.bool/scene.fxml"));

        BorderPane.setAlignment(sceneWithLayout, Pos.CENTER);
        rootPane.setTop(sceneWithLayout);

        GraphEditor graphEditor = new DefaultGraphEditor();
        GModel model = GraphFactory.eINSTANCE.createGModel();
        graphEditor.setModel(model);
        addNodes(model);

        rootPane.setBottom(graphEditor.getView());

        Scene scene = new Scene(rootPane, 800, 600);
        stage.setTitle("BoolApplication");
        stage.setScene(scene);
        stage.show();
    }

    private GNode createNode() {

        GNode node = GraphFactory.eINSTANCE.createGNode();

        GConnector input = GraphFactory.eINSTANCE.createGConnector();
        GConnector output = GraphFactory.eINSTANCE.createGConnector();

        input.setType("left-input");
        output.setType("right-output");

        node.getConnectors().add(input);
        node.getConnectors().add(output);

        return node;
    }

    private void addNodes(GModel model) {

        GNode firstNode = createNode();
        GNode secondNode = createNode();

        firstNode.setX(150);
        firstNode.setY(150);

        secondNode.setX(400);
        secondNode.setY(200);
        secondNode.setWidth(200);
        secondNode.setHeight(150);

        Commands.addNode(model, firstNode);
        Commands.addNode(model, secondNode);
    }

    public static void main(String[] args) {
        launch(args);
    }

}
