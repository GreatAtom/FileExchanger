package ru.fileexchanger.server;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import ru.fileexchanger.server.controls.Controller;
import ru.fileexchanger.server.model.socket.Server;

import java.util.List;
import java.util.concurrent.*;

public class ServerMain extends Application {

    private static Server server;
    private static Controller controller;

    public static void setController(Controller controller) {
        ServerMain.controller = controller;
    }

    public static void updateListView(String str) {
        Platform.runLater(() -> controller.updateListView(str));
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getClassLoader().getResource("views/main.fxml"));
        primaryStage.setTitle("ServerExchange");
        primaryStage.setScene(new Scene(root, 330, 250));
        primaryStage.show();
    }

    public static void startServer() {
        log("Server start...");

        BlockingQueue<Runnable> queue = new SynchronousQueue<>();
        ExecutorService executorService = new ThreadPoolExecutor(2, 2,
                0L, TimeUnit.MILLISECONDS,
                queue);

        server = new Server(8989);
        server.start();
    }

    public synchronized static void log(String log) {
        ServerMain.updateListView(log);
        System.out.println(log);
    }

    public static void stopServer() {
        server.closeServer();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
