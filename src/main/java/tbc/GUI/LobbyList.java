package tbc.GUI;

import java.util.Arrays;
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
import tbc.server.Server;

public class LobbyList {

    private static final Logger LOGGER = LogManager.getLogger(LobbyList.class);

    public static void display() {
        Stage window = new Stage();

        window.initModality(Modality.APPLICATION_MODAL);
        window.setTitle("Lobbyliste");
        window.setMinWidth(300);
        window.setMinHeight(400);

        Label label = new Label();
        label.setText("Die aktuelle Lobbyliste: ");

        TextArea textArea = new TextArea();
        textArea.setMinSize(300, 400);
        textArea.setEditable(false);
        textArea.setFocusTraversable(false);
        textArea.setWrapText(true);

        // TODO output the lobby list to the window

        //System.out.println("LobbyList.display     getLobbies() " + Arrays.toString(Server.getLobbies()));
        //sout: []

        String lobbylist = Arrays.toString(Server.getLobbies());

        textArea.setText(lobbylist);

        //System.out.println("LobbyList.display     receiveLobbyList " + clientHandler.receiveLobbyList());

        //clientHandler.askForLobbyList();
        //sout: These are the available lobbies: There are no lobbies

        Button close = new Button("Schliessen");
        close.setOnAction(e -> {
            Stage stage = (Stage) close.getScene().getWindow();
            stage.close();
            LOGGER.info("Closed lobby list window.");
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
