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

    public String myName;
    private Socket clientSocket;
    private ChatClient chatClient;
    private BufferedReader clientInputStream;
    private PrintWriter clientOutputStream;
    private boolean unknownHostname = false;

    public ClientHandler(String hostName, int portNumber) {
        try {
            clientSocket = new Socket(hostName, portNumber);
            clientInputStream = new BufferedReader(new InputStreamReader(
                    new DataInputStream(clientSocket.getInputStream()), StandardCharsets.UTF_8));
            clientOutputStream = new PrintWriter(clientSocket.getOutputStream());
        } catch (UnknownHostException e) {
            unknownHostname = true;
            System.err.println("Unknown hostname: " + hostName);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean isUnknownHostname() {
        return unknownHostname;
    }

    public void run() {
        while (true) {
            String s = null;
            try {
                s = clientInputStream.readLine();
            } catch (IOException e) {
                System.err.println("Reading from ClientInputStream failed: ");
                e.printStackTrace();
            }
            if (s == null) System.err.println("ClientHandler " + myName + "received an empty message.");
            String[] commands = s.split("#");
            switch (commands[0]) {
                case "CHAT":
                    String sender = commands[1];
                    String isPrivateMessage = commands[3];
                    String msg = commands[4];
                    chatClient.chatArrived(sender, isPrivateMessage, msg);
                    break;
                case "CHANGEOK":
                    String newName = commands[1];
                    Client.nameChangeFeedback(true, newName);
                    myName = newName;
                    break;
                case "CHANGENO":
                    Client.nameChangeFeedback(false, "xyz");
                    break;
                case "SENDLOBBYLIST":
                    receiveLobbyList(commands);
                case "LOBBYJOINED":
                    String lobbyName = commands[1];
                    System.out.println("You joined the lobby " + lobbyName);
                case "GIVECARD":
                    String cardName = commands[1];
                    Client.game.addCard(cardName);
                case "GAMESTARTED":
                    Client.startGame();
                case "GIVETURN":
                    Client.game.giveTurn();
                default:
                    System.err.println("ClientHandler " + myName + "received an invalid message.");
            }
        }
    }

    public void changeName(String userName) {
        clientOutputStream.println("CHANGENAME" + "#" + userName);
        clientOutputStream.flush();
    }

    public void sendMessage(String receiver, String isPrivateMsg, String msg) {
        clientOutputStream.println("CHAT" + "#" + myName + "#" + receiver + "#" + isPrivateMsg + "#" + msg);
        clientOutputStream.flush();
    }

    public void registerChatClient(ChatClient chatClient) {
        this.chatClient = chatClient;
    }

    public void logOut() {
        clientOutputStream.println("LOGOUT");
        clientOutputStream.flush();
    }

    void sendLobbyList (String lobbyName) {
        clientOutputStream.println("CREATELOBBY" + "#" + lobbyName);
    }

    void receiveLobbyList(String[] commands) {
        String[] lobbies = new String[commands.length - 1];
        for (int i = 1; i < commands.length; i++) {
            lobbies[i - 1] = commands[i];
        }
        //TODO: Further processing of available lobbies --> GUI
    }

    void joinLobby(String lobbyName) {
        clientOutputStream.println("JOINLOBBY" + "#" + lobbyName);
    }

    void startGame() {
        clientOutputStream.println("STARTGAME");
    }
}
