package tbc.client;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class ClientHandler implements Runnable {

    private Socket clientSocket;
    private BufferedReader clientInputStream;
    private BufferedWriter clientOutputStream;
    private BufferedReader input;

    public ClientHandler(String hostName, int portNumber) {
        try {
            clientSocket = new Socket(hostName, portNumber);
            clientInputStream = new BufferedReader(new InputStreamReader(
                    new DataInputStream(clientSocket.getInputStream())));
            clientOutputStream = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));
            input = new BufferedReader(new InputStreamReader(System.in, StandardCharsets.UTF_8));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void run() {


        while (true) {
            String s = clientInputStream.readLine();
            String[] commands = s.split("|");
            switch (commands[0]) {
                case "CHAT_":
                    String sender = commands[1];
                    String receiver = commands[2];
                    String msg = commands[3];
                    //tell client to print to console sender + message
                    Client.chatArrived(sender, msg);
                    break;
                case "CHANGEOK":
                    String newName = commands[1];
                    Client.nameChanged(newName);
            }
        }
    }

    public void changeName(String userName) {
        try {
            clientOutputStream.write("CHANGENAME" + "|" + userName);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendMessage(String receiver, String msg) {

    }

}
