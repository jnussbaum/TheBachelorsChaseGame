package tbc.gui;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * This class is used to play the background music.
 */
public class PlayMusic {

    private static final Logger LOGGER = LogManager.getLogger(PlayMusic.class);
    private static MediaPlayer mediaPlayer;

    /**
     * This method plays the music file.
     */
    public static void playAudio() {
        String fileName = "backgroundMusic.wav";
        try {
            String filePath = String.valueOf(PlayMusic.class.getResource(fileName));
            Media sound = new Media(filePath);
            mediaPlayer = new MediaPlayer(sound);
            startAudio();
        } catch (Exception e) {
            LOGGER.error("Could not find the music file: " + fileName);
        }
    }

    /**
     * Start playing the music. Begin from the beginning if the music comes to an end.
     */
    public static void startAudio() {
        mediaPlayer.setOnEndOfMedia(() -> mediaPlayer.seek(Duration.ZERO));
        mediaPlayer.play();
    }

    /**
     * Stop playing the music.
     */
    public static void stopAudio() {
        mediaPlayer.stop();
    }
}
