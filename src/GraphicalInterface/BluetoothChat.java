package GraphicalInterface;

import javax.bluetooth.*;
import javax.microedition.io.*;
import java.io.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class BluetoothChat {
    private static final String SERVER_UUID = "1101"; // UUID for SPP
    private JTextArea textArea;
    private JTextField textField;
    private PrintWriter writer;

    public static void main(String[] args) {
        new BluetoothChat().createAndShowGUI();
    }

    private void createAndShowGUI() {
        JFrame frame = new JFrame("Bluetooth Chat");
        textArea = new JTextArea();
        textField = new JTextField();
        JButton startServerButton = new JButton("Start Server");
        JButton connectClientButton = new JButton("Connect as Client");

        frame.setLayout(new BorderLayout());
        frame.add(new JScrollPane(textArea), BorderLayout.CENTER);
        frame.add(textField, BorderLayout.SOUTH);
        frame.add(startServerButton, BorderLayout.WEST);
        frame.add(connectClientButton, BorderLayout.EAST);
        frame.setSize(500, 400);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);

        startServerButton.addActionListener(e -> startServer());
        connectClientButton.addActionListener(e -> connectClient());

        textField.addActionListener(e -> {
            String message = textField.getText();
            if (writer != null) {
                writer.println(message);
                textArea.append("Me: " + message + "\n");
                textField.setText("");
            }
        });
    }

    private void startServer() {
        new Thread(() -> {
            try {
                LocalDevice localDevice = LocalDevice.getLocalDevice();
                localDevice.setDiscoverable(DiscoveryAgent.GIAC);

                StreamConnectionNotifier notifier = (StreamConnectionNotifier) Connector.open("btspp://localhost:" + SERVER_UUID + ";name=BluetoothServer");

                textArea.append("Server started. Waiting for clients...\n");

                StreamConnection connection = notifier.acceptAndOpen();
                textArea.append("Client connected.\n");

                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.openInputStream()));
                writer = new PrintWriter(new OutputStreamWriter(connection.openOutputStream()), true);

                String line;
                while ((line = reader.readLine()) != null) {
                    textArea.append("Client: " + line + "\n");
                }

                connection.close();
                notifier.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void connectClient() {
        new Thread(() -> {
            try {
                LocalDevice localDevice = LocalDevice.getLocalDevice();
                DiscoveryAgent agent = localDevice.getDiscoveryAgent();

                String connectionURL = "btspp://<server_address>:" + SERVER_UUID; // Replace <server_address> with the server's Bluetooth address
                StreamConnection connection = (StreamConnection) Connector.open(connectionURL);

                textArea.append("Connected to server.\n");

                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.openInputStream()));
                writer = new PrintWriter(new OutputStreamWriter(connection.openOutputStream()), true);

                String line;
                while ((line = reader.readLine()) != null) {
                    textArea.append("Server: " + line + "\n");
                }

                connection.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }
}
