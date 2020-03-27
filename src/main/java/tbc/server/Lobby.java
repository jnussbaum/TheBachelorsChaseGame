package tbc.server;

import tbc.game.Card;
import tbc.game.Game;

import java.util.HashMap;

public class Lobby {

    private String lobbyName;
    private static HashMap<String, ServerHandler> clients = new HashMap<>();
    private Game game;
    private boolean gameActive = false;

    public Lobby(String lobbyName, ServerHandler sh) {
        this.lobbyName = lobbyName;
        clients.put(sh.getName(), sh);
    }

    void join(String clientName, ServerHandler sh) {
        if (clients.containsKey(clientName) == false && clients.size() < 4) {
            clients.put(clientName, sh);
            sh.lobbyJoined(lobbyName);
        } else {
            System.err.println("This client cannot join the lobby twice");
        }
    }

    void startGame() {
        game = new Game(this, (String[]) clients.keySet().toArray());
        for (ServerHandler sh : clients.values()) {
            sh.gameStarted();
        }
    }

    public ServerHandler getServerHandler(String clientName) {
        return clients.get(clientName);
    }
}
