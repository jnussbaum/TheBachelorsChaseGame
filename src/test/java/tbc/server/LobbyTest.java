package tbc.server;

import org.junit.Assert;
import org.junit.Test;

import java.util.HashMap;

public class LobbyTest {

    /**
     * This method tests if a client who already is in a lobby will be rejected if he wants to enter again.
     */
    @Test
    public void testJoin() {
        //extract the HashMap with all clients in the lobby, and pick one client out
        Lobby lobby = Server.getLobby("myLobby");
        HashMap<String, ServerHandler> oldClients = lobby.getClients();
        String client1 = (String) oldClients.keySet().toArray()[0];
        ServerHandler sh1 = oldClients.get(client1);

        //the picked client, who already is in the lobby, tries to reenter
        lobby.join(client1, sh1);

        //check if the clients in the lobby remained the same
        Assert.assertEquals(oldClients, lobby.getClients());
    }
}
