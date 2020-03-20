package tbc.chat;

import tbc.client.ClientHandler;

import java.io.DataInputStream;
import java.io.PrintStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class ChatClient implements Runnable {

    private ClientHandler clientHandler;
    private BufferedReader input;

    public ChatClient(ClientHandler clientHandler) {
        this.clientHandler = clientHandler;
    }

    public void run() {
        input = new BufferedReader(new InputStreamReader(System.in, StandardCharsets.UTF_8));
        String s;
        try {
            while ((s = input.readLine()) != null) {
                processInput(s);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void processInput(String s) {
        if (s.startsWith("@")) {
            String name = s.substring(1, s.indexOf(" "));
            String msg = s.substring(s.indexOf(" ") + 1, s.length() - 1);
            clientHandler.sendMessage(name, msg);
        } else {
            clientHandler.sendMessage("ALL", s);
        }
    }
}