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
import tbc.game.Player;

/**
 * Processes a client's request to start the Game. Here it will show all the graphical components of
 * the game logic, which makes it possible for the clients to play the game.
 */
public class GameWindowController {

    private static final Logger LOGGER = LogManager.getLogger(GameWindowController.class);
    //private ObservableList<Player> players = observableArrayList();

    @FXML
    public TableView<Player> highScoreTable;
    @FXML
    public TableColumn<Player, String> highScoreName;
    @FXML
    public TableColumn<Player, Integer> highScoreCoins;
    @FXML
    public TextField msgField;
    @FXML
    public Button btnSend;
    @FXML
    public Button btnNewMatch;
    @FXML
    public ImageView coffeeCard;
    @FXML
    public ImageView redBullCard;
    @FXML
    public ImageView wLanCard;
    @FXML
    public ImageView studyCard;
    @FXML
    public ImageView profCard;
    @FXML
    public ImageView partyCard;
    @FXML
    public ImageView plagCard;
    @FXML
    private TextArea txtAChat;
    @FXML
    private Label coffeeCnt;
    @FXML
    private Label redBullCnt;
    @FXML
    private Label wLanCnt;
    @FXML
    private Label studyCnt;
    @FXML
    private Label profCnt;
    @FXML
    private Label partyCnt;
    @FXML
    private Label plagCnt;
    @FXML
    private Button btnRdy;
    @FXML
    private TextArea gameChatArea;
    private Window secondStage;

    /*
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Platform.runLater(() -> {
            Player[] players = Client.game.getPlayers();
            System.out.println("GameWindowController.initialize " + Arrays.toString(players));
            for (Player name : players) {
                String playerName = name.getName();
                int coins = name.getNumOfCoins();
                LOGGER.info("Name: " + playerName + " Coins: " + coins);
                //addPlayerToList(playerName, coins);
            }
            //setTable();
        });
    }

    private void setTable() {
        highScoreName.setCellValueFactory(new PropertyValueFactory<>("name"));
        highScoreCoins.setCellValueFactory(new PropertyValueFactory<>("numOfCoins"));

        highScoreTable.setItems(players);
    }

    private void addPlayerToList(String name, int coins) {
        players.add(new Player(name, coins));
    }*/

    /**
     * Switches the card name and loads the specific card image to the imageView place.
     * If there is none card it will load the specific card image and increments the number in the label.
     * Else it checks the number of existing cards and increments its number in the label.
     *
     * @param card The name of the card that has been drawn.
     */
    public void showCard(String card) {
        int cnt;
        switch (card) {
            case ("Coffee"):
                if (coffeeCnt.getText().isEmpty()) {
                    Image coffee = new Image("tbc/gui/img/coffee.png");
                    coffeeCard.setImage(coffee);
                    cnt = 1;
                    coffeeCnt.setText(cnt + "x");
                } else {
                    String label = coffeeCnt.getText();
                    cnt = getCnt(label);
                    cnt++;
                    coffeeCnt.setText(cnt + "x");
                }
                break;
            case ("RedBull"):
                if (redBullCnt.getText().isEmpty()) {
                    Image redBull = new Image("tbc/gui/img/redbull.png");
                    redBullCard.setImage(redBull);
                    cnt = 1;
                    redBullCnt.setText(cnt + "x");
                } else {
                    String label = redBullCnt.getText();
                    cnt = getCnt(label);
                    cnt++;
                    redBullCnt.setText(cnt + "x");
                }
                break;
            case ("WLAN"):
                if (wLanCnt.getText().isEmpty()) {
                    Image wLan = new Image("tbc/gui/img/wlan.png");
                    wLanCard.setImage(wLan);
                    cnt = 1;
                    wLanCnt.setText(cnt + "x");
                } else {
                    String label = wLanCnt.getText();
                    cnt = getCnt(label);
                    cnt++;
                    wLanCnt.setText(cnt + "x");
                }
                break;
            case ("Study"):
                if (studyCnt.getText().isEmpty()) {
                    Image study = new Image("tbc/gui/img/study.png");
                    studyCard.setImage(study);
                    cnt = 1;
                    studyCnt.setText(cnt + "x");
                } else {
                    String label = studyCnt.getText();
                    cnt = getCnt(label);
                    cnt++;
                    studyCnt.setText(cnt + "x");
                }
                break;
            case ("GoodLecturer"):
                if (profCnt.getText().isEmpty()) {
                    Image goodLecturer = new Image("tbc/gui/img/goodlecturer.png");
                    profCard.setImage(goodLecturer);
                    cnt = 1;
                    profCnt.setText(cnt + "x");
                } else {
                    String label = profCnt.getText();
                    cnt = getCnt(label);
                    cnt++;
                    profCnt.setText(cnt + "x");
                }
                break;
            case ("Party"):
                if (partyCnt.getText().isEmpty()) {
                    Image party = new Image("tbc/gui/img/party.png");
                    partyCard.setImage(party);
                    cnt = 1;
                    partyCnt.setText(cnt + "x");
                } else {
                    String label = partyCnt.getText();
                    cnt = getCnt(label);
                    cnt++;
                    partyCnt.setText(cnt + "x");
                }
                break;
            case ("Plagiarism"):
                if (plagCnt.getText().isEmpty()) {
                    Image plagiarism = new Image("tbc/gui/img/plagiarism.png");
                    plagCard.setImage(plagiarism);
                    cnt = 1;
                    plagCnt.setText(cnt + "x");
                } else {
                    String label = plagCnt.getText();
                    cnt = getCnt(label);
                    cnt++;
                    plagCnt.setText(cnt + "x");
                }
                break;
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
        switch (logo) {
            case "girly":
                txtAChat.setStyle("-fx-text-fill: red");
                break;
            case "nerd":
                txtAChat.setStyle("-fx-text-fill: green");
                break;
            case "hippy":
                txtAChat.setStyle("-fx-text-fill: yellow");
                break;
            case "emo":
                txtAChat.setStyle("-fx-text-fill: purple");
                break;
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
     * from the class ClientHandler. It disables the button 'New match' and sets all imageView to demo.png.
     * Also deletes the label, which counts the cards number.
     */
    public void startNewMatch() {
        Client.clientHandler.askForNewMatch();
        btnNewMatch.setDisable(true);

        // reset the card images to the demo image
        Image demo = new Image("tbc/gui/img/demo.png");
        coffeeCard.setImage(demo);
        redBullCard.setImage(demo);
        wLanCard.setImage(demo);
        studyCard.setImage(demo);
        profCard.setImage(demo);
        partyCard.setImage(demo);
        plagCard.setImage(demo);

        // reset the label of the card counts
        coffeeCnt.setText("");
        redBullCnt.setText("");
        wLanCnt.setText("");
        studyCnt.setText("");
        profCnt.setText("");
        partyCnt.setText("");
        plagCnt.setText("");
    }

    /**
     * Method to check how many number of cards you have from a specific card.
     *
     * @param labelCnt A String from the label of the drawn cards.
     * @return Number of already drawn cards.
     */
    public int getCnt(String labelCnt) {
        String count = labelCnt.substring(0, labelCnt.indexOf("x"));
        int cnt = Integer.parseInt(count);
        return cnt;
    }

    /**
     * Switches the card name and loads the specific card image to the imageView place.
     * If there is already one card it loads the demo.png and deletes the label.
     * Else it checks the number of the existing cards and decrements the number in the label.
     *
     * @param cardName The name of the card that the user wants to throw away.
     */
    public void throwTheCard(String cardName) {
        int cnt;
        switch (cardName) {
            case ("Coffee"):
                if (coffeeCnt.getText().equals("1x")) {
                    Image coffee = new Image("tbc/gui/img/demo.png");
                    coffeeCard.setImage(coffee);
                    coffeeCnt.setText("");
                } else {
                    String label = coffeeCnt.getText();
                    cnt = getCnt(label);
                    cnt--;
                    coffeeCnt.setText(cnt + "x");
                }
                break;
            case ("RedBull"):
                if (redBullCnt.getText().equals("1x")) {
                    Image redBull = new Image("tbc/gui/img/demo.png");
                    redBullCard.setImage(redBull);
                    redBullCnt.setText("");
                } else {
                    String label = redBullCnt.getText();
                    cnt = getCnt(label);
                    cnt--;
                    redBullCnt.setText(cnt + "x");
                }
                break;
            case ("WLAN"):
                if (wLanCnt.getText().equals("1x")) {
                    Image wLan = new Image("tbc/gui/img/demo.png");
                    wLanCard.setImage(wLan);
                    wLanCnt.setText("");
                } else {
                    String label = wLanCnt.getText();
                    cnt = getCnt(label);
                    cnt--;
                    wLanCnt.setText(cnt + "x");
                }
                break;
            case ("Study"):
                if (studyCnt.getText().equals("1x")) {
                    Image study = new Image("tbc/gui/img/demo.png");
                    studyCard.setImage(study);
                    studyCnt.setText("");
                } else {
                    String label = studyCnt.getText();
                    cnt = getCnt(label);
                    cnt--;
                    studyCnt.setText(cnt + "x");
                }
                break;
            case ("GoodLecturer"):
                if (profCnt.getText().equals("1x")) {
                    Image goodLecturer = new Image("tbc/gui/img/demo.png");
                    profCard.setImage(goodLecturer);
                    profCnt.setText("");
                } else {
                    String label = profCnt.getText();
                    cnt = getCnt(label);
                    cnt--;
                    profCnt.setText(cnt + "x");
                }
                break;
            case ("Party"):
                if (partyCnt.getText().equals("1x")) {
                    Image party = new Image("tbc/gui/img/demo.png");
                    partyCard.setImage(party);
                    partyCnt.setText("");
                } else {
                    String label = partyCnt.getText();
                    cnt = getCnt(label);
                    cnt--;
                    partyCnt.setText(cnt + "x");
                }
                break;
            case ("Plagiarism"):
                if (plagCnt.getText().equals("1x")) {
                    Image plagiarism = new Image("tbc/gui/img/demo.png");
                    plagCard.setImage(plagiarism);
                    plagCnt.setText("");
                } else {
                    String label = plagCnt.getText();
                    cnt = getCnt(label);
                    cnt--;
                    plagCnt.setText(cnt + "x");
                }
                break;
            default:
                LOGGER.error(cardName + " does not exist.");
        }
    }
}
