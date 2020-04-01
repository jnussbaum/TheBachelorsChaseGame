package tbc.game;

import java.util.Timer;
import java.util.TimerTask;

public class ServerMatch implements Runnable {

    private ServerGame serverGame;

    public ServerMatch(ServerGame serverGame) {
        this.serverGame = serverGame;
    }

    public void run() {
        System.out.println("ServerMatch started run method");
        serverGame.distributeCards();
        while (serverGame.matchEnd == false) {
            serverGame.giveTurnToNext();
            Timer timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    serverGame.giveTurnToNext();
                }
            }, 11000);
        }

    }
}
