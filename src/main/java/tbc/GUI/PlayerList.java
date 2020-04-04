package tbc.GUI;

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

public class PlayerList {

    private static final Logger LOGGER = LogManager.getLogger(PlayerList.class);

    public static void display() {
        Stage window = new Stage();

        window.initModality(Modality.APPLICATION_MODAL);
        window.setTitle("Playerliste");
        window.setMinWidth(300);
        window.setMinHeight(400);

        Label label = new Label();
        label.setText("Die aktuelle Playerliste: ");

        TextArea textArea = new TextArea();
        textArea.setMinSize(300, 400);
        textArea.setEditable(false);
        textArea.setFocusTraversable(false);
        textArea.setWrapText(true);

        // TODO output the player list to the window
        /*for (ServerHandler sh : Server.getServerHandlers()) {
            System.out.println(sh);
        }*/

        //String playerList = ;
        //textArea.setText(playerList);

        Button close = new Button("Schliessen");
        close.setOnAction(e -> {
            Stage stage = (Stage) close.getScene().getWindow();
            stage.close();
            LOGGER.info("Closed player list window.");
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
