package tbc.server;

import java.io.IOException;
import java.net.*;
import java.util.Calendar;

public class PingPongServer implements Runnable {

    private Socket socket;
    private boolean PingPongFailed;
    private long lastFail = 0;

    /**
     * @param socket: Pass the already existing socket to the PingPong-Thread, enabling the PingPong-
     *              Thread to communicate via this socket.
     * @param PingPongFailed: First create this boolean in your Server with the value "false",
     *                      and then pass it to the PingPong-Thread. The PingPong-Thread will swap it
     *                      to "true" as soon as ...
     *                      You should watch this boolean within the class which calls
     *                      this constructor.
     */
    public PingPong(Socket socket, boolean PingPongFailed) {
        this.socket = socket;
        this.PingPongFailed = PingPongFailed;
    }

    private static class ConnectionTestException extends IOException {
        long timeInMillis;
        ConnectionTestException(long timeInMillis) {
            this.timeInMillis = timeInMillis;
        }
    }

    @Override
    public void run() {
        while(socket.isConnected()) {
            try {
                socket.sendUrgentData(90);
                Thread.sleep((long) 100);
            } catch (IOException e) {
                long thisFail = Calendar.getInstance().getTimeInMillis();
                if (thisFail - lastFail >= 1000) { //everything is okay
                    lastFail = thisFail;
                } else {

                }
            } catch (InterruptedException e) { /*do nothing*/
            } catch (ConnectionTestException e) {

            }

        }
    }
}