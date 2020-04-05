package tbc.GUI;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import tbc.client.Client;

/**
 * Processes a client's request to start the Game. Here it will show all the graphical
 * components of the game logic, which makes it possible for the clients to play the game.
 */
public class GameWindowController {

    private static final Logger LOGGER = LogManager.getLogger(GameWindowController.class);

    @FXML public TableView highscoreTable;
    @FXML public TableColumn highscoreName;
    @FXML public TableColumn highscoreCoins;
    @FXML public TextField msgField;
    @FXML private TextArea txtAChat;
    @FXML public Button btnSend;

    public void sendMsg() {
        Client.chatClient.processInput(msgField.getText());
        msgField.clear();
    }

    public void appendMsg(String msg) {
        txtAChat.appendText(msg + "\n");
    }

    public void changeUsername() {
        LOGGER.info("Show change name window.");
        ChangeNameWindow.display();
    }

    public void logOut() {
        LOGGER.info("Show Confirm Box");
        ConfirmBox.display();
    }

    public void giveInfo() {
        LOGGER.info("Show game info.");
        GameInfoWindow.display();
    }

    public void giveChatInfo() {
        LOGGER.info("Show chat info.");
        ChatInfoWindow.display();
    }

}
