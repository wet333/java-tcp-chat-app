package online.awet.threads;

import online.awet.system.Connector;
import online.awet.system.Translator;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.Scanner;

public class UserInterfaceThread implements Runnable {

    public static UserInterfaceThread instance;

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
        try {
            Translator translator = Translator.getInstance();

            // Loop listening for user input
            while (true) {
                if (cliScanner.hasNextLine()) {
                    String userInput = cliScanner.nextLine();

                    // If the user enters a command, translate it to the PROTOCOL and send it
                    if (translator.isServerAction(userInput)) {
                        userInput = translator.translate(userInput);
                    }

                    writer.write(userInput);
                    writer.newLine();
                    writer.flush();

                    // Move the cursor up one line
                    System.out.print("\033[F");
                    // Clear the entire line
                    System.out.print("\033[2K");
                }
            }
        } catch (Exception e) {
            System.out.println("Error sending message to the server, Cause: " + e.getMessage());

            // Restart Loop
            this.run();
        }
    }
}
