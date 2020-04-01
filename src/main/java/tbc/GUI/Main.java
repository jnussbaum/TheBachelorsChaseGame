package tbc.GUI;

import javafx.application.Application;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import tbc.client.Client;
import tbc.server.Server;

/**
 * the main class to run all the other GUI classes
 */

public class Main {

    private static final Logger logger = LogManager.getLogger();

    public static void main(String[] args) {
        if (args[0].equals("client")) {
            Client.main(args);
            Application.launch(Login.class, args);
        } else if (args[0].equals("server")) {
            Server.main(args);
        } else {
            logger.error("Usage: client <hostadress>:<port> [<username>] | server <port>");
        }
    }

}