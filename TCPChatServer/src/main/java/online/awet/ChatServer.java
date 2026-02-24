package online.awet;

import online.awet.system.Configurations;
import online.awet.system.userManagement.AccountManager;
import online.awet.system.userManagement.AccountManagerContract;
import online.awet.system.userManagement.FileStorageAccountManagerImpl;
import online.awet.threads.ClientHandlerThread;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Path;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ChatServer {

    public static void main(String[] args) {
        ExecutorService threadPool = Executors.newFixedThreadPool(Configurations.THREAD_POOL_COUNT);

        int portNumber = args.length >= 1 ? Integer.parseInt(args[0]) : Configurations.PORT;

        try (ServerSocket serverSocket = new ServerSocket(portNumber)) {
            System.out.println("Chat server started on port: " + portNumber);

            // Initialize the account manager
            AccountManagerContract accountManagerImpl = new FileStorageAccountManagerImpl(Path.of("accounts.txt"));
            AccountManager.setDelegate(accountManagerImpl);

            // Start the server loop
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
