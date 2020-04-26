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

    private static final Logger LOGGER = LogManager.getLogger();
    public String lobbyName;
    ArrayList<String> readyGameClients = new ArrayList<>();
    ArrayList<String> readyMatchClients = new ArrayList<>();

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
    boolean isGameActive = false;

    /**
     * When a new lobby is created, the serverHandler of the client
     * who initiated this lobby is added to the clients' administration.
     *
     * @param lobbyName The name of the lobby.
     * @param sh        The server handler from the client who joins the lobby.
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
    public void join(String clientName, ServerHandler sh) {
        if (clients.size() > 3 || isGameActive || clients.containsKey(clientName)) {
            sh.reject();
        } else {
            clients.put(clientName, sh);
            sh.lobbyJoined(lobbyName);
        }
    }

    /**
     * When a player is ready to start a game and clicks his "Ready" button, this method will be called.
     *
     * @param myName: Name of the player who is ready
     */
    void readyForGame(String myName) {
        if (clients.containsKey(myName)) {
            readyGameClients.add(myName);
        } else {
            LOGGER.error(myName + " is not a client in this lobby, and cannot be ready therefore.");
        }
        if (readyGameClients.size() == clients.size() && readyGameClients.size() > 1) {
            startGame();
        }
    }

    /**
     * When a player is ready to start a new match and clicks his "Ready" button, this method will be called.
     *
     * @param myName: Name of the player who is ready
     */
    void readyForMatch(String myName) {
        if (clients.containsKey(myName)) {
            readyMatchClients.add(myName);
        } else {
            LOGGER.error(myName + " is not a client in this lobby, and cannot be ready therefore.");
        }
        if (readyMatchClients.size() == clients.size() && readyMatchClients.size() > 1) {
            serverGame.startMatchAgain();
            LOGGER.info("everyone is ready and the next match is starting");
            readyMatchClients.clear();
        }
    }

    /**
     * As soon as all clients in the lobby are ready, the game is started.
     */
    void startGame() {
        LOGGER.info("Lobby's startGame() method was invoked");
        if (isGameActive == false) {
            Object[] playersAsObj = clients.keySet().toArray();
            String[] players = Arrays.copyOf(playersAsObj, playersAsObj.length, String[].class);
            serverGame = new ServerGame(this, players);
            for (ServerHandler sh : clients.values()) {
                sh.gameStarted(players);
            }
            Thread gameThread = new Thread(serverGame);
            gameThread.start();
            isGameActive = true;
            LOGGER.info("Lobby's startGame() method terminated successfully");
        }
    }

    public ServerHandler getServerHandler(String clientName) {
        return clients.get(clientName);
    }

    public String getLobbyName() {
        return lobbyName;
    }

    public HashMap<String, ServerHandler> getClients() {
        return clients;
    }

    /**
     * When a client logs out, he is removed from the lobby and the serverGame.
     *
     * @param name: Name of the client who logged out
     */
    public void logout(String name) {
        clients.remove(name);
        serverGame.logout(name);
    }
}
