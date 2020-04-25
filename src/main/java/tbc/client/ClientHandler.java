package tbc.client;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import javafx.application.Platform;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import tbc.chat.ChatClient;
import tbc.gui.RejectJoiningLobbyWindow;

/**
 * At the beginning of his life, a client starts a clientHandler-Thread, which will be responsible
 * for all communication between the server and this client.
 */
public class ClientHandler implements Runnable {

    private static final Logger LOGGER = LogManager.getLogger(ClientHandler.class);

    private String myName;
    private Socket clientSocket;
    private ChatClient chatClient;
    private BufferedReader clientInputStream;
    private PrintWriter clientOutputStream;
    private String lobbiesGui;
    private String playersGui;

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
     * following substrings (from commands[1] on) are the parameters of the Network Protocol command.
     */
    void decode(String s) {
        LOGGER.info("ClientHandler received message: " + s);
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
                String name = commands[1];
                Client.nameChangeFeedback(false, name);
                break;
            case "SENDPLAYERLIST":
                receivePlayerList(commands);
                break;
            case "SENDLOBBYLIST":
                receiveLobbyList(commands);
                break;
            case "LOBBYJOINED":
                String lobbyName = commands[1];
                LOGGER.info("You joined the lobby " + lobbyName);
                break;
            case "GIVECARD":
                String cardName = commands[1];
                Client.getGame().addCard(cardName);
                break;
            case "GAMESTARTED":
                String players = commands[1];
                Client.startGame(players);
                break;
            case "GIVETURN":
                Client.getGame().giveTurn();
                break;
            case "ENDMATCH":
                String winnerName = commands[1];
                LOGGER.info(
                        "endmatch of ClientGame " + Client.getGame() + " was called with winnername "
                                + winnerName);
                Client.getGame().endMatch(winnerName);
                break;
            case "SENDCOINS":
                String allCoins = commands[1];
                Client.getGame().receiveCoins(allCoins);
                break;
            case "LOGOUT":
                System.exit(0);
                break;
            case "DROPPEDOUT":
                Client.getGame().droppedOut();
                break;
            case "REJECTTOJOINLOBBY":
                //TODO: Show a message in GUI that the client can not join this lobby
                Platform.runLater(() -> {
                    RejectJoiningLobbyWindow.display();
                });
                break;
            default:
                LOGGER.error("ClientHandler " + myName + " received an invalid message.");
        }
    }

    public void registerChatClient(ChatClient chatClient) {
        this.chatClient = chatClient;
    }

    /**
     * Sends an request to change the name of the client
     *
     * @param userName - the name that the client wants to change in to
     */
    public void changeName(String userName) {
        clientOutputStream.println("CHANGENAME" + "#" + userName);
        clientOutputStream.flush();
    }

    /**
     * sends a chatMessage
     *
     * @param receiver - the name of the receiver
     * @param isPrivateMsg - statment if the message is private
     * @param msg - the Message the client wants to send
     */
    public void sendMessage(String receiver, String isPrivateMsg, String msg) {
        clientOutputStream
            .println("CHAT" + "#" + myName + "#" + receiver + "#" + isPrivateMsg + "#" + msg);
        clientOutputStream.flush();
    }

    /**
     * sends the logout-request
     */
    public void logOut() {
        clientOutputStream.println("LOGOUT");
        clientOutputStream.flush();
    }

    /**
     *sends a request for the Lobby-list
     */
    public void askForLobbyList() {
        clientOutputStream.println("GETLOBBYLIST");
        clientOutputStream.flush();
    }

    /**
     *
     *
     * @param lobbyName
     */
    void createLobby(String lobbyName) {
        clientOutputStream.println("CREATELOBBY" + "#" + lobbyName);
    }

    /**
     *this method takes the received String array and translates it
     *
     * @param commands - the received list of lobby names
     */
    public void receiveLobbyList(String[] commands) {
        if (commands.length > 1) {
            String[] lobbies = new String[commands.length - 1];
            for (int i = 1; i < commands.length; i++) {
                lobbies[i - 1] = commands[i];
            }

            LOGGER.info("These are the available lobbies: ");
            StringBuffer lobby = new StringBuffer();
            for (String s : lobbies) {
                lobby.append(s).append("\n");
                LOGGER.info(s);
            }
            lobbiesGui = lobby.toString();
        } else {
            //There are no lobbies
            LOGGER.info("There are no lobbies");
        }
    }

    /**
     * gets the LobbyGui name
     *
     * @return - the name of the LobbyGUI
     */
    public String getLobbiesGui() {
        return lobbiesGui;
    }

    /**
     * send an request for the PlayerList
     */
    public void askForPlayerList() {
        clientOutputStream.println("GETPLAYERLIST");
        clientOutputStream.flush();
    }

    /**
     * translate the received list
     *
     * @param commands - the list of Player-names
     */
    public void receivePlayerList(String[] commands) {
        if (commands.length > 1) {
            String[] players = new String[commands.length - 1];
            for (int i = 1; i < commands.length; i++) {
                players[i - 1] = commands[i];
            }

            LOGGER.info("These are the current players: ");
            StringBuffer player = new StringBuffer();
            for (String s : players) {
                player.append(s).append("\n");
            }
            playersGui = player.toString();
            LOGGER.info(playersGui);
        } else {
            //There are no players
            LOGGER.info("There are no players");
        }
    }

    /**
     * Gets the PlayerListGui
     * @return - the PlayerlistGUI
     */
    public String getPlayerListGui() {
        return playersGui;
    }

    /**
     * sends an request to join the named lobby
     *
     * @param lobbyName - the Lobby-name
     */
    void joinLobby(String lobbyName) {
        clientOutputStream.println("JOINLOBBY" + "#" + lobbyName);
        clientOutputStream.flush();
    }

    /**
     * send an request for an card
     */
    public void askForCard() {
        clientOutputStream.println("ASKFORCARD");
        clientOutputStream.flush();
    }

    /**
     * send an request to throw away a card
     * @param cardName - name of the card that shall be cast away
     */
    public void throwCard(String cardName) {
        clientOutputStream.println("THROWCARD" + "#" + cardName);
        clientOutputStream.flush();
    }

    /**
     * sending the server the message that the client wants to skip the
     * active match
     */
    public void quitThisMatch() {
        clientOutputStream.println("QUITTHISMATCH");
        clientOutputStream.flush();
    }

    /**
     * sending the server a message that the client is ready to start an new game
     */
    public void readyForGame() {
        LOGGER.info("readyforgame was send");
        clientOutputStream.println("READYFORGAME");
        clientOutputStream.flush();
    }
    /**
     * sending the server a message that the client is ready to start an new match
     */
    public void askForNewMatch() {
        clientOutputStream.println("READYFORMATCH");
        clientOutputStream.flush();
    }
}
