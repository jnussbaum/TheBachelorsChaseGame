package tbc.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import tbc.game.Card;
import tbc.game.Player;

import static java.lang.Thread.sleep;

public class ClientGame {

    private static final Logger LOGGER = LogManager.getLogger();
    private String myName;
    private ClientHandler clientHandler;
    private int points;
    private ArrayList<Card> cards = new ArrayList<>();
    private Player[] players;
    private final int THROWCOST = 10; //number of coins you pay to throw away a card
    private BufferedReader input;
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

    public void addCard(String cardName) {
        cards.add(Card.valueOf(cardName));
        LOGGER.info("You received the card " + cardName);
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
        //TODO: Use this countdown in the GUI
        */
        selectWithoutGUI();
    }

    void selectWithoutGUI() {
        String s = "";
        for (Card c : cards) {
            s = s + c.toString() + " ";
        }
        LOGGER.info("Your Cards are: " + s);
        LOGGER.info("Number of your coins: " + nameToPlayer(myName).getNumOfCoins());
        LOGGER.info("Please select your turn: \n" +
                "Type 1 to take a card,\n" +
                "Type 2 to throw a card\n" +
                "Type 3 to quit this match."
        );
        try {
            String answer = input.readLine();
            LOGGER.info("The input was: " + answer);
            if (answer == null) {
                quitThisMatch();
            } else if (answer.contains("1")) {
                takeCard();
            } else if (answer.contains("2")) {
                LOGGER.info("Your Cards are: " + s);
                LOGGER.info("Please type in the card you want to throw away: ");
                answer = input.readLine();
                throwCard(answer);
            } else if (answer.contains("3")) {
                quitThisMatch();
            } else {
                LOGGER.info("User input could not be parsed to one of the three actions");
                LOGGER.info("please tip one of the following options: ");
                selectWithoutGUI();
            }
        } catch (IOException e) {
            LOGGER.error("IO Error while reading user input to select a turn.");
            e.printStackTrace();
        }
    }

    void takeCard() {
        //timer.cancel();
        clientHandler.askForCard();
        calculatePoints();
    }

    void throwCard(String cardName) {
        //timer.cancel();
        ArrayList<String> cardsAsStrings = new ArrayList<>();
        for (Card c : cards) {
            cardsAsStrings.add(c.toString());
        }
        if (cardsAsStrings.contains(cardName) == false) {
            LOGGER.info("You don't possess such a card. Please select another.");
            selectWithoutGUI();
        } else {
            int coins = nameToPlayer(myName).getNumOfCoins();
            if (coins >= THROWCOST) {
                cards.remove(Card.valueOf(cardName));
                clientHandler.throwCard(cardName);
                coins -= THROWCOST;
                nameToPlayer(myName).setNumOfCoins(coins);
                calculatePoints();
                LOGGER.info("The Card " + cardName + " was thrown away");
            } else {
                LOGGER.info("You don't have enough coins to throw away a card. Please select another option.");
                selectWithoutGUI();
            }
        }
    }

    void quitThisMatch() {
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
        LOGGER.info("Points have been calculated and they are: " + points);
    }

    public void endMatch(String winnerName) {
        LOGGER.info("The match has ended, the winner is " + winnerName +
            " You scored " + points + " points");
        LOGGER.info("You new Number of Coins is: " + nameToPlayer(myName).getNumOfCoins());
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
            LOGGER.info(p.getName() + " Has been resetted");
        }
    }

    void askToStartNewMatch() {
        try {
            LOGGER.info("Would you like to start a new match? Type yes or no.");
            String answer = input.readLine();
            LOGGER.info(answer);
            if (answer.equalsIgnoreCase("yes")) {
                clientHandler.askForNewMatch();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
