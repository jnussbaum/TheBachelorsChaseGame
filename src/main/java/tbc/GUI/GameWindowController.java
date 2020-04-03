package tbc.GUI;

import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import tbc.client.Client;

/**
 * Processes a client's request to start the Game. Here it will be shown all the graphical
 * components of the game logic, which makes it possible for the clients to play the game.
 */

public class GameWindowController {

    @FXML private TableView highscore;
    @FXML private TableColumn highscoreName, highscoreCoins;
    @FXML private TextField msgField;
    @FXML private TextArea txtAChat;

    public void sendMsg() {
        Client.chatClient.processInput(msgField.getText());
        msgField.clear();
    }

    public void appendMsg(String msg) {
        txtAChat.appendText(msg + "\n");
    }

}
