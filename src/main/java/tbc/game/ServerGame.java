package tbc.game;

import java.util.Timer;
import java.util.TimerTask;
import tbc.server.Lobby;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

public class ServerGame implements Runnable {

    /**
     * Here, the clients are stored in a String-Array
     */
    private String[] clientsAsArray = new String[4];

    boolean matchEnd = false;

    private Timer timer;

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
     * @param lobby: The lobby from which this game was started
     * @param clientNames: All clients who will be in this game
     */
    public ServerGame(Lobby lobby, String[] clientNames) {
        this.lobby = lobby;
        clientsAsArray = clientNames;
        players = new Player[clientNames.length];
        for (int i = 0; i < clientNames.length; i++) {
            int x= 0;
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

    public void giveCardToClient(String clientName) {
        timer.cancel();
        giveRandomCard(clientName);
        giveTurnToNext();
        System.out.println("ServerGame called giveRandomCard and giveTurnToNext");
    }

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

    public void throwCard(String clientName, String cardName) {
        //Remove the card from the client's cardset, and if not possible, print error message.
        timer.cancel();
        if (!nametoPlayer(clientName).cards.remove(Card.valueOf(cardName))) {
            System.err.println("The client " + clientName + "cannot throw away the card "
            + cardName + " because he does not have such a card.");
        }
        giveTurnToNext();
    }

    public void jumpThisTurn() {
        timer.cancel();
        giveTurnToNext();
    }

    public void giveTurnToNext() {
        System.out.println("ServerGame's method giveTurnToNext() was called. Initial values: \n" +
                "numOfDroppedOut = " + numOfDroppedOut +
                "\nactiveClient = " + activeClient);
        if(numOfDroppedOut < clientsAsArray.length) {
            if (activeClient == players.length - 1) {
                activeClient = 0;
                System.out.println("ServerGame's method giveTurnToNext() set activeClient to 0");
            } else {
                activeClient++;
                System.out.println("ServerGame's method giveTurnToNext() set activeClient++, new value = " + activeClient);
            }
            if (nametoPlayer(clientsAsArray[activeClient]).tooMuchPoints == true) {
                giveTurnToNext();
                System.out.println("ServerGame's method giveTurnToNext() called giveTurnToNext()");
            } else {
                lobby.getServerHandler(clientsAsArray[activeClient]).giveTurn();
                System.out.println("ServerGame's method giveTurnToNext() gave turn to active client = " + activeClient);
            }
        }
        else {
            System.out.println("All players dropped out of the game");
        }
    }



    void endMatch(String winnerName) {
        //Give coins to all clients
        matchEnd = true;
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
        return s.substring(0,s.length()-2);
    }

    private Player nametoPlayer(String clientName){
        for (int i = 0; i<players.length; i++){
            if(players[i].getName().equals(clientName)){
                return players[i];
            }
        }
        System.err.println("no Player with that name ");
        return new Player("Badplayer");
    }

    /**
     * Checks all Players if the Win or Lose-Conditions are met
     */
    // wird nach jedem zug aufgerufen
    void calculatePoints(){
        String winner = null;
        for(int i = 0; i<players.length; i++){
            int sum = players[i].calculatePoints();
            if (sum == 180) {
                winner = players[i].name;
            } else if (sum > 180) {
                players[i].tooMuchPoints = true;
            }
        }
        if(winner != null){
            endMatch(winner);
        }

    }

    /**
     * Calculates the Coins of all Players and resets the Points to 0
     */
    void calculateCoins(){
        for(int i = 0; i<players.length; i++){
            Player a = players[i];
            if (a.getNumOfPoints() == 180) {
                a.setNumOfCoins(a.getNumOfCoins()+180*2);
            } else if(a.getNumOfPoints() < 180){
                a.setNumOfCoins(a.getNumOfCoins()+a.getNumOfPoints());
            }
            a.setNumOfPoints(0);
        }
    }

    public void startTimer() {
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                giveTurnToNext();
            }
        };
    }

    /**
     * During its lifetime, this thread communicates to the clients whose turn it is
     */
    public void run() {
        distributeCards();
        giveTurnToNext();
        while(matchEnd == false) {
            timer = new Timer();
            timer.schedule(new TimerTask() {
                public void run() {
                    giveTurnToNext();
                }
            }, 10000);
            //TODO: Block until TimerTask finished
        }
        if (matchEnd == true) {
            //TODO
        }
    }


}
