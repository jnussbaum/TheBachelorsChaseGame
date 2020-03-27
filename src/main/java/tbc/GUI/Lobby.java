package tbc.GUI;

import java.io.File;
import java.net.URL;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Lobby extends Application {
    @Override
    public void start(Stage primaryStage) {
        try {
            URL lobbyFxmlUrl = new File("src/main/java/resources/LobbyFXML.fxml").toURI().toURL();
            Parent root = FXMLLoader.load(lobbyFxmlUrl);
            primaryStage.setTitle("The Bachelor's Chase");
            primaryStage.setScene(new Scene(root, 1000, 600));
            primaryStage.show();
        } catch (Exception e) {
            System.out.println("Couldn't find LobbyFXML file.");
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
