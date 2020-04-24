package tbc.game;

import tbc.server.ServerHandler;

import java.util.ArrayList;

public class Player {

    boolean quitMatch = false;
    boolean notifyDropOut = false;
    String name;
    int numOfPoints;
    int numOfCoins = 0;
    int numOfCards;
    ServerHandler serverHandler;
    ArrayList<Card> cards = new ArrayList<>();

    public Player(String name) {
        this.name = name;
    }

    public Player(String userName, int coins) {
        this.name = userName;
        this.numOfCoins = coins;
    }

    public ServerHandler getServerHandler() {
        return serverHandler;
    }

    public void setServerHandler(ServerHandler serverHandler) {
        this.serverHandler = serverHandler;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getNumOfPoints() {
        return numOfPoints;
    }

    public void setNumOfPoints(int numOfPoints) {
        this.numOfPoints = numOfPoints;
    }

    public int getNumOfCoins() {
        return numOfCoins;
    }

    public void setNumOfCoins(int numOfCoins) {
        this.numOfCoins = numOfCoins;
    }

    public ArrayList<Card> getCards() {
        return cards;
    }

    /**
     * Calculates the Points the Player through the given Carts
     */

    int calculatePoints() {
        //Iterate through a client's cards
        int sum = 0;
        for (Card c : cards) {
            sum += c.getValue();
        }
        setNumOfPoints(sum);
        return sum;
    }

    public void clearCards() {
        cards.clear();
    }

    public void setQuitMatch(boolean quitMatch) {
        this.quitMatch = quitMatch;
    }

    public void setNotifyDropOut(boolean notifyDropOut) {
        this.notifyDropOut = notifyDropOut;
    }
}
