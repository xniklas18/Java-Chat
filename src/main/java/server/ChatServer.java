package server;

import javax.swing.*;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class ChatServer extends JFrame {

    private final List<ClientHandler> clients;
    private ServerSocket serverSocket;
    private JPanel panel;
    private JTextArea outputTextArea;
    private JScrollPane outputScrollPane;

    public ChatServer(int port) {
        initGUI();
        this.clients = new CopyOnWriteArrayList<>();

        try {
            this.serverSocket = new ServerSocket(port);
            logToConsole("Started chat server on port " + port);

            while (true) {
                logToConsole("Waiting for new client...");
                Socket connectionToClient = serverSocket.accept();
                ClientHandler client = new ClientHandler(this, connectionToClient);
                clients.add(client);
                logToConsole("Accepted new client");
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                if (serverSocket != null) {
                    serverSocket.close();
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

    }

    private void initGUI() {
        setContentPane(panel);
        setTitle("Console Output");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(400, 300);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    public void broadcastMessage(String message) {
        if (message == null) return;

        logToConsole(message);

        for (ClientHandler client : clients) {
            client.sendMessage(message);
        }
    }

    private void logToConsole(String message) {
        outputTextArea.append(message + "\n");
        System.out.println(message);
        outputScrollPane.getVerticalScrollBar().setValue(outputScrollPane.getVerticalScrollBar().getMaximum());
    }

    public void removeClient(ClientHandler client) {
        clients.remove(client);
    }

    public static void main(String[] args) {
        new ChatServer(3141);
    }

}