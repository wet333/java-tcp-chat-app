package online.awet.threads;

import online.awet.commons.Command;
import online.awet.commons.CommandSerializer;
import online.awet.system.Connector;
import online.awet.system.ConnectorException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class TcpReceiverThread implements Runnable {

    private static TcpReceiverThread instance;

    private final BlockingQueue<Command> commandQueue;

    private TcpReceiverThread() {
        commandQueue = new LinkedBlockingQueue<>();
    }

    public static TcpReceiverThread getInstance() {
        if (instance == null) {
            instance = new TcpReceiverThread();
        }
        return instance;
    }

    @Override
    public void run() {
        try {
            Socket serverSocket = Connector.getInstance().getServerSocket();
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(serverSocket.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    if (line.isBlank()) continue;
                    Command command = null;
                    try {
                        command = CommandSerializer.fromJson(line);
                    } catch (Exception e) {
                        System.out.println("Failed to deserialize server message: " + e.getMessage());
                    }
                    if (command != null) {
                        commandQueue.put(command);
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("Error while running or starting TcpReceiverThread: " + e.getMessage());
        } catch (ConnectorException e) {
            System.out.println(e.getMessage());
        } catch (InterruptedException e) {
            System.out.println("TcpReceiverThread was interrupted.");
        }
    }

    public BlockingQueue<Command> getCommandQueue() {
        return commandQueue;
    }

    public Command getNextCommand() throws InterruptedException {
        return commandQueue.take();
    }
}
