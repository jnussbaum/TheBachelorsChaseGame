package tbc.gui;

import static tbc.client.Client.clientHandler;

import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextArea;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Processes a client's request to get the information of the pushed Button. It opens a window for
 * almost every Button and shows the details about the Game.
 */
public class LobbyController {

    private static final Logger LOGGER = LogManager.getLogger(LobbyController.class);
    public static GameWindowController gameWindowController;
    public static VariousWindowController variousWindowController;
    public static CardWindowController cardWindowController;
    private Stage secondStage;
    @FXML
    private BorderPane window;
    @FXML
    private TextArea textArea;

    /**
     * If the circle "Start" is pressed a new window will open, where the user has to chose or
     * create a lobby. If no lobby is chosen, the game window won't show up.
     *
     * @param mouseEvent mouse event to pick the top-most node under cursor.
     */
    public void startGame(MouseEvent mouseEvent) {
        LOGGER.info("Join or create a lobby.");
        SelectLobby.display();

        // check if server has rejected the joining of a player to the lobby
        if (clientHandler.rejected == true) {
            RejectJoiningLobbyWindow.display();
        }

        // check if a lobby is chosen and if the game in this lobby has already started.
        if (SelectLobby.lobbyChosen == true && clientHandler.rejected == false) {
            LOGGER.info("Show game window.");
            try {
                PlayMusic.playAudio();

                Stage gameWindow = (Stage) ((Node) mouseEvent.getSource()).getScene().getWindow();
                FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("GameWindowFXML.fxml"));
                Parent root = loader.load();

                gameWindow.setTitle("The Bachelor's Chase");
                gameWindow.setScene(new Scene(root, 1000, 750));
                gameWindow.show();

                gameWindowController = loader.getController();

                // welcomes the client in all the chat windows
                clientHandler.sendMessage("ALL", "false", "Welcome, "
                    + clientHandler.getMyName());

                // tells the user to press the 'Ready' button
                gameWindowController
                    .appendGameMsg("Press the button 'Ready' if you are ready for the game");

                // press enter to send the chat message
                gameWindowController.btnSend.setDefaultButton(true);

                // the 'Send' button from the chat is disabled as long as there is no input in the chat field
                gameWindowController.btnSend.disableProperty().bind(
                    Bindings.isEmpty(gameWindowController.msgField.textProperty())
                        .and(Bindings.isEmpty(gameWindowController.msgField.textProperty()))
                        .and(Bindings.isEmpty(gameWindowController.msgField.textProperty()))
                );

                gameWindow.setOnCloseRequest(e -> ConfirmBox.display());
            } catch (Exception e) {
                LOGGER.error("Couldn't find GameWindowFXML file.");
                e.printStackTrace();
            }
        }
    }

    /**
     * If the circle "Cards" is pressed a new window will open. It shows the three cards that we use
     * in our game.
     */
    public void showCards() {
        LOGGER.info("Show cards.");
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("CardWindowFXML.fxml"));
            BorderPane card = loader.load();

            Stage cardWindow = new Stage();
            cardWindow.setTitle("The Bachelor's Chase - Cards");
            cardWindow.initModality(Modality.APPLICATION_MODAL);
            cardWindow.initOwner(secondStage);
            Scene cardScene = new Scene(card);
            cardWindow.setScene(cardScene);
            cardWindow.show();

            cardWindowController = loader.getController();

        } catch (Exception e) {
            LOGGER.error("Couldn't find CardWindowFXML file.");
            e.printStackTrace();
        }
    }

    /**
     * If the circle "Rules" is pressed it will show you on the right side a field with the
     * description of the rules.
     */
    public void showRules() {
        LOGGER.info("Show rules.");
        window.setVisible(true);
        textArea.setText("Game description:"
            + "\nEach player receives a random card at the beginning of the game."
            + "In each round the player has to decide within 10 seconds, "
            + "whether he wants to draw a card, throw away a card (only possible against coins)"
            + "or wants to take a turn respectively not draw a card."
            + "\n \nRules:"
            + "\nCoinsystem: \nEach player has an account, "
            + "that is still empty at the beginning of the first round."
            + "After a player has reached 180 KP, the coins are distributed as follows:"
            + "\nThe player with 180 KP receives 360 coins."
            + "\nThose with more than 180 HP receive 0 coins."
            + "\nAll other players receive as many coins as their sum of the number of cards reduced by 50 coins."
            + "\nThese coins will be awarded after a game round has ended, "
            + "that means if one of the player has reached 180 KP, "
            + "the coins will be sum up and can therefore be used in the next game round "
            + "(e.g. to throw away a card).");
    }

    /**
     * If the circle "Goal" is pressed it will show you on the right side a field with the
     * description of the goal.
     */
    public void showGoal() {
        LOGGER.info("Show goal.");
        window.setVisible(true);
        textArea.setText("Goal: \nGoal of the game is to be the first who gets 180 credits."
            + "In each round the player can decide whether to draw a card, "
            + "throw away a card or miss this turn."
            + "If a player has over 180 credit points, "
            + "he lost and gets 0 coins.");
    }

    /**
     * If the circle "Various" is pressed it will open a new window, where you can change your
     * username, get the player and lobby list.
     */
    public void showVarious() {
        LOGGER.info("Show various.");
        try {
            FXMLLoader loader = new FXMLLoader(
                getClass().getResource("VariousWindowFXML.fxml"));
            BorderPane various = loader.load();

            Stage variousWindow = new Stage();
            variousWindow.setTitle("The Bachelor's Chase - Various");
            variousWindow.initModality(Modality.APPLICATION_MODAL);
            variousWindow.initOwner(secondStage);
            Scene diversScene = new Scene(various);
            variousWindow.setScene(diversScene);
            variousWindow.show();

            variousWindowController = loader.getController();

            // press enter to enter your name
            variousWindowController.btnEnter.setDefaultButton(true);

            // you can't press the enter button if you didn't type in an username.
            variousWindowController.btnEnter.disableProperty().bind(
                Bindings.isEmpty(variousWindowController.newUsername.textProperty())
                    .and(Bindings.isEmpty(variousWindowController.newUsername.textProperty()))
                    .and(Bindings.isEmpty(variousWindowController.newUsername.textProperty()))
            );

        } catch (Exception e) {
            LOGGER.error("Couldn't find VariousWindowFXML file.");
            e.printStackTrace();
        }
    }

    /**
     * The button 'Close', which closes the 'Rules' or 'Goal' window if open.
     */
    public void close() {
        LOGGER.info("Close current window.");
        textArea.clear();
        window.setVisible(false);
    }

}
