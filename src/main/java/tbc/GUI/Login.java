package tbc.GUI;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import tbc.client.Client;

/**
 * The first GUI window, the login window. After a client is set, this window will open and
 * the username has been set. You can't change your username here, but later on in the lobby
 * or in the game window.
 */
public class Login extends Application {

    private static final Logger LOGGER = LogManager.getLogger(Login.class);

    @Override
    public void start(Stage primaryStage) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("LoginFXML.fxml"));
            Parent root = loader.load();

            primaryStage.setTitle("The Bachelor's Chase - Login");
            primaryStage.setScene(new Scene(root, 398, 581));
            primaryStage.show();

            LoginController loginController = loader.getController();
            loginController.setUserName(Client.userName);
        } catch (Exception e) {
            LOGGER.error("Couldn't find LoginFXML file.");
            e.printStackTrace();
        }
        primaryStage.setOnCloseRequest(e -> Client.clientHandler.logOut());
    }

    public static void main(String[] args) {
        launch(args);
    }

}
