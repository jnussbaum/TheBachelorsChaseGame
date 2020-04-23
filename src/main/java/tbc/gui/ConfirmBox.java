package tbc.gui;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import tbc.client.Client;

/**
 * A confirmation box. Alerts the user, that s/he's about to close the game.
 */
public class ConfirmBox {

    public static void display() {
        Stage window = new Stage();

        window.initModality(Modality.APPLICATION_MODAL);
        window.setTitle("The Bachelor's Chase - Logout");
        window.setMinWidth(250);

        Label label = new Label();
        label.setText("Are you sure to leave the game?");

        Button yes = new Button("Yes");
        yes.setOnAction(e -> {
            Client.chatClient.processInput("UserWantsToLogout");
        });
        Button no = new Button("No");
        no.setOnAction(e -> window.close());

        HBox hBox = new HBox(10);
        hBox.setAlignment(Pos.CENTER);
        hBox.getChildren().addAll(yes, no);

        VBox layout = new VBox(10);
        layout.getChildren().addAll(label, hBox);
        layout.setAlignment(Pos.CENTER);

        Scene scene = new Scene(layout, 300, 150);
        window.setScene(scene);
        window.showAndWait();
    }

}
