package tbc.GUI;

import javafx.scene.image.Image;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import tbc.client.Client;
import tbc.client.ClientGame;
import tbc.client.ClientHandler;
import tbc.game.Card;
import tbc.client.ClientGame;

import java.util.ArrayList;

public class CardDeck<cards> {

  cards = new ArrayList<Card>
  ArrayList<String> cardsAsStrings = new ArrayList<>();
    for (Card c : cards) {
    cardsAsStrings.add(c.toString());
    Client.game.getCards();

  //  ArrayList<String> cardsAsStrings = new ArrayList<>();


    public void showCard(cards) {
        switch(card) {
          case (cardsAsStrings.contains("Party")):
            Image party = new Image("tbc/GUI/img/party.png");
            LobbyController.gameWindowController.imageView1.setImage(party);
            break;

          case (cardsAsStrings.contains("Plagiarism")):
            Image plagiarism = new Image("tbc/GUI/img/plagiarism.png");
            LobbyController.gameWindowController.imageView2.setImage(plagiarism);
            break;

          case (cardsAsStrings.contains("Coffee")):
            Image coffee = new Image("tbc/GUI/img/coffee.png");
            LobbyController.gameWindowController.imageView3.setImage(coffee);
            break;

          case (cardsAsStrings.contains("Study")):
            Image study = new Image("tbc/GUI/img/study.png");
            LobbyController.gameWindowController.imageView4.setImage(study);
            break;

          case (cardsAsStrings.contains("GoodLecturer")):
            Image goodLecturer = new Image("tbc/GUI/img/GoodLecturer.png");
            LobbyController.gameWindowController.imageView5.setImage(goodLecturer);
            break;

          case (cardsAsStrings.contains("Redbull")):
            Image redbull = new Image("tbc/GUI/img/redbull.png");
            LobbyController.gameWindowController.imageView6.setImage(redbull);
            break;

          case (cardsAsStrings.contains("WLan")):
            Image wlan = new Image("tbc/GUI/img/wlan.png");
            LobbyController.gameWindowController.imageView7.setImage(wlan);
            break;
          default:
            System.out.println(cards + " does not exist.");
          }
       }
    }
}
