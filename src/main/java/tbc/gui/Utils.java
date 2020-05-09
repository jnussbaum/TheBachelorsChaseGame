package tbc.gui;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;

/**
 * This class is used to play sounds.
 */
public class Utils {

    /**
     * This method plays the music file given from as a parameter.
     *
     * @param fileName The name of the music file.
     */
    public void playAudio(String fileName) {
        try {
            String filePath = String.valueOf(getClass().getResource(fileName));
            Media sound = new Media(filePath);
            MediaPlayer mediaPlayer = new MediaPlayer(sound);
            mediaPlayer.setOnEndOfMedia(() -> mediaPlayer.seek(Duration.ZERO));
            mediaPlayer.play();

            mediaPlayer.play();
        } catch (Exception e) {
            System.out.println("Could not find the music file.");
        }

    }
}
