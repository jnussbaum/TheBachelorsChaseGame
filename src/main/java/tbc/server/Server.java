package tbc.server;

import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.*;
import java.net.Socket;
import java.net.ServerSocket;

public class Server{
	public static void main (String[]args){
	
	try{
ServerSocket server = new ServerSocket(8095); //dem konstruktor vom Serversocket einen Port zugewiesen
System.out.println("Server wurde gestartet...");

Socket client = server.accept(); //wartet auf einen Client zum verbinden

	
/** Die Daten die von den Server an den Client gesendet werden müssen in einem *outputstream objekt gesepichert werden

OutputStream out = client.getOutputStream();

PrintWriter writer = new PrintWriter(out); //Um normale Strings aufzufangen sollte das reichen

//Hier werden die Daten die vom Client gesendet werden bearbeitet und gespeichert
InputStream in = client.getInputStream(); 
BufferedReader reader = new BufferedReader(new InputStreamReader(in));

//Daten die vom Client gesendet werden, müssen auf dem Server wieder ausgegeben werden können.

String s = null;
//schleife liest jeden string der rein kommt solange es kein nullzeiger ist

while((s = reader.readLine()) != null){
writer.write(s + "\n");
writer.flush();
System.out.println("Client:"+ s);
}
//geöffnete Streams wieder schliessen
reader.close();
writer.close();
**/
		} catch (IOException e){
	e.printStackTrace();
	
		}
	}
}