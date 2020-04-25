package tbc.game;

import java.util.ArrayList;
import tbc.server.ServerHandler;

/**
 * this class ist used to save the Dater of the players on the server and client-side respectively
 */
public class Player {

    boolean quitMatch = false;
    boolean notifyDropOut = false;
    String name;
    int numOfPoints;
    int numOfCoins = 0;
    int numOfCards;
    ServerHandler serverHandler;
    ArrayList<Card> cards = new ArrayList<>();

    /**
     * the Constructor
     *
     * @param name - the name of the player
     */
    public Player(String name) {
        this.name = name;
    }

    public Player(String userName, int coins) {
        this.name = userName;
        this.numOfCoins = coins;
    }

    /**
     * returns the serverHandler of the player
     * @return - serverHandler
     */
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

    /**
     * clear the player of all cards
     */
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
