package tbc.gui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * This Class opens a window which tells the player that he reached over 180 points.
 */
public class DroppedOutWindow {

    private static final Logger LOGGER = LogManager.getLogger(DroppedOutWindow.class);

    /**
     * Opens a little window to tell that the player has reached over 180 points. The button 'OK'
     * closes this window. The player can't continue the game while the match has not ended.
     */
    public static void display() {
        Stage window = new Stage();

        window.initModality(Modality.APPLICATION_MODAL);
        window.setTitle("The Bachelor's Chase - Dropped out");
        window.setMinWidth(300);

        TextArea textArea = new TextArea();
        textArea.setPrefSize(300, 100);
        textArea.setEditable(false);
        textArea.setFocusTraversable(false);
        textArea.setWrapText(true);
        textArea.setText(
            "You have reached over 180 CP, so you dropped out. "
                + "\nPlease wait for the match to end...");

        Button ok = new Button("OK");
        ok.setOnAction(e -> {
            Stage stage = (Stage) ok.getScene().getWindow();
            stage.close();
            LOGGER.info("Closed dropped out window.");
        });

        VBox layout = new VBox(10);
        layout.setPadding(new Insets(10, 10, 10, 10));
        layout.getChildren().addAll(textArea, ok);
        layout.setAlignment(Pos.TOP_CENTER);

        Scene scene = new Scene(layout, 300, 150);
        window.setScene(scene);
        window.showAndWait();
    }
}
