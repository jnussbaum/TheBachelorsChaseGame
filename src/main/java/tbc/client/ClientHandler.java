package tbc.client;

import tbc.chat.ChatClient;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class ClientHandler implements Runnable {

    private String myName;
    private Socket clientSocket;
    private ChatClient chatClient;
    private BufferedReader clientInputStream;
    private PrintWriter clientOutputStream;

    public ClientHandler(String hostName, int portNumber) {
        try {
            clientSocket = new Socket(hostName, portNumber);
            clientInputStream = new BufferedReader(new InputStreamReader(
                    new DataInputStream(clientSocket.getInputStream())));
            clientOutputStream = new PrintWriter(clientSocket.getOutputStream());
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
            String[] commands = s.split("#");
            switch (commands[0]) {
                case "CHAT_":
                    String sender = commands[1];
                    String receiver = commands[2];
                    String msg = commands[3];
                    Client.chatArrived(sender, msg);
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
        clientOutputStream.println("CHAT_" + "#" + myName + "#" + receiver + "#" + msg);
        clientOutputStream.flush();
    }

    public void registerChatClient(ChatClient chatClient) {
        this.chatClient = chatClient;
    }

}
