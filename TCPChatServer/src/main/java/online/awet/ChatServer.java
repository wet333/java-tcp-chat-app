package online.awet;

import online.awet.threads.ClientHandlerThread;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ChatServer {

    private static final int PORT = 7560;
    private static final int THREAD_POLL_COUNT = 12;

    public static void main(String[] args) {

        ExecutorService threadPool = Executors.newFixedThreadPool(THREAD_POLL_COUNT);

        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            while (true) {
                Socket clientConnection = serverSocket.accept();
                ClientHandlerThread clientHandlerThread = new ClientHandlerThread(clientConnection);
                threadPool.execute(clientHandlerThread);
            }
        } catch (IOException e) {
            System.out.println("Could not create server on port: " + PORT);
            e.printStackTrace();
            System.out.println("Terminating server...");
        } finally {
            threadPool.shutdown();
        }
    }
}