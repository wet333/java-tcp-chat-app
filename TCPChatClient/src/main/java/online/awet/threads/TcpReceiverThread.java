package online.awet.threads;

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

    private final BlockingQueue<String> messageQueue;

    private TcpReceiverThread() {
        messageQueue = new LinkedBlockingQueue<>();
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
            try (BufferedReader serverMessageStream = new BufferedReader(new InputStreamReader(serverSocket.getInputStream()))) {
                String serverMessage;
                while ((serverMessage = serverMessageStream.readLine()) != null) {
                    if (!serverMessage.isBlank()) {
                        messageQueue.put(serverMessage);
                        // TODO: Remove this print when CLI classes are implemented
                        System.out.println(serverMessage);
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("Error while running or starting TCPReceiverThread: " + e.getMessage());
        } catch (ConnectorException e) {
            System.out.println(e.getMessage());
        } catch (InterruptedException e) {
            System.out.println("TcpReceiverThread was interrupted.");
        }
    }

    public boolean hasMessages() {
        return !messageQueue.isEmpty();
    }

    public String getNextMessage() throws InterruptedException {
        return messageQueue.take();
    }
}
