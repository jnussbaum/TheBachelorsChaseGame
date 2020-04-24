package tbc.client;

import javafx.application.Application;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import tbc.chat.ChatClient;
import tbc.gui.Login;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

/**
 * When a client is started, its main method connects the server and starts a name setting process.
 * Once the name is definitively set, the main method is finished, but the clientHandler and the
 * chatClient continue their lives.
 */
public class Client {

    private static final Logger LOGGER = LogManager.getLogger(Client.class);

    public static String userName;
    public static ClientHandler clientHandler;
    public static ChatClient chatClient;
    public static ClientGame game;
    private static BufferedReader input;
    private static Thread clientHandlerThread;

    public static ClientGame getGame() {
        return game;
    }

    /**
     * When a new Client connects to the server, he chooses his name and sends a request to the server
     * to connect with this name. The Server answers by invoking this method.
     */
    public static void nameChangeFeedback(boolean feedback, String newName) {
        if (feedback) {
            userName = newName;
            LOGGER.info("Hello " + userName + ", welcome to our chat!");
        } else {
            LOGGER.error("This name is not available any more. "
                    + "Your name has been set from the server.");
            clientHandler.changeName(newName);
        }
    }

    public static void startGame(String player) {
        String[] players = player.split("::");
        game = new ClientGame(clientHandler, players, input);
        System.out.println("Client's startGame() was invoked");
    }

    public static void main(String[] args) {
        // Run jar without a username. Username is set from the server as the system name.
        if (args.length < 3) {
            userName = System.getProperty("user.name");
            LOGGER.info("Username has been set from the server: " + userName);
        } else {
            // Run jar with username
            userName = args[2];
            LOGGER.info("Username has been set from the client: " + userName);
        }

        String hostName = args[1].substring(0, args[1].indexOf(':'));
        LOGGER.info("Hostname: " + hostName);
        int portNumber = Integer.parseInt(args[1].substring(args[1].indexOf(':') + 1));
        LOGGER.info("Portnumber: " + portNumber);

        input = new BufferedReader(new InputStreamReader(System.in, StandardCharsets.UTF_8));

        try {
            clientHandlerThread = new Thread(
                    clientHandler = new ClientHandler(userName, hostName, portNumber));
            chatClient = new ChatClient(clientHandler);
        } catch (Exception e) {
            LOGGER.error("Couldn't get I/O for the connection to the hostname");
        }

        try {
            clientHandlerThread.start();
            clientHandler.registerChatClient(chatClient);
            // Check if username is already being used
            clientHandler.changeName(userName);
            Application.launch(Login.class, args);
        } catch (Exception e) {
            LOGGER.error("Could not set the username.");
        }
    }

    public static void joinALobby(String lobby) {
        LOGGER.info("A request will be sent to join the lobby " + lobby);
        clientHandler.joinLobby(lobby);
    }

    public static void askToStartAGame() {
        clientHandler.readyForGame();
    }

}
