package chatwithbluetooth;

import javax.bluetooth.*;
import javax.microedition.io.*;
import java.io.*;
import java.util.UUID;

public class Server {
    private static final UUID MY_UUID = UUID.randomUUID();
    private static final String CONNECTION_URL = "btspp://localhost:" + MY_UUID + ";name=BluetoothChat";

    public static void main(String[] args) {
        try {
            LocalDevice localDevice = LocalDevice.getLocalDevice();
            localDevice.setDiscoverable(DiscoveryAgent.GIAC);

            StreamConnectionNotifier notifier = (StreamConnectionNotifier) Connector.open(CONNECTION_URL);
            System.out.println("Waiting for connection...");

            StreamConnection connection = notifier.acceptAndOpen();
            RemoteDevice remoteDevice = RemoteDevice.getRemoteDevice(connection);
            System.out.println("Connected to " + remoteDevice.getFriendlyName(true));

            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.openInputStream()));
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(connection.openOutputStream()));

            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println("Received: " + line);
                writer.write("Echo: " + line + "\n");
                writer.flush();
            }

            reader.close();
            writer.close();
            connection.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
