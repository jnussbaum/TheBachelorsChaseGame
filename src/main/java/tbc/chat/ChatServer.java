package tbc.chat;

import tbc.server.Server;
import tbc.server.ServerHandler;

import java.io.PrintStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.ServerSocket;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Server. Has to be started before a client.
 */
public class ChatServer implements Runnable {

    Server server;
    HashMap<String, ServerHandler> clients;

    public ChatServer() {
        //this.server = server;
    }

    void run() {
        while (true)
        //Receives a Message and a receiver. Then he forwards the message to the receivers(=ServerHandlers)
    }

    public void receiveMessage(String sender, String receiver, String msg) {
        if (receiver.equals("ALL")) {
            //send message to all ServerHandlers except sender
            for (ServerHandler sh : clients.values())
                sh.sendMessage(sender, msg);
            }
        }
    }
}