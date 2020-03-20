package tbc.client;

import tbc.chat.ChatClient;
import tbc.client.ClientHandler;

import java.io.BufferedReader;
import java.io.PrintWriter;
import java.net.UnknownHostException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class Client {

    private static String myName;
    private static ClientHandler clientHandler;
    private static ChatClient chatClient;

    public static void chatArrived(String sender, String msg) {
        System.out.println(sender + ": " + msg);
    }

    public static void nameChangeFeedback(boolean feedback, String newName) {
        if (feedback) {
            myName = newName;
            System.out.println("Your name has been validated.");
        } else {
            String name;
            System.out.println("This name is not available any more. Please enter another name.");
            try (BufferedReader input = new BufferedReader(new InputStreamReader(System.in, StandardCharsets.UTF_8))) {
                name = input.readLine();
                while (name.indexOf('@') != -1 || name.length() == 0) {
                    System.out.println("The name should not be empty nor contain the '@' character." +
                            "\nPlease try another name.");
                    name = input.readLine();
                }
                clientHandler.changeName(name);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        String hostName = args[0];
        //int portNumber = Integer.parseInt(args[1]);
        int portNumber = 8096;
        Thread th1 = new Thread(clientHandler = new ClientHandler(hostName, portNumber));
        Thread th2 = new Thread(chatClient = new ChatClient(clientHandler));
        th1.start();
        clientHandler.registerChatClient(chatClient);
        try {
            System.out.println("Is your name " + System.getProperty("user.name") + "? ");
            System.out.println("Please answer with yes or no.");
            String name;
            BufferedReader input = new BufferedReader(new InputStreamReader(System.in, StandardCharsets.UTF_8));
            if (input.readLine().equalsIgnoreCase("yes")) {
                name = System.getProperty("user.name");
                System.out.println("Hello " + name + ", welcome to our chat!");
            } else {
                System.out.println("Ok, what's your name then?");
                name = input.readLine();
                while (name.indexOf('@') != -1 || name.length() == 0) {
                    System.out.println("The name should not be empty nor contain the '@' character." +
                            "\nPlease try another name.");
                    name = input.readLine();
                }
            }
            clientHandler.changeName(name);
        } catch (IOException e) {
            //TODO
        }
        th2.start();
    }
}
