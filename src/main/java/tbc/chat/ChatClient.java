package tbc.chat;

import javafx.application.Platform;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import tbc.client.ClientHandler;
import tbc.gui.LobbyController;

/**
 * At the beginning of his life, a client starts a ChatClientThread who will be responsible for the
 * client side of the chat.
 */
public class ChatClient {

    private static final Logger LOGGER = LogManager.getLogger(ChatClient.class);

    /**
     * The clientHandler who is responsible for the communication with the server.
     */
    private final ClientHandler clientHandler;

    public ChatClient(ClientHandler clientHandler) {
        this.clientHandler = clientHandler;
    }

    /**
     * When something was typed on the keyboard, the run method passes it to this method.
     *
     * @param s The String which was read from the chat field.
     */
    public void processInput(String s) {
        // the client wants to logout
        if (s.startsWith("UserWantsToLogout")) {
            clientHandler.sendMessage("ALL", "false", " has left the game.");
            clientHandler.logOut();
            LOGGER.info("You have logged out.");
            return;
        }

        // send private message
        if (s.startsWith("@")) {
            String receiver = s.substring(1, s.indexOf(" "));
            String msg = s.substring(s.indexOf(" ") + 1);
            if (s.contains(" ") && msg.length() != 0) {
                clientHandler.sendMessage(receiver, "true", msg);
                Platform.runLater(
                    () -> LobbyController.gameWindowController.appendMsg("[PRIVATE] "
                        + clientHandler.getMyName() + ": " + msg));
            } else {
                LobbyController.gameWindowController
                    .appendMsg("Usage: @<Username of the receiver> <message>");
            }
            // send public message
        } else {
            clientHandler.sendMessage("ALL", "false", s);
        }
    }

    /**
     * When a chat message arrives, the clientHandler invokes this method. The message will then be
     * printed out.
     *
     * @param sender       The user that sent the message.
     * @param isPrivateMsg String to tell if the message is private or not.
     * @param msg          The String message which will be printed to the screen.
     */
    public void chatArrived(String sender, String isPrivateMsg, String msg) {
        if (isPrivateMsg.equals("true")) {
            LobbyController.gameWindowController.appendMsg("[PRIVATE] " + sender + ": " + msg);
        } else {
            if (msg.startsWith("Welcome")) {
                LobbyController.gameWindowController.appendMsg(msg);
                return;
            }

            if (msg.equals(" has left the game.")) {
                LobbyController.gameWindowController.appendMsg(sender + msg);
            } else {
                LobbyController.gameWindowController.appendMsg(sender + ": " + msg);
            }
        }
    }

}