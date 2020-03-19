package tbc.server;

import tbc.chat.ChatServer;

import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.*;
import java.net.Socket;
import java.net.ServerSocket;
import java.util.HashMap;

public class Server {

	/**
	 * Here, all clients are administrated.
	 */
	static HashMap<String, ServerHandler> clients = new HashMap<>();

	public void changeName(String requestor, String userName) {
		if (clients.containsKey(requestor)) {
			clients.get(requestor).changeFeedback(false, requestor);
		} else {
			//find the ServerHandler and tell him: yes
			changeFeedback(true, requestor);
		}
	}

	public static void main (String[] args) {
		//int portNumber = Integer.parseInt(args[0]);
		int portNumber = 8095;
		ServerSocket serverSocket;
		Socket client1socket;
		try {
			serverSocket = new ServerSocket(portNumber);
			client1socket = serverSocket.accept();
		} catch (IOException e) {
			e.printStackTrace();
		}

		ServerHandler client1 = new ServerHandler("client1", client1socket);
		clients.put("client1", client1);
		ChatServer chatserver = new ChatServer();
	}
}