package tbc;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import tbc.client.Client;
import tbc.server.Server;

/**
 * The main class to run the game. It parses the arguments given from the user.
 */
public class Main {

    private static final Logger LOGGER = LogManager.getLogger(Main.class);

    public static void main(String[] args) {
        if (args[0].equalsIgnoreCase("client")) {
            LOGGER.info("Client started...");
            Client.main(args);
        } else if (args[0].equalsIgnoreCase("server")) {
            LOGGER.info("Server started...");
            Server.main(args);
        } else {
            LOGGER.error("Usage: client <hostadress>:<port> [<username>] | server <port>");
        }
    }
/*
    @Override
    public void start(Stage primaryStage) throws Exception {
        String musicFile = "backgroundMusic.wav";     // For example

        Media sound = new Media(new File(musicFile).toURI().toString());
        MediaPlayer mediaPlayer = new MediaPlayer(sound);
        mediaPlayer.play();
    }*/
}