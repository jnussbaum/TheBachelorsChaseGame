package tbc.server;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.stream.Stream;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import tbc.chat.ChatServer;

/**
 * All that the Server makes during his lifetime (in the main method) is to listen to new incoming connections,
 * add them to the clients administration, and register them at the chatServer.
 */
public class Server {

    private static final Logger LOGGER = LogManager.getLogger(Server.class);

    /**
     * This HashMap administrates all clients by their name and ServerHandler.
     */
    private static final HashMap<String, ServerHandler> clients = new HashMap<>();

    /**
     * Administration of all lobbies.
     */
    private static final HashMap<String, Lobby> lobbies = new HashMap<>();

    /**
     *
     */
    private static int clientNameCount = 0;

    /**
     * Processes a client's request to change his name. If newUserName is occupied, it sends a negative
     * answer back and puts a number behind the newUserName. If newUserName is available,
     * it changes requester's name to newUserName and sends a positive answer back.
     *
     * @param requester   The user who wants to change her/his name.
     * @param newUserName The new name which the user wants to change to.
     */
    public static void changeName(String requester, String newUserName) {
        if (clients.containsKey(newUserName)) {
            clients.get(requester).giveFeedbackToChange(false, newUserName + clientNameCount);
            clientNameCount++;
        } else {
            ServerHandler sh = clients.get(requester);
            clients.remove(requester);
            sh.setName(newUserName);
            clients.put(newUserName, sh);
            clients.get(newUserName).giveFeedbackToChange(true, newUserName);
        }
    }

    /**
     * This method removes the client from the list.
     *
     * @param logoutUser The client who requested the LOGOUT.
     */
    public static void removeUser(String logoutUser) {
        clients.remove(logoutUser);
        LOGGER.info(logoutUser + " removed from the Server");
    }

    /**
     * This method returns a copy of a string array with all the players name in it.
     *
     * @return The copy of the string array from the player.
     */
    public static String[] getPlayers() {
        Object[] objArray = clients.keySet().toArray();
        return Arrays.copyOf(objArray, objArray.length, String[].class);
    }

    /**
     * Creates a new lobby and puts the first client(the one how asked to make one) in it
     *
     * @param lobbyName - the name of the new lobby
     * @param sh        - ServerHandler of the first occupant of the lobby
     */
    public static void createLobby(String lobbyName, ServerHandler sh) {
        Lobby lobby = new Lobby(lobbyName, sh);
        lobbies.put(lobbyName, lobby);
        sh.setLobby(lobby);
    }

    /**
     * gets the list of existing lobbies.
     * if der are no lobbies the list will only contain the entry: no lobbies
     *
     * @return - an string-array
     */
    public static String[] getLobbies() {
        Object[] objArray = lobbies.keySet().toArray();
        String[] stringArray = Arrays.copyOf(objArray, objArray.length, String[].class);
        return stringArray;
    }

    /**
     * returns the lobby-object
     *
     * @param lobbyName - the name of the lobby
     * @return the lobby-object
     */
    public static Lobby getLobby(String lobbyName) {
        return lobbies.get(lobbyName);
    }

    /**
     * gets the serverHandler
     *
     * @return All server handlers
     */
    public static Collection<ServerHandler> getServerHandlers() {
        return clients.values();
    }

    /**
     * add`s an clint to the clients-hashmap
     *
     * @param clientName          - the name of the client
     * @param clientServerHandler - the serverHandler of the client
     */
    public static void addClient(String clientName, ServerHandler clientServerHandler) {
        clients.put(clientName, clientServerHandler);
    }

    /**
     * puts an client in an lobby.
     * is the lobby he wants to join does not exist it will make a new one
     *
     * @param lobbyName - the name of the lobby
     * @param sh        - the serverHandler
     */
    public static void joinLobby(String lobbyName, ServerHandler sh) {
        if (lobbies.containsKey(lobbyName)) {
            Lobby lobby = lobbies.get(lobbyName);
            lobby.join(sh.getName(), sh);
            sh.setLobby(lobby);
        } else {
            Lobby lobby = new Lobby(lobbyName, sh);
            lobbies.put(lobbyName, lobby);
            sh.setLobby(lobby);
            LOGGER.info("Inexisting lobby, created it new.");
        }
    }

    /**
     * the main method
     *
     * @param args The String array with the arguments from the user. Will be split into port number.
     */
    public static void main(String[] args) {
        int portNumber = Integer.parseInt(args[1]);
        LOGGER.info("Port: " + portNumber);
        ServerSocket serverSocket;

        // This is the Headquarter of the Chat application.
        ChatServer chatServer = new ChatServer();

        try {
            serverSocket = new ServerSocket(portNumber);
            LOGGER.info("Type this address in the client after starting the client: "
                    + InetAddress.getLocalHost().getHostAddress());
            Socket socket;
            int i = 0;
            while (true) {
                socket = serverSocket.accept();
                String name = socket.getInetAddress().getHostName() + i;
                i++;
                ServerHandler serverHandler = new ServerHandler(name, socket, chatServer);
                Thread shThread = new Thread(serverHandler);
                shThread.start();
                clients.put(name, serverHandler);
                chatServer.register(name, serverHandler);
            }
        } catch (IOException e) {
            LOGGER.error("IOException while creating serverSocket or while listening to new incoming connections");
            e.printStackTrace();
        }
    }

    /**
     * gets an hashmap with all clients that are connected to the server
     *
     * @return an hashmap
     */
    public static HashMap<String, ServerHandler> getClients() {
        return clients;
    }

    /**
     * Reads from the HighScroe.txt file and pass the highscore as a String to the serverHandler.
     * '£' as a regex to split between players.
     *
     * @param sh The name of the client, used to get the serverhandler of it
     */
    public static void getHighScoreData(String sh) {
        String path = "HighScore.txt";
        StringBuilder sb = new StringBuilder();

        try (Stream<String> stream = Files.lines(Paths.get(path), StandardCharsets.UTF_8)) {
            stream.forEach(s -> {
                sb.append(s);
                sb.append("£");
            });
        }
        catch (IOException e) {
            LOGGER.error("Could not read from the HighScore.txt file.");
        }

        // String with highscore
        String data = sb.toString();

        // Check if the highscore list is empty. If yes, then give 'data' a String
        if (data.isEmpty()) {
            data = "No highscore";
            clients.get(sh).giveHighScore(data);
        } else { // 'data' is not empty
            clients.get(sh).giveHighScore(data);
        }
    }
}