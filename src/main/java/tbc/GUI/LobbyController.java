package tbc.GUI;

import java.io.File;
import java.net.URL;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;

public class LobbyController {
    @FXML private Circle circleCards, circleRules, circleStart, circleGoal, circleSettings;
    @FXML private BorderPane window, loginWindow;
    @FXML private TextArea textArea;
    @FXML private TextField userName;
    @FXML private Button closeBtn, okBtn;
    @FXML private Label labelStatus;

    public void showCards() {
        circleCards.setOnMouseClicked(event -> {
            System.out.println("Show cards.");
            loginWindow.setVisible(false);
        });
    }

    public void showRules() {
        circleRules.setOnMouseClicked(event -> {
            System.out.println("Show rules.");
            loginWindow.setVisible(false);
            window.setVisible(true);
            textArea.setText("Spielbeschreibung: "
                + "\nJeder Spieler erhält am Anfang des Spieles eine zufällige Karte. "
                + "In jeder Runde muss der Spieler innerhalb von 10 Sekunden entscheiden, "
                + "ob er eine Karte ziehen, eine Karte wegschmeissen (nur gegen Coins möglich) "
                + "oder diese eine Runde aussetzen möchte bzw. keine Karte ziehen."
                + "\n\nRegeln: "
                + "\nCoinsystem: \nJeder Spieler hat ein Konto, "
                + "das zur Beginn der ersten Runde noch leer ist. "
                + "Nachdem ein Spieler 180 KP erzielt hat, werden die Coins folgendermasse verteilt: "
                + "\nDer Spieler mit 180 KP erhält 360 Coins. "
                + "\nDiejenigen Spieler mit über 180 KP erhalten 0 coins. "
                + "\nAlle anderen Spieler erhalten soviele Coins wie ihre Summe der Kartenaugenzahl."
                + "\nDiese Coins werden nach Beendigung einer Spielrunde, "
                + "das heisst nachdem ein Spieler 180 KP erreicht hat, "
                + "aufsummiert und kann somit bei der nächsten Spielrunde verwendet werden "
                + "(z.B. um eine Karte wegzuschmeissen)");
        });

    }

    public void showLogin() {
        circleStart.setOnMouseClicked(event -> {
            window.setVisible(false);
            System.out.println("Show login.");
            loginWindow.setVisible(true);
            userName.setText("Is your name " + System.getProperty("user.name")
                + "? If no, please enter a name.");
        });
    }

    public void showGoal() {
        circleGoal.setOnMouseClicked(event -> {
            loginWindow.setVisible(false);
            System.out.println("Show goal.");
            window.setVisible(true);
            textArea.setText("Ziel: \nZiel des Spiels ist es 180  Kreditpunkte zu erzielen. "
                + "In jeder Runde darf der Spieler entscheiden ob er eine Karte ziehen, "
                + "eine Karte wegschmeissen oder diese Runde aussetzen möchte. "
                + "Hat ein Spieler über 180 Kreditpunkte erzielt, "
                + "hat er verloren und bekommt 0 Coins.");
        });
    }

    public void showSettings() {
        circleSettings.setOnMouseClicked(event -> {
           loginWindow.setVisible(false);
           System.out.println("Show settings.");
        });
    }

    public void checkName() {
        okBtn.setOnMouseClicked(event -> {
            System.out.println("Check name");
            if (!userName.getText().isEmpty()) {
                String userName_ = userName.getText();
                System.out.println(userName.getText() + " tries to login");
                try {
                    URL lobbyFxmlUrl = new File("src/main/java/resources/GameWindowController.fxml").toURI().toURL();
                    FXMLLoader loader = new FXMLLoader(lobbyFxmlUrl);

                    System.out.println(userName_ + " has entered the room.");
                    GameWindowController loginGameWindow = loader.getController();
                    loginGameWindow.getUserName(userName_);

                    Parent root = FXMLLoader.load(lobbyFxmlUrl);
                    Stage mainGameWindow = (Stage) ((Node)event.getSource()).getScene().getWindow();
                    mainGameWindow.setScene(new Scene(root));
                    mainGameWindow.setTitle("The Bachelor's Chase");
                    mainGameWindow.show();
                } catch (Exception e) {
                    System.out.println("Couldn't find GameWindowController file.");
                    e.printStackTrace();
                }
            } else {
                labelStatus.setText("Username nicht verfügbar.");
            }
        });
    }

    public void close() {
        closeBtn.setOnAction(event -> {
            System.out.println("Close current window.");
            textArea.clear();
            window.setVisible(false);
        });
    }
}
