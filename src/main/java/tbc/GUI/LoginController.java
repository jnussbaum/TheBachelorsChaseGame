package tbc.GUI;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import tbc.client.Client;

/**
 * Processes a client's request to login with a username. First the system-name will be requested
 * and than the process to find a legit username will begin.
 */

public class LoginController implements Initializable {

    private static final Logger LOGGER = LogManager.getLogger();

    @FXML public TextField userName;
    @FXML private Label labelStatus, labelGirly, labelNerd, labelEmo, labelHippy;
    public static String chosenLogo;

    public void setUserName(String name) {
        userName.setText(name);
        System.out.println(userName.getText());
    }

    public void checkName(ActionEvent event) {
        if (logoChosen() == false) {
            labelStatus.setText("Bitte wähle einen Logo aus.");
        } else {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("LobbyFXML.fxml"));
                Parent root =loader.load();
                Stage lobbyWindow = (Stage) ((Node)event.getSource()).getScene().getWindow();
                lobbyWindow.setTitle("The Bachelor's Chase - Lobby");
                lobbyWindow.setScene(new Scene(root, 1000, 600));
                lobbyWindow.show();

                LobbyController lobbyController = loader.getController();

                LOGGER.info(Client.userName + " has logged in.");
                LOGGER.info(Client.userName + " has chosen the logo: " + chosenLogo);

            } catch (Exception e) {
                LOGGER.error("Couldn't find LobbyFXML file.");
                e.printStackTrace();
            }
        }
    }

    public void girly() {
        chosenLogo = "girly";
        labelNerd.setFont(Font.font(24));
        labelEmo.setFont(Font.font(24));
        labelHippy.setFont(Font.font(24));
        labelGirly.setFont(Font.font(36));
    }

    public void nerd() {
        chosenLogo = "nerd";
        labelGirly.setFont(Font.font(24));
        labelEmo.setFont(Font.font(24));
        labelHippy.setFont(Font.font(24));
        labelNerd.setFont(Font.font(36));
    }

    public void hippy() {
        chosenLogo = "hippy";
        labelGirly.setFont(Font.font(24));
        labelEmo.setFont(Font.font(24));
        labelNerd.setFont(Font.font(24));
        labelHippy.setFont(Font.font(36));
    }

    public void emo() {
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

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }
}
