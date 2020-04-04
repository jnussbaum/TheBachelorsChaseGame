package tbc.GUI;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class GameInfoWindow {

    private static final Logger LOGGER = LogManager.getLogger(GameInfoWindow.class);

    public static void display() {
        Stage window = new Stage();

        window.initModality(Modality.APPLICATION_MODAL);
        window.setTitle("Game Info");
        window.setMinWidth(400);

        TextArea textArea = new TextArea();
        textArea.setPrefSize(700, 500);
        textArea.setEditable(false);
        textArea.setFocusTraversable(false);
        textArea.wrapTextProperty();
        textArea.setText("Spielbeschreibung:"
            + "\nJeder Spieler erhält am Anfang des Spieles eine zufällige Karte."
            + "In jeder Runde muss der Spieler innerhalb von 10 Sekunden entscheiden,"
            + "ob er eine Karte ziehen, eine Karte wegschmeissen (nur gegen Coins möglich)"
            + "oder diese eine Runde aussetzen möchte bzw. keine Karte ziehen.\n"
            + "\nRegeln: "
            + "\nCoinsystem: \nJeder Spieler hat ein Konto, "
            + "das zur Beginn der ersten Runde noch leer ist. "
            + "Nachdem ein Spieler 180 KP erzielt hat, werden die Coins folgendermasse verteilt:"
            + "\nDer Spieler mit 180 KP erhält 360 Coins."
            + "\nDiejenigen Spieler mit über 180 KP erhalten 0 coins."
            + "\nAlle anderen Spieler erhalten soviele Coins wie ihre Summe der Kartenaugenzahl."
            + "\nDiese Coins werden nach Beendigung einer Spielrunde, "
            + "das heisst nachdem ein Spieler 180 KP erreicht hat, "
            + "aufsummiert und kann somit bei der nächsten Spielrunde verwendet werden "
            + "(z.B. um eine Karte wegzuschmeissen)"
            + "\nZiel: \nZiel des Spiels ist es 180 Kreditpunkte zu erzielen. "
            + "In jeder Runde darf der Spieler entscheiden ob er eine Karte ziehen, "
            + "eine Karte wegschmeissen oder diese Runde aussetzen möchte. "
            + "Hat ein Spieler über 180 Kreditpunkte erzielt, "
            + "hat er verloren und bekommt 0 Coins.");

        Button close = new Button("Schliessen");
        close.setOnAction(e -> {
            Stage stage = (Stage) close.getScene().getWindow();
            stage.close();
            LOGGER.info("Closed game info window.");
        });

        VBox layout = new VBox(10);
        layout.setPadding(new Insets(10, 10, 10, 10));
        layout.getChildren().addAll(textArea, close);
        layout.setAlignment(Pos.CENTER);

        Scene scene = new Scene(layout, 700, 500);
        window.setScene(scene);
        window.showAndWait();
    }

}
