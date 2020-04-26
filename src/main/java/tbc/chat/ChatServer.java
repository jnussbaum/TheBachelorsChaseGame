package tbc.chat;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import tbc.server.Lobby;
import tbc.server.Server;
import tbc.server.ServerHandler;

/**
 * The Server starts this thread, which will be the Headquarter of the Chat application.
 */
public class ChatServer {

    private static final Logger LOGGER = LogManager.getLogger(ChatServer.class);

    /**
     * When the ChatServer receives a message, he forwards it to the correct clients.
     *
     * @param sender   The user who sent a message.
     * @param receiver The user who gets the message
     * @param msg      The message which was sent.
     */
    public void receiveMessage(String sender, String receiver, String msg) {
        if (receiver.equals("ALL")) {
            // send message to all
            for (ServerHandler sh : Server.getServerHandlers()) {
                if (sh.getName().equals(sender)) {
                    Lobby playerLobby = sh.getLobby();
                    for (ServerHandler serverHandler : playerLobby.getClients().values()) {
                        serverHandler.sendChatMessage(sender, "false", msg);
                        LOGGER.info("ChatServer sent message to the ServerHandler of " + sh.getName());
                    }
                }
            }
        } else {
            // if receiver does not exist send a private message back to the sender
            if (!checkUser(receiver)) {
                LOGGER.info("User not found.");
                for (ServerHandler sh : Server.getServerHandlers()) {
                    if (sh.getName().equals(sender)) {
                        sh.sendChatMessage("Server", "true", "User not found.");
                    }
                }
            } else {
                // send message to a specific receiver
                for (ServerHandler sh : Server.getServerHandlers()) {
                    if (sh.getName().equals(receiver)) {
                        sh.sendChatMessage(sender, "true", msg);
                        LOGGER.info("ChatServer sent message to the ServerHandler of " + sh.getName());
                    }
                }
            }
        }
    }

    /**
     * Use this method to tell the ChatServer that a new client exists.
     *
     * @param clientName          The new user's name who needs to be added.
     * @param clientServerHandler The serverHandler from the new user.
     */
    public void register(String clientName, ServerHandler clientServerHandler) {
        Server.addClient(clientName, clientServerHandler);
    }

    /**
     * Verify if a user is registered in this ChatServer.
     *
     * @param checkName The name of the user who has to be checked it she/he is registered.
     * @return False if not registered.
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