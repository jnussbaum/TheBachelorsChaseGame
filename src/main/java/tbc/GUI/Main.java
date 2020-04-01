package tbc.GUI;

import javafx.application.Application;
import tbc.client.Client;
import tbc.server.Server;

/**
 * the main class to run all the other GUI classes
 */

public class Main {

    public static void main(String[] args) {
        if (args[0].equals("client")) {
            Client.main(args);
            Application.launch(Login.class, args);
        } else if (args[0].equals("server")) {
            Server.main(args);
        }
    }

}