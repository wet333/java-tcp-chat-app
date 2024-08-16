package online.awet.threads;

import online.awet.actions.collection.userManagement.RegisterAction;
import online.awet.actions.lib.AbstractAction;
import online.awet.actions.lib.ActionFactory;
import online.awet.actions.lib.exceptions.ActionException;
import online.awet.system.Connector;

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
            ActionFactory actionFactory = ActionFactory.getInstance();

            // Loop listening for user input
            while (true) {
                if (cliScanner.hasNextLine()) {
                    String userInput = cliScanner.nextLine();

                    if ("/exit".equals(userInput)) {
                        writer.flush();
                        cliScanner.close();
                        serverSocket.close();
                        break;
                    }

                    // TODO: make a class to handle input parsing, so i can easily handle multiple future message kinds
                    AbstractAction registerAction = actionFactory.getAction(RegisterAction.class);
                    if (registerAction.isTriggeredByClientMessage(userInput)) {
                        try {
                            String registerMessage = registerAction.translateToServerMessage(userInput);
                            writer.write(registerMessage);
                            writer.newLine();
                            writer.flush();
                            continue;
                        } catch (ActionException e) {
                            System.out.println(e.getMessage());
                        }
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
            System.out.println("Error sending message to the server");
            System.out.println("Cause: " + e.getMessage());
        }
    }
}
