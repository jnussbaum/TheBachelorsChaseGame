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
import tbc.game.Player;
import tbc.game.coinsCompare;

public class WriteHighScore {

    private static final Logger LOGGER = LogManager.getLogger(WriteHighScore.class);
    private boolean appendToFile;

    public WriteHighScore(boolean appendValue) {
        appendToFile = appendValue;
    }

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

            Collections.sort(playerRecords, new coinsCompare());
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
