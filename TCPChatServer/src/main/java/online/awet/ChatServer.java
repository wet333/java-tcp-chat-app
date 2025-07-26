package online.awet;

import online.awet.system.Configurations;
import online.awet.threads.ClientHandlerThread;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ChatServer {

    public static void main(String[] args) {
        ExecutorService threadPool = Executors.newFixedThreadPool(Configurations.THREAD_POOL_COUNT);

        int portNumber = Configurations.PORT;

        try (ServerSocket serverSocket = new ServerSocket(portNumber)) {
            System.out.println("Chat server started on port: " + portNumber);

            while (true) {
                Socket clientConnection = serverSocket.accept();

                ClientHandlerThread clientHandlerThread = new ClientHandlerThread(clientConnection);

                threadPool.execute(clientHandlerThread);
            }
        } catch (IOException e) {
            System.out.println("Could not create server on port: " + portNumber);
            e.printStackTrace();
            System.out.println("Terminating server...");
        } finally {
            threadPool.shutdown();
        }
    }
}
