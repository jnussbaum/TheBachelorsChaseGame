package tbc.GUI;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

/**
 * Processes a client's request to start the Game. Here it will be shown all the graphical
 * components of the game logic, which makes it possible for the clients to play the game.
 */

public class GameWindowController {

    @FXML private TableView highscore;
    @FXML private TableColumn highscoreName, highscoreCoins;

    public void sendMsg(ActionEvent actionEvent) {
    }

}
