package tbc.chat;

import java.io.BufferedReader;
import java.io.IOException;
import tbc.client.ClientHandler;

/**
 * At the beginning of his life, a client starts a ChatClientThread who will be responsible for the client
 * side of the chat.
 */
public class ChatClient implements Runnable {

    /**
     * The clientHandler who is responsible for the communication with the server.
     */
    private ClientHandler clientHandler;

    /**
     * Input Reader from System.in
     */
    private BufferedReader input;

    public ChatClient(ClientHandler clientHandler, BufferedReader input) {
        this.clientHandler = clientHandler;
        this.input = input;
    }

    /**
     * The run method listens to the keyboard inputs and sends them to processInput().
     */
    public void run() {
        String s;
        try {
            while ((s = input.readLine()) != null) {
                processInput(s);
            }
        } catch (IOException e) {
            System.err.println("IOException when the ChatClient tried to read from System.in");
            e.printStackTrace();
        }
    }

    /**
     * When something was typed on the keyboard, the run method passes it to this method.
     */
    public void processInput(String s) {
        // the client wants to logout
        if (s.startsWith("LOGOUT")) {
            clientHandler.logOut();
            System.out.println("You have logged out.");
            return;
        }

        // send private message
        if (s.startsWith("@")) {
            String receiver = s.substring(1, s.indexOf(" "));
            String msg = s.substring(s.indexOf(" ") + 1);
            if (msg.length() == 0 || msg.length()-1 == 0) {
                System.out.println("Usage: @<user> <message>.");
            } else {
                clientHandler.sendMessage(receiver, "true", msg);
            }

        // send public message
        } else {
            if (s.length() == 0) {
                System.out.println("Please enter a message.");
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
            System.out.println("[PRIVATE] " + sender + ": " + msg);
        } else {
            System.out.println(sender + ": " + msg);
        }
    }
}