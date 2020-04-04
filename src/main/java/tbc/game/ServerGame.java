package tbc.game;

import java.util.HashMap;
import java.util.Random;
import java.util.Timer;
import java.util.concurrent.Semaphore;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import tbc.server.Lobby;

public class ServerGame implements Runnable {

    private static final Logger LOGGER = LogManager.getLogger();

    /**
     * Here, the clients are stored in a String-Array
     */
    private String[] clientsAsArray = new String[4];

    boolean matchEnd = false;

    private Timer timer;
    private Semaphore turnController = new Semaphore(1);

    /**
     * The lobby from which this game was started.
     */
    private Lobby lobby;

    /**
     * Administration of the clients in this game with their respective cardset.
     * Administration of the coins of each client.
     * Administration of the points of each client.
     */
    private Player[] players;

    /**
     * Administration of all cards which are not yet distributed to a client.
     * Card: Type of card
     * Integer: number of cards of this type which are still available.
     */
    private HashMap<Card, Integer> cardDeck = new HashMap<>();

    /**
     * Counts through the clients and represents which client's turn it is.
     */
    private int activeClient = 0;

    private int numOfDroppedOut = 0;

    /**
     * Create a game and initialize the carddeck
     *
     * @param lobby:       The lobby from which this game was started
     * @param clientNames: All clients who will be in this game
     */
    public ServerGame(Lobby lobby, String[] clientNames) {
        this.lobby = lobby;
        clientsAsArray = clientNames;
        players = new Player[clientNames.length];
        for (int i = 0; i < clientNames.length; i++) {
            int x = 0;
            players[i] = new Player(clientNames[i]);
        }
        //Fill the carddeck with the specified number of cards per card type
        cardDeck.put(Card.Plagiarism, 5);
        cardDeck.put(Card.Party, 5);
        cardDeck.put(Card.Coffee, 10);
        cardDeck.put(Card.RedBull, 10);
        cardDeck.put(Card.WLAN, 10);
        cardDeck.put(Card.Study, 5);
        cardDeck.put(Card.GoodLecturer, 2);
    }

    void startMatch() {
        Thread matchThread = new Thread(new ServerMatch(this));
        matchThread.start();
    }

    int getDeckSize() {
        int n = 0;
        for (int i : cardDeck.values()) {
            n += i;
        }
        return n;
    }

    /**
     * Get the deck as String-array which contains every single card, one per array position.
     * The cards are not mixed, but grouped together by their type.
     */
    String[] getDeckAsArray() {
        String[] output = new String[getDeckSize()];
        int pos = 0;
        //Iterate over the card types
        for (Card c : cardDeck.keySet()) {
            //Iterate over the number of cards of this card type
            for (int i = 0; i < cardDeck.get(c); i++) {
                output[pos++] = c.name();
            }
        }
        return output;
    }

    /**
     * Initial distribution of one card to each client.
     */
    public void distributeCards() {
        for (String clientName : clientsAsArray) {
            giveRandomCard(clientName);
        }
    }

    /**
     * Helper method to give a random card to a client. This method is invoked by other methods who want to
     * give out cards.
     */
    public void giveRandomCard(String clientName) {
        //Choose a random card
        Random random = new Random();
        int randomInt = random.nextInt(getDeckSize());
        String randomCardName = getDeckAsArray()[randomInt];

        //Give the chosen random card to the client
        nametoPlayer(clientName).cards.add(Card.valueOf(randomCardName));
        lobby.getServerHandler(clientName).giveCard(randomCardName);

        //Reset the number of available cards in the carddeck
        int pos = cardDeck.get(Card.valueOf(randomCardName));
        cardDeck.put(Card.valueOf(randomCardName), pos - 1);
    }

    /**
     * First of the three possibilities when it is a client's turn: Give a card to the client.
     */
    public void giveCardToClient(String clientName) {
        //timer.cancel();
        giveRandomCard(clientName);
        turnController.release();
        LOGGER.info("ServerGame called giveRandomCard");
        calculatePoints();
    }

    /**
     * Second of the three possibilities when it is a client's turn: Jump this turn.
     */
    public void jumpThisTurn() {
        // timer.cancel();
        calculatePoints();

        turnController.release();
    }

    /**
     * Third of the three possibilities when it is a client's turn: throw away a card.
     */
    public void throwCard(String clientName, String cardName) {
        //timer.cancel();
        //Remove the card from the client's cardset, and if not possible, print error message.
        if (!nametoPlayer(clientName).cards.remove(Card.valueOf(cardName))) {
            LOGGER.error("The client " + clientName + "cannot throw away the card "
                    + cardName + " because he does not have such a card.");
        }
        LOGGER.info(
            "number of coins pre-calculatePoints: " + nametoPlayer(clientName).getNumOfPoints());
        calculatePoints();
        LOGGER.info(
            "number of coins after calculatePoints: " + nametoPlayer(clientName).getNumOfPoints());
        //Subtract an coin from player
        //TODO add trowCost instead of a 1
        nametoPlayer(clientName).setNumOfCoins(nametoPlayer(clientName).getNumOfCoins() - 1);
        turnController.release();
    }

    public void giveTurnToNext() {
        LOGGER.info("ServerGame's method giveTurnToNext() was called. numOfDroppedOut = " + numOfDroppedOut);
        if (numOfDroppedOut < clientsAsArray.length) {
            //Find out the number of the next client
            if (activeClient == players.length - 1) {
                activeClient = 0;
            } else {
                activeClient++;
            }
            //Give the turn to the client whose number was set before
            if (nametoPlayer(clientsAsArray[activeClient]).tooMuchPoints == true) {
                numOfDroppedOut++;
                giveTurnToNext();
            } else {
                lobby.getServerHandler(clientsAsArray[activeClient]).giveTurn();
                System.out.println("ServerGame's method giveTurnToNext() gave turn to " + clientsAsArray[activeClient]);
            }
        } else {
            System.out.println("All players dropped out of the game");
            endMatch("NoBody");
        }
    }


    void endMatch(String winnerName) {
        //Give coins to all clients
        calculateCoins();
        // Tell all clients that XY won and that they receive now their coins
        for (int i = 0; i < clientsAsArray.length; i++) {
            String clientName = clientsAsArray[i];
            lobby.getServerHandler(clientName).sendCoins(allCoinsToString());
            lobby.getServerHandler(clientName).endMatch(winnerName);
        }

        //terminate match:
    }

    String allCoinsToString() {
        String s = null;
        for (int i = 0; i < clientsAsArray.length; i++) {
            String clientName = clientsAsArray[i];
            s = s + clientName + "::";
            s = s + players[i].getNumOfCoins() + "::";
        }
        return s.substring(0, s.length() - 2);
    }

    private Player nametoPlayer(String clientName) {
        for (int i = 0; i < players.length; i++) {
            if (players[i].getName().equals(clientName)) {
                return players[i];
            }
        }
        LOGGER.error("no Player with that name ");
        return new Player("Badplayer");
    }

    /**
     * Checks all Players if the Win or Lose-Conditions are met
     */
    // wird nach jedem zug aufgerufen
    void calculatePoints() {
        String winner = null;
        for (int i = 0; i < players.length; i++) {
            int sum = players[i].calculatePoints();
            if (sum == 180) {
                winner = players[i].name;
            } else if (sum > 180) {
                players[i].tooMuchPoints = true;
                players[i].setNumOfPoints(0);
            }
        }
        if (winner != null) {
            endMatch(winner);
        }
    }

    /**
     * Calculates the Coins of all Players and resets the Points to 0
     */
    void calculateCoins() {
        for (int i = 0; i < players.length; i++) {
            Player a = players[i];
            if (a.getNumOfPoints() == 180) {
                a.setNumOfCoins(a.getNumOfCoins() + 180 * 2);
            } else if (a.getNumOfPoints() < 180) {
                a.setNumOfCoins(a.getNumOfCoins() + a.getNumOfPoints());
            }
            a.setNumOfPoints(0);
        }
    }

    /**
     * During its lifetime, this thread communicates to the clients whose turn it is
     */
    public void run() {
        try {
            distributeCards();
            while (matchEnd == false) {
                turnController.acquire();
                giveTurnToNext();
                //timer = new Timer();
                /*timer.schedule(new TimerTask() {
                    public void run() {
                        turnController.release();
                    }
                }, 10000);
                */
                checkIfPlayersIn();
            }
            if (matchEnd == true) {
                //TODO
                calculatePoints();
                endMatch("Nobody");
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void checkIfPlayersIn() {
        int playersOut = 0;
        for (Player p : players) {
            if (p.tooMuchPoints) {
                playersOut++;
            }
        }
        if (playersOut == players.length) {
            matchEnd = true;
        }
    }


}
