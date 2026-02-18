package online.awet.threads;

import online.awet.system.Connector;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.Scanner;

public class UserInterfaceThread implements Runnable {

    private static UserInterfaceThread instance;

    private Scanner cliScanner;
    private Socket serverSocket;
    private BufferedWriter writer;

    private UserInterfaceThread() {
        try {
            cliScanner = new Scanner(System.in);
            serverSocket = Connector.getInstance().getServerSocket();
            writer = new BufferedWriter(new OutputStreamWriter(serverSocket.getOutputStream()));
        } catch (IOException e) {
            System.out.println("There was an error initializing the UserInterfaceThread instance.");
        } catch (Exception e) {
            System.out.println();
        }
    }

    public static UserInterfaceThread getInstance() {
        if (instance == null) {
            instance = new UserInterfaceThread();
        }
        return instance;
    }

    @Override
    public void run() {
        while (true) {
            try {
                if (cliScanner.hasNextLine()) {
                    String userInput = cliScanner.nextLine();

                    if (userInput == null || userInput.isBlank()) {
                        continue;
                    }

                    writer.write(userInput);
                    writer.newLine();
                    writer.flush();

                    System.out.print("\033[F");
                    System.out.print("\033[2K");
                }
            } catch (IOException e) {
                System.out.println("Error sending message to the server, Cause: " + e.getMessage());
                break;
            }
        }
    }
}
