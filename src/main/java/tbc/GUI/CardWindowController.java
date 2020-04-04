package tbc.GUI;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

public class CardWindowController {


  @FXML private Button btnZurueck;


  public void closeCardWindow() {
    Stage stage = (Stage) btnZurueck.getScene().getWindow();
    stage.close();
    System.out.println("Closed cards window.");
  }
}
