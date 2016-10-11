package main;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import main.controls.Controller;
import main.model.socket.Server;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.*;

public class Main extends Application {

    private static Server server;
    private static Controller controller;

    public static void setController(Controller controller) {
        Main.controller = controller;
    }

    public static void updateListView(List<String> list) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                controller.updateListView(list);
            }
        });
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("views/main.fxml"));
        primaryStage.setTitle("ServerExchange");
        primaryStage.setScene(new Scene(root, 330, 250));
        primaryStage.show();
    }

    public static void startServer() {
        System.out.println("Server start...");

        BlockingQueue<Runnable> queue = new SynchronousQueue<>();
        ExecutorService executorService = new ThreadPoolExecutor(2, 2,
                0L, TimeUnit.MILLISECONDS,
                queue);

        server = new Server(8989);
        server.start();
    }

    public static void stopServer() {
        server.closeServer();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
