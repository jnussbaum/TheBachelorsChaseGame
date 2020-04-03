package tbc.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import javafx.application.Application;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import tbc.GUI.Login;
import tbc.chat.ChatClient;

/**
 * When a client is started, its main method connects the server and starts a name setting process.
 * Once the name is definitively set, the main method is finished, but the clientHandler and the
 * chatClient continue their lives.
 */
public class Client {

    private static final Logger LOGGER = LogManager.getLogger(Client.class);

    public static String userName;
    private static BufferedReader input;
    private static ClientHandler clientHandler;
    private static Thread clientHandlerThread;
    public static ChatClient chatClient;
    private static ClientGame game;

    public static ClientGame getGame() {
        return game;
    }

    /**
     * When a new Client connects to the server, he chooses his name and sends a request to the
     * server to connect with this name. The Server answers by invoking this method.
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
        game = new ClientGame(clientHandler, players);
        System.out.println("Client's startGame() was invoked");
    }

    public static void main(String[] args) {
        // run jar without a username
        if (args.length < 3) {
            userName = System.getProperty("user.name");
            LOGGER.info("Username has been set from the server: " + userName);
        } else {
            // run jar with username
            userName = args[2];
            LOGGER.info("User has been set from the client: " + userName);
        }

        String hostName = args[1].substring(0, args[1].indexOf(':'));
        LOGGER.info("Hostname: " + hostName);
        int portNumber = Integer.parseInt(args[1].substring(args[1].indexOf(':') + 1));
        LOGGER.info("Portnumber: " + portNumber);

        input = new BufferedReader(new InputStreamReader(System.in, StandardCharsets.UTF_8));

        try {
            clientHandlerThread = new Thread(clientHandler = new ClientHandler(userName, hostName, portNumber));
            chatClient = new ChatClient(clientHandler);
        } catch (Exception e) {
            LOGGER.error("Couldn't get I/O for the connection to the hostname");
        }

        try {
            clientHandlerThread.start();
            clientHandler.registerChatClient(chatClient);
            clientHandler.changeName(userName);
        } catch (Exception e) {
            LOGGER.error("Could not set the username.");
        }

        /*
        do {
            logger.info("Please enter the IP address given from the server: ");
            input = new BufferedReader(new InputStreamReader(System.in, StandardCharsets.UTF_8));
            try {
                //hostName = input.readLine();
                clientHandlerThread = new Thread(
                    clientHandler = new ClientHandler(hostName, portNumber));
                chatClientThread = new Thread(chatClient = new ChatClient(clientHandler, input));
            } catch (Exception e) {
                logger.error("Couldn't get I/O for the connection to the hostname");
            }
        } while (clientHandler.isUnknownHostname() == true);
         */

    /*
        try {
            //clientHandlerThread.start();
            //clientHandler.registerChatClient(chatClient);
            String systemName = System.getProperty("user.name");
            if (systemName.indexOf('@') != -1) {
                systemName.replace('@', '_');
            }
            System.out.println("Is your name " + systemName + "? ");
            System.out.println("Please answer with yes or no.");
            String name = null;
            String s = input.readLine();
            while (name == null) {
                if (s.equalsIgnoreCase("yes")) {
                    name = systemName;
                } else if (s.equalsIgnoreCase("no")) {
                    System.out.println("Ok, what's your name then?");
                    name = input.readLine();
                    while (name.indexOf('@') != -1 || name.length() == 0) {
                        System.out.println(
                                "The name should not be empty nor contain the '@' character." +
                                        "\nPlease try another name.");
                        name = input.readLine();
                    }
                } else {
                    System.out.println("Please answer with yes or no.");
                    name = null;
                    s = input.readLine();
                }
            }
            //clientHandler.changeName(name);
        } catch (IOException e) {
            LOGGER.error("There was an IOException when setting the username.");
        }
    */

        Application.launch(Login.class, args);
        joinALobby();
    }

    static void joinALobby() {
        try {
            clientHandler.askForLobbyList();
            System.out.println("Please type the name of an existing lobby or type a new lobby name");
            String lobby = input.readLine();
            System.out.println("A request will be sent to join the lobby " + lobby);
            clientHandler.joinLobby(lobby);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static void askToStartAGame() {
        try {
            System.out.println("Would you like to start a game? Type yes or no.");
            String answer = input.readLine();
            System.out.println(answer);
            if (answer.contains("yes")) {
                clientHandler.readyForGame();
            }
            input.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
