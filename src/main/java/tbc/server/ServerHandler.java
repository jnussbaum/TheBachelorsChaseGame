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
    private final Socket clientSocket;
    private final ChatServer chatServer;
    private String myName;
    private BufferedReader clientInputStream;
    private PrintWriter clientOutputStream;
    private boolean exit = false;
    private Lobby lobby;

    /**
     * When the server starts a ServerHandler-Thread, it needs to pass the following arguments to
     * it:
     *
     * @param myName       Identifier of the client for which this serverHandler is responsible.
     * @param clientSocket The socket which belongs to this client. Needed to open the I/O-Streams.
     * @param chatServer   The chat Headquarter on server side, to which all incoming chat messages
     *                     are forwarded.
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
     * All which a ServerHandler-Thread makes during its lifetime is to listen to incoming
     * information on the clientInputStream, and pass this information to decode().
     */
    public void run() {
        while (exit == false) {
            String s = null;
            try {
                s = clientInputStream.readLine();
            } catch (IOException e) {
                LOGGER.error("Reading from ClientInputStream failed: ");
                lostConnectionHandling();
            }
            if (s == null) {
                System.err.println("The ServerHandler of " + myName + " received an empty message");
            } else {
                decode(s);
            }

        }
    }

    void lostConnectionHandling() {
        try {
            lobby.logout(myName);
            clientOutputStream.close();
            clientInputStream.close();
            clientSocket.close();
            LOGGER
                .info("Closed streams and socket from " + myName + " because of a Connection loss");
            Server.removeUser(myName);
            exit = true;
        } catch (Exception e) {
            LOGGER.error("Closing streams and socket failed in ServerHandler " + myName);
            e.printStackTrace();
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
            case "GETPLAYERLIST":
                sendPlayerList();
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
                LOGGER.info(
                    "ServerHandler received READYFORGAME and will execute lobby.readyForGame with "
                        +
                        "this lobby: " + lobby.getLobbyName());
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
            case "QUITTHISMATCH":
                lobby.serverGame.quitThisMatch(myName);
                break;
            case "READYFORMATCH":
                lobby.readyForMatch(myName);
                break;
            case "ASKFORHIGHSCORE":
                Server.getHighScoreData(myName);
                break;
            case "LOGOUT":
                closeConnection();
                break;
            case "CHEAT":
                cheat(commands[1]);
                break;
            default:
                LOGGER.error("ServerHandler " + myName + " received an invalid message.");
        }
    }

    /**
     * The chatServer sends a chat message to this handler's client.
     *
     * @param sender       The user who sent the message.
     * @param isPrivateMsg String to tell if the message is private or not.
     * @param msg          The String with the message from a user.
     */
    public void sendChatMessage(String sender, String isPrivateMsg, String msg) {
        clientOutputStream
            .println("CHAT" + "#" + sender + "#" + myName + "#" + isPrivateMsg + "#" + msg);
        clientOutputStream.flush();
        LOGGER.info("ServerHandler " + myName + " sent message to ClientOutputStream");
    }

    /**
     * The server sends a feedback to this handler's client, if his name change request was allowed
     * or rejected.
     *
     * @param feedback - tells if the Change was accepted
     * @param newName  - the requested name
     */
    public void giveFeedbackToChange(boolean feedback, String newName) {
        if (feedback) {
            clientOutputStream.println("CHANGEOK" + "#" + newName);
        } else {
            clientOutputStream.println("CHANGENO" + "#" + newName);
        }
        clientOutputStream.flush();
    }

    /**
     * This method closes all the streams and the socket of the client who requested the LOGOUT.
     * Before closing the streams and the socket it should give a message to the
     * clientOutputStream.
     */
    public void closeConnection() {
        try {
            // if a lobby is joined
            if (lobby != null) {
                lobby.logout(myName);
            }
            clientOutputStream.println("LOGOUT");
            clientOutputStream.flush();
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
     * Send a list of the current players to the client.
     */
    public void sendPlayerList() {
        String[] players = Server.getPlayers();
        //Create the string which will be sent to the clientOutputStream
        StringBuilder stringBuilder = new StringBuilder("SENDPLAYERLIST");
        if (players.length != 0) {
            //append the players to the string
            for (int i = 0; i < players.length; i++) {
                stringBuilder.append("#").append(players[i]);
            }
        } else {
            stringBuilder.append("#").append("No Players");
        }
        String s = stringBuilder.toString();
        clientOutputStream.println(s);
        clientOutputStream.flush();
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
     *
     * @param lobbyName - the Name of the lobby
     */
    public void lobbyJoined(String lobbyName) {
        clientOutputStream.println("LOBBYJOINED" + "#" + lobbyName);
        clientOutputStream.flush();
        //store the lobby in this object field
        this.lobby = Server.getLobby(lobbyName);
        LOGGER.info("ServerHandler of " + myName + " set its lobby variable.");
    }

    /**
     * Sends the given card to the Client
     *
     * @param cardName - the name of the cart that is about to be send
     */
    public void giveCard(String cardName) {
        clientOutputStream.println("GIVECARD" + "#" + cardName);
        clientOutputStream.flush();
        LOGGER.info("ServerHandler " + myName + " sent the string " + "GIVECARD" + "#" + cardName
            + " to Clienthandler");
    }

    /**
     * Sends the notification that the game has Started
     *
     * @param players - an Array with all the names of the participating players
     */
    public void gameStarted(String[] players) {
        String output = "GAMESTARTED" + "#";
        for (int i = 0; i < players.length; i++) {
            output = output + players[i] + "::";
        }
        output = output.substring(0, output.length() - 2);
        clientOutputStream.println(output);
        clientOutputStream.flush();
    }

    /**
     * Sends the Client the notification that his turn has begun.
     */
    public void giveTurn() {
        clientOutputStream.println("GIVETURN");
        clientOutputStream.flush();
    }

    /**
     * Sends the notification that the match has ended
     *
     * @param winnerName - the name of the winner
     */
    public void endMatch(String winnerName) {
        clientOutputStream.println("ENDMATCH" + "#" + winnerName);
        clientOutputStream.flush();
        LOGGER.info("endmatch has been sent to ClientHandler of " + myName);
    }

    /**
     * Sends the new amount of coins of every player
     *
     * @param allCoins - all names with there corresponding coins
     */
    public void sendCoins(String allCoins) {
        clientOutputStream.println("SENDCOINS" + "#" + allCoins);
        clientOutputStream.flush();
        LOGGER.info("Coins have been sent");
    }

    /**
     * gets the name of the Client that the serverHandler is responding to
     *
     * @return the name of the client
     */
    public String getName() {
        return myName;
    }

    /**
     * sets the name of the client the serverHandler is responding to
     *
     * @param name the name of the client as an String
     */
    public void setName(String name) {
        myName = name;
    }

    /**
     * gets the lobby the serverHandler is assigned to
     *
     * @return the lobby object
     */
    public Lobby getLobby() {
        return lobby;
    }

    /**
     * Sets the Lobby the serverHandler is assigned to
     *
     * @param lobby - the lobby
     */
    public void setLobby(Lobby lobby) {
        this.lobby = lobby;
    }

    /**
     * sends a Notification to the client that he has dropped out of the game
     */
    public void droppedOut() {
        System.out.println("ServerHandler.droppedOut");
        clientOutputStream.println("DROPPEDOUT");
        clientOutputStream.flush();
    }

    /**
     * the Client has been rejected from joining an lobby and is getting feedback
     */
    public void reject() {
        clientOutputStream.println("REJECTTOJOINLOBBY");
        clientOutputStream.flush();
    }

    /**
     * Giving the highscore values as a String to the clientHandler. The String can never be null.
     *
     * @param highScoreData The names and coins from the highscore as a String
     */
    public void giveHighScore(String highScoreData) {
        clientOutputStream.println("GIVEHIGHSCORE" + "#" + highScoreData);
        clientOutputStream.flush();
    }

    /**
     * gives the cheat-amount to serverGame
     *
     * @param points - the amount the player wants
     */
    void cheat(String points) {
        int p = Integer.parseInt(points);
        lobby.serverGame.cheat(p, myName);
    }
}
