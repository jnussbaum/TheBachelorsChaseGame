package tbc.GUI;

import java.io.File;
import java.net.URL;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class LoginController {

    @FXML private TextField userName;
    @FXML private Label labelStatus;
    public static String userName_;

    public void checkName(ActionEvent event) {

        if (userName.getText().isEmpty()) {
            userName.setPromptText("Ist dein Name " + System.getProperty("user.name") + "?");
            labelStatus.setText("Bitte tippe deinen Namen ein.");
        } else {
            userName_ = userName.getText();
            try {
                URL loginFxmlUrl = new File("src/main/java/resources/LobbyFXML.fxml").toURI().toURL();
                Parent root = FXMLLoader.load(loginFxmlUrl);
                Stage lobbyWindow = (Stage) ((Node)event.getSource()).getScene().getWindow();
                lobbyWindow.setTitle("The Bachelor's Chase - Lobby");
                lobbyWindow.setScene(new Scene(root, 1000, 600));
                lobbyWindow.show();
                System.out.println(userName_ + " has logged in.");

            } catch (Exception e) {
                System.out.println("Couldn't find LobbyFXML file.");
                e.printStackTrace();
            }
        }
    }

}
