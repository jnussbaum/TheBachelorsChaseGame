package tbc.client;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import tbc.chat.ChatClient;

/**
 * At the beginning of his life, a client starts a clientHandler-Thread, which will be responsible
 * for all communication between the server and this client.
 */
public class ClientHandler implements Runnable {

    private static final Logger LOGGER = LogManager.getLogger(ClientHandler.class);
    public boolean rejected = false;
    private String myName;
    private Socket clientSocket;
    private ChatClient chatClient;
    private BufferedReader clientInputStream;
    private PrintWriter clientOutputStream;
    private String lobbiesGui;
    private String playersGui;
    private String highScoreGui;

    /**
     * The constructor of ClientHandler tries to connect to the server.
     *
     * @param name       The name of the user.
     * @param hostName   The hostname which the user typed in.
     * @param portNumber The port number which the user typed in.
     */
    public ClientHandler(String name, String hostName, int portNumber) {
        this.myName = name;
        try {
            clientSocket = new Socket(hostName, portNumber);
            clientInputStream = new BufferedReader(new InputStreamReader(
                new DataInputStream(clientSocket.getInputStream()), StandardCharsets.UTF_8));
            clientOutputStream = new PrintWriter(clientSocket.getOutputStream());
        } catch (UnknownHostException e) {
            LOGGER.error("Unknown hostname: " + hostName);
        } catch (IOException e) {
            LOGGER.error("IOException when creating the ClientHandler " + myName);
            e.printStackTrace();
        }
    }

    public String getMyName() {
        return myName;
    }

    /**
     * All which a ClientHandler-Thread makes during its lifetime is to listen to incoming
     * information on the clientInputStream, and pass this information to decode().
     */
    public void run() {
        while (true) {
            String s = null;
            try {
                s = clientInputStream.readLine();
            } catch (IOException e) {
                LOGGER.error("Reading from ClientInputStream failed: ");
                e.printStackTrace();
            }
            if (s == null) {
                LOGGER.error("ClientHandler " + myName + " received an empty message.");
            } else {
                decode(s);
            }
        }
    }

    /**
     * As soon as information comes in on the clientInputStream, this String is passed to decode().
     * This method looks at the first substring (commands[0], the Network Protocol command) and then
     * invokes the appropriate methods in this object in order to process the information. The
     * following substrings (from commands[1] on) are the parameters of the Network Protocol
     * command.
     */
    void decode(String s) {
        LOGGER.info("ClientHandler received message: " + s);
        String[] commands = s.split("#");
        switch (commands[0]) {
            case "CHAT":
                // handles chat messages
                String sender = commands[1];
                String isPrivateMessage = commands[3];
                String msg = commands[4];
                chatClient.chatArrived(sender, isPrivateMessage, msg);
                break;
            case "CHANGEOK":
                // feedback that the name change was successful
                String newName = commands[1];
                Client.nameChangeFeedback(true, newName);
                myName = newName;
                break;
            case "CHANGENO":
                // feedback that the name change was unsuccessful
                String name = commands[1];
                Client.nameChangeFeedback(false, name);
                break;
            case "SENDPLAYERLIST":
                // receives all players as a string
                receivePlayerList(commands);
                break;
            case "SENDLOBBYLIST":
                // receives a list of all existing lobbies
                receiveLobbyList(commands);
                break;
            case "LOBBYJOINED":
                // feedback that the named lobby was joined
                String lobbyName = commands[1];
                LOGGER.info("You joined the lobby " + lobbyName);
                break;
            case "GIVECARD":
                // receives the named card
                String cardName = commands[1];
                Client.getGame().addCard(cardName);
                break;
            case "GAMESTARTED":
                // feedback that the game started with the named players
                String players = commands[1];
                Client.startGame(players);
                break;
            case "GIVETURN":
                // message that it's this client's turn
                Client.getGame().giveTurn();
                break;
            case "ENDMATCH":
                // message that the match ended and that winnerName won
                String winnerName = commands[1];
                LOGGER.info(
                    "endmatch of ClientGame " + Client.getGame() + " was called with winnername "
                        + winnerName);
                Client.getGame().endMatch(winnerName);
                break;
            case "SENDCOINS":
                // receives the new number of coins of all players
                String allCoins = commands[1];
                Client.getGame().receiveCoins(allCoins);
                break;
            case "DROPPEDOUT":
                // message that this player dropped out of the ongoing match
                Client.getGame().droppedOut();
                break;
            case "REJECTTOJOINLOBBY":
                // message that this client cannot enter this lobby
                rejected = true;
                break;
            case "GIVEHIGHSCORE":
                // receives the new highscore list
                String data = commands[1];
                setHighScore(data);
                break;
            case "LOGOUT":
                // message that this client logged out
                System.exit(0);
                break;
            default:
                LOGGER.error("ClientHandler " + myName + " received an invalid message.");
        }
    }

    /**
     * With this method, the client tells the clientHandler which chatClient belongs to him.
     * @param chatClient The chatClient belonging to this clientHandler
     */
    public void registerChatClient(ChatClient chatClient) {
        this.chatClient = chatClient;
    }

    /**
     * Sends a request to change the name of the client
     *
     * @param userName the new name that the client wants to have
     */
    public void changeName(String userName) {
        clientOutputStream.println("CHANGENAME" + "#" + userName);
        clientOutputStream.flush();
    }

    /**
     * Sends a chat message
     *
     * @param receiver     the name of the receiver
     * @param isPrivateMsg statement if the message is private
     * @param msg          the Message the client wants to send
     */
    public void sendMessage(String receiver, String isPrivateMsg, String msg) {
        clientOutputStream
            .println("CHAT" + "#" + myName + "#" + receiver + "#" + isPrivateMsg + "#" + msg);
        clientOutputStream.flush();
    }

    /**
     * Sends the logout-request to the Server
     */
    public void logOut() {
        clientOutputStream.println("LOGOUT");
        clientOutputStream.flush();
    }

    /**
     * Sends a request to obtain a list of available lobbies
     */
    public void askForLobbyList() {
        clientOutputStream.println("GETLOBBYLIST");
        clientOutputStream.flush();
    }

    /**
     * This method takes the received String array and processes it
     *
     * @param commands the received list of lobby names
     */
    public void receiveLobbyList(String[] commands) {
        if (commands.length > 1) {
            String[] lobbies = new String[commands.length - 1];
            for (int i = 1; i < commands.length; i++) {
                lobbies[i - 1] = commands[i];
            }

            LOGGER.info("These are the available lobbies: ");
            StringBuilder lobby = new StringBuilder();
            for (String s : lobbies) {
                lobby.append(s).append("\n");
                LOGGER.info(s);
            }
            lobbiesGui = lobby.toString();
        } else {
            // there are no lobbies
            LOGGER.info("There are no lobbies");
        }
    }

    public String getLobbiesGui() {
        return lobbiesGui;
    }

    /**
     * Sends a request to the server to obtain a list of all players
     */
    public void askForPlayerList() {
        clientOutputStream.println("GETPLAYERLIST");
        clientOutputStream.flush();
    }

    /**
     * This method receives the list of all players and processes it
     *
     * @param commands the String array of all Player-names
     */
    public void receivePlayerList(String[] commands) {
        if (commands.length > 1) {
            String[] players = new String[commands.length - 1];
            for (int i = 1; i < commands.length; i++) {
                players[i - 1] = commands[i];
            }

            LOGGER.info("These are the current players: ");
            StringBuilder player = new StringBuilder();
            for (String s : players) {
                player.append(s).append("\n");
            }
            playersGui = player.toString();
            LOGGER.info(playersGui);
        } else {
            // there are no players
            LOGGER.info("There are no players");
        }
    }

    public String getPlayerListGui() {
        return playersGui;
    }

    /**
     * Sends a request to the server to join the named lobby
     *
     * @param lobbyName the Lobby-name
     */
    void joinLobby(String lobbyName) {
        clientOutputStream.println("JOINLOBBY" + "#" + lobbyName);
        clientOutputStream.flush();
    }

    /**
     * Sends a request for a card to the server, when the user clicks on "Hit"
     */
    public void askForCard() {
        clientOutputStream.println("ASKFORCARD");
        clientOutputStream.flush();
    }

    /**
     * Sends a request to the server to throw away a card
     *
     * @param cardName name of the card that shall be thrown away
     */
    public void throwCard(String cardName) {
        clientOutputStream.println("THROWCARD" + "#" + cardName);
        clientOutputStream.flush();
    }

    /**
     * Sends a request to the server to quit the ongoing match
     */
    public void quitThisMatch() {
        clientOutputStream.println("QUITTHISMATCH");
        clientOutputStream.flush();
    }

    /**
     * Sends the message to the server that the client is ready to start a game
     */
    public void readyForGame() {
        LOGGER.info("readyforgame was send");
        clientOutputStream.println("READYFORGAME");
        clientOutputStream.flush();
    }

    /**
     * Sends the message to the server that the client is ready to start a new match
     */
    public void askForNewMatch() {
        clientOutputStream.println("READYFORMATCH");
        clientOutputStream.flush();
    }

    /**
     * Sends a message to the server that the client wants to get the highscore list
     */
    public void askForHighScore() {
        clientOutputStream.println("ASKFORHIGHSCORE");
        clientOutputStream.flush();
    }

    /**
     * Saves the highscore, which this method gets as the parameter, into the variable 'highScoreGui'
     *
     * @param data the names and coins of the highscore as a String
     */
    private void setHighScore(String data) {
        highScoreGui = data;
    }

    public String getHighScoreGui() {
        return highScoreGui;
    }

    /**
     * Sends the message to the server that this player wants to cheat
     * @param points The number of points the player wants to achieve.
     */
    public void cheat(int points) {
        clientOutputStream.println("CHEAT#" + points);
        clientOutputStream.flush();
    }
}
