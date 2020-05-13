package tbc.server;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import tbc.game.CoinsCompare;
import tbc.game.Player;


/**
 * This Class writes the HighScore into a txt-file in order to have it persistently.
 */
public class WriteHighScore {

    private static final Logger LOGGER = LogManager.getLogger(WriteHighScore.class);
    private boolean appendToFile;

    /**
     * The constructor of the class.
     *
     * @param appendValue boolean to allow to append values to the file.
     */
    public WriteHighScore(boolean appendValue) {
        appendToFile = appendValue;
    }

    /**
     * The method to write into the txt-file.
     *
     * @param name  the username of the player.
     * @param coins the coins belonging to the player.
     */
    public void writeToFile(String name, int coins) {
        String path = "HighScore.txt";
        FileWriter writer;
        BufferedWriter bufferedWriter;
        PrintWriter printWriter;
        try {
            writer = new FileWriter(path, appendToFile);
            bufferedWriter = new BufferedWriter(writer);
            printWriter = new PrintWriter(bufferedWriter);
            printWriter.print(name + " " + coins + "\n");

            sortHighScore();

            printWriter.close();

            LOGGER.info("Highscore has been written to the HighScore.txt");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Takes the information inside of the HighScore.txt file, sorts the values inside of it and
     * writes it again.
     */
    private void sortHighScore() {
        try {
            BufferedReader reader = new BufferedReader(new FileReader("HighScore.txt"));
            ArrayList<Player> playerRecords = new ArrayList<>();
            String currentLine = reader.readLine();

            while (currentLine != null) {
                String[] playerDetail = currentLine.split(" ");
                String name = playerDetail[0];
                int coin = Integer.parseInt(playerDetail[1]);
                playerRecords.add(new Player(name, coin));

                currentLine = reader.readLine();
            }

            Collections.sort(playerRecords, new CoinsCompare());
            BufferedWriter writer = new BufferedWriter(new FileWriter("HighScore.txt"));

            for (Player player : playerRecords) {
                writer.write(player.getName());
                writer.write(" " + player.getNumOfCoins());
                writer.newLine();
            }

            reader.close();
            writer.close();
        } catch (FileNotFoundException e) {
            LOGGER.error("Could not find HighScore.txt");
        } catch (IOException e) {
            LOGGER.error("Could not read from HighScore.txt");
        }
    }

}
