package tbc.gui;

import static tbc.client.Client.clientHandler;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import tbc.client.Client;

/**
 * If the circle "Various" in the Lobby window is pressed, it will open a new window "The Bachelor's
 * Chase - Various". In this window you can change your username, get the player and lobby list.
 */
public class VariousWindowController {

    private static final Logger LOGGER = LogManager.getLogger(VariousWindowController.class);
    @FXML
    public TextField newUsername;
    @FXML
    public Button btnEnter;
    @FXML
    private Label usernameStatus;
    @FXML
    private Button btnClose;

    /**
     * If the button 'Enter' is pressed it will get the text from the field and checks if the new
     * username is available. If the username is not available anymore, it will be set from the
     * system automatically.
     */
    public void checkNewUsername() {
        String clientName = newUsername.getText();
        newUsername.clear();

        clientHandler.changeName(clientName);

        Thread thread = new Thread(() -> {
            Runnable updater = () -> usernameStatus
                .setText("New Name: " + Client.clientHandler.getMyName());

            while (true) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException ex) {
                    LOGGER.error("InterruptedException in the class VariousWindowController.");
                }
                // UI update is run on the Application thread
                Platform.runLater(updater);
            }
        });
        // don't let thread prevent JVM shutdown
        thread.setDaemon(true);
        thread.start();
    }

    /**
     * If the user presses on 'Playerlist', this method will call up the display() method from the
     * class PlayerList. It opens a window to show a list of the current players.
     */
    public void showPlayerList() {
        LOGGER.info("Show playerlist");
        PlayerList.display();
    }

    /**
     * If the user presses on 'Lobbylist', this method will call up the display() method from the
     * class LobbyList. It opens a window to show a list of the current lobbies.
     */
    public void showLobbyList() {
        LOGGER.info("Show lobbylist");
        LobbyList.display();
    }

    /**
     * If the button 'Close' is pressed, it closes this window.
     */
    public void closeVariousWindow() {
        Stage stage = (Stage) btnClose.getScene().getWindow();
        stage.close();
        LOGGER.info("Closed various window.");
    }

}

