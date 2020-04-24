package tbc.client;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import tbc.chat.ChatClient;

import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;

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
            default:
                LOGGER.error("ClientHandler " + myName + " received an invalid message.");
        }
    }

    public void registerChatClient(ChatClient chatClient) {
        this.chatClient = chatClient;
    }

    public void changeName(String userName) {
        clientOutputStream.println("CHANGENAME" + "#" + userName);
        clientOutputStream.flush();
    }

    public void sendMessage(String receiver, String isPrivateMsg, String msg) {
        clientOutputStream
                .println("CHAT" + "#" + myName + "#" + receiver + "#" + isPrivateMsg + "#" + msg);
        clientOutputStream.flush();
    }

    public void logOut() {
        clientOutputStream.println("LOGOUT");
        clientOutputStream.flush();
    }

    public void askForLobbyList() {
        clientOutputStream.println("GETLOBBYLIST");
        clientOutputStream.flush();
    }

    void createLobby(String lobbyName) {
        clientOutputStream.println("CREATELOBBY" + "#" + lobbyName);
    }

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

    public String getLobbiesGui() {
        return lobbiesGui;
    }

    public void askForPlayerList() {
        clientOutputStream.println("GETPLAYERLIST");
        clientOutputStream.flush();
    }

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

    public String getPlayerListGui() {
        return playersGui;
    }

    void joinLobby(String lobbyName) {
        clientOutputStream.println("JOINLOBBY" + "#" + lobbyName);
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

    public void quitThisMatch() {
        clientOutputStream.println("QUITTHISMATCH");
        clientOutputStream.flush();
    }

    public void readyForGame() {
        LOGGER.info("readyforgame was send");
        clientOutputStream.println("READYFORGAME");
        clientOutputStream.flush();
    }

    public void askForNewMatch() {
        clientOutputStream.println("READYFORMATCH");
        clientOutputStream.flush();
    }
}
