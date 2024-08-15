package online.awet.threads;

import online.awet.system.Connector;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class TcpReceiverThread implements Runnable {

    public static TcpReceiverThread instance;

    private BufferedReader serverMessageStream;
    private final BlockingQueue<String> messageQueue;

    private TcpReceiverThread() {
        Socket serverSocket = Connector.getInstance().getServerSocket();
        messageQueue = new LinkedBlockingQueue<>();
        try {
            serverMessageStream = new BufferedReader(new InputStreamReader(serverSocket.getInputStream()));
        } catch (IOException e) {
            System.out.println("The TcpReceiverThread couldn't get the socket inputStream.");
        }
    }

    public static TcpReceiverThread getInstance() {
        if (instance == null) {
            instance = new TcpReceiverThread();
        }
        return instance;
    }

    @Override
    public void run() {
        String serverMessage;
        try {
            while ((serverMessage = serverMessageStream.readLine()) != null) {
                if (!serverMessage.isBlank()) {
                    messageQueue.put(serverMessage);
                    // TODO: Remove this print when CLI classes are implemented
                    System.out.println(serverMessage);
                }
            }
        } catch (IOException e) {
            System.out.println("Error while running or starting TCPReceiverThread.");
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
