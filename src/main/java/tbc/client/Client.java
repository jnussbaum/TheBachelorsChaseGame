package tbc.client;

import javafx.application.Application;
import javafx.application.Platform;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import tbc.chat.ChatClient;
import tbc.gui.LobbyController;
import tbc.gui.Login;


public class Client {

    private static final Logger LOGGER = LogManager.getLogger(Client.class);
    public static String userName;
    public static ClientHandler clientHandler;
    public static ChatClient chatClient;
    public static ClientGame game;
    private static Thread clientHandlerThread;

    public static ClientGame getGame() {
        return game;
    }

    /**
     * When a new Client connects to the server, he chooses his name and sends a request to the server
     * to connect with this name. The Server answers by invoking this method.
     *
     * @param feedback: Answer yes/no if the name changing succeeded or not.
     * @param newName:  The new name.
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

    /**
     * Answer from the Server to this client that a game starts now.
     *
     * @param player: String with all players in this game
     */
    public static void startGame(String player) {
        String[] players = player.split("::");
        game = new ClientGame(clientHandler, players);
        LOGGER.info("Client's startGame() was invoked");
        Platform.runLater(() -> {
            LobbyController.gameWindowController.setHighScore();
        });
    }

  /**
   * try`s to connect to the server and starts the game-window
   */
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

    /**
     * Send a request to the server to join a lobby
     *
     * @param lobby: The lobby which should be joined
     */
    public static void joinALobby(String lobby) {
        LOGGER.info("A request will be sent to join the lobby " + lobby);
        clientHandler.joinLobby(lobby);
    }

    /**
     * Send a request to the server to start a game.
     */
    public static void askToStartAGame() {
        clientHandler.readyForGame();
    }

}
