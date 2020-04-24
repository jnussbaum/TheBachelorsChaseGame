package tbc.client;

import org.junit.Assert;
import org.junit.Test;
import tbc.game.Card;
import tbc.game.ServerGame;
import tbc.server.Lobby;
import tbc.server.Server;
import tbc.server.ServerHandler;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;

public class ClientGameTest {

    ClientGame clientGame;

    /**
     * Tests if the points and coins are calculated correctly when the players receive a random card.
     */
    /*@Test
    public void testGiveCard() {
        //Save the old coins, points, and cards of both players
        int client1coins = game1.nameToPlayer(name1).getNumOfCoins();
        int client2coins = game2.nameToPlayer(name2).getNumOfCoins();
        int client1points = game1.getPoints();
        int client2points = game2.getPoints();
        ArrayList<Card> client1cards = game1.getCards();
        ArrayList<Card> client2cards = game2.getCards();

        //Change the points by giving a card to each player
        serverGame.giveRandomCard(name1);
        serverGame.giveRandomCard(name2);

        //Save the new coins, points, and the just received card of both players
        int client1newCoins = game1.nameToPlayer(name1).getNumOfCoins();
        int client2newCoins = game2.nameToPlayer(name2).getNumOfCoins();
        int client1newPoints = game1.getPoints();
        int client2newPoints = game2.getPoints();
        ArrayList<Card> cards1 = (ArrayList<Card>) game1.getCards().clone();
        cards1.removeAll(client1cards);
        Card client1newCard = cards1.get(0);
        ArrayList<Card> cards2 = (ArrayList<Card>) game2.getCards().clone();
        cards2.removeAll(client2cards);
        Card client2newCard = cards2.get(0);

        //calculate the expected difference between old and new points
        int client1diffPoints = client1newCard.getValue();
        int client2diffPoints = client2newCard.getValue();

        //Check if the coins remained the same
        Assert.assertEquals(client1coins, client1newCoins);
        Assert.assertEquals(client2coins, client2newCoins);

        //Check if the difference of points is correct
        Assert.assertEquals(client1points, client1newPoints + client1diffPoints);
        Assert.assertEquals(client2points, client2newPoints + client2diffPoints);
    }
     */
}
