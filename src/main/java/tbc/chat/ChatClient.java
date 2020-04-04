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
            s = " hat das Spiel verlassen.";
            clientHandler.sendMessage("ALL", "false", s);
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
            } else {
                //LOGGER.error(clientHandler.getMyName() + " tried to send an empty message.");
                LobbyController.gameWindowController.appendMsg("Usage: @<Username vom anderen Spieler> <Nachricht>");
            }
        // send public message
        } else {
            clientHandler.sendMessage("ALL", "false", s);
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
            if (msg.equals(" hat das Spiel verlassen.")) {
                LOGGER.info(sender + msg);
                LobbyController.gameWindowController.appendMsg(sender + msg);
            } else if (msg.startsWith("Willkommen")) {
                LOGGER.info("Let us welcome " + sender + "!");
                LobbyController.gameWindowController.appendMsg(msg);
            } else {
                LOGGER.info(sender + ": " + msg);
                LobbyController.gameWindowController.appendMsg(sender + ": " + msg);
            }
        }
    }

}