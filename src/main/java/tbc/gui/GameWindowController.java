package tbc.gui;

import static javafx.collections.FXCollections.observableArrayList;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
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
    public ImageView energyCard;
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
    public ImageView cheatCard;
    private ObservableList<Player> players = observableArrayList();
    @FXML
    private TextArea txtAChat;
    @FXML
    private Label coffeeCnt;
    @FXML
    private Label energyCnt;
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
                txtAChat.setStyle("-fx-text-fill: #cfba21");
                break;
            case "emo":
                txtAChat.setStyle("-fx-text-fill: purple");
                break;
        }
        txtAChat.appendText(msg + "\n");
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
     * from the class GameInfoWindow. It opens a window where you can see the information of the
     * game (rules and goals).
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
     * If the user presses on 'Cards', this method will load the CardWindowFXML.fxml file. It opens
     * a window to show the cards of the game.
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
     * This method appends the String that he gets as a String parameter.
     *
     * @param msg The String that will be appended to the gameChatArea.
     */
    public void appendGameMsg(String msg) {
        gameChatArea.appendText(msg + "\n");
    }

    /**
     * If the user presses the button 'Ready', this method will call the method askToStartAGame()
     * from the class Client. It tells the user to wait for other players and disables the button
     * 'Ready'.
     */
    public void rdyForTheGame() {
        Client.askToStartAGame();
        appendGameMsg("Waiting for other players to be ready...");
        btnRdy.setDisable(true);
    }

    /**
     * Switches the card name and loads the specific card image to the imageView place. If there is
     * none card it will load the specific card image and increments the number in the label. Else
     * it checks the number of existing cards and increments its number in the label.
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
                } else {
                    String label = coffeeCnt.getText();
                    cnt = getCnt(label);
                    cnt++;
                }
                coffeeCnt.setText(cnt + "x Coffee");
                break;
            case ("Energy"):
                if (energyCnt.getText().isEmpty()) {
                    Image energy = new Image("tbc/gui/img/energy.png");
                    energyCard.setImage(energy);
                    cnt = 1;
                } else {
                    String label = energyCnt.getText();
                    cnt = getCnt(label);
                    cnt++;
                }
                energyCnt.setText(cnt + "x Energy");
                break;
            case ("WLAN"):
                if (wLanCnt.getText().isEmpty()) {
                    Image wLan = new Image("tbc/gui/img/wlan.png");
                    wLanCard.setImage(wLan);
                    cnt = 1;
                } else {
                    String label = wLanCnt.getText();
                    cnt = getCnt(label);
                    cnt++;
                }
                wLanCnt.setText(cnt + "x WLAN");
                break;
            case ("Study"):
                if (studyCnt.getText().isEmpty()) {
                    Image study = new Image("tbc/gui/img/study.png");
                    studyCard.setImage(study);
                    cnt = 1;
                } else {
                    String label = studyCnt.getText();
                    cnt = getCnt(label);
                    cnt++;
                }
                studyCnt.setText(cnt + "x Study");
                break;
            case ("GoodLecturer"):
                if (profCnt.getText().isEmpty()) {
                    Image goodLecturer = new Image("tbc/gui/img/goodlecturer.png");
                    profCard.setImage(goodLecturer);
                    cnt = 1;
                } else {
                    String label = profCnt.getText();
                    cnt = getCnt(label);
                    cnt++;
                }
                profCnt.setText(cnt + "x GoodLecturer");
                break;
            case ("Party"):
                if (partyCnt.getText().isEmpty()) {
                    Image party = new Image("tbc/gui/img/party.png");
                    partyCard.setImage(party);
                    cnt = 1;
                } else {
                    String label = partyCnt.getText();
                    cnt = getCnt(label);
                    cnt++;
                }
                partyCnt.setText(cnt + "x Party");
                break;
            case ("Plagiarism"):
                if (plagCnt.getText().isEmpty()) {
                    Image plagiarism = new Image("tbc/gui/img/plagiarism.png");
                    plagCard.setImage(plagiarism);
                    cnt = 1;
                } else {
                    String label = plagCnt.getText();
                    cnt = getCnt(label);
                    cnt++;
                }
                plagCnt.setText(cnt + "x Plagiarism");
                break;
            case ("BOSS"):
                Image cheat = new Image("tbc/gui/img/cheat.png");
                cheatCard.setImage(cheat);
                break;
            case ("LOSER"):
                Image loser = new Image("tbc/gui/img/cheat.png");
                cheatCard.setImage(loser);
            default:
                LOGGER.error(card + " does not exist.");
        }
    }

    /**
     * Switches the card name and loads the specific card image to the imageView place. If there is
     * already one card it loads the demo.png and deletes the label. Else it checks the number of
     * the existing cards and decrements the number in the label.
     *
     * @param cardName The name of the card that the user wants to throw away.
     */
    public void throwTheCard(String cardName) {
        int cnt;
        switch (cardName) {
            case ("Coffee"):
                if (coffeeCnt.getText().startsWith("1x")) {
                    Image coffee = new Image("tbc/gui/img/demo.png");
                    coffeeCard.setImage(coffee);
                    coffeeCnt.setText("");
                } else {
                    String label = coffeeCnt.getText();
                    cnt = getCnt(label);
                    cnt--;
                    coffeeCnt.setText(cnt + "x Coffee");
                }
                break;
            case ("Energy"):
                if (energyCnt.getText().startsWith("1x")) {
                    Image redBull = new Image("tbc/gui/img/demo.png");
                    energyCard.setImage(redBull);
                    energyCnt.setText("");
                } else {
                    String label = energyCnt.getText();
                    cnt = getCnt(label);
                    cnt--;
                    energyCnt.setText(cnt + "x Energy");
                }
                break;
            case ("WLAN"):
                if (wLanCnt.getText().startsWith("1x")) {
                    Image wLan = new Image("tbc/gui/img/demo.png");
                    wLanCard.setImage(wLan);
                    wLanCnt.setText("");
                } else {
                    String label = wLanCnt.getText();
                    cnt = getCnt(label);
                    cnt--;
                    wLanCnt.setText(cnt + "x WLAN");
                }
                break;
            case ("Study"):
                if (studyCnt.getText().startsWith("1x")) {
                    Image study = new Image("tbc/gui/img/demo.png");
                    studyCard.setImage(study);
                    studyCnt.setText("");
                } else {
                    String label = studyCnt.getText();
                    cnt = getCnt(label);
                    cnt--;
                    studyCnt.setText(cnt + "x Study");
                }
                break;
            case ("GoodLecturer"):
                if (profCnt.getText().startsWith("1x")) {
                    Image goodLecturer = new Image("tbc/gui/img/demo.png");
                    profCard.setImage(goodLecturer);
                    profCnt.setText("");
                } else {
                    String label = profCnt.getText();
                    cnt = getCnt(label);
                    cnt--;
                    profCnt.setText(cnt + "x GoodLecturer");
                }
                break;
            case ("Party"):
                if (partyCnt.getText().startsWith("1x")) {
                    Image party = new Image("tbc/gui/img/demo.png");
                    partyCard.setImage(party);
                    partyCnt.setText("");
                } else {
                    String label = partyCnt.getText();
                    cnt = getCnt(label);
                    cnt--;
                    partyCnt.setText(cnt + "x Party");
                }
                break;
            case ("Plagiarism"):
                if (plagCnt.getText().startsWith("1x")) {
                    Image plagiarism = new Image("tbc/gui/img/demo.png");
                    plagCard.setImage(plagiarism);
                    plagCnt.setText("");
                } else {
                    String label = plagCnt.getText();
                    cnt = getCnt(label);
                    cnt--;
                    plagCnt.setText(cnt + "x Plagiarism");
                }
                break;
            default:
                LOGGER.error(cardName + " does not exist.");
        }
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
     * If the user presses the button 'New match', this method will call the method askForNewMatch()
     * from the class ClientHandler. It disables the button 'New match' and sets all imageView to
     * demo.png. Also deletes the label, which counts the cards number.
     */
    public void startNewMatch() {
        Client.clientHandler.askForNewMatch();
        btnNewMatch.setDisable(true);

        // reset the card images to the demo image
        Image demo = new Image("tbc/gui/img/demo.png");
        coffeeCard.setImage(demo);
        energyCard.setImage(demo);
        wLanCard.setImage(demo);
        studyCard.setImage(demo);
        profCard.setImage(demo);
        partyCard.setImage(demo);
        plagCard.setImage(demo);

        // reset the label of the card counts
        coffeeCnt.setText("");
        energyCnt.setText("");
        wLanCnt.setText("");
        studyCnt.setText("");
        profCnt.setText("");
        partyCnt.setText("");
        plagCnt.setText("");
    }

    /**
     * This method gets all the names and the coins of the Players, which are playing in the same
     * game.
     */
    public void setHighScore() {
        highScoreTable.getItems().clear();
        Platform.runLater(() -> {
            Player[] players = Client.game.getPlayers();
            for (Player name : players) {
                String playerName = name.getName();
                int coins = name.getNumOfCoins();
                LOGGER.info("Name: " + playerName + " Coins: " + coins);
                addPlayerToList(playerName, coins);
            }
            setTable();
        });
    }

    /**
     * Setting the high score table with the value (a player's name and its coins).
     */
    private void setTable() {
        highScoreName.setCellValueFactory(new PropertyValueFactory<>("name"));
        highScoreCoins.setCellValueFactory(new PropertyValueFactory<>("numOfCoins"));

        highScoreTable.setItems(players);
    }

    /**
     * Adding a player to the ObservableList<Player> players with the name and coins.
     *
     * @param name  The name of a player.
     * @param coins The coins from that player
     */
    private void addPlayerToList(String name, int coins) {
        players.add(new Player(name, coins));
    }

    /**
     * If the user presses the button 'Highscore', this method will call the method display() from
     * the class HighScoreWindow. It shows the player the persistently highscore from the game.
     */
    public void showHighScore() {
        HighScoreWindow.display();
    }

    public void showGamePlay() {
        //PlayMusic.stopAudio();
        try {
            PlayVideo gamePlay = new PlayVideo();
            gamePlay.display("GamePlay.mp4");
        } catch (Exception e) {
            LOGGER.error("Could not find GamePlay.mp4 file");
        }
    }

    /**
     * MenuItem to start playing the music.
     */
    public void startMusic() {
        PlayMusic.startAudio();
    }

    /**
     * MenuItem to stop playing the music.
     */
    public void stopMusic() {
        PlayMusic.stopAudio();
    }
}
