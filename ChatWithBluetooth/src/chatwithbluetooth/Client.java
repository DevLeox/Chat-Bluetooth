package chatwithbluetooth;

import javax.bluetooth.*;
import javax.microedition.io.*;
import java.io.*;
import java.util.*;

public class Client {
    //private static final UUID MY_UUID = UUID.randomUUID(); // استفاده از UUID تصادفی
    private static final String SERVER_URL = "btspp://001122334455:1"; // آدرس بلوتوث سرور را جایگزین کنید

    public static void main(String[] args) {
        try {
            LocalDevice localDevice = LocalDevice.getLocalDevice();
            DiscoveryAgent agent = localDevice.getDiscoveryAgent();

            System.out.println("Connecting to server...");
            StreamConnection connection = (StreamConnection) Connector.open(SERVER_URL);

            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.openInputStream()));
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(connection.openOutputStream()));

            Scanner scanner = new Scanner(System.in);
            String message;
            while (true) {
                System.out.print("Enter message: ");
                message = scanner.nextLine();
                writer.write(message + "\n");
                writer.flush();

                String response = reader.readLine();
                System.out.println("Received: " + response);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}