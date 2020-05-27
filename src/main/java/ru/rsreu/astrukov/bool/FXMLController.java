package ru.rsreu.astrukov.bool;

import de.tesis.dynaware.grapheditor.GraphEditor;
import de.tesis.dynaware.grapheditor.GraphEditorContainer;
import de.tesis.dynaware.grapheditor.core.DefaultGraphEditor;
import de.tesis.dynaware.grapheditor.model.GModel;
import de.tesis.dynaware.grapheditor.model.GraphFactory;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;

import java.net.URL;
import java.util.ResourceBundle;

public class FXMLController implements Initializable {
    @FXML
    private BorderPane modelPane;

    @Override
    public void initialize(URL url, ResourceBundle rb) {


        GraphEditor graphEditor = new DefaultGraphEditor();
        GraphEditorContainer graphEditorContainer = new GraphEditorContainer();

        GModel model = GraphFactory.eINSTANCE.createGModel();
//        model.setContentWidth(1600);
//        model.setContentHeight(1200);

        graphEditor.setModel(model);
        NodeManager.addNodes(model);

        graphEditorContainer.setGraphEditor(graphEditor);
        graphEditorContainer.getMinimap().setVisible(true);

        Region graphView = graphEditor.getView();
        graphView.setBorder(new Border(
                new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, null, null)
        ));

        modelPane.setCenter(graphEditorContainer);
    }
}
