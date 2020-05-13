package tbc.game;

import java.util.Comparator;

public class CoinsCompare implements Comparator<Player> {

    @Override
    public int compare(Player p1, Player p2) {
        return p2.numOfCoins - p1.numOfCoins;
    }
}
