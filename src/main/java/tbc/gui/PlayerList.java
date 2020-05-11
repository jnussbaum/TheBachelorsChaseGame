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
 * If the button "Show Playerlist" in the Various window is pressed, it will open a this window "The
 * Bachelor's Chase - Playerlist". Here you can see a list of the current players.
 */
public class PlayerList {

    private static final Logger LOGGER = LogManager.getLogger(PlayerList.class);

    public static void display() {
        Stage window = new Stage();

        window.initModality(Modality.APPLICATION_MODAL);
        window.setTitle("The Bachelor's Chase - Playerlist");
        window.setMinWidth(250);
        window.setMinHeight(400);

        Label label = new Label();
        label.setText("The Playerlist: ");

        TextArea textArea = new TextArea();
        textArea.setMinSize(300, 400);
        textArea.setEditable(false);
        textArea.setFocusTraversable(false);
        textArea.setWrapText(true);

        //Get the player list if button is pressed
        clientHandler.askForPlayerList();
        try {
            sleep(1500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        textArea.setText(clientHandler.getPlayerListGui());

        Button close = new Button("Close");
        close.setOnAction(e -> {
            Stage stage = (Stage) close.getScene().getWindow();
            stage.close();
            LOGGER.info("Closed player list window.");
        });

        VBox layout = new VBox(10);
        layout.setPadding(new Insets(10, 10, 10, 10));
        layout.getChildren().addAll(label, textArea, close);
        layout.setAlignment(Pos.TOP_CENTER);

        Scene scene = new Scene(layout);
        window.setScene(scene);
        window.showAndWait();
    }
}
