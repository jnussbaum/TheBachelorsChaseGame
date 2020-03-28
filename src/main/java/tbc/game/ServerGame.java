package tbc.game;

import tbc.server.Lobby;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

public class ServerGame implements Runnable {

    /**
     * Here, the clients are stored in a String-Array
     */
    private String[] clientsAsArray = new String[4];

    /**
     * The lobby from which this game was started.
     */
    private Lobby lobby;

    /**
     * Administration of the clients in this game with their respective cardset.
     */
    private HashMap<String, ArrayList<Card>> clients;

    /**
     * Administration of the points of each client.
     */
    private HashMap<String, Integer> clientpoints;

    /**
     * Administration of the coins of each client.
     */
    private HashMap<String, Integer> clientcoins;

    /**
     * Administration of all cards which are not yet distributed to a client.
     * Card: Type of card
     * Integer: number of cards of this type which are still available.
     */
    private HashMap<Card, Integer> cardDeck;

    /**
     * Counts through the clients and represents which client's turn it is.
     */
    private int activeClient = 0;

    /**
     * Create a game and initialize the carddeck
     * @param lobby: The lobby from which this game was started
     * @param clientNames: All clients who will be in this game
     */
    public ServerGame(Lobby lobby, String[] clientNames) {
        this.lobby = lobby;
        clientsAsArray = clientNames;
        for (int i = 0; i < clientNames.length; i++) {
            clients.put(clientNames[i], new ArrayList<Card>());
        }
        //Fill the carddeck with the specified number of cards per card type
        cardDeck.put(Card.Plagiarism, 5);
        cardDeck.put(Card.Party, 5);
        cardDeck.put(Card.Coffee, 10);
        cardDeck.put(Card.RedBull, 10);
        cardDeck.put(Card.WLAN, 10);
        cardDeck.put(Card.Study, 5);
        cardDeck.put(Card.GoodLecturer, 2);

        startMatch();
    }

    void startMatch() {
        Thread matchThread = new Thread(new ServerMatch(this));
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
        for (int i = 0; i < clientsAsArray.length; i++) {
            String clientName = clientsAsArray[i];
            giveRandomCard(clientName);
        }
    }

    public void giveCardToClient(String clientName) {
        giveRandomCard(clientName);
        giveTurnToNext();
    }

    public void giveRandomCard(String clientName) {
        //Choose a random card
        Random random = new Random();
        int randomInt = random.nextInt(getDeckSize());
        String randomCardName = getDeckAsArray()[randomInt];

        //Give the chosen random card to the client
        clients.get(clientName).add(Card.valueOf(randomCardName));
        lobby.getServerHandler(clientName).giveCard(randomCardName);

        //Reset the number of available cards in the carddeck
        int pos = cardDeck.get(randomCardName);
        cardDeck.put(Card.valueOf(randomCardName), pos - 1);
    }

    public void throwCard(String clientName, String cardName) {
        //Remove the card from the client's cardset, and if not possible, print error message.
        if (!clients.get(clientName).remove(Card.valueOf(cardName))) {
            System.err.println("The client " + clientName + "cannot throw away the card "
            + cardName + " because he does not have such a card.");
        }
        giveTurnToNext();
    }

    public void giveTurnToNext() {
        if (activeClient == 3) {
            activeClient = 0;
        } else {
            activeClient++;
        }
        lobby.getServerHandler(clientsAsArray[activeClient]).giveTurn();
    }

    void calculatePoints() {
        //Iterate through all clients
        for (int i = 0; i < clientsAsArray.length; i++) {
            String clientName = clientsAsArray[i];
            ArrayList<Card> cardlist = clients.get(clientName);

            //Iterate through a client's cards
            int sum = 0;
            for (Card c : cardlist) {
                sum += c.getValue();
            }
            clientpoints.put(clientName, sum);
            if (sum == 180) {
                endMatch(clientName);
            } else if (sum > 180) {
                //TODO
            }
        }
    }

    void endMatch(String winnerName) {
        //Give coins to all clients
        for (int i = 0; i < clientsAsArray.length; i++) {
            String clientName = clientsAsArray[i];
            int oldcoins = clientcoins.get(clientName);
            int points = clientpoints.get(clientName);
            if (points < 180) {
                clientcoins.put(clientName, oldcoins + points);
            } else if (points == 180) {
                clientcoins.put(clientName, oldcoins + (points * 2));
            } else if (points > 180) {
                clientcoins.put(clientName, oldcoins);
            }
        }
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
            s = s + clientcoins.get(clientName) + "::";
        }
        return s;
    }

    /**
     * During its lifetime, this thread communicates to the clients whose turn it is
     */
    public void run() {
        //lobby.getServerHandler(/*client*/.giveTurn(); //TODO
        //wait(10000);
        //calculatePoints();
    }
}
