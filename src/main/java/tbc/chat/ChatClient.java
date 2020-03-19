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

    ClientHandler clientHandler;
    BufferedReader input;

    public ChatClient(ClientHandler clientHandler) {
        this.clientHandler = clientHandler;
    }

    public void run() {
        input = new BufferedReader(new InputStreamReader(System.in, StandardCharsets.UTF_8));
        String s;
        try {
            while ((s = input.readLine()) != null) {
                clientHandler.sendMessage();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }