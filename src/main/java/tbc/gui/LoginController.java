package tbc.gui;

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
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import tbc.client.Client;

/**
 * Get the username from the text field in the login window. Before the Lobby window opens it will
 * check if a logo is chosen.
 */
public class LoginController {

    private static final Logger LOGGER = LogManager.getLogger(LoginController.class);
    public static String chosenLogo;
    @FXML
    public TextField userName;
    @FXML
    private Label labelStatus, labelGirly, labelNerd, labelEmo, labelHippy;

    public static String getChosenLogo() {
        return chosenLogo;
    }

    public void setUserName(String name) {
        userName.setText(name);
    }

    /**
     * If the user hasn't choose a logo, the user will be told to do that. If a logo has been chosen
     * the lobby window will open.
     *
     * @param event The event that represents the pressed button.
     */
    public void checkLogo(ActionEvent event) {
        if (logoChosen() == false) {
            labelStatus.setText("Please choose a logo.");
        } else {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("LobbyFXML.fxml"));
                Parent root = loader.load();
                Stage lobbyWindow = (Stage) ((Node) event.getSource()).getScene().getWindow();
                lobbyWindow.setTitle("The Bachelor's Chase - Lobby");
                lobbyWindow.setScene(new Scene(root, 1000, 600));
                lobbyWindow.show();

                LobbyController lobbyController = loader.getController();

                LOGGER.info(Client.userName + " has logged in.");
                LOGGER.info(Client.userName + " has chosen the logo: " + chosenLogo);
                lobbyWindow.setOnCloseRequest(e -> Client.clientHandler.logOut());
            } catch (Exception e) {
                LOGGER.error("Couldn't find LobbyFXML file.");
                e.printStackTrace();
            }

        }

    }

    /**
     * If the logo 'Girly' is pressed, its font will be set to 36 and all fonts of the other logos
     * will be set to 24.
     */
    public void girly() {
        chosenLogo = "girly";
        labelNerd.setFont(Font.font(24));
        labelEmo.setFont(Font.font(24));
        labelHippy.setFont(Font.font(24));
        labelGirly.setFont(Font.font(36));
    }

    /**
     * If the logo 'Nerd' is pressed, its font will be set to 36 and all fonts of the other logos
     * will be set to 24.
     */
    public void nerd() {
        chosenLogo = "nerd";
        labelGirly.setFont(Font.font(24));
        labelEmo.setFont(Font.font(24));
        labelHippy.setFont(Font.font(24));
        labelNerd.setFont(Font.font(36));
    }

    /**
     * If the logo 'Hippy' is pressed, its font will be set to 36 and all fonts of the other logos
     * will be set to 24.
     */
    public void hippy() {
        chosenLogo = "hippy";
        labelGirly.setFont(Font.font(24));
        labelEmo.setFont(Font.font(24));
        labelNerd.setFont(Font.font(24));
        labelHippy.setFont(Font.font(36));
    }

    /**
     * If the logo 'Emo' is pressed, its font will be set to 36 and all fonts of the other logos
     * will be set to 24.
     */
    public void emo() {
        chosenLogo = "emo";
        labelGirly.setFont(Font.font(24));
        labelNerd.setFont(Font.font(24));
        labelHippy.setFont(Font.font(24));
        labelEmo.setFont(Font.font(36));
    }

    /**
     * If one logo is chosen and its font is set to 36, the boolean logoChosen will be set to true.
     *
     * @return True if one logo is chosen.
     */
    private boolean logoChosen() {
        return labelGirly.getFont().getSize() == 36 || labelNerd.getFont().getSize() == 36
            || labelHippy.getFont().getSize() == 36 || labelEmo.getFont().getSize() == 36;
    }

}
