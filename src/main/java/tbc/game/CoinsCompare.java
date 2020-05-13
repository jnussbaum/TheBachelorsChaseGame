package tbc.game;

import java.util.Comparator;

/**
 * This Class compares the coins of two Players in order to fill the HighScore.
 */
public class CoinsCompare implements Comparator<Player> {

    /**
     *
     * @param p1 is the first Player.
     * @param p2 is the second Player.
     * @return
     */
    @Override
    public int compare(Player p1, Player p2) {
        return p2.numOfCoins - p1.numOfCoins;
    }
}
