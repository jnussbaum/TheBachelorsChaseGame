package tbc.server;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import tbc.game.ServerGame;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

/**
 * Server-side lobby, created by the Server.
 */
public class Lobby {

    private static final Logger logger = LogManager.getLogger(Lobby.class);

    /**
     * Name of this lobby as string.
     */
    private String lobbyName;

    /**
     * Administration of all clients by their name and serverHandler
     */
    private HashMap<String, ServerHandler> clients = new HashMap<>();

    /**
     * Controls all clients if they are ready to start a game or not.
     */
    private ArrayList<String> readyClients = new ArrayList<>();

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
        sh.lobbyJoined(lobbyName);
    }

    /**
     * A client's join request leads to the invocation of this method
     *
     * @param clientName: Name of the client who wants to join
     * @param sh:         This client's ServerHandler
     */
    void join(String clientName, ServerHandler sh) {
        if (clients.size() > 3) {
            //TODO: Tell client that this lobby is already full
        }
        if (!clients.containsKey(clientName)) {
            clients.put(clientName, sh);
            sh.lobbyJoined(lobbyName);
        } else {
            logger.error("This client cannot join the lobby twice");
        }
    }

    void readyForGame(String myName) {
        readyClients.add(myName);
        if (readyClients.size() == clients.size() && readyClients.size() > 1) {
            startGame();
        }
    }

    void startGame() {
        logger.info("Lobby's startGame() method was invoked");
        if (isGameActive == false) {
            Object[] playersAsObj = clients.keySet().toArray();
            String[] players = Arrays.copyOf(playersAsObj, playersAsObj.length, String[].class);
            serverGame = new ServerGame(this, players);
            for (ServerHandler sh : clients.values()) {
                sh.gameStarted(players);
            }
            Thread gamethread = new Thread(serverGame);
            gamethread.start();
            isGameActive = true;
            logger.info("Lobby's startGame() method terminated successfully");
        }
    }

    public ServerHandler getServerHandler(String clientName) {
        return clients.get(clientName);
    }

    public boolean isGameActive() {
        return isGameActive;
    }
}
