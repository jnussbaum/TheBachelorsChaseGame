package tbc.chat;

import tbc.server.ServerHandler;
import java.util.HashMap;

/**
 * The Server starts this thread, which will be the Headquarter of the Chat application.
 */
public class ChatServer {

    /**
     * This HashMap administrates all clients by their name and ServerHandler.
     */
    private static HashMap<String, ServerHandler> clients = new HashMap<>();

    public void receiveMessage(String sender, String receiver, String msg) {
        if (receiver.equals("ALL")) {
            //send message to all, except the sender
            for (ServerHandler sh : clients.values()) {
                if (!sh.getName().equals(sender)) {
                    sh.sendChatMessage(sender, msg, false);
                    System.out.println("ChatServer sent message to the serverhandler of " + sh.getName());
                }
            }
        } else {
            //if receiver does not exist
            if (!checkUser(receiver)) {
                System.out.println("User not found.");
            } else {
                //send message to receiver
                for (ServerHandler sh : clients.values()) {
                    if (sh.getName().equals(receiver)) {
                        sh.sendChatMessage(sender, msg, true);
                        System.out.println("ChatServer sent message to the serverhandler of " + sh.getName());
                    }
                }
            }
        }
    }

    public void register(String clientName, ServerHandler clientServerHandler) {
        clients.put(clientName, clientServerHandler);
    }

    public void setName(String oldName, String newName) {
        ServerHandler sh = clients.get(oldName);
        clients.remove(oldName);
        clients.put(newName, sh);
    }

    public static boolean checkUser(String checkName) {
        for (ServerHandler sh : clients.values()) {
            if (sh.getName().equals(checkName)) {
                return true;
            }
        }
        return false;
    }
}