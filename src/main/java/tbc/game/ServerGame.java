package tbc.game;

import java.util.HashMap;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Semaphore;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import tbc.server.Lobby;
import tbc.server.WriteHighScore;

/**
 * The server-side game.
 */
public class ServerGame implements Runnable {

    private static final Logger LOGGER = LogManager.getLogger();
    private final Lobby lobby;
    /**
     * Number of coins you pay to throw away a card
     */
    private final int THROWCOST = 10;
    /**
     * Administration of all cards which are not yet distributed to a client. Card: Type of card
     * Integer: number of cards of this type which are still available.
     */
    private final HashMap<Card, Integer> cardDeck = new HashMap<>();
    volatile boolean matchEnd = false;
    boolean winner = false;
    private Timer timer;
    private Semaphore turnController;
    private int numOfDroppedOut = 0;
    /**
     * Administration of the clients in this game with their respective cardsets, coins and points.
     */
    private Player[] players;

    /**
     * Counts through the clients and represents which client's turn it is.
     */
    private int activeClient = 0;


    /**
     * Create a game and initialize the card deck
     *
     * @param lobby       The lobby from which this game was started
     * @param clientNames All clients who will be in this game
     */
    public ServerGame(Lobby lobby, String[] clientNames) {
        this.lobby = lobby;
        players = new Player[clientNames.length];
        for (int i = 0; i < clientNames.length; i++) {
            players[i] = new Player(clientNames[i]);
        }
        restackDeck();
    }

    /**
     * Get the number of cards which still are in the card deck
     *
     * @return Number of cards
     */
    public int getDeckSize() {
        int n = 0;
        for (int i : cardDeck.values()) {
            n += i;
        }
        return n;
    }

    /**
     * Restacks the cards in the card deck
     */
    private void restackDeck() {
        cardDeck.put(Card.Plagiarism, 2);
        cardDeck.put(Card.Party, 10);
        cardDeck.put(Card.Coffee, 10);
        cardDeck.put(Card.Energy, 10);
        cardDeck.put(Card.WLAN, 10);
        cardDeck.put(Card.Study, 5);
        cardDeck.put(Card.GoodLecturer, 2);
    }

    /**
     * Get the deck as String array which contains every single card, one per array position. The
     * cards are not mixed, but grouped together by their type.
     *
     * @return String array with all the cards of the deck
     */
    public String[] getDeckAsArray() {
        String[] output = new String[getDeckSize()];
        int pos = 0;
        // iterate through the card types
        for (Card c : cardDeck.keySet()) {
            // iterate through the number of cards of this card type
            for (int i = 0; i < cardDeck.get(c); i++) {
                output[pos++] = c.name();
            }
        }
        return output;
    }

    /**
     * Initial distribution of one card to each client.
     */
    private void distributeCards() {
        for (Player p : players) {
            giveRandomCard(p.getName());
        }
    }

    /**
     * Helper method to give a random card to a client. This method is invoked by other methods who
     * want to give out cards.
     */
    void giveRandomCard(String clientName) {
        // choose a random card
        Random random = new Random();
        int randomInt = random.nextInt(getDeckSize());
        String randomCardName = getDeckAsArray()[randomInt];

        // give the chosen random card to the client
        nameToPlayer(clientName).cards.add(Card.valueOf(randomCardName));
        lobby.getServerHandler(clientName).giveCard(randomCardName);

        // reset the number of available cards in the cardDeck
        int pos = cardDeck.get(Card.valueOf(randomCardName));
        cardDeck.put(Card.valueOf(randomCardName), pos - 1);
    }

    /**
     * First of the three possibilities when it is a client's turn: Give a card to the client.
     *
     * @param clientName The name of the user to whom the server gives the card.
     */
    public void giveCardToClient(String clientName) {
        timer.cancel();
        giveRandomCard(clientName);
        turnController.release();
        LOGGER.info("ServerGame called giveRandomCard");
        calculatePoints();
        giveTurnToNext();
    }

    /**
     * Second of the three possibilities when it is a client's turn: Quit this match.
     *
     * @param clientName The name of the user who wants to quit this match.
     */
    public void quitThisMatch(String clientName) {
        timer.cancel();
        calculatePoints();
        nameToPlayer(clientName).setQuitMatch(true);
        turnController.release();
        giveTurnToNext();
    }

    /**
     * Third of the three possibilities when it is a client's turn: throw away a card.
     *
     * @param clientName The name of the user who wants to throw away a card.
     * @param cardName   The name of the card which the user wants to throw away.
     */
    public void throwCard(String clientName, String cardName) {
        // remove the card from the client's cardset, and if not possible, print error message.
        if (!nameToPlayer(clientName).cards.remove(Card.valueOf(cardName))) {
            LOGGER.error("The client " + clientName + " cannot throw away the card " + cardName
                + " because he does not have such a card.");
        }
        timer.cancel();
        LOGGER
            .info("number of coins pre-calculatePoints: " + nameToPlayer(clientName)
                .getNumOfPoints());
        calculatePoints();
        LOGGER.info(
            "number of coins after calculatePoints: " + nameToPlayer(clientName).getNumOfPoints());
        // subtract coins from player
        nameToPlayer(clientName)
            .setNumOfCoins(nameToPlayer(clientName).getNumOfCoins() - THROWCOST);
        turnController.release();
        giveTurnToNext();
    }

    /**
     * Updates the number of dropped out players
     */
    private void updateDroppedOut() {
        int i = 0;
        for (Player p : players) {
            if (p.quitMatch) {
                i++;
            }
        }
        numOfDroppedOut = i;
    }

    /**
     * Determines who is the next player, and gives the turn to him
     */
    private void giveTurnToNext() {
        updateDroppedOut();
        LOGGER.info(
            "ServerGame's method giveTurnToNext() was called. numOfDroppedOut = "
                + numOfDroppedOut);
        if (winner == false) {
            if (numOfDroppedOut < players.length) {
                // find out the number of the next client
                if (activeClient == players.length - 1) {
                    activeClient = 0;
                } else {
                    activeClient++;
                }
                // give the turn to the client whose number was set before
                if (players[activeClient].quitMatch == true) {
                    giveTurnToNext();
                } else {
                    lobby.getServerHandler(players[activeClient].getName()).giveTurn();
                    System.out.println("ServerGame's method giveTurnToNext() gave turn to "
                        + players[activeClient].getName());
                }
            } else {
                System.out.println("All players dropped out of the game");
                endMatch("nobody");
            }
        }
    }


    /**
     * Is called to execute the routine at the end of a match
     *
     * @param winnerName the name of the winner of the match
     */
    private void endMatch(String winnerName) {
        LOGGER.info("endMatch has been called");
        matchEnd = true;
        winner = true;
        // give coins to all clients
        calculateCoins();
        // tell all clients that XY won and that they receive now their coins
        for (int i = 0; i < players.length; i++) {
            String clientName = players[i].getName();
            lobby.getServerHandler(clientName).sendCoins(allCoinsToString());
            lobby.getServerHandler(clientName).endMatch(winnerName);
            LOGGER.info("coins and winner name have been sent to " + clientName);
        }
        LOGGER.info(
            "endMatch has sent the coins and the winner name " + winnerName + " to all clients");
        writeHighScore();
    }

    /**
     * This method writes the highscore (names and coins of all players) to the HighScore.txt file
     * after a match has ended.
     */
    private void writeHighScore() {
        WriteHighScore data = new WriteHighScore(true);
        for (Player playerNames : players) {
            String playerName = playerNames.getName();
            int coins = playerNames.getNumOfCoins();
            data.writeToFile(playerName, coins);
            LOGGER.info("Name: " + playerName + " Coins: " + coins);
        }
    }

    /**
     * Takes the coins of all the players and generates a string with them.
     *
     * @return The String with all the player names and their coins.
     */
    String allCoinsToString() {
        String s = "";
        for (int i = 0; i < players.length; i++) {
            String clientName = players[i].getName();
            s = s + clientName + "::";
            s = s + players[i].getNumOfCoins() + "::";
        }
        return s.substring(0, s.length() - 2);
    }

    /**
     * Takes a string and returns the matching Player-object
     *
     * @param clientName The name of the client
     * @return If the requested player exists, his player-object is returned. Else, a new Player
     * with the name 'Badplayer' is returned.
     */
    public Player nameToPlayer(String clientName) {
        for (int i = 0; i < players.length; i++) {
            if (players[i].getName().equals(clientName)) {
                return players[i];
            }
        }
        LOGGER.error("no Player with that name ");
        return new Player("Badplayer");
    }

    /**
     * Checks all players if the win-condition is met. This method is always called after every
     * turn.
     */
    private void calculatePoints() {
        String winner = null;
        for (int i = 0; i < players.length; i++) {
            int sum = players[i].calculatePoints();
            if (sum == 180) {
                winner = players[i].name;
            } else if (sum > 180) {
                players[i].quitMatch = true;
                lobby.getServerHandler(players[i].name).droppedOut();
                players[i].setNumOfPoints(0);
            }
        }
        if (winner != null) {
            LOGGER.info("calculatePoints has determined a winner");
            endMatch(winner);
        }
    }

    /**
     * Calculates the coins of all players and resets their points to 0
     */
    private void calculateCoins() {
        LOGGER.info("Coins will be calculated");
        for (int i = 0; i < players.length; i++) {
            Player a = players[i];
            if (a.cheater) {
                a.setNumOfCoins(a.getNumOfCoins() - 50);
            } else if (a.getNumOfPoints() == 180) {
                a.setNumOfCoins(a.getNumOfCoins() + 180 * 2);
            } else if (a.getNumOfPoints() < 180) {
                if (a.getNumOfPoints() >= 50 && a.getNumOfPoints() <= 180) {
                    //Player receives 50 coins less than he has points
                    a.setNumOfCoins(a.getNumOfCoins() + a.getNumOfPoints() - 50);
                }
                // else: he does not receive any coins
            }
            a.setNumOfPoints(0);
        }
    }

    /**
     * Preparing and restarting the match
     */
    public void startMatchAgain() {
        turnController = new Semaphore(1);
        reset();
        LOGGER.info("Match has been started again");
        winner = false;
        matchEnd = false;
        restackDeck();
    }

    /**
     * Resets important values of the player-objects in order to start a new match
     */
    private void reset() {
        for (Player p : players) {
            p.cheater = false;
            p.setNumOfPoints(0);
            p.clearCards();
            p.setQuitMatch(false);
            p.setNotifyDropOut(false);
            LOGGER.info(p.getName() + " Has been reset");
            LOGGER.info(p.numOfPoints + " is the number of his points");
        }
    }

    /**
     * During its lifetime, this thread communicates to the clients whose turn it is
     */
    public void run() {
        try {
            turnController = new Semaphore(1);
            while (true) {
                while (!matchEnd) {
                    distributeCards();
                    LOGGER.info("matchEnd status: " + matchEnd);
                    giveTurnToNext();
                    while (matchEnd == false) {
                        turnController.acquire();
                        timer = new Timer();
                        timer.schedule(new TimerTask() {
                            public void run() {
                                giveTurnToNext();
                                turnController.release();
                            }
                        }, 10000);
                    }
                    LOGGER.info("matchEnd-while-loop terminated.");
                }
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * Handles a logout in the game logic
     *
     * @param name The name of the client who wants to leave
     */
    public void logout(String name) {
        if (players.length > 0) {
            // the players-array should only be rewritten if its length is > 0. If not, no action is required.
            Player[] newPlayers = new Player[players.length - 1];
            int a = 0;
            for (int i = 0; i < players.length; i++) {
                if (players[i].getName().equals(name) == false && players.length > 1) {
                    newPlayers[a] = players[i];
                    a++;
                }
            }
            players = newPlayers;
        }
    }

    /**
     * Gives the cheating Player a Cheatcard to win the game
     *
     * @param p    the number of points he wants to have
     * @param name the name of the player
     */
    public void cheat(int p, String name) {
        LOGGER.info("ServerGame called cheat()");
        timer.cancel();
        nameToPlayer(name).cheater = true;
        int playerPoints = nameToPlayer(name).getNumOfPoints();
        int diff = p - playerPoints;

        // first, find out how many Cheat180-cards have to be given
        int numOfCheat180Cards = diff / 180;

        // find out which of the lower cards has to be given
        int lowerCard = diff % 180;
        LOGGER.info("numOfCheat180Cards = " + numOfCheat180Cards);
        LOGGER.info("lowerCard = " + lowerCard);

        // distribute the lower card
        if (lowerCard >= 10 && lowerCard <= 180) {
            nameToPlayer(name).cards.add(Card.valueOf("Cheat" + lowerCard));
            lobby.getServerHandler(name).giveCard("Cheat" + lowerCard);
        } else {
            LOGGER.error("lowerCard has an invalid value, namely " + lowerCard);
        }

        // distribute the Cheat180 card
        int i = 0;
        while (i < numOfCheat180Cards) {
            nameToPlayer(name).cards.add(Card.valueOf("Cheat180"));
            lobby.getServerHandler(name).giveCard("Cheat180");
            i++;
        }

        turnController.release();
        calculatePoints();
        giveTurnToNext();
    }
}
