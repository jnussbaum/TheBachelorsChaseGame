package tbc.chat;

import java.io.BufferedReader;
import java.io.IOException;
import tbc.client.ClientHandler;

public class ChatClient implements Runnable {

    private ClientHandler clientHandler;
    BufferedReader input;

    public ChatClient(ClientHandler clientHandler, BufferedReader input) {
        this.clientHandler = clientHandler;
        this.input = input;
    }

    public void run() {
        String s;
        try {
            while ((s = input.readLine()) != null) {
                processInput(s);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

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

    public void chatArrived(String sender, String isPrivateMsg, String msg) {
        if (isPrivateMsg.equals("true")) {
            System.out.println("[PRIVATE] " + sender + ": " + msg);
        } else {
            System.out.println(sender + ": " + msg);
        }
    }
}