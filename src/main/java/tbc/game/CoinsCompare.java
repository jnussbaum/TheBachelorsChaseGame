package tbc.game;

import java.util.Comparator;

/**
 * This class compares the coins of two players in order to fill the highscore.
 */
public class CoinsCompare implements Comparator<Player> {

    /**
     * Overrides the compare method from Comparator. Compares the given coins of two players.
     * @param p1 The first player
     * @param p2 The second player
     * @return The difference between the player two and player one.
     */
    @Override
    public int compare(Player p1, Player p2) {
        return p2.numOfCoins - p1.numOfCoins;
    }
}
