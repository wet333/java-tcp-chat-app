package online.awet.threads;

import online.awet.action.ActionException;
import online.awet.actions.userManagement.RegisterAction;

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
                        break;
                    }

                    // TODO: make a class to handle input parsing, so i can easily handle multiple future message kinds
                    if (RegisterAction.getInstance().isTriggeredByClientMessage(message)) {
                        try {
                            String registerMessage = RegisterAction.getInstance().parseUserMessage(message);
                            writer.write(registerMessage);
                            writer.newLine();
                            writer.flush();
                            continue;
                        } catch (ActionException e) {
                            System.out.println(e.getMessage());
                        }
                    }

                    writer.write(message);
                    writer.newLine();
                    writer.flush();

                    // Move the cursor up one line
                    System.out.print("\033[F");
                    // Clear the entire line
                    System.out.print("\033[2K");
                }
            }
        } catch (IOException e) {
            System.out.println("Error sending message to the server");
            System.out.println("Cause: " + e.getMessage());
        }
    }
}
