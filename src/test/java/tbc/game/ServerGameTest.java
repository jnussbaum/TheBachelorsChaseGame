package tbc.game;

import org.junit.Assert;
import org.junit.Test;
import tbc.client.Client;
import tbc.client.ClientGame;
import tbc.server.Lobby;
import tbc.server.Server;
import tbc.server.ServerHandler;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

public class ServerGameTest {

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

    public ServerGameTest() throws java.lang.InterruptedException, java.io.FileNotFoundException {

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
     * Tests if the int variable deckSize has the correct value.
     */
    @Test
    public void testDeckSize() {
        String[] deckAsArray = serverGame.getDeckAsArray();
        int deckSize = serverGame.getDeckSize();
        Assert.assertEquals(deckAsArray.length, deckSize);
    }

    /**
     * Tests if the ServerGame correctly builds the String with every player's coins in it.
     */
    @Test
    public void testAllCoinsToString() {
        //expected coins: 0 each
        String allCoins = serverGame.allCoinsToString();
        String expected = name1 + "::0::" + name2 + "::0";
        Assert.assertEquals(expected, allCoins);
    }

    /**
     * Tests if the ServerGame created its players correctly. Works only before any game action has taken place.
     * The expected players are pseudoClient1 and pseudoClient2, but not any more.
     */
    @Test
    public void testNametoPlayer() {
        Player player1 = new Player(name1);
        Player player2 = new Player(name2);
        Player badplayer = new Player("Badplayer");
        Assert.assertEquals(player1, serverGame.nametoPlayer(name1));
        Assert.assertEquals(player2, serverGame.nametoPlayer(name2));
        Assert.assertEquals(badplayer, serverGame.nametoPlayer("pseudoClient3"));
    }


    /**
     * Tests if the points and coins stored in the ServerGame are equal
     * to the points and coins stored in the ClientGames.
     */
    @Test
    public void testServerClientEquality() {
        Assert.assertEquals(serverGame.nametoPlayer(name1).numOfCoins, game1.nameToPlayer(name1).getNumOfCoins());
        Assert.assertEquals(serverGame.nametoPlayer(name2).numOfCoins, game2.nameToPlayer(name2).getNumOfCoins());
        Assert.assertEquals(serverGame.nametoPlayer(name1).numOfPoints, game1.nameToPlayer(name1).getNumOfPoints());
        Assert.assertEquals(serverGame.nametoPlayer(name2).numOfPoints, game1.nameToPlayer(name2).getNumOfPoints());
    }
}
