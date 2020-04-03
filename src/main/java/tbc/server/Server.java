package tbc.server;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import tbc.chat.ChatServer;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;

/**
 * All that the Server makes during his lifetime (in the main method) is to listen to new incoming connections,
 * add them to the clients administration, and register them at the chatServer.
 */
public class Server {

		private static final Logger LOGGER = LogManager.getLogger(Server.class);

    /**
     * This HashMap administrates all clients by their name and ServerHandler.
     */
    private static HashMap<String, ServerHandler> clients = new HashMap<>();

    /**
     * Administration of all lobbies.
     */
    private static HashMap<String, Lobby> lobbies = new HashMap<>();

    private static int clientNameCount = 0;

    /**
     * Processes a client's request to change his name. If newUserName is occupied, it sends a negative
     * answer back. If newUserName is available, it changes requester's name to newUserName and sends a
     * positive answer back.
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
        //TODO: Inform other clients that this client logged out.
        LOGGER.info(logoutUser + " removed from the Server");
    }

    public static void createLobby(String lobbyName, ServerHandler sh) {
        Lobby lobby = new Lobby(lobbyName, sh);
        lobbies.put(lobbyName, lobby);
        sh.setLobby(lobby);
    }

    public static String[] getLobbies() {
        Object[] objArray = lobbies.keySet().toArray();
        String[] stringArray = Arrays.copyOf(objArray, objArray.length, String[].class);
        return stringArray;
    }

    public static Lobby getLobby(String lobbyName) {
        return lobbies.get(lobbyName);
    }

    public static Collection<ServerHandler> getServerHandlers() {
        return clients.values();
    }

    public static void addClient(String clientName, ServerHandler clientServerHandler) {
        clients.put(clientName, clientServerHandler);
    }

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

    public static void main(String[] args) {
        int portNumber = Integer.parseInt(args[1]);
        LOGGER.info("Port: " + portNumber);
        ServerSocket serverSocket;

        //This is the Headquarter of the Chat application.
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
						LOGGER.error("IOException while creating serverSocket or while listening " +
								"to new incoming connections");
						e.printStackTrace();
				}
		}

}