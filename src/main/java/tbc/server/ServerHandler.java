package tbc.server;

import tbc.chat.ChatServer;
import tbc.chat.ClientThread;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerHandler implements Runnable {

    String myName;
    Socket clientSocket;
    DataInputStream clientInputStream;       //DataInputStream is not ideal, because its readLine() method
                                                //is deprecated. BufferedReader would be better.
    PrintStream clientOutputStream;

    /**
     * As soon as a new clients connects to the server, the server instantiates a new ServerHandler-Object,
     * passing the clientSocket to it.
     */
    public ServerHandler(String myName, Socket clientSocket) {
        this.myName = myName;
        this.clientSocket = clientSocket;
        try {
            clientInputStream = new DataInputStream(clientSocket.getInputStream());
            clientOutputStream = new PrintStream(clientSocket.getOutputStream());
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

    public void decode(String s) {
        String[] commands = s.split("|");
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
                contribute(sender, receiver, msg);
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
    public void contribute(String sender, String receiver, String msg) {
        //Inform the chat-program on the server that the sender sent a message to the specified receiver.
        ChatServer.receiveMessage();
    }

    /**
     * The server sends a message to this handler's client
     */
    public void sendMessage(String sender, String msg) {
        clientOutputStream.print("CHAT_" + "|" + sender + "|" + " " + "|" + msg);
    }

    public void changeFeedback(boolean feedback, String name) {
        if (feedback) {
            clientOutputStream.print("CHANGEOK" + "|" + name);
        } else {
            clientOutputStream.print("CHANGENO");
        }
    }
}
