package tbc.server;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

public class WriteHighScore {

    private static final Logger LOGGER = LogManager.getLogger(WriteHighScore.class);
    private String path = "HighScore.txt";
    private boolean appendToFile = false;

    public WriteHighScore(boolean appendValue) {
        appendToFile = appendValue;
    }

    public void writeToFile(String text) {
        Date date = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");

        FileWriter writer;
        BufferedWriter bufferedWriter;
        PrintWriter printWriter = null;
        try {
            writer = new FileWriter(path, appendToFile);
            bufferedWriter = new BufferedWriter(writer);
            printWriter = new PrintWriter(bufferedWriter);
            printWriter.println(formatter.format(date));
            printWriter.println(text);

            LOGGER.info("High score has been written to the HighScore.txt");
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            printWriter.close();
        }
    }

}
