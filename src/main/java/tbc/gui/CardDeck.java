package tbc.gui;

import javafx.application.Platform;
import javafx.scene.image.Image;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class CardDeck {

    private static final Logger LOGGER = LogManager.getLogger(CardDeck.class);

    public static void showCard(String card) {
        LOGGER.info(card);
        switch (card) {

            case ("Party"):
                LOGGER.info(card);
                Platform.runLater(()->{
                    Image party = new Image("tbc/gui/img/party.png");
                    LobbyController.gameWindowController.imageView1.setImage(party);
                });
                break;
            case ("Plagiarism"):
                LOGGER.info(card);
                Platform.runLater(()->{
                    Image plagiarism = new Image("tbc/gui/img/plagiarism.png");
                    LobbyController.gameWindowController.imageView2.setImage(plagiarism);
                });
                break;
            case ("Coffee"):
                LOGGER.info(card);
                Platform.runLater(()->{
                    Image coffee = new Image("tbc/gui/img/coffee.png");
                    LobbyController.gameWindowController.imageView3.setImage(coffee);
                });
                break;
            case ("Study"):
                LOGGER.info(card);
                Platform.runLater(()->{
                    Image study = new Image("tbc/gui/img/study.png");
                    LobbyController.gameWindowController.imageView4.setImage(study);
                });
                break;
            case ("GoodLecturer"):
                LOGGER.info(card);
                Platform.runLater(()->{
                    Image goodLecturer = new Image("tbc/gui/img/goodlecturer.png");
                    LobbyController.gameWindowController.imageView5.setImage(goodLecturer);
                });
                break;
            case ("RedBull"):
                LOGGER.info(card);
                Platform.runLater(()->{
                    Image redbull = new Image("tbc/gui/img/redbull.png");
                    LobbyController.gameWindowController.imageView6.setImage(redbull);
                });
                break;
            case ("WLAN"):
                LOGGER.info(card);
                Platform.runLater(()->{
                    Image wlan = new Image("tbc/gui/img/wlan.png");
                    LobbyController.gameWindowController.imageView7.setImage(wlan);
                });
                break;
            default:
                LOGGER.error(card + " does not exist.");
        }
    }

}
