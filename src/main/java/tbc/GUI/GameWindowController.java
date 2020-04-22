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

    /**
     * If the button send is pressed, it calls the method processInput() with the input from the chat field as parameter.
     * Clears the chat field after the send button is pressed.
     */
    public void sendMsg() {
        Client.chatClient.processInput(msgField.getText());
        msgField.clear();
    }

    /**
     * This method appends the parameter msg to the chat area. The text color of the chat depends on the chosen logo.
     * @param msg The String which will be appended to the chat area.
     */
    public void appendMsg(String msg) {
        String logo = LoginController.getChosenLogo();
        if (logo.equals("girly")) {
            txtAChat.setStyle("-fx-text-fill: red");
        } else if (logo.equals("nerd")) {
            txtAChat.setStyle("-fx-text-fill: green");
        } else if (logo.equals("hippy")) {
            txtAChat.setStyle("-fx-text-fill: yellow");
        } else if (logo.equals("emo")) {
            txtAChat.setStyle("-fx-text-fill: purple");
        }
        txtAChat.appendText(msg + "\n");
    }

    /**
     * If the user presses on Change username in the menubar, this method will call up the display() method
     * from the class ChangeNameWindow. It opens a window where the user can change his/her username.
     */
    public void changeUsername() {
        LOGGER.info("Show change name window.");
        ChangeNameWindow.display();
    }

    /**
     * If the user presses on Logout in the menubar, this method will call up the display() method
     * from the class ConfirmBox. It opens a window where it alerts the user that she/he will logout
     * and asks for a confirmation.
     */
    public void logOut() {
        LOGGER.info("Show Confirm Box");
        ConfirmBox.display();
    }

    /**
     * If the user presses on Info in the menubar, this method will call up the display() method
     * from the class GameInfoWindow. It opens a window where you can see the information of the
     * game (rules and goals).
     */
    public void giveInfo() {
        LOGGER.info("Show game info.");
        GameInfoWindow.display();
    }

    /**
     * If the user presses on Chat Info in the menubar, this method will call up the display() method
     * from the class ChatInfoWindow. It opens a window where you can see the information of the
     * game about chatting. Example: How to send a private message.
     */
    public void giveChatInfo() {
        LOGGER.info("Show chat info.");
        ChatInfoWindow.display();
    }

}
