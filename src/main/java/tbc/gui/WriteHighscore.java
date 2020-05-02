package tbc.gui;

import javafx.application.Platform;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class WriteHighscore {

    private static final Logger LOGGER = LogManager.getLogger(WriteHighscore.class);
    private String path;
    private boolean appendToFile = false;

    public WriteHighscore( String file_path ) {

        path = file_path;


    }
    public WriteHighscore( String file_path , boolean append_value ) {

        path = file_path;
        appendToFile = append_value;

    }


    public void writeToFile(String name, int coins) {
        System.out.println("GameWindowController.writeToFile        name: " + name);
        System.out.println("GameWindowController.writeToFile        coins: " + coins);
        Platform.runLater(() -> {
            try {
                FileWriter output = new FileWriter(path, appendToFile);
                PrintWriter printWriter = new PrintWriter(output);
                printWriter.printf(name + " " + coins);
                printWriter.close();
            } catch (IOException e) {
                LOGGER.error("Could not write to the Highscore.txt");
            }
        });
    }
}
