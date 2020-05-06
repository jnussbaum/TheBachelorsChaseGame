package tbc.client;

import javafx.application.Platform;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import tbc.game.Card;
import tbc.game.Player;
import tbc.gui.DroppedOutWindow;
import tbc.gui.LobbyController;
import tbc.gui.SelectOptions;

import java.util.ArrayList;

public class ClientGame {

    private static final Logger LOGGER = LogManager.getLogger();
    private final String myName;
    private final ClientHandler clientHandler;
    private final ArrayList<Card> cards = new ArrayList<>();
    private final Player[] players;
    private final int THROWCOST = 10; //number of coins you pay to throw away a card
    private int points;

    /**
     * The Client starts a new game.
     *
     * @param ch:          the clientHandler for sending stuff to the server
     * @param namePlayers: the player names that are participating in the game
     */
    public ClientGame(ClientHandler ch, String[] namePlayers) {
        this.clientHandler = ch;
        myName = clientHandler.getMyName();
        players = new Player[namePlayers.length];
        for (int i = 0; i < namePlayers.length; i++) {
            players[i] = new Player(namePlayers[i]);
        }
    }

    /**
     * Appends the new card to the ArrayList 'cards'.
     *
     * @param cardName The new card, which the user got, will be appended to the ArrayList 'cards'.
     */
    public void addCard(String cardName) {
        cards.add(Card.valueOf(cardName));
        Platform.runLater(
                () -> {
                    LobbyController.gameWindowController.appendGameMsg("You received the card " + cardName);
                    LobbyController.gameWindowController.showCard(cardName);
                }
        );
        calculatePoints();
    }

    /**
     * This method is called when the server gives the turn to this client.
     */
    public void giveTurn() {
        LOGGER.info("Show the three options");
        Platform.runLater(
                () -> {
                    String s = "";
                    for (Card c : cards) {
                        s = s + c.toString() + " ";
                    }
                    LobbyController.gameWindowController.appendGameMsg("Your cards: " + s);
                    SelectOptions.display();
                }
        );
    }

    /**
     * Directs a request for a new card to the clientHandler
     */
    public void takeCard() {
        clientHandler.askForCard();
    }

    /**
     * Handles the process of throwing away a card on client side
     *
     * @param cardName: name of the card to throw away
     */
    public void throwCard(String cardName) {
        ArrayList<String> cardsAsStrings = new ArrayList<>();
        for (Card c : cards) {
            cardsAsStrings.add(c.toString());
        }
        if (cardsAsStrings.contains(cardName) == false) {
            Platform.runLater(
                    () -> {
                        LobbyController.gameWindowController.appendGameMsg(
                                "You don't possess such a card. Please select another.");
                    }
            );
            SelectOptions.display();
        } else {
            int coins = nameToPlayer(myName).getNumOfCoins();
            if (coins >= THROWCOST) {
                cards.remove(Card.valueOf(cardName));
                clientHandler.throwCard(cardName);
                coins -= THROWCOST;
                nameToPlayer(myName).setNumOfCoins(coins);
                calculatePoints();
                Platform.runLater(
                        () -> {
                            LobbyController.gameWindowController.appendGameMsg(
                                    "The Card " + cardName + " was thrown away");
                            LobbyController.gameWindowController.throwTheCard(cardName);
                            // update the high score table
                            LobbyController.gameWindowController.setHighScore();
                        }
                );
            } else {
                Platform.runLater(
                        () -> {
                            LobbyController.gameWindowController.appendGameMsg(
                                    "You don't have enough coins to throw away a card. Please select another option.");
                        }
                );
                SelectOptions.display();
            }
        }
    }

    /**
     * Handles the exiting of the player
     */
    public void quitThisMatch() {
        nameToPlayer(myName).setQuitMatch(true);
        clientHandler.quitThisMatch();
        calculatePoints();
        LOGGER.info("quitThisMatch has been executed");
    }

    /**
     * Calculates the points and checks if they are too high
     */
    void calculatePoints() {
        int sum = 0;
        for (Card c : cards) {
            sum += c.getValue();
        }
        points = sum;
        Platform.runLater(() -> {
            LobbyController.gameWindowController.appendGameMsg("You have " + points + " points.");
        });

        if (points > 180) {
            droppedOut();
        }
    }

    /**
     * Handles the routine at the end of a match
     *
     * @param winnerName: Name of the client who has won
     */
    public void endMatch(String winnerName) {
        Platform.runLater(
                () -> {
                    LobbyController.gameWindowController.appendGameMsg("The match has ended, the winner is "
                            + winnerName + ". You scored " + points + " points!");
                    LobbyController.gameWindowController.appendGameMsg("Your new number of coins is: "
                            + nameToPlayer(myName).getNumOfCoins());
                }
        );

        reset();
        Platform.runLater(
                () -> {
                    LobbyController.gameWindowController.appendGameMsg(
                            "Press the button 'New match' if you want to start a new match");
                    LobbyController.gameWindowController.btnNewMatch.setDisable(false);
                }
        );
    }

    /**
     * Receives a string with the coins of all players, in order to update the own coins
     * and for the highscore table
     *
     * @param allCoins: All coins and the corresponding player names as one string
     */
    public void receiveCoins(String allCoins) {
        //split string into substrings, then write information into the player objects
        String[] s = allCoins.split("::");
        String name;
        for (int i = 0; i < s.length - 1; i++) {
            if (i % 2 == 0) {
                //a name is at this position
                name = s[i];
                int coins = Integer.parseInt(s[i + 1]);
                nameToPlayer(name).setNumOfCoins(coins);
                LOGGER.info("Player " + name + " received his coins");
            }
        }

        // update the high score table
        Platform.runLater(() -> {
            LobbyController.gameWindowController.setHighScore();
        });
    }

    /**
     * Returns the player object of the requested player
     *
     * @param clientName: Name of the client as string
     * @return The player-object
     */
    public Player nameToPlayer(String clientName) {
        for (int i = 0; i < players.length; i++) {
            if (players[i].getName().equals(clientName)) {
                return players[i];
            }
        }
        LOGGER.error("no Player with that name ");
        return new Player("Badplayer");
    }

    /**
     * Resets important values of the player-objects in order to start a new match
     */
    private void reset() {
        for (Player p : players) {
            p.setNumOfPoints(0);
            p.clearCards();
            p.setQuitMatch(false);
            LOGGER.info(p.getName() + " Has been reset");
        }
        cards.clear();
    }

    public Player[] getPlayers() {
        return players;
    }

    /**
     * Displays a window with the alert that the player has dropped out because of too many points
     */
    public void droppedOut() {
        Platform.runLater(() -> {
            DroppedOutWindow.display();
        });
    }
}
