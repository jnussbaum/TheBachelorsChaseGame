package tbc.server;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import tbc.chat.ChatServer;

/**
 * As soon as a new client connects to the server, the server starts a new ServerHandler-Thread,
 * which will be responsible for the communication between the server and this client.
 */
public class ServerHandler implements Runnable {

    private static final Logger LOGGER = LogManager.getLogger(ServerHandler.class);

    private String myName;
    private Socket clientSocket;
    private ChatServer chatServer;
    private BufferedReader clientInputStream;
    private PrintWriter clientOutputStream;
    private boolean exit = false;
    private Lobby lobby;

    /**
     * When the server starts a ServerHandler-Thread, it needs to pass the following arguments to it:
     *
     * @param myName       Identifier of the client for which this serverHandler is responsible.
     * @param clientSocket The socket which belongs to this client. Needed to open the I/O-Streams.
     * @param chatServer   The chat Headquarter on server side, to which all incoming chat messages are
     *                     forwarded.
     */
    public ServerHandler(String myName, Socket clientSocket, ChatServer chatServer) {
        this.myName = myName;
        this.clientSocket = clientSocket;
        this.chatServer = chatServer;
        try {
            clientInputStream = new BufferedReader(new InputStreamReader(
                    new DataInputStream(clientSocket.getInputStream()), StandardCharsets.UTF_8));
            clientOutputStream = new PrintWriter(clientSocket.getOutputStream());
        } catch (IOException e) {
            LOGGER.error("IOException while trying to create the ServerHandler " + myName);
            e.printStackTrace();
        }
    }

    /**
     * All which a ServerHandler-Thread makes during its lifetime is to listen to incoming information
     * on the clientInputStream, and pass this information to decode().
     */
    public void run() {
        while (exit == false) {
            String s = null;
            try {
                s = clientInputStream.readLine();
            } catch (IOException e) {
                LOGGER.error("Reading from ClientInputStream failed: ");
                e.printStackTrace();
            }
            if (s == null) System.err.println("The ServerHandler of " + myName + " received an empty message");
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
            case "CHANGENAME":
                String userName = commands[1];
                Server.changeName(myName, userName);
                break;
            case "CHAT":
                String sender = commands[1];
                String receiver = commands[2];
                String msg = commands[4];
                LOGGER.info("ServerHandler " + myName + " sent message to ChatServer");
                chatServer.receiveMessage(sender, receiver, msg);
                break;
            case "CREATELOBBY":
                String lobbyName = commands[1];
                Server.createLobby(lobbyName, this);
                break;
            case "GETLOBBYLIST":
                sendLobbyList();
                break;
            case "JOINLOBBY":
                String name = commands[1];
                Server.joinLobby(name, this);
                break;
            case "READYFORGAME":
                System.out.println("ServerHandler received READYFORGAME and will execute lobby.readyForGame with" +
                        "this lobby: " + lobby);
                System.out.println(myName);
                lobby.readyForGame(myName);
                break;
            case "ASKFORCARD":
                lobby.serverGame.giveCardToClient(myName);
                LOGGER.info("ServerHandler invoked giveCardToClient() in ServerGame");
                break;
            case "THROWCARD":
                String cardName = commands[1];
                lobby.serverGame.throwCard(myName, cardName);
                break;
            case "JUMPTHISTURN":
                lobby.serverGame.jumpThisTurn();
                break;
            case "LOGOUT":
                closeConnection();
                break;
            default:
                LOGGER.error("ServerHandler " + myName + " received an invalid message.");
        }
    }

    /**
     * The chatServer sends a chat message to this handler's client.
     */
    public void sendChatMessage(String sender, String isPrivateMsg, String msg) {
        clientOutputStream.println("CHAT" + "#" + sender + "#" + myName + "#" + isPrivateMsg + "#" + msg);
        clientOutputStream.flush();
        LOGGER.info("ServerHandler " + myName + " sent message to ClientOutputStream");
    }

    /**
     * The server sends a feedback to this handler's client,
     * if his name change request was allowed or rejected.
     */
    public void giveFeedbackToChange(boolean feedback, String newName) {
        if (feedback) {
            clientOutputStream.println("CHANGEOK" + "#" + newName);
        } else {
            clientOutputStream.println("CHANGENO" + "#" + newName);
        }
        clientOutputStream.flush();
    }

    public String getName() {
        return myName;
    }

    public void setName(String name) {
        myName = name;
    }

    /**
     * This method closes all the streams and the socket of the client who requested the LOGOUT.
     */
    public void closeConnection() {
        clientOutputStream.println("LOGOUT");
        clientOutputStream.flush();

        try {
            clientOutputStream.close();
            clientInputStream.close();
            clientSocket.close();
            LOGGER.info("Closed streams and socket from " + myName);
            Server.removeUser(myName);
            exit = true;
        } catch (IOException e) {
            LOGGER.error("Closing streams and socket failed in ServerHandler " + myName);
        }
    }

    /**
     * Send a list of available lobbies to the client.
     */
    public void sendLobbyList() {
        String[] lobbies = Server.getLobbies();
        //Create the string which will be sent to the clientOutputStream
        String s = "SENDLOBBYLIST";
        if (lobbies.length != 0) {
            //append the lobbies to the string
            for (int i = 0; i < lobbies.length; i++) {
                s = s + "#" + lobbies[i];
            }
        } else {
            s = s + "#" + "No Lobbies";
        }
        clientOutputStream.println(s);
        clientOutputStream.flush();
    }

    /**
     * Tell the client that he successfully joined the specified lobby
     */
    public void lobbyJoined(String lobbyName) {
        clientOutputStream.println("LOBBYJOINED" + "#" + lobbyName);
        clientOutputStream.flush();
        //store the lobby in this object field
        this.lobby = Server.getLobby(lobbyName);
        LOGGER.info("ServerHandler of " + myName + " set its lobby variable.");
    }

    public void giveCard(String cardName) {
        clientOutputStream.println("GIVECARD" + "#" + cardName);
        clientOutputStream.flush();
        LOGGER.info("ServerHandler " + myName + " sent the string " + "GIVECARD" + "#" + cardName + " to Clienthandler");
    }

    public void gameStarted(String[] players) {
        String output = "GAMESTARTED" + "#";
        for (int i = 0; i < players.length; i++) {
            output = output + players[i] + "::";
        }
        output = output.substring(0, output.length() - 2);
        clientOutputStream.println(output);
        clientOutputStream.flush();
    }

    public void giveTurn() {
        clientOutputStream.println("GIVETURN");
        clientOutputStream.flush();
    }

    public void endMatch(String winnerName) {
        clientOutputStream.println("ENDMATCH" + "#" + winnerName);
        clientOutputStream.flush();
    }

    public void sendCoins(String allCoins) {
        clientOutputStream.println("SENDCOINS" + "#" + allCoins);
        clientOutputStream.flush();
    }

    public void setLobby(Lobby lobby) {
        this.lobby = lobby;
    }
}
