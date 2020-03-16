package tbc.server;

import java.net.ServerSocket;
import java.net.Socket;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.*;

public class Handler {
    public Socket socket;
    public ServerSocket serv;
    public String s;
    public String r;
    public boolean isServer;
    private OutputStream out;
    private PrintWriter writer;
    private InputStream in;
    private BufferedReader reader;



    /**Starts the Handler for the Client**/

    public Handler(Socket socket) {
        this.socket = socket;
        setInOut();

    }

    /**Starts the Handler for the Server**/

    public Handler(ServerSocket serv) {
        this.serv = serv;
        try {
            socket = serv.accept(); //wartet auf einen Client zum verbinden
            setInOut();
            serverStard();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**Set`s the:
     * OutputStream
     * InputStream
     * BufferedReader
     * PrintWriter
     * **/

    private void setInOut(){

        try {
            out = socket.getOutputStream();
            writer = new PrintWriter(out);

            in = socket.getInputStream();
            reader = new BufferedReader(new InputStreamReader(in));

        }catch (IOException e){
            e.printStackTrace();
        }
    }

    private void serverStard(){
        System.out.println();
        try {
             r = null;
//schleife liest jeden string der rein kommt solange es kein nullzeiger ist

            while((r = reader.readLine()) != null){
                switch (r){
                    case "Ping_":
                        //TODO
                        break;
                    case "Pong_":
                        //TODO
                        break;
                    case  "CHAT_":
                        //TODO
                        break;
                    case "GAMST":
                        //TODO
                        break;
                    case "GIVCA":
                        //TODO;
                        break;
                    case "GETCA":
                        //TODO;
                        break;
                    case "THRCA":
                        //TODO;
                        break;
                    case "TURST":
                        //TODO;
                        break;
                    case "TUREN":
                        //TODO;
                        break;

                }
                writer.write(s + "\n");
                writer.flush();
                System.out.println("Client:"+ s);
            }
//geöffnete Streams wieder schliessen
            reader.close();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void send(){
        writer.write(s +"\n");
        writer.flush();
    }

    public void sPong_(){
        s="Pong_";
        send();
    }

    public void sPing_(){
        s="Ping_";
        send();
    }
    public void sCHAT_(String message){
        s="CHAT_ = "+message;
        send();
    }
    public void sGAMST(){
        s="GAMST";
        send();
    }

    public static void main(String arg[]){

    }

}
