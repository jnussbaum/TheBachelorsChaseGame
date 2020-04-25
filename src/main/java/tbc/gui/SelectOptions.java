package tbc.gui;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;
import tbc.client.Client;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Opens a window where the user can press 'Hit', 'Throw away' or 'Quit'
 * The button 'Throw away' is disabled as long as there is nothing typed in the field below.
 * This window will close automatically after 10 seconds if the user did not press any button.
 */
public class SelectOptions {

    private static final Integer STARTTIME = 10;
    private static Timeline timeline;
    private static TextField timerField = new TextField();
    private static IntegerProperty timeSeconds = new SimpleIntegerProperty(STARTTIME);

    public static void display() {
        Stage window = new Stage();

        window.initModality(Modality.APPLICATION_MODAL);
        window.setTitle("The Bachelor's Chase - Options");
        window.setMinWidth(250);

        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            public void run() {
                Platform.runLater(() -> {
                    window.close();
                });
            }
        }, 10000);

        Label label = new Label();
        label.setText("Please select an option:");

        Button hit = new Button("Hit");
        hit.setOnAction(e -> {
            Client.game.takeCard();
            timer.cancel();
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
            timer.cancel();
            Stage stage = (Stage) throwAway.getScene().getWindow();
            stage.close();
        });

        Button quit = new Button("Quit");
        quit.setOnAction(e -> {
            Client.game.quitThisMatch();
            timer.cancel();
            Stage stage = (Stage) quit.getScene().getWindow();
            stage.close();
        });

        // Timer for each round
        timerField.setEditable(false);
        timerField.setPrefWidth(30.0);
        timerField.textProperty().bind(timeSeconds.asString());

        if (timeline != null) {
            timeline.stop();
        }
        timeSeconds.set(STARTTIME);
        timeline = new Timeline();
        timeline.getKeyFrames().add(
                new KeyFrame(Duration.seconds(STARTTIME + 1),
                        new KeyValue(timeSeconds, 0)));
        timeline.playFromStart();

        Label timerLabel = new Label(" seconds left");
        HBox hBox = new HBox(10);
        hBox.getChildren().addAll(timerField, timerLabel);
        hBox.setAlignment(Pos.CENTER);

        VBox layout = new VBox(10);
        layout.setPadding(new Insets(10, 10, 10, 10));
        layout.getChildren().addAll(label, hit, throwAway, cardName, quit, hBox);
        layout.setAlignment(Pos.TOP_CENTER);

        Scene scene = new Scene(layout, 300, 250);
        window.setScene(scene);
        window.show();
    }

}
