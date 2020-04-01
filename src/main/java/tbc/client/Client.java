package tbc.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import tbc.chat.ChatClient;

/**
 * When a client is started, its main method connects the server and starts a name setting process.
 * Once the name is definitively set, the main method is finished, but the clientHandler and the
 * chatClient continue their lives.
 */
public class Client {

    private static final Logger logger = LogManager.getLogger(Client.class);
    private static String myName;
    private static boolean nameSettingSucceeded = false;
    private static BufferedReader input;
    private static ClientHandler clientHandler;
    private static Thread clientHandlerThread;
    private static ChatClient chatClient;
    private static Thread chatClientThread;
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
            myName = newName;
            nameSettingSucceeded = true;
            logger.info("Hello " + myName + ", welcome to our chat!");
        } else {
            String name;
            logger.info("This name is not available any more. Please enter another name.");
            try {
                name = input.readLine();
                while (name.indexOf('@') != -1 || name.length() == 0) {
                    logger.info("The name should not be empty nor contain the '@' character."
                        + "\nPlease try another name.");
                    name = input.readLine();
                }
                clientHandler.changeName(name);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void startGame(String player) {
        String[] players = player.split("::");
        game = new ClientGame(clientHandler, players, input);
        System.out.println("Client's startGame() was invoked");
        //TODO: oben input wieder rausnehmen
    }

    public static void main(String[] args) {
        String hostName = args[1].substring(0, args[0].indexOf(':'));
        int portNumber = Integer.parseInt(String.valueOf(args[1].indexOf(':')));
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

        try {
            clientHandlerThread.start();
            clientHandler.registerChatClient(chatClient);
            String systemName = System.getProperty("user.name");
            if (systemName.indexOf('@') != -1) {
                systemName.replace('@', '_');
            }
            logger.info("Is your name " + systemName + "? ");
            logger.info("Please answer with yes or no.");
            String name = null;
            String s = input.readLine();
            while (name == null)
                if (s.equalsIgnoreCase("yes")) {
                    name = systemName;
                } else if (s.equalsIgnoreCase("no")) {
                    logger.info("Ok, what's your name then?");
                    name = input.readLine();
                    while (name.indexOf('@') != -1 || name.length() == 0) {
                        logger.info(
                            "The name should not be empty nor contain the '@' character." +
                                "\nPlease try another name.");
                        name = input.readLine();
                    }
                } else {
                    logger.info("Please answer with yes or no.");
                    name = null;
                    s = input.readLine();
                }
            clientHandler.changeName(name);
        } catch (IOException e) {
            logger.error("There was an IOException when setting the username.");
        }

        //Wait until the name is definitively set
        while (!nameSettingSucceeded) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                //do nothing
                //not a very elegant solution. Better ideas at https://stackoverflow.com/questions/5999100/
            }
        }

        //It is important to wait until the name setting is finished, before starting the chatClientThread.
        //Otherwise the chatClientThread will listen to the System.in at the same time than Client.main() does.
        //TODO: Hier Kommentare wieder rausnehmen:
        //chatClientThread.start();

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
            if (answer.equalsIgnoreCase("yes")) {
                clientHandler.startGame();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
