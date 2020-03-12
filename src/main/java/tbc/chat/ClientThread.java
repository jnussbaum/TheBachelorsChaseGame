package tbc.chat;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;

/**
 * ClientThread. Handles all the clients information and actions.
 */
class ClientThread extends Thread {

    private String clientName = null;
    private DataInputStream input = null;
    private PrintStream output = null;
    private Socket clientSocket = null;
    private final ClientThread[] threads;
    private int maxClientsCount;

    public ClientThread(Socket clientSocket, ClientThread[] threads) {
        this.clientSocket = clientSocket;
        this.threads = threads;
        maxClientsCount = threads.length;
    }

    public void run() {
        int maxClientsCount = this.maxClientsCount;
        ClientThread[] threads = this.threads;

        /*
         * TODO Chatting with umlaut.
         *  Implemented change username, but not tested with more than 1 client.
         *  Do not interrupt the writing if you get a message.
         */
        try {
            input = new DataInputStream(clientSocket.getInputStream());
            output = new PrintStream(clientSocket.getOutputStream());
            String name;
            while (true) {
                name = input.readLine();
                if (name.indexOf('@') == -1) {
                    break;
                } else {
                    output.println("The name should not contain '@' character.");
                }
            }

            output.println("Welcome " + name + " to our chat room."
                + "\nTo leave enter /quit in a new line.");
            System.out.println(name + " entered the chat room.");
            synchronized (this) {
                /* No user is connected to the server yet */
                for (int i = 0; i < maxClientsCount; i++) {
                    if (threads[i] != null && threads[i] == this) {
                        clientName = "@" + name;
                        break;
                    }
                }
                /* Another user in already connected to the server */
                for (int i = 0; i < maxClientsCount; i++) {
                    if (threads[i] != null && threads[i] != this) {
                        threads[i].output.println(name + " entered the chat room.");
                    }
                }
            }
            while (true) {
                String line = input.readLine();
                /* To leave the chat room */
                if (line.startsWith("/quit")) {
                    break;
                }
                /* To change the own username */
                if (line.startsWith("/changeName")) {
                    name = line.substring(12);
                    clientName = "@" + name;
                }
                /* Start with '@' to send a private message to a specific user */
                if (line.startsWith("@")) {
                    String[] words = line.split("\\s", 2);
                    if (words.length > 1 && words[1] != null) {
                        words[1] = words[1].trim();
                        if (!words[1].isEmpty()) {
                            synchronized (this) {
                                for (int i = 0; i < maxClientsCount; i++) {
                                    if (threads[i] != null && threads[i] != this
                                            && threads[i].clientName != null
                                            && threads[i].clientName.equals(words[0])) {
                                                threads[i].output.println(name + ": " + words[1]);
                                                this.output.println(name + ": " + words[1]);
                                                break;
                                    }
                                }
                            }
                        }
                    }
                } else {
                    /* Public message */
                    synchronized (this) {
                        for (int i = 0; i < maxClientsCount; i++) {
                            if (threads[i] != null && threads[i].clientName != null) {
                                threads[i].output.println(name + ": " + line);
                            }
                        }
                    }
                }
            }
            synchronized (this) {
                for (int i = 0; i < maxClientsCount; i++) {
                    if (threads[i] != null && threads[i] != this && threads[i].clientName != null) {
                        threads[i].output.println(name + " left the chat room.");
                    }
                }
            }
            output.println("See you again " + name + "!");
            System.out.println(name + " left the chat room.");
            /*
             * Set the current thread variable to null for a new client.
             */
            synchronized (this) {
                for (int i = 0; i < maxClientsCount; i++) {
                    if (threads[i] == this) {
                        threads[i] = null;
                    }
                }
            }
            input.close();
            output.close();
            clientSocket.close();
        } catch (IOException e) {
        }
    }
}