package tbc.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Timer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import tbc.game.Card;
import tbc.game.Player;

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
        System.out.println("You received the card " + cardName);
    }

    public void giveTurn() {
        System.out.println("It's your turn. Seconds left:");
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
        System.out.println("Your Cards are: " + s);
        System.out.println("Number of your coins" + nametoPlayer(myName).getNumOfCoins());
        System.out.println("Please select your turn: \n" +
                "Type 1 to take a card,\n" +
                "Type 2 to throw a card\n" +
                "Type 3 to jump this turn."
        );
        try {
            String answer = input.readLine();
            LOGGER.info("The input was: " + answer);
            if (answer == null) {
                jumpThisTurn();
            } else if (answer.contains("1")) {
                takeCard();
                System.out.println("takeCard() was invoked in ClientCame");
            } else if (answer.contains("2")) {
                System.out.println("Your Cards are: " + s);
                System.out.println("Please tipe in the Crad you want to Throw away: ");
                answer = input.readLine();
                throwCard(answer);
            } else if (answer.contains("3")) {
                jumpThisTurn();
            } else {
                LOGGER.info("User input could not be parsed to one of the three actions");
                System.out.println("please tip one of the following options: ");
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
        int coins = -1;
        for (int i = 0; i < players.length; i++) {
            if (players[i].getName().equals(myName)) {
                coins = players[i].getNumOfCoins();
            }
        }
        if (coins == -1) System.err.println("ClientGame of " + myName + "could not find number of coins");
        if (coins >= THROWCOST) {
            cards.remove(Card.valueOf(cardName));
            clientHandler.throwCard(cardName);
            coins -= THROWCOST;
            nametoPlayer(myName).setNumOfCoins(coins);
            calculatePoints();
            System.out.println("The Card " + cardName + " was thrown away");
        }
    }

    void jumpThisTurn() {
        //timer.cancel();
        clientHandler.jumpThisTurn();
        calculatePoints();
    }

    void calculatePoints() {
        int sum = 0;
        for (Card c : cards) {
            sum += c.getValue();
        }
        points = sum;
        LOGGER.info("Points have bin Calculated and they are: " + points);
    }

    public void endMatch(String winnerName) {
        System.out.println("The match has ended, the winner is " + winnerName +
            " You scored " + points + " points");
        System.out.println("You new Number of Coins is: " + nametoPlayer(myName).getNumOfCoins());
        //TODO the reset has to wörk properly
        //reset();
        Client.askToStartNewMatch();
    }

    public void receiveCoins(String allCoins) {
        //split String into substrings, then write information into Player-Array
        String[] s = allCoins.split("::");
        String name;
        for (int i = 0; i < s.length / 2; i++) {
            if (i % 2 == 0) {
                //A name is at this position
                name = s[i];
                //look up the name in the Player-Array and write the coins (next position in s) into it
                for (int j = 0; j < players.length; j++) {
                    if (players[j].getName().equals(name)) {
                        players[j].setNumOfCoins(Integer.parseInt(s[i + 1]));
                    }
                }
            }
        }
    }

    private Player nametoPlayer(String clientName) {
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

            LOGGER.info(p.getName() + " Has bin resetted");
        }
    }
}
