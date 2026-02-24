package online.awet;

import online.awet.system.Configurations;
import online.awet.system.commands.CommandRouter;
import online.awet.system.userManagement.AccountManager;
import online.awet.system.userManagement.AccountManagerContract;
import online.awet.system.userManagement.FileStorageAccountManagerImpl;
import online.awet.threads.ClientHandlerThread;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Path;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ChatServer {

    private static final Logger logger = LoggerFactory.getLogger(ChatServer.class);

    public static void main(String[] args) {
        ExecutorService threadPool = Executors.newFixedThreadPool(Configurations.THREAD_POOL_COUNT);

        int portNumber = args.length >= 1 ? Integer.parseInt(args[0]) : Configurations.PORT;

        try (ServerSocket serverSocket = new ServerSocket(portNumber)) {
            logger.info("Chat server started on port: {}", portNumber);

            AccountManagerContract accountManagerImpl = new FileStorageAccountManagerImpl(Path.of("accounts.txt"));
            AccountManager.setDelegate(accountManagerImpl);

            CommandRouter commandRouter = new CommandRouter();

            while (true) {
                Socket clientConnection = serverSocket.accept();
                ClientHandlerThread clientHandlerThread = new ClientHandlerThread(clientConnection, commandRouter);
                threadPool.execute(clientHandlerThread);
            }

        } catch (IOException e) {
            logger.error("Could not create server on port: {}", portNumber, e);
            logger.info("Terminating server...");
        } finally {
            threadPool.shutdown();
        }
    }
}
