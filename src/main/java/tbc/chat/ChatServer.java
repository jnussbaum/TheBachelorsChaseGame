package tbc.chat;

import java.io.PrintStream;
import java.io.IOException;
import java.net.Socket;
import java.net.ServerSocket;

/*
 * The server. Has to be started before a client.
 */
public class ChatServer {

    private static ServerSocket serverSocket = null;
    private static Socket clientSocket = null;

    private static final int maxClientsCount = 4;
    private static final ClientThread[] threads = new ClientThread[maxClientsCount];

    public static void main(String args[]) {

        int portNumber = 8095;
        if (args.length < 1) {
            System.out.println("Welcome! You are the Server. \nPort number: " + portNumber);
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
                    PrintStream os = new PrintStream(clientSocket.getOutputStream());
                    os.println("Server full.");
                    os.close();
                    clientSocket.close();
                }
            } catch (IOException e) {
                System.out.println(e);
            }
        }
    }
}