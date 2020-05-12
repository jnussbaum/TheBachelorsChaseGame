package tbc.gui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaPlayer.Status;
import javafx.scene.media.MediaView;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Class to open a window to show the video. Video starts automatically after window is opened.
 * There's a play/pause button at the bottom center of the window to start/pause the video. Press
 * the button or the enter on the keyboard to activate it. On the right side of it is a close button
 * to close the window.
 */
public class PlayVideo extends BorderPane {

    private static final Logger LOGGER = LogManager.getLogger(PlayVideo.class);

    private MediaPlayer mediaPlayer;

    public void display(String fileName) {
        Stage window = new Stage();

        window.initModality(Modality.APPLICATION_MODAL);
        window.setTitle("The Bachelor's Chase - Gameplay");

        String filePath = String.valueOf(getClass().getResource(fileName));

        Pane pane = new Pane();

        Media media = new Media(filePath);
        mediaPlayer = new MediaPlayer(media);
        MediaView mediaView = new MediaView(mediaPlayer);
        pane.getChildren().add(mediaView);
        mediaView.setFitHeight(720);
        mediaView.setFitWidth(1280);
        // Start playing the video as soon as the window is open
        mediaPlayer.play();
        LOGGER.info("Playing the video");

        Button playButton = new Button("||");

        playButton.setPrefWidth(30);
        playButton.setDefaultButton(true);

        playButton.setOnAction(e -> {
            Status status = mediaPlayer.getStatus();
            if (status == Status.PLAYING) {
                // If the status is Video playing
                if (mediaPlayer.getCurrentTime()
                    .greaterThanOrEqualTo(mediaPlayer.getTotalDuration())) {
                    // If the player is at the end of video
                    mediaPlayer.seek(mediaPlayer.getStartTime()); // Restart the video
                    mediaPlayer.play();
                } else {
                    // Pausing the player
                    mediaPlayer.pause();
                    LOGGER.info("Pausing the video");
                    playButton.setText(">");
                }
            } // If the video is stopped, halted or paused
            if (status == Status.HALTED || status == Status.STOPPED || status == Status.PAUSED) {
                mediaPlayer.play(); // Start the video
                playButton.setText("||");
            }
        });

        Button close = new Button("Close Gameplay");
        close.setOnAction(e -> {
            Stage stage = (Stage) close.getScene().getWindow();
            stage.close();
            LOGGER.info("Closed Gameplay window.");
        });

        HBox hBox = new HBox(10);
        hBox.setAlignment(Pos.CENTER);
        hBox.getChildren().addAll(playButton, close);

        VBox layout = new VBox(10);
        layout.setPadding(new Insets(10, 10, 10, 10));
        layout.getChildren().addAll(pane, hBox);
        layout.setAlignment(Pos.CENTER);

        Scene scene = new Scene(layout, 1280, 760);
        window.setScene(scene);
        window.showAndWait();
    }
}
