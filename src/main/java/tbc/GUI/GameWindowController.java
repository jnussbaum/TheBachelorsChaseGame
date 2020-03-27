package tbc.GUI;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class GameWindowController {

    @FXML private Label userName;

    public void getUserName(String userName) {
        this.userName.setText(userName);
    }
}
