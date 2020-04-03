package tbc.GUI;

import static tbc.client.Client.clientHandler;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import tbc.client.Client;

public class DiversController {

    private static final Logger LOGGER = LogManager.getLogger(DiversController.class);

    @FXML private TextField newUsername;
    @FXML private static Label usernameStatus;
    @FXML private Button btnClose;

    public void checkNewUsername() {
        LOGGER.info("DiversController.checkNewUsername: alt " + Client.userName);

        if (newUsername.getText().isEmpty()) {
            usernameStatus.setText("Gib einen neuen Username ein.");
        } else {
            String newName = newUsername.getText();
            String oldName = Client.userName;
            LOGGER.info("Name im Feld: " + newName);
            newUsername.clear();

            clientHandler.changeName(newName);

            if (oldName.equals(newName) == false) {
                usernameStatus.setText("new Name: " + newName);
            }
        }
    }
    /*
    public void setStatus(String statusText){
        System.out.println("DiversController.setStatus          test, statusText = "+statusText+", isVisible = "+usernameStatus.isVisible());
        usernameStatus.setVisible(true);
        usernameStatus.setText(statusText);
        System.out.println("DiversController.setStatus          usernameStatus = " + usernameStatus.getText());
    }
     */

    public void showPlayerList(ActionEvent actionEvent) {
    }

    public void showLobbyListe(ActionEvent actionEvent) {
    }

    public void closeDiversWindow() {
        Stage stage = (Stage) btnClose.getScene().getWindow();
        stage.close();
        System.out.println("Closed divers window.");
    }

}
