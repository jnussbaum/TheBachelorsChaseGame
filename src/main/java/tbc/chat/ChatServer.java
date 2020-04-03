package tbc.chat;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import tbc.server.Server;
import tbc.server.ServerHandler;

/**
 * The Server starts this thread, which will be the Headquarter of the Chat application.
 */
public class ChatServer {

    private static final Logger LOGGER = LogManager.getLogger();

    /**
     * When the ChatServer receives a message, he forwards it to the correct clients.
     */
    public void receiveMessage(String sender, String receiver, String msg) {
        if (receiver.equals("ALL")) {
            //send message to all, except the sender
            for (ServerHandler sh : Server.getServerHandlers()) {
                sh.sendChatMessage(sender, "false", msg);
                System.out.println("ChatServer sent message to the ServerHandler of "
                    + sh.getName());
            }
        } else {
            //if receiver does not exist
            if (!checkUser(receiver)) {
                LOGGER.info("User not found.");
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

    /**
     * Use this method to tell the ChatServer that a new client exists.
     */
    public void register(String clientName, ServerHandler clientServerHandler) {
        Server.addClient(clientName, clientServerHandler);
    }

    /**
     * Verify if a user is registered in this ChatServer.
     */
    public boolean checkUser(String checkName) {
        for (ServerHandler sh : Server.getServerHandlers()) {
            if (sh.getName().equals(checkName)) {
                return true;
            }
        }
        return false;
    }

}