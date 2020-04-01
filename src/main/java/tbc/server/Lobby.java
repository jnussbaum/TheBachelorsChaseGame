package tbc.server;

import tbc.game.ServerGame;

import java.util.Arrays;
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
    public ServerGame serverGame;

    /**
     * This boolean stores the information whether a game is active right now or not.
     */
    private boolean isGameActive = false;

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
        System.out.println("Lobby's startGame() method was invoked");
        if (isGameActive == false) {
            Object[] playersAsObj = clients.keySet().toArray();
            String [] players = Arrays.copyOf(playersAsObj, playersAsObj.length, String[].class);
            serverGame = new ServerGame(this, players);
            for (ServerHandler sh : clients.values()) {
                sh.gameStarted(players);
            }
            Thread gamethread = new Thread(serverGame);
            gamethread.start();
            isGameActive = true;
            System.out.println("Lobby's startGame() method terminated successfully");
        }
    }

    public ServerHandler getServerHandler(String clientName) {
        return clients.get(clientName);
    }

    public boolean isGameActive() {
        return isGameActive;
    }
}
