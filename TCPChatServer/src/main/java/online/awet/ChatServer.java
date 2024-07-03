package online.awet;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ChatServer {
    public static void main(String[] args) {

        int port = 7560;
        int threadPoolCount = 12;

        ExecutorService threadPool = Executors.newFixedThreadPool(threadPoolCount);

        try (ServerSocket serverSocket = new ServerSocket(port)) {
            while (true) {
                Socket clientConnection = serverSocket.accept();
                threadPool.execute(new ConnectionHandler(clientConnection));
            }
        } catch (IOException e) {
            System.out.println("Could not create server on port: " + port);
            e.printStackTrace();
            System.out.println("Terminating server...");
        }

    }
}