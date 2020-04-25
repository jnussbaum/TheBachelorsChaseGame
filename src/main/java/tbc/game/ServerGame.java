package tbc.game;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import tbc.server.Lobby;

import java.util.HashMap;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Semaphore;

public class ServerGame implements Runnable {

    private static final Logger LOGGER = LogManager.getLogger();
    private final int THROWCOST = 10; //number of coins you pay to throw away a card
    /**
     * The lobby from which this game was started.
     */
    private final Lobby lobby;
    /**
     * Administration of the clients in this game with their respective cardset. Administration of the
     * coins of each client. Administration of the points of each client.
     */
    private Player[] players;
    /**
     * Administration of all cards which are not yet distributed to a client. Card: Type of card
     * Integer: number of cards of this type which are still available.
     */
    private final HashMap<Card, Integer> cardDeck = new HashMap<>();
    volatile boolean matchEnd = false;
    boolean winner = false;
    /**
     * Here, the clients are stored in a String-Array
     */
    private String[] clientsAsArray = new String[4];
    private Timer timer;
    private Semaphore turnController;
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
        restackDeck();
    }

    public int getDeckSize() {
        int n = 0;
        for (int i : cardDeck.values()) {
            n += i;
        }
        return n;
    }

  /**
   * restacks the cands in the carddeck
   */
  void restackDeck() {
        cardDeck.put(Card.Plagiarism, 2);
        cardDeck.put(Card.Party, 10);
        cardDeck.put(Card.Coffee, 10);
        cardDeck.put(Card.RedBull, 10);
        cardDeck.put(Card.WLAN, 10);
        cardDeck.put(Card.Study, 5);
        cardDeck.put(Card.GoodLecturer, 2);
    }

    /**
     * Get the deck as String-array which contains every single card, one per array position. The
     * cards are not mixed, but grouped together by their type.
     */
    public String[] getDeckAsArray() {
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
     * Helper method to give a random card to a client. This method is invoked by other methods who
     * want to give out cards.
     */
    public void giveRandomCard(String clientName) {
        //Choose a random card
        Random random = new Random();
        int randomInt = random.nextInt(getDeckSize());
        String randomCardName = getDeckAsArray()[randomInt];

        //Give the chosen random card to the client
        nameToPlayer(clientName).cards.add(Card.valueOf(randomCardName));
        lobby.getServerHandler(clientName).giveCard(randomCardName);

        //Reset the number of available cards in the cardDeck
        int pos = cardDeck.get(Card.valueOf(randomCardName));
        cardDeck.put(Card.valueOf(randomCardName), pos - 1);
    }

    /**
     * First of the three possibilities when it is a client's turn: Give a card to the client.
     */
    public void giveCardToClient(String clientName) {
        timer.cancel();
        giveRandomCard(clientName);
        turnController.release();
        LOGGER.info("ServerGame called giveRandomCard");
        calculatePoints();
    }

    /**
     * Second of the three possibilities when it is a client's turn: Quit this match.
     */
    public void quitThisMatch(String clientName) {
        timer.cancel();
        calculatePoints();
        nameToPlayer(clientName).setQuitMatch(true);
        turnController.release();
    }

    /**
     * Third of the three possibilities when it is a client's turn: throw away a card.
     */
    public void throwCard(String clientName, String cardName) {
        timer.cancel();
        //Remove the card from the client's cardset, and if not possible, print error message.
        if (!nameToPlayer(clientName).cards.remove(Card.valueOf(cardName))) {
            LOGGER.error("The client " + clientName + "cannot throw away the card " + cardName
                    + " because he does not have such a card.");
        }
        LOGGER
                .info("number of coins pre-calculatePoints: " + nameToPlayer(clientName).getNumOfPoints());
        calculatePoints();
        LOGGER.info(
                "number of coins after calculatePoints: " + nameToPlayer(clientName).getNumOfPoints());
        //Subtract coins from player
        nameToPlayer(clientName).setNumOfCoins(nameToPlayer(clientName).getNumOfCoins() - THROWCOST);
        turnController.release();
    }

  /**
   * updates the number of dropped out players
   */
  void updateDroppedOut() {
        int i = 0;
        for (Player p : players) {
            if (p.quitMatch) {
                i++;
            }
        }
        numOfDroppedOut = i;
    }

  /**
   * determinants and gives the turn to the next player
   */
  public void giveTurnToNext() {
        updateDroppedOut();
        LOGGER.info(
                "ServerGame's method giveTurnToNext() was called. numOfDroppedOut = " + numOfDroppedOut);
        if (winner == false) {
            if (numOfDroppedOut < clientsAsArray.length) {
                //Find out the number of the next client
                if (activeClient == players.length - 1) {
                    activeClient = 0;
                } else {
                    activeClient++;
                }
                //Give the turn to the client whose number was set before
                if (nameToPlayer(clientsAsArray[activeClient]).quitMatch == true) {
                    giveTurnToNext();
                } else {
                    lobby.getServerHandler(clientsAsArray[activeClient]).giveTurn();
                    System.out.println("ServerGame's method giveTurnToNext() gave turn to "
                            + clientsAsArray[activeClient]);
                }
            } else {
                System.out.println("All players dropped out of the game");
                endMatch("NoBody");
            }
        }
    }


  /**
   * is called to execute the routine at the end of an match
   *
   * @param winnerName - the name of the winner ot the match
   */
  void endMatch(String winnerName) {
        LOGGER.info("endMatch has been called");
        matchEnd = true;
        winner = true;
        //Give coins to all clients
        calculateCoins();
        // Tell all clients that XY won and that they receive now their coins
        for (int i = 0; i < clientsAsArray.length; i++) {
            String clientName = clientsAsArray[i];
            lobby.getServerHandler(clientName).sendCoins(allCoinsToString());
            lobby.getServerHandler(clientName).endMatch(winnerName);
            LOGGER.info("coins and winnername have been sent to " + clientName);
        }
        LOGGER.info("endMatch has sent the coins and the winnername " + winnerName + " to all clients");
    }

  /**
   * takes all the coins of the players and generates an string
   *
   * @return the string
   */
  String allCoinsToString() {
        String s = "";
        for (int i = 0; i < clientsAsArray.length; i++) {
            String clientName = clientsAsArray[i];
            s = s + clientName + "::";
            s = s + players[i].getNumOfCoins() + "::";
        }
        return s.substring(0, s.length() - 2);
    }

  /**
   * takes a string and serches for the matching Player-object
   *
   * @param clientName - the name of the client
   * @return - the player-object
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
     * Checks all Players if the Win or Lose-Conditions are met. This method is always called after
     * every turn.
     */
    void calculatePoints() {
        String winner = null;
        for (int i = 0; i < players.length; i++) {
            int sum = players[i].calculatePoints();
            if (sum == 180) {
                winner = players[i].name;
            } else if (sum > 180) {
                players[i].quitMatch = true;
                players[i].setNumOfPoints(0);
            }
        }
        if (winner != null) {
            LOGGER.info("calculatePoints has determined a winner");
            endMatch(winner);
        }
    }

    /**
     * Calculates the Coins of all Players and resets the Points to 0
     */
    void calculateCoins() {
        LOGGER.info("Coins will be calculated");
        for (int i = 0; i < players.length; i++) {
            Player a = players[i];
            if (a.getNumOfPoints() == 180) {
                a.setNumOfCoins(a.getNumOfCoins() + 180 * 2);
            } else if (a.getNumOfPoints() < 180) {
                if (a.getNumOfPoints() < 50) {
                    a.setNumOfCoins(a.getNumOfCoins());
                } else {
                    a.setNumOfCoins(a.getNumOfCoins() + a.getNumOfPoints() - 50);
                }
            }
            a.setNumOfPoints(0);
        }
    }

  /**
   * preparing and restarting the match
   */
  public void startMatchAgain() {
        turnController = new Semaphore(1);
        reset();
        LOGGER.info("Match has bin started again");
        winner = false;
        matchEnd = false;
        restackDeck();
    }

  /**
   * resets important values
   */
  private void reset() {
        for (Player p : players) {
            p.setNumOfPoints(0);
            p.clearCards();
            p.setQuitMatch(false);
            p.setNotifyDropOut(false);
            LOGGER.info(p.getName() + " Has been resetted");
            LOGGER.info(p.numOfPoints + " is the number of his points");
        }
    }

    public void setMatchEnd(boolean b) {
        matchEnd = b;
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
                    while (matchEnd == false) {
                        turnController.acquire();
                        giveTurnToNext();
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
   * handels an logout in the Game-logic
   *
   * @param name - the name of the Client that wants to leave
   */
    public void logout(String name) {
        Player[] newPlayers = new Player[players.length - 1];
        int a = 0;
        for (int i = 0; i < players.length; i++) {
            if (players[i].getName().equals(name) == false) {
                newPlayers[a] = players[i];
                a++;
            }
        }
        players = newPlayers;

        String[] newClients = new String[clientsAsArray.length - 1];
        int b = 0;
        for (int i = 0; i < clientsAsArray.length; i++) {
            if (clientsAsArray[i].equals(name) == false) {
                newClients[b] = clientsAsArray[i];
                b++;
            }
        }
        clientsAsArray = newClients;
    }
}
