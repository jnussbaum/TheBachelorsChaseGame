package tbc.server;

import org.junit.Assert;
import org.junit.Test;
import tbc.chat.ChatServer;
import tbc.game.ServerGame;

import java.io.IOException;
import java.net.Socket;
import java.util.HashMap;

public class LobbyTest {

    Lobby lobby;
    ServerGame serverGame;

    public LobbyTest() throws IOException {
        lobby = new Lobby("myLobby", new ServerHandler(
                "Player1",
                new Socket("localhost", 135),
                new ChatServer()));
        lobby.join("Player2", new ServerHandler(
                "Player2",
                new Socket("localhost", 135),
                new ChatServer()));
        serverGame = new ServerGame(lobby, new String[]{"Player1", "Player2"});
    }

    /**
     * This method tests if a client who already is in a lobby will be rejected if he wants to enter again.
     */
    @Test
    public void testDoubleJoin() {
        //extract the HashMap with all clients in the lobby, and pick one client out
        HashMap<String, ServerHandler> oldClients = lobby.getClients();
        String name1 = (String) oldClients.keySet().toArray()[0];
        ServerHandler sh1 = oldClients.get(name1);

        //the picked client, who already is in the lobby, tries to reenter
        lobby.join(name1, sh1);

        //check if the clients in the lobby remained the same
        Assert.assertEquals(oldClients, lobby.getClients());
    }

    /**
     * This method tests if the lobby correctly rejects new players when a game is active.
     */
    @Test
    public void testJoinWhenGameIsActive() {

    }

    /**
     * Tests if a client who is not in the lobby can be registered as ready for a game
     */
    @Test
    public void testReadyForGame() {
        int oldSize = lobby.readyGameClients.size();
        lobby.join("InvalidPlayer", new ServerHandler(
                "InvalidPlayer",
                new Socket(null),
                new ChatServer()));
    }

    /**
     * Tests if a client who is not in the lobby can be registered as ready for a match
     */
    @Test
    public void testReadyForMatch() {
        readyGameClients
    }


}
