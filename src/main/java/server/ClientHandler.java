package server;

import javax.swing.*;
import java.io.*;
import java.net.Socket;

public class ClientHandler implements Runnable {

    private final ChatServer chatServer;
    private final Socket connectionToClient;
    private final String name;

    private BufferedReader fromClientReader;
    private PrintWriter toClientWriter;


    public ClientHandler(ChatServer chatServer, Socket connectionToClient) {
        this.chatServer = chatServer;
        this.connectionToClient = connectionToClient;

        this.name = connectionToClient.getInetAddress().getHostAddress();

        new Thread(this).start();

    }

    public void sendMessage(String message) {
        toClientWriter.println(message);
        toClientWriter.flush();
    }

    @Override
    public void run() {
        try {
            this.fromClientReader = new BufferedReader(new InputStreamReader(connectionToClient.getInputStream()));
            this.toClientWriter = new PrintWriter(new OutputStreamWriter(connectionToClient.getOutputStream()));

            chatServer.broadcastMessage(name + " connected.");

            while (true) {
                String message = fromClientReader.readLine();
                if (message != null) {
                    chatServer.broadcastMessage(name + ": " + message);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            if (fromClientReader != null) {
                try {
                    fromClientReader.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }

            if (toClientWriter != null) {
                toClientWriter.close();
            }

            chatServer.removeClient(this);
            chatServer.broadcastMessage(name + " disconnected.");
        }

    }
}
