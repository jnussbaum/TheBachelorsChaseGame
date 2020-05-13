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
 * This will be called if a user can not join a lobby anymore because the game already started in
 * this lobby.
 */
public class RejectJoiningLobbyWindow {

    private static final Logger LOGGER = LogManager.getLogger(RejectJoiningLobbyWindow.class);

    /**
     * It opens a window with the message to tell the user he can not join anymore. The button 'OK'
     * closes this window.
     */
    public static void display() {
        Stage window = new Stage();

        window.initModality(Modality.APPLICATION_MODAL);
        window.setTitle("The Bachelor's Chase - Reject to join the lobby");
        window.setMinWidth(300);

        TextArea textArea = new TextArea();
        textArea.setPrefSize(300, 100);
        textArea.setEditable(false);
        textArea.setFocusTraversable(false);
        textArea.setWrapText(true);
        textArea.setText("You can not join this lobby anymore...");

        Button ok = new Button("OK");
        ok.setOnAction(e -> {
            Stage stage = (Stage) ok.getScene().getWindow();
            stage.close();
            SelectLobby.display();
            LOGGER.info("Closed rejection window.");
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
