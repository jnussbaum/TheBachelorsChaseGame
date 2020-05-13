package tbc.gui;

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

/**
 * This class shows a window with information about the game like a description of the game, the
 * rules and the goals.
 */
public class GameInfoWindow {

    private static final Logger LOGGER = LogManager.getLogger(GameInfoWindow.class);

    /**
     * Opens a window with the information about the game. The button 'Close' closes this window.
     */
    public static void display() {
        Stage window = new Stage();

        window.initModality(Modality.APPLICATION_MODAL);
        window.setTitle("The Bachelor's Chase - Game Info");
        window.setMinWidth(400);

        TextArea textArea = new TextArea();
        textArea.setPrefSize(700, 500);
        textArea.setEditable(false);
        textArea.setFocusTraversable(false);
        textArea.setWrapText(true);
        textArea.setText("Game description:"
            + "\nEach player receives a random card at the beginning of the game."
            + "In each round the player has to decide within 10 seconds, "
            + "whether he wants to draw a card, throw away a card (only possible against coins) "
            + "or wants to take a turn respectively not draw a card. \n"
            + "\nRules:"
            + "\nCoin-System: \nEach player has an account, "
            + "that is still empty at the beginning of the first round."
            + "After a player has reached 180 KP, the coins are distributed as follows:"
            + "\nThe player with 180 KP receives 360 coins."
            + "\nThose with more than 180 HP receive 0 coins."
            + "\nAll other players receive as many coins as their sum of the number of cards reduced by 50 coins."
            + "\nThese coins will be awarded after a game round has ended, "
            + "that means after one of the player has reached 180 KP, "
            + "the coins will be sum up and can therefore be used in the next game round "
            + "(e.g. to throw away a card)."
            + "\nGoal: \nGoal of the game is to be the first who gets 180 credits."
            + "In each round the player can decide whether to draw a card, "
            + "throw away a card or miss this turn."
            + "If a player has over 180 credit points, "
            + "he lost and gets 0 coins.");

        Button close = new Button("Close");
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
