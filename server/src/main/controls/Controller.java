package main.controls;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import main.Main;

import java.util.List;

public class Controller {
    @FXML
    private Button startServerButton;
    @FXML
    private Button stopServerButton;
    @FXML
    private javafx.scene.control.Button closeButton;
    @FXML
    private ListView<String> clientListView;

    @FXML
    public void initialize() {
        Main.setController(this);
        stopServerButton.setDisable(true);

        startServerButton.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                startServerButton.setDisable(true);
                stopServerButton.setDisable(false);
                Main.startServer();
            }
        });

        stopServerButton.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                Main.stopServer();
                startServerButton.setDisable(false);
                stopServerButton.setDisable(true);
            }
        });
    }

    public void updateListView(List<String> list) {
        if (list != null) {
            ObservableList<String> clients = FXCollections.observableArrayList(list);
            clientListView.setItems(clients);
        }
    }
}
