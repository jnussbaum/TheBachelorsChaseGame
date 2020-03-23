package tbc.server;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import tbc.chat.ChatServer;

/**
 * As soon as a new client connects to the server, the server starts a new ServerHandler-Thread,
 * which will be responsible for the communication between the server and this client.
 */
public class ServerHandler implements Runnable {

    private String myName;
    public Socket clientSocket;
    private ChatServer chatServer;
    public BufferedReader clientInputStream;
    public PrintWriter clientOutputStream;
    private boolean exit = false;

    /**
     * When the server starts a ServerHandler-Thread, it needs to pass the following arguments to it:
     * @param myName Identifier of the client for which this serverHandler is responsible.
     * @param clientSocket The socket which belongs to this client. Needed to open the I/O-Streams.
     * @param chatServer The chat Headquarter on server side, to which all incoming chat messages are
     *                   forwarded.
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
            e.printStackTrace();
        }
    }

    /**
     * All which a ServerHandler-Thread makes during its lifetime is to listen to incoming information
     * on the clientInputStream, and pass this information to decode().
     */
    public void run() {
        try {
            String s;
            while (exit == false && (s = clientInputStream.readLine()) != null) {
                decode(s);
            }
        } catch (IOException e) {
            System.err.println("The ServerHandler of client " + myName
                + " experienced the following IOException"
                + " while listening to its clientInputStream:\n");
            e.printStackTrace();
        }
    }

    /**
     * As soon as information comes in on the clientInputStream, this String is passed to decode(). This method
     * looks at the first substring (commands[0], the Network Protocol command) and then invokes the appropriate
     * methods in this object in order to process the information. The following substrings (from commands[] on)
     * are the parameters of the Network Protocol command.
     */
    void decode(String s) {
        String[] commands = s.split("#");
        switch (commands[0]) {
            case "CHANGENAME":
                String userName = commands[1];
                Server.changeName(myName, userName);
                break;
            case "LOGOUT":
                closeConnection();
                break;
            case "CHAT":
                String sender = commands[1];
                String receiver = commands[2];
                String msg = commands[4];
                System.out.println("ServerHandler sent message to ChatServer");
                chatServer.receiveMessage(sender, receiver, msg);
                break;
            default:
                System.err.println("No such command in the ServerHandler protocol.");
        }
    }

    /**
     * The chatServer sends a chat message to this handler's client.
     */
    public void sendChatMessage(String sender, String isPrivateMsg, String msg) {
        clientOutputStream.println("CHAT" + "#" + sender + "#" + myName + "#" + isPrivateMsg + "#" + msg);
        clientOutputStream.flush();
        System.out.println("ServerHandler sent message to ClientOutputStream");
    }

    /**
     * The server sends a feedback to this handler's client,
     * if his name change request was allowed or rejected.
     */
    public void giveFeedbackToChange(boolean feedback, String newName) {
        if (feedback) {
            clientOutputStream.println("CHANGEOK" + "#" + newName);
            clientOutputStream.flush();
        } else {
            clientOutputStream.println("CHANGENO");
            clientOutputStream.flush();
        }
    }

    public String getName() {
        return myName;
    }

    public void setName(String name) {
        myName = name;
    }

    /**
     * This method closes all the stream and the socket of the client, who requested the LOGOUT.
     */
    public void closeConnection() {
        try {
            clientOutputStream.close();
            clientInputStream.close();
            clientSocket.close();
            System.out.println("Closed stream and socket from " + myName);
            Server.removeUser(myName);
            exit = true;
        } catch (IOException e) {
            System.err.println("Closing failed in ServerHandler.");
        }
    }
}
