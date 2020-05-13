package tbc.gui;

import static java.lang.Thread.sleep;
import static tbc.client.Client.clientHandler;

import javafx.beans.binding.Bindings;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import tbc.client.Client;

/**
 * Opens a window with the list of the current lobbies. Here the user has to type in the name of a
 * lobby, which she/he wants to join/create.
 */
public class SelectLobby {

    private static final Logger LOGGER = LogManager.getLogger(ChatInfoWindow.class);
    public static boolean lobbyChosen = false;

    /**
     * Opens a window to show a lobby list. Here it needs an user input to create/join a lobby.
     * Enter a lobby name and press enter or the button 'Create/Join a lobby'. After that it will
     * check if you can join the lobby or not.
     */
    public static void display() {
        Stage window = new Stage();

        window.initModality(Modality.APPLICATION_MODAL);
        window.setTitle("The Bachelor's Chase - Select or create a lobby");
        window.setMinWidth(300);
        window.setMinHeight(400);

        Label currentLobbies = new Label("Current Lobbies: ");
        TextArea lobbyList = new TextArea();
        lobbyList.setMinHeight(200);
        lobbyList.setFocusTraversable(false);
        lobbyList.setEditable(false);
        clientHandler.askForLobbyList();
        try {
            sleep(1500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        lobbyList.setText(clientHandler.getLobbiesGui());

        TextField newLobby = new TextField();
        newLobby.setFocusTraversable(false);
        newLobby.setPromptText("Type in a new or an existing lobby name");

        Button createLobby = new Button("Create/Join a lobby");

        createLobby.setDefaultButton(true);

        createLobby.disableProperty().bind(
            Bindings.isEmpty(newLobby.textProperty())
                .and(Bindings.isEmpty(newLobby.textProperty()))
                .and(Bindings.isEmpty(newLobby.textProperty()))
        );

        createLobby.setOnAction(e -> {
            String lobbyName = newLobby.getText();
            LOGGER.info("Create/join a lobby.");
            clientHandler.rejected = false;
            Client.joinALobby(lobbyName);

            lobbyChosen = true;

            newLobby.clear();

            Stage stage = (Stage) createLobby.getScene().getWindow();
            stage.close();
            LOGGER.info("Closed select lobby window.");
        });

        VBox layout = new VBox(10);
        layout.setPadding(new Insets(10, 10, 10, 10));
        layout.getChildren().addAll(currentLobbies, lobbyList, newLobby, createLobby);
        layout.setAlignment(Pos.CENTER);

        Scene scene = new Scene(layout, 300, 180);
        window.setScene(scene);
        window.showAndWait();

        window.setOnCloseRequest(e -> display());
    }

}
