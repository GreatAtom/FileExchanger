package ru.fileexchanger.server.controls;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseEvent;
import ru.fileexchanger.server.ServerMain;

public class Controller {
    @FXML
    private Button startServerButton;
    @FXML
    private Button stopServerButton;
    @FXML
    private javafx.scene.control.Button closeButton;
    @FXML
    private ListView<String> logView;

    @FXML
    public void initialize() {
        ServerMain.setController(this);
        stopServerButton.setDisable(true);

        startServerButton.addEventHandler(MouseEvent.MOUSE_CLICKED, mouseEvent -> {
            startServerButton.setDisable(true);
            stopServerButton.setDisable(false);
            ServerMain.startServer();
        });

        stopServerButton.addEventHandler(MouseEvent.MOUSE_CLICKED, mouseEvent -> {
            ServerMain.stopServer();
            startServerButton.setDisable(false);
            stopServerButton.setDisable(true);
        });
    }

    public void updateListView(String str) {
        logView.getItems().add(str);
    }
}

