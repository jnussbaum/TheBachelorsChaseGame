package tbc.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import tbc.chat.ChatClient;
import tbc.game.Game;

public class Client {

    private static String myName;
    private static boolean nameSettingSucceeded = false;
    private static ClientHandler clientHandler;
    private static ChatClient chatClient;
    private static BufferedReader input;
    private static Thread clientHandlerThread;
    private static Thread chatClientThread;
    static ClientGame game;

    public static void nameChangeFeedback(boolean feedback, String newName) {
        if (feedback) {
            myName = newName;
            nameSettingSucceeded = true;
            System.out.println("Hello " + myName + ", welcome to our chat!");
        } else {
            String name;
            System.out.println("This name is not available any more. Please enter another name.");
            try {
                name = input.readLine();
                while (name.indexOf('@') != -1 || name.length() == 0) {
                    System.out.println("The name should not be empty nor contain the '@' character."
                        + "\nPlease try another name.");
                    name = input.readLine();
                }
                clientHandler.changeName(name);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void startGame() {
        game = new ClientGame();
    }

    public static void main(String[] args) {
        String hostName;
        int portNumber = 8096;
        do {
            System.out.println("Please enter the IP address given from the server: ");
            input = new BufferedReader(new InputStreamReader(System.in, StandardCharsets.UTF_8));
            try {
                hostName = input.readLine();
                clientHandlerThread = new Thread(
                    clientHandler = new ClientHandler(hostName, portNumber));
                chatClientThread = new Thread(chatClient = new ChatClient(clientHandler, input));
            } catch (IOException ex) {
                System.err.println("Couldn't get I/O for the connection to the hostname");
            }
        } while (clientHandler.isUnknownHostname() == true);

        try {
            clientHandlerThread.start();
            clientHandler.registerChatClient(chatClient);
            String systemName = System.getProperty("user.name");
            if (systemName.indexOf('@') != -1) {
                systemName.replace('@', '_');
            }
            System.out.println("Is your name " + systemName + "? ");
            System.out.println("Please answer with yes or no.");
            String name = null;
            String s = input.readLine();
            while (name == null)
                if (s.equalsIgnoreCase("yes")) {
                    name = systemName;
                } else if (s.equalsIgnoreCase("no")) {
                    System.out.println("Ok, what's your name then?");
                    name = input.readLine();
                    while (name.indexOf('@') != -1 || name.length() == 0) {
                        System.out.println(
                            "The name should not be empty nor contain the '@' character." +
                                "\nPlease try another name.");
                        name = input.readLine();
                    }
                } else {
                    System.out.println("Please answer with yes or no.");
                    name = null;
                    s = input.readLine();
                }
            clientHandler.changeName(name);
        } catch (IOException e) {
            System.err.println("There was an IOException when setting the username.");
        }

        while (!nameSettingSucceeded) {
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                //do nothing
            }
        }

        chatClientThread.start();
    }

}
