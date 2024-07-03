package online.awet.threads;

import java.io.BufferedWriter;
import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;

public class ClientTerminal implements Runnable {

    private Socket socket;
    private BufferedWriter writer;
    private Scanner scanner;

    public ClientTerminal(Socket socket, BufferedWriter writer, Scanner scanner) {
        this.socket = socket;
        this.writer = writer;
        this.scanner = scanner;
    }

    @Override
    public void run() {
        try {
            while (true) {
                if (scanner.hasNextLine()) {
                    String message = scanner.nextLine();

                    if ("/exit".equals(message)) {
                        writer.flush();
                        scanner.close();
                        socket.close();
                    }

                    writer.write(message);
                    writer.newLine();
                    writer.flush();
                }
            }
        } catch (IOException e) {
            System.out.println("Error sending message to the server");
            System.out.println("Cause: " + e.getMessage());
        }
    }
}