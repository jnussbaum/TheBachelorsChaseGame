package tbc.GUI;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import tbc.client.Client;

import static tbc.client.Client.clientHandler;

/**
 * If the circle "Various" in the Lobby window is pressed,
 * it will open a new window "The Bachelor's Chase - Various".
 * In this window you can change your username, get the player and lobby list.
 */
public class VariousWindowController {

    private static final Logger LOGGER = LogManager.getLogger(VariousWindowController.class);

    @FXML
    public TextField newUsername;
    @FXML
    private Label usernameStatus;
    @FXML
    private Button btnClose;
    @FXML
    public Button btnEnter;

    public void checkNewUsername() {
        String clientName = newUsername.getText();
        newUsername.clear();

        clientHandler.changeName(clientName);

        Thread thread = new Thread(() -> {
            Runnable updater = () -> {
                usernameStatus.setText("New Name: " + Client.clientHandler.getMyName());
            };

            while (true) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException ex) {
                }

                // UI update is run on the Application thread
                Platform.runLater(updater);
            }
        });
        // don't let thread prevent JVM shutdown
        thread.setDaemon(true);
        thread.start();
    }

    public void showPlayerList() {
        LOGGER.info("Show playerlist");
        PlayerList.display();
    }

    public void showLobbyList() {
        LOGGER.info("Show lobbylist");
        LobbyList.display();
    }

    public void closeVariousWindow() {
        Stage stage = (Stage) btnClose.getScene().getWindow();
        stage.close();
        LOGGER.info("Closed various window.");
    }

}

