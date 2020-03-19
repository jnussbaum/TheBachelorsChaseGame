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

    static String myName;

    public static void chatArrived(String sender, String msg) {
        System.out.println(sender + ": " + msg);
    }

    public static void nameChanged(String newName) {
        myName = newName;
    }

    public static void main(String[] args){
        String hostName = args[0];
        int portNumber = Integer.parseInt(args[1]);
        ClientHandler clientHandler = new ClientHandler(hostName, portNumber);
        ChatClient chatClient = new ChatClient(clientHandler);

        try {
            System.out.println("Is your name " + System.getProperty("user.name") + "? " +
                    "\nPlease answer with yes or no.");
            String name;
            BufferedReader input = new BufferedReader(new InputStreamReader(System.in, StandardCharsets.UTF_8));
            while(true) {
                /* The client suggests a nickname based on the system username */
                if (input.readLine().equalsIgnoreCase("yes")) {
                    name = System.getProperty("user.name");
                } else {
                    System.out.println("Ok, what's your name then?");
                    name = input.readLine();
                    if (name.indexOf('@') != -1) {
                        System.out.println("The name should not contain '@' character." +
                                "\nWhat's your name then?");
                        //TODO: Add a loop here which asks again for a new name, until it does not contain @ anymore.
                    }

                }
            }
            clientHandler.changeName(name);
        } catch (IOException e) {
            //TODO
        }

    }
}
