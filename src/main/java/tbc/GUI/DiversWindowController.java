package tbc.GUI;

import static tbc.client.Client.clientHandler;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import tbc.client.Client;

public class DiversWindowController {

    private static final Logger LOGGER = LogManager.getLogger(DiversWindowController.class);

    @FXML public TextField newUsername;
    @FXML private static Label usernameStatus;
    @FXML private Button btnClose;
    @FXML public Button btnEnter;

    public void checkNewUsername() {
        LOGGER.info("DiversController.checkNewUsername: alt " + Client.userName);

        String clientName = newUsername.getText();
        newUsername.clear();

        clientHandler.changeName(clientName);

        /*
        Platform.runLater(() -> {
            if (oldName.equals(clientName) == false) {
                System.out.println("DiversWindowController.checkNewUsername     oldname = " + oldName);
                System.out.println("DiversWindowController.checkNewUsername     clientname = " + clientName);
                usernameStatus.setText("new Name: " + clientName);
            }
        });*/
    }

    /*
    public void setUsernameStatus(String clientName, String oldName) {
        if (oldName.equals(clientName) == false) {
            usernameStatus.setText("new Name: " + clientName);
        }
    }
    */

    /*
    public void setStatus(String statusText){
        System.out.println("DiversController.setStatus          test, statusText = "+statusText+", isVisible = "+usernameStatus.isVisible());
        usernameStatus.setVisible(true);
        usernameStatus.setText(statusText);
        System.out.println("DiversController.setStatus          usernameStatus = " + usernameStatus.getText());
    }*/

    public void showPlayerList() {
        LOGGER.info("Show playerlist");
        PlayerList.display();
    }

    public void showLobbyList() {
        LOGGER.info("Show lobbylist");
        LobbyList.display();
    }

    public void closeDiversWindow() {
        Stage stage = (Stage) btnClose.getScene().getWindow();
        stage.close();
        LOGGER.info("Closed divers window.");
    }
}
