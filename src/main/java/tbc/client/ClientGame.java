package tbc.client;

import tbc.game.Card;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class ClientGame {

    private class Player {
        String name;
        int numOfCards;
        int numOfCoins;
    }

    private String myName;
    private ClientHandler clientHandler;
    private int points;
    private ArrayList<Card> cards;
    private Player[] players;
    private boolean isMyTurn;

    private final int THROWCOST = 10; //number of coins you pay to throw away a card

    public ClientGame(ClientHandler ch) {
        this.clientHandler = ch;
        myName = clientHandler.getMyName();
    }

    public void addCard(String cardName) {
        cards.add(Card.valueOf(cardName));
    }

    public void giveTurn() {
        isMyTurn = true;
        System.out.println("It's your turn. Seconds left:");
        new Timer().schedule(new TimerTask() {
                public void run() {
                    int countdown = 10;
                    countdown = countdown - 1;
                    System.out.println(countdown);
                }
            },0, 1000);
        //TODO: What to do when the user takes an action before the 10 secs are left
        //TODO: Use this countdown in the GUI
    }

    void takeCard() {
        clientHandler.askForCard();
        calculatePoints();
    }

    void throwCard(String cardName) {
        int coins = -1;
        for (int i = 0; i < players.length; i++) {
            if (players[i].name.equals(myName)) {
                coins = players[i].numOfCoins;
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
        //TODO: What else has to be done to end the match?
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
                    if (players[j].name.equals(name)) {
                        players[j].numOfCoins = Integer.parseInt(s[i + 1]);
                    }
                }
            }
        }
    }
}
