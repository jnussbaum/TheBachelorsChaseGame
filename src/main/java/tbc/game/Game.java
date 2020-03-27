package tbc.game;

import tbc.server.Lobby;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

public class Game implements Runnable {

    /**
     * The lobby from which this game was started.
     */
    private Lobby lobby;

    /**
     * Administration of the clients as a String-Array.
     */
    private String[] clientsAsArray;

    /**
     * Administration of the clients in this game with their respective cardset.
     */
    private HashMap<String, ArrayList<Card>> clients;

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
    public Game(Lobby lobby, String[] clientNames) {
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
    void distributeCards() {
        Random random = new Random();
        for (int i = 0; i < clientsAsArray.length; i++) {
            //Find out the client's  name who will receive a card
            String clientName = clientsAsArray[i];
            ArrayList<Card> cardsOfClient = clients.get(clientName);

            //Choose a random card
            int randomInt = random.nextInt(getDeckSize());
            String randomCardName = getDeckAsArray()[randomInt];

            //Give the chosen random card to the client
            cardsOfClient.add(Card.valueOf(randomCardName));
            lobby.getServerHandler(clientName).giveCard(randomCardName);

            //Reset the number of available cards in the carddeck
            int pos = cardDeck.get(randomCardName);
            cardDeck.put(Card.valueOf(randomCardName), pos - 1);
        }
    }

    /**
     * During its lifetime, this thread communicates to the clients whose turn it is
     */
    public void run() {
        lobby.getServerHandler(clientsAsArray[0]).giveTurn();

    }
}
