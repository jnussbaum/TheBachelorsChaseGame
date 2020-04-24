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

    //the often used variables are stored here
    //Server server = new Server();
    Lobby lobby;
    ServerGame serverGame;
    Client client1 = new Client();
    Client client2 = new Client();
    ServerHandler sh1;
    ServerHandler sh2;
    String name1;
    String name2;
    ClientGame game1;
    ClientGame game2;

    public ClientGameTest() throws java.lang.InterruptedException, java.io.FileNotFoundException {

        System.out.println("Entered the constructor of ServerGameTest");

        //Set up the expected keyboard input for the clients, defined in a txt file
        final InputStream original = System.in;
        final FileInputStream fips = new FileInputStream(new File("src/test/resources/SystemInForTest.txt"));
        System.setIn(fips);

        //start a server and two clients
        Server.main(new String[]{"Server", "8000"});
        System.out.println("Server was started");
        wait(5000);
        client1.main(new String[]{"Client", "192.168.11.113:8000"});
        System.out.println("Client1 was started");
        wait(5000);
        client2.main(new String[]{"Client", "192.168.11.113:8000"});
        System.out.println("Client2 was started");
        wait(5000);

        //write the relevant infos into variables
        lobby = Server.getLobby("myLobby");
        serverGame = lobby.serverGame;
        ServerHandler[] sh = (ServerHandler[]) Server.getClients().values().toArray();
        sh1 = sh[0];
        sh2 = sh[1];
        name1 = sh1.getName();
        name2 = sh2.getName();
        game1 = client1.getGame();
        game2 = client2.getGame();

        //set System.in back to its original
        System.setIn(original);

        System.out.println("Constructor of ServerGameTest has reached its end");
    }

    /**
     * Tests if the points and coins are calculated correctly when the players receive a random card.
     */
    @Test
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
}
