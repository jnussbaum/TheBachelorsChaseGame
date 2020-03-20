package tbc.server;

import tbc.chat.ChatServer;
import tbc.chat.ClientThread;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerHandler implements Runnable {

    private String myName;
    private Socket clientSocket;
    private ChatServer chatServer;
    private BufferedReader clientInputStream;
    private PrintWriter clientOutputStream;

    /**
     * As soon as a new clients connects to the server, the server instantiates a new ServerHandler-Object,
     * passing the clientSocket to it.
     */
    public ServerHandler(String myName, Socket clientSocket, ChatServer chatServer) {
        this.myName = myName;
        this.clientSocket = clientSocket;
        this.chatServer = chatServer;
        try {
            clientInputStream = new BufferedReader(new InputStreamReader(
                    new DataInputStream(clientSocket.getInputStream())));
            clientOutputStream = new PrintWriter(clientSocket.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void run() {
        //Listen to incoming
        try {
            String s;
            while ((s = clientInputStream.readLine()) != null) {
                decode(s);
            }
        } catch (IOException e) {
            //do something
        }
    }

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
            case "CHAT_":
                String sender = commands[1];
                String receiver = commands[2];
                String msg = commands[3];
                contributeToChat(sender, receiver, msg);
                //handle ALL as receiver
                break;
            case "GAMST":
                //TODO
                break;
            case "GIVCA":
                //TODO;
                break;
            case "GETCA":
                //TODO;
                break;
            case "THRCA":
                //TODO;
                break;
            case "TURST":
                //TODO;
                break;
            case "TUREN":
                //TODO;
                break;
            case "LOGIN":
                String clientName = commands[1];
                //TODO
                break;
            case "LOGOU":
                //String clientName = commands[1];
                //TODO
                break;
        }
    }

    /**
     * Writes the message into the chat message history on the server
     */
    public void contributeToChat(String sender, String receiver, String msg) {
        chatServer.receiveMessage(sender, receiver, msg);
    }

    /**
     * The server sends a message to this handler's client
     */
    public void sendChatMessage(String sender, String msg) {
        clientOutputStream.println("CHAT_" + "#" + sender + "#" + myName + "#" + msg);
        clientOutputStream.flush();
        System.out.println("ServerHandler just sent this to clienthandler: " + sender + msg);
    }

    public void giveFeedbackToChange(boolean feedback, String name) {
        if (feedback) {
            clientOutputStream.println("CHANGEOK" + "#" + name);
            clientOutputStream.flush();
        } else {
            clientOutputStream.println("CHANGENO");
            clientOutputStream.flush();
        }
    }
}
