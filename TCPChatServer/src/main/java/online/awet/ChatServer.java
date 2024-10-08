package online.awet;

import online.awet.threads.ClientHandlerThread;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * ChatServer is the entry point for the CLI-based chat application server.
 * It listens on a specified port for incoming client connections and
 * assigns each connection to a dedicated handler thread for processing.
 * The server runs continuously, accepting new connections until it is terminated.
 *
 * <p>
 *     The server uses a thread pool to manage client handler threads,
 *     which improves performance by reusing threads and limiting the maximum
 *     number of concurrent connections.
 * </p>
 *
 * @author
 * @version 1.0
 */
public class ChatServer {

    /**
     * The port number that the server listens on for incoming connections.
     */
    private static final int PORT = 7560;

    /**
     * The maximum number of threads in the thread pool.
     * This limits the number of concurrent clients the server can handle.
     */
    private static final int THREAD_POOL_COUNT = 12;

    /**
     * Main method that initializes and starts the chat server.
     * It creates a server socket on the specified port and waits
     * for client connections.
     *
     * @param args Command-line arguments (not used yet)
     */
    public static void main(String[] args) {
        // Create a fixed thread pool with a predefined maximum number of threads.
        ExecutorService threadPool = Executors.newFixedThreadPool(THREAD_POOL_COUNT);

        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Chat server started on port: " + PORT);

            // Continuously accept client connections.
            while (true) {
                // Accepts an incoming client connection.
                Socket clientConnection = serverSocket.accept();

                // Creates a new thread for handling the client connection.
                ClientHandlerThread clientHandlerThread = new ClientHandlerThread(clientConnection);

                // Executes the client handler in the thread pool.
                threadPool.execute(clientHandlerThread);
            }
        } catch (IOException e) {
            // Handle exceptions related to I/O and server socket.
            System.out.println("Could not create server on port: " + PORT);
            e.printStackTrace();
            System.out.println("Terminating server...");
        } finally {
            // Properly shutdown the thread pool when server stops.
            threadPool.shutdown();
        }
    }
}
