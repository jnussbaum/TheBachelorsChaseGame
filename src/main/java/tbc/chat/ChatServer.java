package tbc.chat;

import tbc.server.Server;
import tbc.server.ServerHandler;

import java.util.HashMap;

/**
 * The Server starts this thread, which will be the Headquarter of the Chat application.
 */
public class ChatServer {

    /**
     * This HashMap administrates all clients by their name and ServerHandler.
     */
    private HashMap<String, ServerHandler> clients = new HashMap<>();

    public void receiveMessage(String sender, String receiver, String msg) {
        if (receiver.equals("ALL")) {
            //TODO: sender should not receive message
            for (ServerHandler sh : clients.values()) {
                sh.sendChatMessage(sender, msg);
            }
        }
    }

    public void register(String clientName, ServerHandler clientServerHandler) {
        clients.put(clientName, clientServerHandler);
    }
}