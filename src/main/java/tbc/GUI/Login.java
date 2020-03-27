package tbc.GUI;

import java.io.File;
import java.net.URL;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Login extends Application {

    @Override
    public void start(Stage primaryStage) {
        try {
            URL loginFxmlUrl = new File("src/main/java/resources/LoginFXML.fxml").toURI().toURL();
            Parent root = FXMLLoader.load(loginFxmlUrl);
            primaryStage.setTitle("The Bachelor's Chase - Login");
            primaryStage.setScene(new Scene(root, 398, 581));
            primaryStage.show();
        } catch (Exception e) {
            System.out.println("Couldn't find LoginFXML file.");
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
