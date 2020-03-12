package tbc.chat;

import java.io.PrintStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.ServerSocket;
import java.net.UnknownHostException;

/*
 * Server. Has to be started before a client.
 */
public class ChatServer {

    private static ServerSocket serverSocket = null;
    private static Socket clientSocket = null;

    private static final int maxClientsCount = 4;
    private static final ClientThread[] threads = new ClientThread[maxClientsCount];

    public static void main(String args[]) {

        int portNumber = 8090;
        if (args.length < 1) {
            System.out.println("Welcome! You are the Server. \nPort number: " + portNumber);
            try {
                System.out.println(InetAddress.getLocalHost().getHostAddress());
            } catch (UnknownHostException e) {
                e.printStackTrace();
            }
        } else {
            portNumber = Integer.valueOf(args[0]).intValue();
        }

        /*
         * Open a server socket on the portNumber (default 8090).
         */
        try {
            serverSocket = new ServerSocket(portNumber);
        } catch (IOException e) {
            System.out.println(e);
        }

        /*
         * Create a client socket for each connection and pass it to a new client thread.
         */
        while (true) {
            try {
                clientSocket = serverSocket.accept();
                int i = 0;
                for (i = 0; i < maxClientsCount; i++) {
                    if (threads[i] == null) {
                        (threads[i] = new ClientThread(clientSocket, threads)).start();
                        break;
                    }
                }
                if (i == maxClientsCount) {
                    PrintStream output = new PrintStream(clientSocket.getOutputStream());
                    output.println("Server full.");
                    System.out.println("Another client tried to enter the chat room. "
                        + "Sadly you are already full..");
                    output.close();
                    clientSocket.close();
                }
            } catch (IOException e) {
                System.out.println(e);
            }
        }
    }
}