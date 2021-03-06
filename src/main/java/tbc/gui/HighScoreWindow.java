package tbc.gui;

import static java.lang.Thread.sleep;
import static tbc.client.Client.clientHandler;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * This class shows a window with the persistently highscore of played matches.
 */
public class HighScoreWindow {

    private static final Logger LOGGER = LogManager.getLogger(HighScoreWindow.class);

    /**
     * Opens a window to show a persistently highscore of the played matches. It asks for the
     * highscore, then replaces the regex and sets the formatted text to the highscore field. The
     * button 'Close' closes this window.
     */
    public static void display() {
        Stage window = new Stage();

        window.initModality(Modality.APPLICATION_MODAL);
        window.setTitle("The Bachelor's Chase - Highscore");
        window.setMinWidth(250);

        Label title = new Label();
        title.setText("Highscore:");

        TextArea highScoreArea = new TextArea();
        highScoreArea.setPrefSize(300, 500);
        highScoreArea.setWrapText(true);
        highScoreArea.setFocusTraversable(false);
        highScoreArea.setEditable(false);

        // get the highscore data from server
        clientHandler.askForHighScore();
        try {
            sleep(1500);
        } catch (InterruptedException e) {
            LOGGER.error("InterruptedException in the class HighScoreWindow.");
        }

        // replace all '$' to a new line
        String data = clientHandler.getHighScoreGui().replaceAll("_9-,", "\n");

        highScoreArea.setText(data);

        Button close = new Button("Close");
        close.setOnAction(e -> {
            Stage stage = (Stage) close.getScene().getWindow();
            stage.close();
            LOGGER.info("Closed highscore window.");
        });

        VBox layout = new VBox(10);
        layout.setPadding(new Insets(10, 10, 10, 10));
        layout.getChildren().addAll(title, highScoreArea, close);
        layout.setAlignment(Pos.CENTER);

        Scene scene = new Scene(layout, 300, 500);
        window.setScene(scene);
        window.showAndWait();
    }

}
