package tbc.server;

import tbc.chat.ChatServer;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

/**
 * As soon as a new client connects to the server, the server starts a new ServerHandler-Thread,
 * which will be responsible for the communication between the server and this client.
 */
public class ServerHandler implements Runnable {

    private String myName;
    private Socket clientSocket;
    private ChatServer chatServer;
    private BufferedReader clientInputStream;
    private PrintWriter clientOutputStream;

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
            while ((s = clientInputStream.readLine()) != null) {
                decode(s);
            }
        } catch (IOException e) {
            System.err.println("The ServerHandler of client " + myName + "experienced the following IOException" +
                    "while listening to its clientInputStream:\n");
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
                //TODO
                break;
            case "CHAT":
                String sender = commands[1];
                String receiver = commands[2];
                String msg = commands[3];
                System.out.println("ServerHandler sent message to chatserver");
                chatServer.receiveMessage(sender, receiver, msg);
                break;
        }
    }

    /**
     * The chatServer sends a chat message to this handler's client.
     */
    public void sendChatMessage(String sender, String msg, boolean privateMessage) {
        if (privateMessage) {
            clientOutputStream.println("CHAT" + "#" + sender + "#" + myName + "#" + msg + "#" + true);
        } else {
            clientOutputStream.println("CHAT" + "#" + sender + "#" + myName + "#" + msg + "#" + false);
        }
        clientOutputStream.flush();
        System.out.println("ServerHandler sent message to clientoutputstream");
    }

    /**
     * The server sends a feedback to this handler's client, if his name change request was allowed or rejected.
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

    public void setName(String newName) {
        myName = newName;
    }
}
