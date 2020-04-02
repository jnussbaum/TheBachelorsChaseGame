package tbc.GUI;

import tbc.client.Client;
import tbc.server.Server;

/**
 * the main class to run all the other GUI classes
 */

public class Main {

    public static void main(String[] args) {
        if (args[0].equals("client")) {
            System.out.println("Client started...");
            Client.main(args);
        } else if (args[0].equals("server")) {
            System.out.println("Server started...");
            Server.main(args);
        } else {
            System.out.println("Usage: client <hostadress>:<port> [<username>] | server <port>");
        }
    }

}