package main;

import data.DatabaseManager;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import views.MainView;
import java.net.URL;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) {
        DatabaseManager.initialize();

        MainView mainView = new MainView();
        Scene scene = new Scene(mainView.getRoot(), 1100, 720);

        // ── Force dark background on scene itself ─────────
        scene.setFill(Color.web("#111827"));

        URL css = getClass().getResource("/styles/style.css");
        if (css != null) {
            scene.getStylesheets().add(css.toExternalForm());
        } else {
            System.out.println("CSS not found — check resources/styles/style.css");
        }

        primaryStage.setTitle("✅ TaskFlow — Task Management");
        primaryStage.setScene(scene);
        primaryStage.setMinWidth(900);
        primaryStage.setMinHeight(600);
        primaryStage.setOnCloseRequest(e -> DatabaseManager.close());
        primaryStage.show();
    }

    public static void main(String[] args) { launch(args); }
}
