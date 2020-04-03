package tbc.GUI;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextArea;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import tbc.client.Client;

/**
 * Processes a client's request to get the informations of the pushed Button.
 * It opens a window for almost every Button and shows the details about the Game.
 */

public class LobbyController {

    private static final Logger LOGGER = LogManager.getLogger();

    @FXML private BorderPane window;
    @FXML private TextArea textArea;
    public static GameWindowController gameWindowController;

    public void startGame(MouseEvent mouseEvent) {
        LOGGER.info("Show game window.");
        try {
            Stage gameWindow = (Stage) ((Node)mouseEvent.getSource()).getScene().getWindow();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("GameWindowFXML.fxml"));
            Parent root = loader.load();

            gameWindow.setTitle("The Bachelor's Chase");
            gameWindow.setScene(new Scene(root, 1000, 600));
            gameWindow.show();

            gameWindowController = loader.getController();
            gameWindowController.appendMsg(Client.userName + " has entered the chat.\n");
        } catch (Exception e) {
            LOGGER.error("Couldn't find GameWindowFXML file.");
            e.printStackTrace();
        }
    }

    public void showCards() {
        // TODO show the cards if clicked on it
        LOGGER.info("Show cards.");
    }

    public void showRules() {
        LOGGER.info("Show rules.");
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
    }

    public void showGoal() {
        LOGGER.info("Show goal.");
        window.setVisible(true);
        textArea.setText("Ziel: \nZiel des Spiels ist es 180  Kreditpunkte zu erzielen. "
            + "In jeder Runde darf der Spieler entscheiden ob er eine Karte ziehen, "
            + "eine Karte wegschmeissen oder diese Runde aussetzen möchte. "
            + "Hat ein Spieler über 180 Kreditpunkte erzielt, "
            + "hat er verloren und bekommt 0 Coins.");
    }

    public void showSettings() {
        // TODO maybe a feature...
        LOGGER.info("Show settings.");
    }

    public void close() {
        LOGGER.info("Close current window.");
        textArea.clear();
        window.setVisible(false);
    }

}
