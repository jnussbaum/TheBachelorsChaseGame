package tbc.client;

import javafx.application.Platform;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import tbc.game.Card;
import tbc.game.Player;
import tbc.gui.LobbyController;
import tbc.gui.SelectOptions;

import java.io.BufferedReader;
import java.util.ArrayList;
import java.util.Timer;

public class ClientGame {

    private static final Logger LOGGER = LogManager.getLogger();
    private final String myName;
    private final ClientHandler clientHandler;
    private final ArrayList<Card> cards = new ArrayList<>();
    private final Player[] players;
    private final int THROWCOST = 10; //number of coins you pay to throw away a card
    private final BufferedReader input;
    private int points;
    private Timer timer;

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
    }

    public void giveTurn() {
        //LOGGER.info("It's your turn. Seconds left:");
        //TODO Implement the Timer to the game when gui ready
        /*
        timer = new Timer();
        timer.schedule(new TimerTask() {
            int countdown = 10;

            public void run() {
                if (countdown > 0) {
                    countdown = countdown - 1;
                    //System.out.println(countdown);
                } else {
                    //countdown == 0
                    timer.cancel();
                    //TODO: Make it impossible for this client to take further actions
                }
            }
        }, 0, 1000);
        //TODO: What to do when the user takes an action before the 10 secs are left
        //TODO: Use this countdown in the gui
        */
        selectOptions();
    }

    /**
     * Calls the method display() from the class SelectOptions, so the user can select one of the
     * options.
     */
    void selectOptions() {
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
        //timer.cancel();
        clientHandler.askForCard();
        calculatePoints();
    }

    public void throwCard(String cardName) {
        //timer.cancel();
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
        //timer.cancel();
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
        Platform.runLater(
                () -> LobbyController.gameWindowController
                        .appendGameMsg("Points have been calculated and they are: "
                                + points)
        );
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
        for (int i = 0; i < s.length - 1; i++) {
            if (i % 2 == 0) {
                //A name is at this position
                name = s[i];
                nameToPlayer(name).setNumOfCoins(Integer.parseInt(s[i + 1]));
                LOGGER.info("Player " + name + " received his coins");
            }
        }
    }

    private Player nameToPlayer(String clientName) {
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

    public Player[] getPlayers() {
        return players;
    }

}
