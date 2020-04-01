package tbc.GUI;

import java.io.File;
import java.net.URL;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * The GUI for Clients to enter they username
 */

public class Login extends Application {

    private static final Logger logger = LogManager.getLogger();

    private static String userName;

    @Override
    public void start(Stage primaryStage) {
        try {
            URL loginFxmlUrl = new File("src/main/java/resources/LoginFXML.fxml").toURI().toURL();
            Parent root = FXMLLoader.load(loginFxmlUrl);
            primaryStage.setTitle("The Bachelor's Chase - Login");
            primaryStage.setScene(new Scene(root, 398, 581));
            primaryStage.show();
        } catch (Exception e) {
            logger.error("Couldn't find LoginFXML file.");
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        if (!args[2].isEmpty()) {
            userName = args[2];
        }

        launch(args);
    }
}
