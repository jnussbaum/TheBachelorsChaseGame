package tbc.GUI;

import javafx.event.ActionEvent;
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
    @FXML public static TextArea txtAChat;

    /*
    wie kann man das txtAChat objekt in eine andere Klasse (außerhalb der Controller-Klasse) bringen?
    oder: gibt es für die Controller Klasse eine update() Methode die kontinuierlich aufgerufen wird?
    FXML tutorials
     */

    /*
    public void update(){
        txtAChat.appendText(Client.chatClient.getText());
    }*/

    public void sendMsg(ActionEvent actionEvent) {
        Client.chatClient.processInput(msgField.getText());
        System.out.println("GameWindowController.sendMsg" + msgField.getText());
        msgField.clear();
    }

    public void appendMsg(String msg) {
        System.out.println(msg);
        //TODO NullPointerException. Gibt die message nicht im GUI Fenster aus.
        txtAChat.appendText(msg);
    }

}
