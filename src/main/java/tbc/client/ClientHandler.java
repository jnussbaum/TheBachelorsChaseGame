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

    public static String myName;
    private Socket clientSocket;
    private ChatClient chatClient;
    private BufferedReader clientInputStream;
    private PrintWriter clientOutputStream;

    /**
     * The constructor of ClientHandler tries to connect to the server.
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
     * All which a ClientHandler-Thread makes during its lifetime is to listen to incoming information
     * on the clientInputStream, and pass this information to decode().
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
            if (s == null) System.err.println("ClientHandler " + myName + " received an empty message.");
            decode(s);
        }
    }

    /**
     * As soon as information comes in on the clientInputStream, this String is passed to decode(). This method
     * looks at the first substring (commands[0], the Network Protocol command) and then invokes the appropriate
     * methods in this object in order to process the information. The following substrings (from commands[1] on)
     * are the parameters of the Network Protocol command.
     */
    void decode(String s) {
        String[] commands = s.split("#");
        switch (commands[0]) {
            case "CHAT":
                String sender = commands[1];
                String isPrivateMessage = commands[3];
                String msg = commands[4];
                chatClient.chatArrived(sender, isPrivateMessage, msg);
                break;
            case "CHANGEOK":
                String newName = commands[1];
                Client.nameChangeFeedback(true, newName);
                myName = newName;
                break;
            case "CHANGENO":
                Client.nameChangeFeedback(false, "xyz");
                break;
            case "SENDLOBBYLIST":
                receiveLobbyList(commands);
                break;
            case "LOBBYJOINED":
                String lobbyName = commands[1];
                LOGGER.info("You joined the lobby " + lobbyName);
                Client.askToStartAGame();
                break;
            case "GIVECARD":
                String cardName = commands[1];
                Client.getGame().addCard(cardName);
                LOGGER.info("ClientHandler received card " + cardName);
                break;
            case "GAMESTARTED":
                Client.startGame(commands[1]);
                break;
            case "GIVETURN":
                Client.getGame().giveTurn();
                break;
            case "ENDMATCH":
                String winnerName = commands[1];
                Client.getGame().endMatch(winnerName);
                break;
            case "SENDCOINS":
                String allCoins = commands[1];
                Client.getGame().receiveCoins(allCoins);
            default:
                LOGGER.error("ClientHandler " + myName + "received an invalid message.");
        }
    }

    public void changeName(String userName) {
        clientOutputStream.println("CHANGENAME" + "#" + userName);
        clientOutputStream.flush();
    }

    public void sendMessage(String receiver, String isPrivateMsg, String msg) {
        clientOutputStream.println("CHAT" + "#" + myName + "#" + receiver + "#" + isPrivateMsg + "#" + msg);
        clientOutputStream.flush();
    }

    public void registerChatClient(ChatClient chatClient) {
        this.chatClient = chatClient;
    }

    public void logOut() {
        clientOutputStream.println("LOGOUT");
        clientOutputStream.flush();
    }

    void askForLobbyList() {
        clientOutputStream.println("GETLOBBYLIST");
        clientOutputStream.flush();
    }

    void createLobby(String lobbyName) {
        clientOutputStream.println("CREATELOBBY" + "#" + lobbyName);
    }

    void receiveLobbyList(String[] commands) {
        if (commands.length > 1) {
            String[] lobbies = new String[commands.length - 1];
            for (int i = 1; i < commands.length; i++) {
                lobbies[i - 1] = commands[i];
            }
            //TODO: Further processing of available lobbies --> GUI
            LOGGER.info("These are the available lobbies: ");
            for (String s : lobbies) {
                LOGGER.info(s);
            }
        } else {
            //There are no lobbies
            LOGGER.info("There are no lobbies");
        }
    }

    void joinLobby(String lobbyName) {
        clientOutputStream.println("JOINLOBBY" + "#" + lobbyName);
        clientOutputStream.flush();
    }

    void startGame() {
        clientOutputStream.println("STARTGAME");
        clientOutputStream.flush();
    }

    public void askForCard() {
        clientOutputStream.println("ASKFORCARD");
        clientOutputStream.flush();
    }

    public void throwCard(String cardName) {
        clientOutputStream.println("THROWCARD" + "#" + cardName);
        clientOutputStream.flush();
    }

    public void jumpThisTurn() {
        clientOutputStream.println("JUMPTHISTURN");
        clientOutputStream.flush();
    }

    public void readyForGame() {
        clientOutputStream.println("READYFORGAME");
        clientOutputStream.flush();
    }
}
