package tbc.server;

import java.io.BufferedReader;
import java.io.PrintWriter;
import java.net.UnknownHostException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.io.*;
import java.util.Scanner;

public class Client{
    public static void main(String[]args){

        Scanner eingabe = new Scanner(System.in);

        try{

//Die Portnummer vom Server übergeben damit eine Verbindung hergestellt werden kann
            Socket client = new Socket("localhost", 8090);
            System.out.println("Client wurde gestartet...");

//Die selben Streams wie beim Server nehmen
            OutputStream out = client.getOutputStream();
            PrintWriter writer = new PrintWriter(out);

            InputStream in = client.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));

//Ausgabe der Eingabezeile
            System.out.print("Eingabe:" );
            String anServer = eingabe.nextLine();

//mit der Methode ist es uns möglich etwas an den Server zu senden|flush zum aktualisieren
            writer.write(anServer +"\n");
            writer.flush();

//string ausgeben lassen

            String s = null;

            while((s = reader.readLine()) != null){
                System.out.println("Server:"+ s);
            }
//streams wieder schliessen
            reader.close();
            writer.close();


        } catch(UnknownHostException e){
            e.printStackTrace();

        }catch (IOException e){
            e.printStackTrace();
        }

    }
}
