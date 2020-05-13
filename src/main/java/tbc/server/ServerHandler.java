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
                if (s != null) {
                    decode(s);
                }
            } catch (IOException e) {
                LOGGER.error("Reading from ClientInputStream failed. Logging out...");
                closeConnection();
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
        String[] commands = s.split("#");
        switch (commands[0]) {
            case "CHANGENAME":
                // request from the client to change his name into the name passed as argument
                String userName = commands[1];
                Server.changeName(myName, userName);
                break;
            case "CHAT":
                // chat message from a client
                String sender = commands[1];
                String receiver = commands[2];
                String msg = commands[4];
                LOGGER.info("ServerHandler " + myName + " sent message to ChatServer");
                chatServer.receiveMessage(sender, receiver, msg);
                break;
            case "GETPLAYERLIST":
                // request from client to receive a list of all players in the lobby
                sendPlayerList();
                break;
            case "GETLOBBYLIST":
                // request from client to receive a list of all lobbies
                sendLobbyList();
                break;
            case "JOINLOBBY":
                // request from client to join the named lobby
                String name = commands[1];
                Server.joinLobby(name, this);
                break;
            case "READYFORGAME":
                // message from client that he is ready to start a game
                LOGGER.info(
                    "ServerHandler received READYFORGAME and will execute lobby.readyForGame with "
                        +
                        "this lobby: " + lobby.getLobbyName());
                System.out.println(myName);
                lobby.readyForGame(myName);
                break;
            case "ASKFORCARD":
                // request from client to get a card ("hit")
                lobby.serverGame.giveCardToClient(myName);
                LOGGER.info("ServerHandler invoked giveCardToClient() in ServerGame");
                break;
            case "THROWCARD":
                // request from client to throw away a card
                String cardName = commands[1];
                lobby.serverGame.throwCard(myName, cardName);
                break;
            case "QUITTHISMATCH":
                // request from client to quit this match
                lobby.serverGame.quitThisMatch(myName);
                break;
            case "READYFORMATCH":
                // message from client that he is ready to start a match
                lobby.readyForMatch(myName);
                break;
            case "ASKFORHIGHSCORE":
                // request from client to receive the highscore list
                Server.getHighScoreData(myName);
                break;
            case "LOGOUT":
                // request from client to log out
                closeConnection();
                break;
            case "CHEAT":
                // message from client that he wants to reach the named number of points by cheating
                String points = commands[1];
                cheat(points);
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
     * @param feedback tells if the Change was accepted
     * @param newName  the requested name
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
     * This method logs this client out from the server, lobby, and serverGame, and then closes all
     * the streams and the socket.
     */
    public void closeConnection() {
        // first, log out from server, lobby, and servergame
        exit = true;
        Server.removeUser(myName);
        if (lobby != null) {
            lobby.logout(myName);
            LOGGER.info("ServerHandler of " + myName + " logged out in the lobby.");
            if (lobby.serverGame != null) {
                lobby.serverGame.logout(myName);
                LOGGER.info("ServerHandler of " + myName + " removed from ServerGame");
            }
        }

        // close all streams and sockets
        try {
            if (!clientOutputStream.checkError()) {
                clientOutputStream.println("LOGOUT");
                clientOutputStream.flush();
                LOGGER.info("ServerHandler of " + myName + " sent LOGOUT to ClientHandler");
            }
            clientOutputStream.close();
            clientInputStream.close();
            clientSocket.close();
            LOGGER.info("ServerHandler of " + myName + " closed streams and socket");
        } catch (IOException e) {
            LOGGER.error("Closing streams and socket failed in ServerHandler " + myName);
            e.printStackTrace();
        }
    }

    /**
     * Sends a list of the current players to the client.
     */
    public void sendPlayerList() {
        String[] players = Server.getPlayers();

        // create the string which will be sent to the clientOutputStream
        StringBuilder stringBuilder = new StringBuilder("SENDPLAYERLIST");
        if (players.length != 0) {
            // append the players to the string
            for (int i = 0; i < players.length; i++) {
                stringBuilder.append("#").append(players[i]);
            }
        } else {
            stringBuilder.append("#").append("No Players");
        }

        // send the string to the client
        String s = stringBuilder.toString();
        clientOutputStream.println(s);
        clientOutputStream.flush();
    }

    /**
     * Sends a list of available lobbies to the client.
     */
    public void sendLobbyList() {
        String[] lobbies = Server.getLobbies();

        // create the string which will be sent to the clientOutputStream
        String s = "SENDLOBBYLIST";
        if (lobbies.length != 0) {
            // append the lobbies to the string
            for (int i = 0; i < lobbies.length; i++) {
                s = s + "#" + lobbies[i];
            }
        } else {
            s = s + "#" + "No Lobbies";
        }

        // send the string to the client
        clientOutputStream.println(s);
        clientOutputStream.flush();
    }

    /**
     * Tells the client that he successfully joined the specified lobby
     *
     * @param lobbyName the Name of the lobby
     */
    public void lobbyJoined(String lobbyName) {
        clientOutputStream.println("LOBBYJOINED" + "#" + lobbyName);
        clientOutputStream.flush();
        this.lobby = Server.getLobby(lobbyName);
        LOGGER.info("ServerHandler of " + myName + " set its lobby variable.");
    }

    /**
     * Sends the given card to the Client
     *
     * @param cardName the name of the card that is about to be sent
     */
    public void giveCard(String cardName) {
        clientOutputStream.println("GIVECARD" + "#" + cardName);
        clientOutputStream.flush();
        LOGGER.info("ServerHandler " + myName + " sent the string " + "GIVECARD" + "#" + cardName
            + " to Clienthandler");
    }

    /**
     * Sends the notification that the game has started
     *
     * @param players an array with all the names of the participating players
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
     * @param winnerName the name of the winner
     */
    public void endMatch(String winnerName) {
        clientOutputStream.println("ENDMATCH" + "#" + winnerName);
        clientOutputStream.flush();
        LOGGER.info("endmatch has been sent to ClientHandler of " + myName);
    }

    /**
     * Sends the new number of coins of every player
     *
     * @param allCoins all names with their corresponding coins
     */
    public void sendCoins(String allCoins) {
        clientOutputStream.println("SENDCOINS" + "#" + allCoins);
        clientOutputStream.flush();
        LOGGER.info("Coins have been sent");
    }

    public String getName() {
        return myName;
    }

    /**
     * Sets the name of the client the serverHandler is communicating to
     *
     * @param name the name of the client as a String
     */
    public void setName(String name) {
        myName = name;
    }

    public Lobby getLobby() {
        return lobby;
    }

    /**
     * Sets the Lobby this serverHandler is assigned to
     *
     * @param lobby the lobby
     */
    public void setLobby(Lobby lobby) {
        this.lobby = lobby;
    }

    /**
     * Sends a notification to the client that he has dropped out of the game
     */
    public void droppedOut() {
        System.out.println("ServerHandler.droppedOut");
        clientOutputStream.println("DROPPEDOUT");
        clientOutputStream.flush();
    }

    /**
     * The Client has been rejected from joining a lobby and is getting feedback
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
     * If a player cheats, this method is called
     *
     * @param points the number of points the player wants
     */
    void cheat(String points) {
        int p = Integer.parseInt(points);
        lobby.serverGame.cheat(p, myName);
    }
}
