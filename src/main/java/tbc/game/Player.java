package tbc.game;

import java.util.ArrayList;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import tbc.server.ServerHandler;

/**
 * This class ist used to save the data of the players on the server and client-side respectively
 */
public class Player {

    private static final Logger LOGGER = LogManager.getLogger(Player.class);
    boolean quitMatch = false;
    boolean notifyDropOut = false;
    boolean cheater = false;
    String name;
    int numOfPoints;
    int numOfCoins = 0;
    ServerHandler serverHandler;
    ArrayList<Card> cards = new ArrayList<>();

    /**
     * Create a player object with the player name only.
     *
     * @param name the name of the player
     */
    public Player(String name) {
        this.name = name;
    }

    /**
     * Create a player object with the player name and the number of coins.
     *
     * @param userName the name of the player
     * @param coins    the number of coins of this player
     */
    public Player(String userName, int coins) {
        this.name = userName;
        this.numOfCoins = coins;
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

    /**
     * Calculates the points of this player by iterating through his card set.
     */
    int calculatePoints() {
        // iterate through a client's card set
        int sum = 0;
        for (Card c : cards) {
            sum += c.getValue();
        }
        setNumOfPoints(sum);
        return sum;
    }

    /**
     * clear all cards of this player. Has to be done before starting a new match.
     */
    public void clearCards() {
        cards.clear();
    }

    /**
     * Set the boolean variable 'quitMatch' to true or false.
     *
     * @param quitMatch Information if this player has quit this match (true) or not (false).
     */
    public void setQuitMatch(boolean quitMatch) {
        this.quitMatch = quitMatch;
    }

    /**
     * Set the boolean 'notifyDropOut' to true or false.
     *
     * @param notifyDropOut Information if this player already dropped out (true) or not (false).
     */
    public void setNotifyDropOut(boolean notifyDropOut) {
        this.notifyDropOut = notifyDropOut;
    }
}

