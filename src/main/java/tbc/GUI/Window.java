package tbc.GUI;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class Window extends Application {
    public static void main(String[] args) {
      Application.launch(args);
    }

    @Override
    public void start(Stage stage) {
        Label l = new Label("Testing ");
        Scene scene = new Scene(new StackPane(l), 640, 480);
        stage.setScene(scene);
        stage.show();
    }
}
