package tbc.GUI;

import static tbc.client.Client.clientHandler;

import javafx.beans.binding.Bindings;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ChangeNameWindow {

    private static final Logger LOGGER = LogManager.getLogger(ChatInfoWindow.class);

    public static void display() {

        Stage window = new Stage();

        window.initModality(Modality.APPLICATION_MODAL);
        window.setTitle("Username ändern");
        window.setMinWidth(300);

        Label label = new Label("Neuer Username: ");
        label.setPrefSize(300, 25);

        TextField newUsername = new TextField();
        newUsername.setFocusTraversable(false);
        newUsername.setPromptText("Gib deinen Username ein.");

        Label labelStatus = new Label();

        Button enter = new Button("Enter");
        enter.disableProperty().bind(
            Bindings.isEmpty(newUsername.textProperty())
                .and(Bindings.isEmpty(newUsername.textProperty()))
                .and(Bindings.isEmpty(newUsername.textProperty()))
        );
        enter.setOnAction(e -> {
            String clientName = newUsername.getText();
                newUsername.clear();
                clientHandler.changeName(clientName);
        });

        Button close = new Button("Schliessen");
        close.setOnAction(e -> {
            Stage stage = (Stage) close.getScene().getWindow();
            stage.close();
            LobbyController.gameWindowController.appendMsg("Dein Name ist " + clientHandler.getMyName());
            LOGGER.info("Closed chat info window.");
        });

        VBox layout = new VBox(10);
        layout.setPadding(new Insets(10, 10, 10, 10));
        layout.getChildren().addAll(label, newUsername, labelStatus, enter, close);
        layout.setAlignment(Pos.CENTER);

        Scene scene = new Scene(layout, 300, 180);
        window.setScene(scene);
        window.showAndWait();
    }


}
