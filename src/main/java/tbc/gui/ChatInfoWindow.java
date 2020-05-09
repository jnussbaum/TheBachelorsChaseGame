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
 * This window shows the information about the chat, for example how to send a private message.
 */
public class ChatInfoWindow {

    private static final Logger LOGGER = LogManager.getLogger(ChatInfoWindow.class);

    public static void display() {

        Stage window = new Stage();

        window.initModality(Modality.APPLICATION_MODAL);
        window.setTitle("The Bachelor's Chase - Chat Info");
        window.setMinWidth(300);

        TextArea textArea = new TextArea();
        textArea.setPrefSize(400, 380);
        textArea.setEditable(false);
        textArea.setFocusTraversable(false);
        textArea.setWrapText(true);
        textArea.setText("Send message to everyone:\n"
            + "<message>\n"
            + "\nSend private message:\n"
            + "@<username of another player> <message>\n \n"
            + "Do NOT use the character '#' in the chat nor for your name.");

        Button close = new Button("Close");
        close.setOnAction(e -> {
            Stage stage = (Stage) close.getScene().getWindow();
            stage.close();
            LOGGER.info("Closed chat info window.");
        });

        VBox layout = new VBox(10);
        layout.setPadding(new Insets(10, 10, 10, 10));
        layout.getChildren().addAll(textArea, close);
        layout.setAlignment(Pos.CENTER);

        Scene scene = new Scene(layout, 350, 400);
        window.setScene(scene);
        window.showAndWait();
    }

}
