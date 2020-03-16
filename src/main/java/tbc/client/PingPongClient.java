package tbc.client;

public class PingPongClient implements Runnable {

    private boolean answered = false;
    private int numOfFails = 0;
    private int numOfFailsTolerated = 2;
    private long pingsPerSecond = 1;

    @Override
    public void run() {
        while (true) {
            answered = false;
            //tell handler to send ping to server
            try {
                Thread.sleep(1000 / pingsPerSecond);
            } catch (InterruptedException e) {
                //do nothing
            }
            if (answered == false) {
                numOfFails++;
                if (numOfFails > numOfFailsTolerated) {
                    //call method of client to tell him that he is disconnected.
                    //Client then has to show message to user that he is disconnected.
                }
            }
        }
    }

    public void pongArrived() {
        answered = true;
    }
}