package tbc.GUI;

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

public class ChatInfoWindow {

    private static final Logger LOGGER = LogManager.getLogger(ChatInfoWindow.class);

    public static void display() {

        Stage window = new Stage();

        window.initModality(Modality.APPLICATION_MODAL);
        window.setTitle("Chat Info");
        window.setMinWidth(300);

        TextArea textArea = new TextArea();
        textArea.setPrefSize(300, 380);
        textArea.setEditable(false);
        textArea.setFocusTraversable(false);
        textArea.setText("Nachricht an alle senden:\n"
            + "<Nachricht>\n"
            + "\nPrivate Nachrichten senden:\n"
            + "@<Username vom anderen Spieler> <Nachricht>\n"
            + "\nZum ausloggen:\n"
            + "LOGOUT im Chat schreiben oder Logout im Menu unter The Bachelor's Chase > Logout");

        Button close = new Button("Schliessen");
        close.setOnAction(e -> {
            Stage stage = (Stage) close.getScene().getWindow();
            stage.close();
            LOGGER.info("Closed chat info window.");
        });

        VBox layout = new VBox(10);
        layout.setPadding(new Insets(10, 10, 10, 10));
        layout.getChildren().addAll(textArea, close);
        layout.setAlignment(Pos.CENTER);

        Scene scene = new Scene(layout, 300, 400);
        window.setScene(scene);
        window.showAndWait();
    }

}
