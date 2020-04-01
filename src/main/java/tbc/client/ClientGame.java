package tbc.client;

import tbc.game.Card;
import tbc.game.Player;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class ClientGame {


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
        this.input = input; //TODO: diesen input wieder aus dieser klasse rausnehmen
        myName = clientHandler.getMyName();
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
        selectWithoutGUI();
    }

    void selectWithoutGUI() {
        System.out.println("Please select your turn: \n" +
                "Type 1 to take a card,\n" +
                "Type 2 to throw a card\n" +
                "Type 3 to jump this turn."
        );
        try {
            String answer = input.readLine();
            System.out.println("Your input was: " + answer);
            if (answer == null) {
                jumpThisTurn();
            } else if (answer.contains("1")) {
                takeCard();
                System.out.println("takeCard() was invoked in ClientCame");
            } else if (answer.contains("2")) {
                //throwCard();
            } else if (answer.contains("3")) {
                jumpThisTurn();
            } else {
                System.out.println("User input could not be parsed to one of the three actions");
            }
        } catch (IOException e) {
            System.out.println("IO Error while reading user input to select a turn.");
            e.printStackTrace();
        }
    }

    void takeCard() {
        timer.cancel();
        clientHandler.askForCard();
        calculatePoints();
    }

    void throwCard(String cardName) {
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

            calculatePoints();
        }
    }

    void jumpThisTurn() {
        clientHandler.jumpThisTurn();
        calculatePoints();
    }

    void calculatePoints() {
        int sum = 0;
        for (Card c : cards) {
            sum += c.getValue();
        }
        points = sum;
    }

    public void endMatch(String winnerName) {
        System.out.println("The match has ended, the winner is " + winnerName +
                "Your received " + points + "coins"); //TODO: calculate the coins received (!= points)
        System.out.println("Would you like to start a new match?");
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
}
