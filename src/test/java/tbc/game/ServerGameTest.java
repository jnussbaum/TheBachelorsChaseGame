package tbc.game;

import org.junit.Assert;
import org.junit.Test;
import tbc.chat.ChatServer;
import tbc.server.Lobby;
import tbc.server.ServerHandler;
import java.io.IOException;
import java.net.Socket;

public class ServerGameTest {

    Lobby lobby;
    ServerGame serverGame;

    public ServerGameTest() throws IOException {
        lobby = new Lobby("myLobby", new ServerHandler(
                "Player1",
                new Socket("localhost", 135),
                new ChatServer()));
        serverGame = new ServerGame(lobby, new String[]{"Player1", "Player2"});
    }

    /**
     * Tests if the int variable deckSize has the correct value.
     */
    @Test
    public void testDeckSize() {
        Assert.assertEquals(serverGame.getDeckAsArray().length, serverGame.getDeckSize());
    }

    /**
     * Tests if the deck size decreases by 1 when a card is given to a player
     */
    @Test
    public void testDeckSizeAfterGiveRandomCard() {
        int OldDeckSize = serverGame.getDeckSize();
        serverGame.giveRandomCard("Player1");
        Assert.assertEquals(OldDeckSize - 1, serverGame.getDeckAsArray().length);
    }

    /**
     * Tests if the ServerGame correctly builds the String with every player's coins in it.
     */
    @Test
    public void testAllCoinsToString() {
        Assert.assertEquals("Player1::0::Player2::0", serverGame.allCoinsToString());
    }

    /**
     * Tests if the ServerGame created its players correctly. Works only before any game action has taken place.
     */
    /*@Test
    public void testNameToPlayer() {
        Assert.assertEquals(new Player("Player1"), serverGame.nameToPlayer("Player1"));
        Assert.assertEquals(new Player("Player2"), serverGame.nameToPlayer("Player2"));
        Assert.assertEquals(new Player("Badplayer"), serverGame.nameToPlayer("Player3"));
    }

*/

}
