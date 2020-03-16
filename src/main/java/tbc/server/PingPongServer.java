package tbc.server;

import java.util.LinkedList;

public class PingPongServer implements Runnable {

    class ClientObject {
        String nameOfClient;
        boolean answered = false;
        int numOfFails = 0;
    }

    LinkedList<ClientObject> clientObjects = new LinkedList();
    private int numOfFailsTolerated = 2;
    private long pingsPerSecond = 1;

    /**
     * At the beginning of its existence, the Server has to create an instance of PingPongServer with this constructor.
     * @param clients: Please pass a LinkedList with all clients to the constructor.
     */
    PingPongServer(LinkedList<String> clients) {
        for (String s : clients) {
            ClientObject co = new ClientObject();
            co.nameOfClient = s;
            clientObjects.add(co);
        }
    }

    @Override
    public void run() {
        while (true) {
            //tell handler to send ping to all clients
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                //do nothing
            }
            for (ClientObject co : clientObjects) {
                if (co.answered == false) {
                    co.numOfFails++;
                    if (co.numOfFails > numOfFailsTolerated) {
                        //tell server that this client is disconnected.
                        //Server then has to kick this client out of the game.
                    }
                }
            }
        }
    }

    /**
     * The handler tells the PingPongServer that a pong arrived by the client named as parameter.
     */
    public void pongArrived(String nameOfClient) {
        for (ClientObject co : clientObjects) {
            if (co.nameOfClient.equals(nameOfClient)) {
                co.answered = true;
                break;
            }
        }
    }

    /**
     * Every time a new client connects to the server, the server has to pass the client's name to this thread.
     */
    public void addClient(String nameOfClient) {
        ClientObject co = new ClientObject();
        co.nameOfClient = nameOfClient;
        clientObjects.add(co);
    }
}