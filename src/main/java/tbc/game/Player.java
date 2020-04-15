package tbc.game;

import java.util.ArrayList;
import tbc.server.ServerHandler;

public class Player {

    boolean quitMatch = false;
    String name;
    int numOfPoints;
    int numOfCoins = 10;
    int numOfCards;
    ServerHandler serverHandler;
    ArrayList<Card> cards = new ArrayList<>();

    public Player(String name) {
        this.name = name;
    }

    public void setServerHandler(ServerHandler serverHandler) {
        this.serverHandler = serverHandler;
    }

    public ServerHandler getServerHandler() {
        return serverHandler;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setNumOfPoints(int numOfPoints) {
        this.numOfPoints = numOfPoints;
    }

    public void setNumOfCoins(int numOfCoins) {
        this.numOfCoins = numOfCoins;
    }

    public String getName() {
        return name;
    }

    public int getNumOfPoints() {
        return numOfPoints;
    }

    public int getNumOfCoins() {
        return numOfCoins;
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
}
