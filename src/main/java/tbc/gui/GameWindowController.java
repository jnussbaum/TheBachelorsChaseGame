package tbc.gui;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import tbc.client.Client;

/**
 * Processes a client's request to start the Game. Here it will show all the graphical components of
 * the game logic, which makes it possible for the clients to play the game.
 */
public class GameWindowController {

    private static final Logger LOGGER = LogManager.getLogger(GameWindowController.class);

    @FXML
    public TableView highScoreTable;
    @FXML
    public TableColumn highScoreName;
    @FXML
    public TableColumn highScoreCoins;
    @FXML
    public TextField msgField;
    @FXML
    public Button btnSend;
    @FXML
    public Button btnNewMatch;
    @FXML
    public ImageView imgView;
    @FXML
    public ImageView imageView1;
    @FXML
    public ImageView imageView2;
    @FXML
    public ImageView imageView3;
    @FXML
    public ImageView imageView4;
    @FXML
    public ImageView imageView5;
    @FXML
    public ImageView imageView6;
    @FXML
    public ImageView imageView7;
    @FXML
    public ImageView imageView8;
    @FXML
    private TextArea txtAChat;
    @FXML
    private Button btnRdy;
    @FXML
    private TextArea gameChatArea;
    private Window secondStage;

    public void showCard(String card) {
        LOGGER.info(card);
        switch (card) {

            case ("Party"):
                try {
                    Image party = new Image("tbc/gui/img/party.png");
                    imageView1.setImage(party);
                }catch (Exception e){
                    LOGGER.error("Image not found");
                }
                break;
                /*
            case ("Plagiarism"):
                Image plagiarism = new Image("tbc/gui/img/plagiarism.png");
                imageView2.setImage(plagiarism);
                break;
            case ("Coffee"):
                Image coffee = new Image("tbc/gui/img/coffee.png");
                imageView3.setImage(coffee);
                break;
            case ("Study"):
                Image study = new Image("tbc/gui/img/study.png");
                imageView4.setImage(study);
                break;
            case ("GoodLecturer"):
                Image goodLecturer = new Image("tbc/gui/img/goodlecturer.png");
                imageView5.setImage(goodLecturer);
                break;
            case ("RedBull"):
                Image redbull = new Image("tbc/gui/img/redbull.png");
                imageView6.setImage(redbull);
                break;
            case ("WLAN"):
                Image wlan = new Image("tbc/gui/img/wlan.png");
                imageView7.setImage(wlan);
                break;*/
            default:
                LOGGER.error(card + " does not exist.");
        }
    }

    /**
     * If the button 'Send' is pressed, it calls the method processInput() with the input from the
     * chat field as parameter. Clears the chat field after the 'Send' button is pressed.
     */
    public void sendMsg() {
        Client.chatClient.processInput(msgField.getText());
        msgField.clear();
    }

    /**
     * This method appends the String parameter 'msg' to the chat area. The text color of the chat
     * depends on the chosen logo.
     *
     * @param msg The String which will be appended to the txtAChat.
     */
    public void appendMsg(String msg) {
        String logo = LoginController.getChosenLogo();
        if (logo.equals("girly")) {
            txtAChat.setStyle("-fx-text-fill: red");
        } else if (logo.equals("nerd")) {
            txtAChat.setStyle("-fx-text-fill: green");
        } else if (logo.equals("hippy")) {
            txtAChat.setStyle("-fx-text-fill: yellow");
        } else if (logo.equals("emo")) {
            txtAChat.setStyle("-fx-text-fill: purple");
        }
        txtAChat.appendText(msg + "\n");
    }

    /**
     * If the user presses on 'Change username' in the menubar, this method will call up the display()
     * method from the class ChangeNameWindow. It opens a window where the user can change his/her
     * username.
     */
    public void changeUsername() {
        LOGGER.info("Show change name window.");
        ChangeNameWindow.display();
    }

    /**
     * If the user presses on 'Logout' in the menubar, this method will call up the display() method
     * from the class ConfirmBox. It opens a window where it alerts the user that she/he will logout
     * and asks for a confirmation.
     */
    public void logOut() {
        LOGGER.info("Show Confirm Box");
        ConfirmBox.display();
    }

    /**
     * If the user presses on 'Info' in the menubar, this method will call up the display() method
     * from the class GameInfoWindow. It opens a window where you can see the information of the game
     * (rules and goals).
     */
    public void giveInfo() {
        LOGGER.info("Show game info.");
        GameInfoWindow.display();
    }

    /**
     * If the user presses on 'Chat Info' in the menubar, this method will call up the display()
     * method from the class ChatInfoWindow. It opens a window where you can see the information of
     * the game about chatting. Example: How to send a private message.
     */
    public void giveChatInfo() {
        LOGGER.info("Show chat info.");
        ChatInfoWindow.display();
    }

    /**
     * If the user presses on 'Playerlist', this method will call up the display() method from the
     * class PlayerList. It opens a window to show a list of the current players.
     */
    public void showPlayerList() {
        LOGGER.info("Show player list.");
        PlayerList.display();
    }

    /**
     * If the user presses on 'Lobbylist', this method will call up the display() method from the
     * class LobbyList. It opens a window to show a list of the current lobbies.
     */
    public void showLobbyList() {
        LOGGER.info("Show lobby list.");
        LobbyList.display();
    }

    /**
     * If the user presses on 'Cards', this method will load the CardWindowFXML.fxml file. It opens a
     * window to show the cards of the game.
     */
    public void showCards() {
        LOGGER.info("Show cards.");
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("CardWindowFXML.fxml"));
            BorderPane card = loader.load();

            Stage cardWindow = new Stage();
            cardWindow.setTitle("The Bachelor's Chase - Cards");
            cardWindow.initModality(Modality.APPLICATION_MODAL);
            cardWindow.initOwner(secondStage);
            Scene cardScene = new Scene(card);
            cardWindow.setScene(cardScene);
            cardWindow.show();

            CardWindowController cardWindowController = loader.getController();

        } catch (Exception e) {
            LOGGER.error("Couldn't find CardWindowFXML file.");
            e.printStackTrace();
        }
    }

    /**
     * If the user presses the button 'Ready', this method will call the method askToStartAGame() from
     * the class Client. It tells the user to wait for other players and disables the button 'Ready'.
     */
    public void rdyForTheGame() {
        Client.askToStartAGame();

        appendGameMsg("Waiting for other players to be ready...");
        btnRdy.setDisable(true);

        // For the highScore
        /*Platform.runLater(() -> {
            Player[] players = Client.game.getPlayers();
            for (Player name : players) {
                String playerName = name.getName();
                int coins = name.getNumOfCoins();
                LOGGER.info("Name: " + playerName + " Coins: " + coins);
            }
        });*/
    }

    /**
     * This method appends the String that he gets as a String parameter.
     *
     * @param msg The String that will be appended to the gameChatArea.
     */
    public void appendGameMsg(String msg) {
        gameChatArea.appendText(msg + "\n");
    }

    /**
     * If the user presses the button 'New match', this method will call the method askForNewMatch()
     * from the class ClientHandler. It disables the button 'New match'.
     */
    public void startNewMatch() {
        Client.clientHandler.askForNewMatch();
        btnNewMatch.setDisable(true);
    }

}
