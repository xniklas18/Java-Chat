package client;

import javax.swing.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.*;
import java.net.Socket;

public class ChatClient extends JFrame {

    private final String address;
    private final int PORT;
    private final String name;

    private Socket connectionToServer;
    private BufferedReader fromServerReader;
    private PrintWriter toServerWriter;

    private JTextArea outputTextArea;
    private JTextField inputTextField;
    private JScrollPane outputScrollPane;
    private JPanel panel;

    public ChatClient(int port) {
        super("Chat");
        this.PORT = port;

        address = JOptionPane.showInputDialog("IP-Adresse");
        name = JOptionPane.showInputDialog("Name");
        if (address != null) {
            receiveMessages();
        }
    }

    private void receiveMessages() {
        try {
            connectionToServer = new Socket(address, PORT);
            fromServerReader = new BufferedReader(new InputStreamReader(connectionToServer.getInputStream()));
            toServerWriter = new PrintWriter(new OutputStreamWriter(connectionToServer.getOutputStream()));

            initGui();

            while (true) {
                String message = fromServerReader.readLine();
                outputTextArea.append(message + "\n");
                outputScrollPane.getVerticalScrollBar().setValue(outputScrollPane.getVerticalScrollBar().getMaximum());
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Verbindung zum Server \"" + address + "\" fehlgeschlagen.");
            dispose();
        } finally {
            if (connectionToServer != null) {
                try {
                    connectionToServer.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }

            if (fromServerReader != null) {
                try {
                    fromServerReader.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }

            if (toServerWriter != null) {
                toServerWriter.close();
            }
        }
    }

    private void initGui() {
        outputTextArea.setBorder(BorderFactory.createTitledBorder("Chat"));
        inputTextField.setBorder(BorderFactory.createTitledBorder("Nachricht eingeben"));

        inputTextField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                super.keyTyped(e);
            }

            @Override
            public void keyPressed(KeyEvent e) {
                super.keyPressed(e);

                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    String message = inputTextField.getText();

                    if (message.isEmpty()) return;

                    toServerWriter.println(message);
                    toServerWriter.flush();
                    inputTextField.setText("");
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {
                super.keyReleased(e);
            }
        });

        setContentPane(panel);
        setVisible(true);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(400, 300);
        setLocationRelativeTo(null);

    }

    public static void main(String[] args) {
        new ChatClient(3141);
    }

    @Override
    public String getName() {
        return name;
    }
}
