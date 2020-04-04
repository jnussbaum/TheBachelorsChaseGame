package tbc.chat;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import tbc.GUI.LobbyController;
import tbc.client.ClientHandler;

/**
 * At the beginning of his life, a client starts a ChatClientThread who will be responsible for the client
 * side of the chat.
 */
public class ChatClient {

    private static final Logger LOGGER = LogManager.getLogger(ChatClient.class);

    /**
     * The clientHandler who is responsible for the communication with the server.
     */
    private ClientHandler clientHandler;

    public ChatClient(ClientHandler clientHandler) {
        this.clientHandler = clientHandler;
    }

    /**
     * When something was typed on the keyboard, the run method passes it to this method.
     */
    public void processInput(String s) {
        // the client wants to logout
        if (s.startsWith("LOGOUT")) {
            s = "hat das Spiel verlassen.";
            clientHandler.sendMessage("ALL", "false", s);
            clientHandler.logOut();
            LOGGER.info("You have logged out.");
            return;
        }

        // send private message
        if (s.startsWith("@")) {
            String receiver = s.substring(1, s.indexOf(" "));
            String msg = s.substring(s.indexOf(" ") + 1);
            if (msg.length() == 0) {
                LOGGER.error("Usage: @<user> <message>.");
            } else {
                clientHandler.sendMessage(receiver, "true", msg);
            }

        // send public message
        } else {
            if (s.length() == 0) {
                LOGGER.info("Please enter a message.");
            } else {
                clientHandler.sendMessage("ALL", "false", s);
            }
        }
    }

    /**
     * When a chat message arrives, the clientHandler invokes this method. The message will then
     * be printed out.
     */
    public void chatArrived(String sender, String isPrivateMsg, String msg) {
        if (isPrivateMsg.equals("true")) {
            LOGGER.info("[PRIVATE] " + sender + ": " + msg);
            LobbyController.gameWindowController.appendMsg("[PRIVATE] " + sender + ": " + msg);
        } else {
            LOGGER.info("ChatClient.chatArrived " + sender + ": " + msg);
            LobbyController.gameWindowController.appendMsg(sender + ": " + msg);
        }
    }

}