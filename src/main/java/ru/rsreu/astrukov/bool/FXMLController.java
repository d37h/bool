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
import kotlin.ExperimentalStdlibApi;

import java.net.URL;
import java.util.ResourceBundle;

import static ru.rsreu.astrukov.bool.service.BoolFuncMockKt.getBf;

@ExperimentalStdlibApi
public class FXMLController implements Initializable {
    @FXML
    private BorderPane modelPane;

    @FXML
    private BorderPane graphicsPane;

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

        getBf(graphicsPane);


//        BoolElementDrawing DrawParams = new BoolElementDrawing(
//                new Coordinates(10.0, 10.0),
//                null,
//                null,
//                null,
//                1.0,
//                5.0
//        );
//
//        draw(DrawParams, BoolElementType.BIP, graphicsPane);
    }
}
