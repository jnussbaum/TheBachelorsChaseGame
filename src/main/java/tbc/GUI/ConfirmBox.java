package tbc.GUI;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import tbc.client.Client;

public class ConfirmBox {

    /**
     * The confirmation box. Alerts the user, that s/he's about to close the game.
     */
    public static void display() {
        Stage window = new Stage();

        window.initModality(Modality.APPLICATION_MODAL);
        window.setTitle("Spiel verlassen");
        window.setMinWidth(250);

        Label label = new Label();
        label.setText("Willst du das Spiel wirklich verlassen?");
        Button yes = new Button("Ja");
        yes.setOnAction(e -> {
            Client.chatClient.processInput("LOGOUT");
        });
        Button no = new Button("Nein");
        no.setOnAction(e -> window.close());

        VBox layout = new VBox(10);
        layout.getChildren().addAll(label, yes, no);
        layout.setAlignment(Pos.CENTER);

        Scene scene = new Scene(layout, 300, 200);
        window.setScene(scene);
        window.showAndWait();
    }

}
