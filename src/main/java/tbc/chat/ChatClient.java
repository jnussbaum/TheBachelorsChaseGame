package tbc.chat;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import tbc.GUI.GameWindowController;
import tbc.client.Client;
import tbc.client.ClientHandler;

/**
 * At the beginning of his life, a client starts a ChatClientThread who will be responsible for the client
 * side of the chat.
 */
public class ChatClient {

    private final static Logger LOGGER = LogManager.getLogger(Client.class);

    /**
     * The clientHandler who is responsible for the communication with the server.
     */
    private ClientHandler clientHandler;

    public ChatClient(ClientHandler clientHandler) {
        this.clientHandler = clientHandler;
    }

    /**
     * The run method listens to the keyboard inputs and sends them to processInput().
     */
   /* public void run() {
        String s;
        try {
            while ((s = input.readLine()) != null) {
                processInput(s);
            }
        } catch (IOException e) {
            logger.error("IOException when the ChatClient tried to read from System.in");
            e.printStackTrace();
        }
    }*/

    /**
     * When something was typed on the keyboard, the run method passes it to this method.
     */
    public void processInput(String s) {
        // the client wants to logout
        if (s.startsWith("LOGOUT")) {
            clientHandler.logOut();
            LOGGER.info("You have logged out.");
            return;
        }

        // send private message
        if (s.startsWith("@")) {
            String receiver = s.substring(1, s.indexOf(" "));
            String msg = s.substring(s.indexOf(" ") + 1);
            if (msg.length() == 0) {
                LOGGER.info("Usage: @<user> <message>.");
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
        GameWindowController gameWindowController = new GameWindowController();
        if (isPrivateMsg.equals("true")) {
           LOGGER.info("[PRIVATE] " + sender + ": " + msg);
           gameWindowController.appendMsg("[PRIVATE] " + sender + ": " + msg);
        } else {
           LOGGER.info(sender + ": " + msg);
           gameWindowController.appendMsg(sender + ": " + msg);
        }
    }
}