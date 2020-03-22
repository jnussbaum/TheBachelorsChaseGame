package tbc.client;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import tbc.chat.ChatClient;

public class ClientHandler implements Runnable {

    //What should we do with the unused object fields? Keep?
    private String myName;
    private Socket clientSocket;
    private ChatClient chatClient;
    private BufferedReader clientInputStream;
    private PrintWriter clientOutputStream;

    public ClientHandler(String hostName, int portNumber) {
        try {
            clientSocket = new Socket(hostName, portNumber);
            clientInputStream = new BufferedReader(new InputStreamReader(
                    new DataInputStream(clientSocket.getInputStream()), StandardCharsets.UTF_8));
            clientOutputStream = new PrintWriter(clientSocket.getOutputStream());
        } catch (UnknownHostException e) {
            System.err.println("Unknown hostname " + hostName);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void run() {
        while (true) {
            String s = null;
            try {
                s = clientInputStream.readLine();
            } catch (IOException e) {
                e.printStackTrace();
            }
            //TODO: Make a separate decode() method with the following (like in ServerHandler)
            String[] commands = s.split("#");
            switch (commands[0]) {
                case "CHAT":
                    String sender = commands[1];
                    String receiver = commands[2];
                    String msg = commands[3];
                    boolean privateMessage = Boolean.getBoolean(commands[4]);
                    chatClient.chatArrived(sender, msg, privateMessage);
                    System.out.println("Clienthandler sent message to chatclient");
                    break;
                case "CHANGEOK":
                    String newName = commands[1];
                    Client.nameChangeFeedback(true, newName);
                    myName = newName;
                    break;
                case "CHANGENO":
                    Client.nameChangeFeedback(false, "xyz");
            }
        }
    }

    public void changeName(String userName) {
        clientOutputStream.println("CHANGENAME" + "#" + userName);
        clientOutputStream.flush();
    }

    public void sendMessage(String receiver, String msg) {
        clientOutputStream.println("CHAT" + "#" + myName + "#" + receiver + "#" + msg);
        clientOutputStream.flush();
        System.out.println("clienthandler sent message out to clientoutputstream");
    }

    public void registerChatClient(ChatClient chatClient) {
        this.chatClient = chatClient;
    }

}
