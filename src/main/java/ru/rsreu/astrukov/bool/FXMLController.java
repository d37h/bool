package ru.rsreu.astrukov.bool;

import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import kotlin.ExperimentalStdlibApi;
import ru.rsreu.astrukov.bool.service.DoubleClickHandler;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.function.Predicate;

import static ru.rsreu.astrukov.bool.service.BoolFuncMockKt.getBf;

@ExperimentalStdlibApi
public class FXMLController implements Initializable {

    @FXML
    private BorderPane graphicsPane;

    @FXML
    private TableColumn<String, String> singleSetsTableColumn;
    @FXML
    private TableColumn<String, String> masksTableColumn;
    @FXML
    private TableColumn<String, String> variablesTableColumn;

    @FXML
    private TableView<String> singleSetsTable;
    @FXML
    private TableView<String> masksTable;
    @FXML
    private TableView<String> variablesTable;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        singleSetsTableColumn.setCellValueFactory(param -> new ReadOnlyStringWrapper(param.getValue()));
        masksTableColumn.setCellValueFactory(param -> new ReadOnlyStringWrapper(param.getValue()));
        variablesTableColumn.setCellValueFactory(param -> new ReadOnlyStringWrapper(param.getValue()));


        singleSetsTable.getItems().add("");
        masksTable.getItems().add("");
        variablesTable.getItems().add("");
//        singleSetsTable.getItems().add("101100");
//        singleSetsTable.getItems().add("101010");
//        singleSetsTable.getItems().add("101110");
//        singleSetsTable.getItems().add("010100");
//
//        singleSetsTable.getItems().add("110100");
//        singleSetsTable.getItems().add("010101");
//        singleSetsTable.getItems().add("110101");
//        singleSetsTable.getItems().add("110010");
//        singleSetsTable.getItems().add("111010");
//
//        masksTable.getItems().add("101--0");
//        masksTable.getItems().add("-1010-");
//        masksTable.getItems().add("11-010");
//
//        variablesTable.getItems().add("x1");
//        variablesTable.getItems().add("x2");
//        variablesTable.getItems().add("x3");
//        variablesTable.getItems().add("x4");
//        variablesTable.getItems().add("x5");
//        variablesTable.getItems().add("x6");

        getBf(graphicsPane);

        singleSetsTable.setOnMouseClicked(new AddRowEventHandler(singleSetsTable));
    }
}

class AddRowEventHandler extends DoubleClickHandler {
    private final TableView<String> tableView;

    public AddRowEventHandler(TableView<String> table) {
        this.tableView = table;
    }

    @Override
    public void performAction() {
        tableView.getItems().add("");
    }
}


class DeleteRowEventHandler implements EventHandler<KeyEvent> {
    private final TableView<String> tableView;

    public DeleteRowEventHandler(TableView<String> table) {
        this.tableView = table;
    }

    @Override
    public void handle(KeyEvent event) {
        if (event.getCode() == KeyCode.DELETE) {
           var indices =  tableView.getSelectionModel().getSelectedIndices();
//            tableView.getItems().remove
        }
    }
}