package tbc.server;

public class PingPongServer implements Runnable {

    /**
     * Object fields:
     * sendPing: Received by server.
     * numOfClients: Received by server.
     * clientsConnectivity: Each position i represents client i. Each pong received by that client adds 1 to the
     *      respective position. The sum on position i represents the number of successful ping/pongs.
     * pingFrequency: How many times per second that a ping should be sent.
     */
    boolean sendPing;
    int numOfClients;
    private int[] clientsConnectivity = new int[100];
    int pingFrequency = 10;

    /**
     * @param sendPing: Object field of the server, initially false. Please pass the field itself to PingPongServer,
     *                not only its value.As soon as PingPongServer swaps it to true,
     *                the server sends a ping to all clients via network protocol. Then, the server resets sendPing
     *                to false. Every access to sendPing has to be synchronized!
     * @param numOfClients: Object field of the server. Please pass the field itself to PingPongServer, not only its
     *                    value.
     */
    public PingPongServer(boolean sendPing, int numOfClients) {
        this.sendPing = sendPing;
        this.numOfClients = numOfClients;
    }

    @Override
    public void run() {
        //pingFrequency times per second, sendPing is set to true in order to make the server send a ping to all clients.
        for (int i = 0; i < pingFrequency; i++) {
            sendPing = true;    //Wie mache ich das synchronized?
            try {
                Thread.sleep((long) (1000 / pingFrequency));
            } catch (InterruptedException e) { /*do nothing*/
            }
        }

        //after 1 second, PingPongServer checks the sums of clientsConnectivity. If one client's sum is less than 9
        // (i.e., 2 or more ping/pongs failed), the server will be notified with connectionBroken(int numOfClient)
        for (int i = 0; i < numOfClients; i++) {
            if (clientsConnectivity[i] < 9) {
                Server.connectionBroken(i);     //Wie adressiere ich den Server?
            }
        }
    }

    /**
     * Each pong received by client numOfClient adds 1 to the sum of successful ping/pongs
     * at clientsConnectivity[numOfClient].
     */
    void pongArrived(int numOfClient) {
        clientsConnectivity[numOfClient]++;
    }
}