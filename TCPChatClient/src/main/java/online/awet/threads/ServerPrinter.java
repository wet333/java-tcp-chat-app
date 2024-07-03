package online.awet.threads;

import java.io.BufferedReader;
import java.io.IOException;

public class ServerPrinter implements Runnable {

    private final BufferedReader bufferedReader;

    public ServerPrinter(BufferedReader serverReader) {
        bufferedReader = serverReader;
    }

    @Override
    public void run() {
        String serverLine;
        try {
            while ((serverLine = bufferedReader.readLine()) != null) {
                if (!serverLine.isBlank()) System.out.println(serverLine);
            }
        } catch (IOException e) {
            System.out.println("Connection ended by server.");
        }
    }
}
