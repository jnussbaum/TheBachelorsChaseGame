package tbc.chat;

import java.io.DataInputStream;
import java.io.PrintStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * Client. After a successful connection to the server, it can start chatting with other clients.
 */

public class ChatClient implements Runnable {

    private static Socket clientSocket = null;
    private static PrintStream output = null;
    private static DataInputStream input = null;
    private static BufferedReader inputLine = null;
    private static boolean closed = false;

    public static void main(String[] args) {

        int portNumber = 8090;

        // TODO Find the IP address of the server automatically
        String hostname = null;
        try {
            hostname = InetAddress.getLocalHost().getHostAddress();
            System.out.println(hostname);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }

        if (args.length < 2) {
            System.out.println("Port number: " + portNumber + "\nWelcome new client!");
        } else {
            hostname = args[0];
            portNumber = Integer.valueOf(args[1]).intValue();
        }

        /*
         * Open a socket on a given host and port. Open input and output streams.
         */
        try {
            // TODO Umlaut still not working
            clientSocket = new Socket(hostname, portNumber);
            inputLine = new BufferedReader(new InputStreamReader(System.in, "UTF-8"));
            output = new PrintStream(clientSocket.getOutputStream(), true, "Cp850");
            input = new DataInputStream(clientSocket.getInputStream());
            System.out.print("Enter your name: ");
        } catch (UnknownHostException e) {
            System.err.println("Unknown hostname " + hostname);
        } catch (IOException e) {
            System.err.println("Couldn't get I/O for the connection to the hostname " + hostname);
        }

        /*
         * If everything has been initialized then we want to write some data to the
         * socket we have opened a connection to on the port portNumber.
         */
        if (clientSocket != null && output != null && input != null) {
            try {
                /* Create a thread to read from the server. */
                new Thread(new ChatClient()).start();
                while (!closed) {
                    output.println(inputLine.readLine().trim());
                }
                output.close();
                input.close();
                clientSocket.close();
            } catch (IOException e) {
                System.err.println(e);
            }
        }
    }

    /*
     * Create a thread to read from the server.
     */
    public void run() {
        /*
         * Keep on reading from the socket till we receive "Bye" from the
         * server. Once we received that then we want to break.
         */
        String responseLine;
        try {
            while ((responseLine = input.readLine()) != null) {
                System.out.println(responseLine);
                if (responseLine.indexOf("*** Bye") != -1) {
                    break;
                }
            }
            closed = true;
        } catch (IOException e) {
            System.err.println(e);
        }
    }
}