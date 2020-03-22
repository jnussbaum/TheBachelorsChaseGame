package tbc.server;

import tbc.chat.ChatServer;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.ServerSocket;
import java.util.HashMap;

public class Server {

	/**
	 * This HashMap administrates all clients by their name and ServerHandler.
	 */
	private static HashMap<String, ServerHandler> clients = new HashMap<>();

	/**
	 * This is the Headquarter of the Chat application.
	 */
	private static ChatServer chatServer;

	/**
	 * Processes a client's request to change his name. If newUserName is occupied, it sends a negative
	 * answer back. If newUserName is available, it changes requester's name to newUserName and sends a
	 * positive answer back.
	 */
	public static void changeName(String requester, String newUserName) {
		if (clients.containsKey(newUserName)) {
			clients.get(requester).giveFeedbackToChange(false, "xy");
		} else {
			ServerHandler sh = clients.get(requester);
			clients.remove(requester);
			sh.setName(newUserName);
			clients.put(newUserName, sh);
			chatServer.setName(requester, newUserName);
			clients.get(newUserName).giveFeedbackToChange(true, newUserName);
		}
	}

	/**
	 * All that the Server makes during his lifetime is to listen to new incoming connections,
	 * add them to the clients administration, and register them at the chatServer.
	 */
	public static void main (String[] args) {
		int portNumber = 8096;
		ServerSocket serverSocket;
		chatServer = new ChatServer();
		try {
			serverSocket = new ServerSocket(portNumber);
			System.out.println("Type this address in the client after starting the client: "
					+ InetAddress.getLocalHost().getHostAddress());
			Socket socket;
			int i = 0;
			while (true) {
				socket = serverSocket.accept();
				String name = socket.getInetAddress().getHostName() + i;
				i++;
				ServerHandler serverHandler = new ServerHandler(name, socket, chatServer);
				Thread shThread = new Thread(serverHandler);
				shThread.start();
				clients.put(name, serverHandler);
				chatServer.register(name, serverHandler);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}