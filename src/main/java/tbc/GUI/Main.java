package tbc.GUI;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import tbc.client.Client;
import tbc.server.Server;

/**
 * the main class to run all the other GUI classes
 */

public class Main {

    private final static Logger LOGGER = LogManager.getLogger(Main.class);

    public static void main(String[] args) {
        if (args[0].equals("client")) {
            LOGGER.info("Client started...");
            Client.main(args);
        } else if (args[0].equals("server")) {
            LOGGER.info("Server started...");
            Server.main(args);
        } else {
            LOGGER.error("Usage: client <hostadress>:<port> [<username>] | server <port>");
        }
    }

}