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
import tbc.client.Client;

/**
 * The GUI for Clients to enter they username
 */

public class Login extends Application {

    private final static Logger LOGGER = LogManager.getLogger(Login.class);

    @Override
    public void start(Stage primaryStage) {
        try {
            URL loginFxmlUrl = new File("src/main/resources/LoginFXML.fxml").toURI().toURL();
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(loginFxmlUrl);
            Parent root = loader.load();

            primaryStage.setTitle("The Bachelor's Chase - Login");
            primaryStage.setScene(new Scene(root, 398, 581));
            primaryStage.show();

            LoginController loginController = loader.getController();
            loginController.setUserName(Client.myName);

        } catch (Exception e) {
            LOGGER.error("Couldn't find LoginFXML file.");
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
