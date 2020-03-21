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
        if (s.startsWith("@")) {
            String name = s.substring(1, s.indexOf(" "));
            String msg = s.substring(s.indexOf(" ") + 1, s.length() - 1);
            clientHandler.sendMessage(name, msg);
            System.out.println("ChatClient sent message to clienthandler");
        } else {
            clientHandler.sendMessage("ALL", s);
        }
    }

    public static void chatArrived(String sender, String msg, boolean privateMessage) {
        if (privateMessage) {
            System.out.println("[PRIVATE] " + sender + ": " + msg);
        } else {
            System.out.println(sender + ": " + msg);
        }
    }
}