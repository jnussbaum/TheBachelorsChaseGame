package tbc.client;

import javafx.application.Platform;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import tbc.game.Card;
import tbc.game.Player;
import tbc.gui.DropppedOutWindow;
import tbc.gui.LobbyController;
import tbc.gui.SelectOptions;

import java.io.BufferedReader;
import java.util.ArrayList;

public class ClientGame {

    private static final Logger LOGGER = LogManager.getLogger();
    private final String myName;
    private final ClientHandler clientHandler;
    private final ArrayList<Card> cards = new ArrayList<>();
    private final Player[] players;
    private final int THROWCOST = 10; //number of coins you pay to throw away a card
    private final BufferedReader input;
    private int points;

    public ClientGame(ClientHandler ch, String[] namePlayers, BufferedReader input) {
        this.clientHandler = ch;
        myName = clientHandler.getMyName();
        this.input = input;
        players = new Player[namePlayers.length];
        for (int i = 0; i < namePlayers.length; i++) {
            players[i] = new Player(namePlayers[i]);
        }
    }

    /**
     * Appends the new card to the ArrayList 'cards'.
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

    public void takeCard() {
        clientHandler.askForCard();
    }

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

    public void quitThisMatch() {
        nameToPlayer(myName).setQuitMatch(true);
        clientHandler.quitThisMatch();
        calculatePoints();
        LOGGER.info("quitThisMatch has been executed");
    }

    void calculatePoints() {
        int sum = 0;
        for (Card c : cards) {
            sum += c.getValue();
        }
        points = sum;
        Platform.runLater(() -> {
            LobbyController.gameWindowController.appendGameMsg("You have " + points + " points.");
        });

        if (points > 180) { droppedOut(); }
    }

    public void endMatch(String winnerName) {
        Platform.runLater(
                () -> {
                    LobbyController.gameWindowController.appendGameMsg("The match has ended, the winner is "
                            + winnerName + ". You scored " + points + " points!");
                    LobbyController.gameWindowController.appendGameMsg("Your new number of coins is: "
                            + nameToPlayer(myName).getNumOfCoins());
                }
        );
        //TODO the reset has to work properly
        reset();
        askToStartNewMatch();
    }

    public void receiveCoins(String allCoins) {
        //split String into substrings, then write information into Player-Array
        String[] s = allCoins.split("::");
        String name;
        int[] scores = new int[players.length];
        for (int i = 0; i < s.length - 1; i++) {
            if (i % 2 == 0) {
                //a name is at this position
                name = s[i];
                int coins = Integer.parseInt(s[i + 1]);
                nameToPlayer(name).setNumOfCoins(coins);
                LOGGER.info("Player " + name + " received his coins");
                scores[i] = coins;
            }
        }

        //use this information for the HighScore
        //bubble sort scores
        for (int i = 0; i < scores.length; i++) {
            for (int j = 0; j < scores.length; j++) {
                if (j + 1 < scores.length && scores[j] < scores[j + 1]) {
                    int old = scores[j];
                    scores[j] = scores[j + 1];
                    scores[j + 1] = old;
                }
            }
        }

        for (int i = 0; i < s.length - 1; i++) {
            if (i % 2 == 1) {
                //number is at this position
                int number = Integer.parseInt(s[i]);
                for (int j = 0; j < scores.length; j++) {
                    if (scores[j] == number) {
                        //TODO: Finish highscore implementation OR: delete all this
                    }
                }
            }
        }
    }

    public Player nameToPlayer(String clientName) {
        for (int i = 0; i < players.length; i++) {
            if (players[i].getName().equals(clientName)) {
                return players[i];
            }
        }
        System.err.println("no Player with that name ");
        return new Player("Badplayer");
    }

    private void reset() {
        for (Player p : players) {
            p.setNumOfPoints(0);
            p.clearCards();
            p.setQuitMatch(false);
            LOGGER.info(p.getName() + " Has been resetted");
        }
        cards.clear();
    }

    void askToStartNewMatch() {
        Platform.runLater(
                () -> {
                    LobbyController.gameWindowController.appendGameMsg(
                            "Press the button 'New match' if you want to start a new match");
                    LobbyController.gameWindowController.btnNewMatch.setDisable(false);
                }
        );
    }

    public int getPoints() {
        return points;
    }

    public ArrayList<Card> getCards() {
        return cards;
    }

    public Player[] getPlayers() {
        return players;
    }

    public void droppedOut() {
        Platform.runLater(() -> {
            DropppedOutWindow.display();
        });
    }
}
