package tbc.GUI;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class CardWindowController {

  private static final Logger LOGGER = LogManager.getLogger(CardWindowController.class);
  @FXML private Button btnZurueck;


  public void closeCardWindow() {
    Stage stage = (Stage) btnZurueck.getScene().getWindow();
    stage.close();
    LOGGER.info("Closed cards window.");
  }
}
