package tbc.game;

public class ServerMatch implements Runnable {

    private ServerGame serverGame;

    public ServerMatch(ServerGame serverGame) {
        this.serverGame = serverGame;
    }

    public void run() {
        serverGame.giveTurnToNext();

    }
}
