package tbc.gui;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Shows the three cards which are being used in the game.
 */
public class CardWindowController {

    private static final Logger LOGGER = LogManager.getLogger(CardWindowController.class);
    @FXML
    private Button btnBack;

    /**
     * Gets the stage of the close button and closes this stage.
     */
    public void closeCardWindow() {
        Stage stage = (Stage) btnBack.getScene().getWindow();
        stage.close();
        LOGGER.info("Closed card window.");
    }

}
