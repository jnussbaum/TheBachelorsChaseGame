package tbc.chat;

import tbc.server.Server;
import tbc.server.ServerHandler;

/**
 * The Server starts this thread, which will be the Headquarter of the Chat application.
 */
public class ChatServer {

    public void receiveMessage(String sender, String receiver, String msg) {
        if (receiver.equals("ALL")) {
            //send message to all, except the sender
            for (ServerHandler sh : Server.getServerHandlers()) {
                if (!sh.getName().equals(sender)) {
                    sh.sendChatMessage(sender, "false", msg);
                    System.out.println("ChatServer sent message to the ServerHandler of "
                        + sh.getName());
                }
            }
        } else {
            //if receiver does not exist
            if (!checkUser(receiver)) {
                System.out.println("User not found.");
            } else {
                //send message to specific receiver
                for (ServerHandler sh : Server.getServerHandlers()) {
                    if (sh.getName().equals(receiver)) {
                        sh.sendChatMessage(sender, "true", msg);
                        System.out.println("ChatServer sent message to the ServerHandler of "
                            + sh.getName());
                    }
                }
            }
        }
    }

    public void register(String clientName, ServerHandler clientServerHandler) {
        Server.addClient(clientName, clientServerHandler);
    }

    public static boolean checkUser(String checkName) {
        for (ServerHandler sh : Server.getServerHandlers()) {
            if (sh.getName().equals(checkName)) {
                return true;
            }
        }
        return false;
    }

}