package online.awet;

import online.awet.server.BroadcastManager;

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
        BroadcastManager broadcastManager = BroadcastManager.getInstance();

        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            while (true) {
                // Each connection made will start a thread of type ConnectionHandler, and will add that thread
                // to the broadcast members, that way other users will be able to see their messages
                Socket clientConnection = serverSocket.accept();
                ConnectionHandler connectionHandler = new ConnectionHandler(clientConnection);
                broadcastManager.addBroadcastMember(connectionHandler);
                threadPool.execute(connectionHandler);
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