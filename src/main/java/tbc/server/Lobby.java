package tbc.server;

import tbc.game.Game;
import java.util.HashMap;

/**
 * Server-side lobby, created by the Server.
 */
public class Lobby {

    /**
     * Name of this lobby as string.
     */
    private String lobbyName;

    /**
     * Administration of all clients by their name and serverHandler
     */
    private HashMap<String, ServerHandler> clients = new HashMap<>();

    /**
     * The game belonging to this lobby is stored in this variable.
     */
    private Game game;

    /**
     * This boolean stores the information whether a game is active right now or not.
     */
    private boolean gameActive = false;

    /**
     * When a new lobby is created, the serverHandler of the client who initiated this lobby
     * is added to the clients' administration.
     */
    public Lobby(String lobbyName, ServerHandler sh) {
        this.lobbyName = lobbyName;
        clients.put(sh.getName(), sh);
    }

    /**
     * A client's join request leads to the invocation of this method
     * @param clientName: Name of the client who wants to join
     * @param sh: This client's ServerHandler
     */
    void join(String clientName, ServerHandler sh) {
        if (clients.size() > 3) {
            //TODO: Tell client that this lobby is already full
        }
        if (!clients.containsKey(clientName)) {
            clients.put(clientName, sh);
            sh.lobbyJoined(lobbyName);
        } else {
            System.err.println("This client cannot join the lobby twice");
        }
    }

    void startGame() {
        game = new Game(this, (String[]) clients.keySet().toArray());
        for (ServerHandler sh : clients.values()) {
            sh.gameStarted();
        }
    }

    public ServerHandler getServerHandler(String clientName) {
        return clients.get(clientName);
    }
}
