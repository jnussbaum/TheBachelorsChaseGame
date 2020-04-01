package tbc.GUI;

import java.io.File;
import java.net.URL;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

/**
 * Processes a client's request to login with a username. First the system-name will be requested
 * and than the process to find a legit username will begin.
 */

public class LoginController {

    private static final Logger logger = LogManager.getLogger();

    @FXML private TextField userName;
    @FXML private Label labelStatus, labelGirly, labelNerd, labelEmo, labelHippy;
    public static String userName_;
    public static String chosenLogo;

    public void checkName(ActionEvent event) {
        if (userName.getText().isEmpty()) {
            userName.setPromptText("Ist dein Name " + System.getProperty("user.name") + "?");
            labelStatus.setText("Bitte tippe deinen Namen ein.");
        } else if (logoChosen() == false) {
            labelStatus.setText("Bitte wähle einen Logo aus.");
        } else {
            userName_ = userName.getText();
            try {
                URL loginFxmlUrl = new File("src/main/java/resources/LobbyFXML.fxml").toURI().toURL();
                Parent root = FXMLLoader.load(loginFxmlUrl);
                Stage lobbyWindow = (Stage) ((Node)event.getSource()).getScene().getWindow();
                lobbyWindow.setTitle("The Bachelor's Chase - Lobby");
                lobbyWindow.setScene(new Scene(root, 1000, 600));
                lobbyWindow.show();
                logger.info(userName_ + " has logged in.");
                logger.info(userName_ + " has chosen the logo: " + chosenLogo);
            } catch (Exception e) {
                logger.error("Couldn't find LobbyFXML file.");
                e.printStackTrace();
            }
        }
    }

    public void girly() {
        logger.info("Girly has been chosen.");
        chosenLogo = "girly";
        labelNerd.setFont(Font.font(24));
        labelEmo.setFont(Font.font(24));
        labelHippy.setFont(Font.font(24));
        labelGirly.setFont(Font.font(36));
    }

    public void nerd() {
        logger.info("Nerd has been chosen.");
        chosenLogo = "nerd";
        labelGirly.setFont(Font.font(24));
        labelEmo.setFont(Font.font(24));
        labelHippy.setFont(Font.font(24));
        labelNerd.setFont(Font.font(36));
    }

    public void hippy() {
        logger.info("Hippy has been chosen.");
        chosenLogo = "hippy";
        labelGirly.setFont(Font.font(24));
        labelEmo.setFont(Font.font(24));
        labelNerd.setFont(Font.font(24));
        labelHippy.setFont(Font.font(36));
    }

    public void emo() {
        logger.info("Emo has been chosen.");
        chosenLogo = "emo";
        labelGirly.setFont(Font.font(24));
        labelNerd.setFont(Font.font(24));
        labelHippy.setFont(Font.font(24));
        labelEmo.setFont(Font.font(36));
    }

    private boolean logoChosen() {
        if (labelGirly.getFont().getSize() == 36 || labelNerd.getFont().getSize() == 36
            || labelHippy.getFont().getSize() == 36 || labelEmo.getFont().getSize() == 36) {
            return true;
        } else {
            return false;
        }
    }
}
