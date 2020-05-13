package tbc.game;

import java.util.Comparator;

/**
 * This Class compares the coins of two Players in order to fill the HighScore.
 */
public class CoinsCompare implements Comparator<Player> {

    /**
     * Overrides the compare method from Comparator. Compares the given coins of two Players.
     *
     * @param p1 is the first Player.
     * @param p2 is the second Player.
     * @return The difference between the coins of player 2 and player 1.
     */
    @Override
    public int compare(Player p1, Player p2) {
        return p2.numOfCoins - p1.numOfCoins;
    }
}
