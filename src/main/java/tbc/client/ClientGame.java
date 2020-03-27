package tbc.client;

import tbc.game.Card;

import java.util.ArrayList;

public class ClientGame {

    private class Player {
        String name;
        int numOfCards;
        int numOfCoins;
    }

    private ArrayList<Card> cards;
    private int points;
    private int coins;
    private Player[] players;

    public ArrayList<Card> getCards() {
        return cards;
    }

    public int getPoints() {
        return points;
    }

    public int getCoins() {
        return coins;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    public void setCoins(int coins) {
        this.coins = coins;
    }

    public void addCard(String cardName) {
        cards.add(Card.valueOf(cardName));
    }

    public void giveTurn() {
        //TODO
    }
}
