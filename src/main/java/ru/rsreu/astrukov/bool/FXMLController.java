package ru.rsreu.astrukov.bool;

import de.tesis.dynaware.grapheditor.GraphEditor;
import de.tesis.dynaware.grapheditor.core.DefaultGraphEditor;
import de.tesis.dynaware.grapheditor.model.GModel;
import de.tesis.dynaware.grapheditor.model.GraphFactory;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.AnchorPane;

import java.net.URL;
import java.util.ResourceBundle;

public class FXMLController implements Initializable {
    @FXML
    private AnchorPane modelAnchorPane;

    @Override
    public void initialize(URL url, ResourceBundle rb) {


        GraphEditor graphEditor = new DefaultGraphEditor();
        GModel model = GraphFactory.eINSTANCE.createGModel();
        graphEditor.setModel(model);
        NodeManager.addNodes(model);

        modelAnchorPane.getChildren().add(graphEditor.getView());
    }
}
