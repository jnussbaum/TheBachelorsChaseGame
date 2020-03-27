package GUI;

import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.layout.BorderPane;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;

public class LobbyController extends Application {
    @FXML private Circle circleCards, circleRules, circleStart, circleGoal, circleSettings;
    @FXML private BorderPane window;
    @FXML private TextArea textArea;
    @FXML private Button closeBtn;

    public static void main(String[] args) {
        Application.launch(args);
    }

    public void showCards() {
        if (circleCards.isPressed()) {
            System.out.println("Show cards.");

        }
    }

    public void showRules() {
        if (circleRules.isPressed()) {
            System.out.println("Show rules.");
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
    }

    public void showLogin() {
        if (circleStart.isPressed()) {
            System.out.println("Show login.");
        }
    }

    public void showGoal() {
        if (circleGoal.isPressed()) {
            System.out.println("Show goal.");
            window.setVisible(true);
            textArea.setText("Ziel: \nZiel des Spiels ist es 180  Kreditpunkte zu erzielen. "
                + "In jeder Runde darf der Spieler entscheiden ob er eine Karte ziehen, "
                + "eine Karte wegschmeissen oder diese Runde aussetzen möchte. "
                + "Hat ein Spieler über 180 Kreditpunkte erzielt, "
                + "hat er verloren und bekommt 0 Coins.");
        }
    }

    public void showSettings() {
        if (circleSettings.isPressed()) {
            System.out.println("Show settings.");
        }
    }

    public void closeBTN() {
        if (closeBtn.isPressed()) {
            System.out.println("Close current window.");
            closeBtn.setOnAction(event ->
                textArea.clear()
            );
            window.setVisible(false);
        }
    }

  @Override
  public void start(Stage primaryStage) throws Exception {
      primaryStage.setTitle("The Bachelor's Chase");
      /*
      FXMLLoader loader = new FXMLLoader();
      loader.setLocation(getClass().getResource("LobbyFXML.fxml"));
      try {
          loader.load();
      } catch (Exception e) {
          System.out.println("Couldn't find LobbyFXML file.");
      }
       */
      Parent root = FXMLLoader.load(getClass().getResource("LobbyFXML.fxml"));
      Scene scene = new Scene(root, 640, 480);
      primaryStage.setScene(scene);
      primaryStage.show();
  }
}
