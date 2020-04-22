package tbc.GUI;

import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import tbc.client.Client;

public class SelectOptions {

    public static void display() {
        Stage window = new Stage();

        window.initModality(Modality.APPLICATION_MODAL);
        window.setTitle("The Bachelor's Chase - Options");
        window.setMinWidth(250);

        Label label = new Label();
        label.setText("Please select an option:");

        Button hit = new Button("Hit");
        hit.setOnAction(e -> {
            Client.game.takeCard();
            Image party = new Image("img/party.png");
            LobbyController.gameWindowController.imageView1.setImage(party);
            Stage stage = (Stage) hit.getScene().getWindow();
            stage.close();
        });

        Button throwAway = new Button("Throw away");
        TextField cardName = new TextField();
        cardName.setFocusTraversable(false);
        cardName.setPromptText("Type in the card you want to throw away");
        throwAway.disableProperty().bind(Bindings.isEmpty(cardName.textProperty())
                .and(Bindings.isEmpty(cardName.textProperty()))
                .and(Bindings.isEmpty(cardName.textProperty()))
        );
        throwAway.setOnAction(e -> {
            Client.game.throwCard(cardName.getText());
            Stage stage = (Stage) throwAway.getScene().getWindow();
            stage.close();
        });

        Button quit = new Button("Quit");
        quit.setOnAction(e -> {
            Client.game.quitThisMatch();
            Stage stage = (Stage) quit.getScene().getWindow();
            stage.close();
        });

        TextField timer = new TextField();
        timer.setEditable(false);
        timer.setPrefWidth(30.0);
        Label timerLabel = new Label(" seconds left");
        HBox hBox = new HBox(10);
        hBox.getChildren().addAll(timer, timerLabel);
        hBox.setAlignment(Pos.CENTER);

        VBox layout = new VBox(10);
        layout.setPadding(new Insets(10, 10, 10, 10));
        layout.getChildren().addAll(label, hit, throwAway, cardName, quit, hBox);
        layout.setAlignment(Pos.TOP_CENTER);

        Scene scene = new Scene(layout, 300, 250);
        window.setScene(scene);
        window.showAndWait();
    }

}
