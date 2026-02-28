package online.awet;

import online.awet.commons.CommandExecutorPool;
import online.awet.system.Configurations;
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

/**
 * <p>Server entry point.</p>
 * <p>Starts the server on the given PORT and listens for client connections. Port is configurable via command line argument, default is {@link Configurations#PORT}.</p>
 * 
 * <p>Server wide components are initialized here.</p>
 * <ul>
 *     <li>{@link AccountManager}</li>
 *     <li>{@link CommandExecutorPool}</li>
 * </ul>
 */
public class ChatServer {

    private static final Logger logger = LoggerFactory.getLogger(ChatServer.class);

    public static void main(String[] args) {
        ExecutorService threadPool = Executors.newFixedThreadPool(Configurations.THREAD_POOL_COUNT);

        // Get the PORT number from the command line arguments, if not provided, use the default PORT from the configurations.
        int portNumber = args.length >= 1 ? Integer.parseInt(args[0]) : Configurations.PORT;

        try (ServerSocket serverSocket = new ServerSocket(portNumber)) {
            logger.info("Chat server started on port: {}", portNumber);

            initializeServerWideComponents();

            while (true) {
                Socket clientConnection = serverSocket.accept();
                ClientHandlerThread clientHandlerThread = new ClientHandlerThread(clientConnection);
                threadPool.execute(clientHandlerThread);
            }

        } catch (IOException e) {
            logger.error("Could not create server on port: {}", portNumber, e);
            logger.info("Terminating server...");
        } finally {
            threadPool.shutdown();
        }
    }

    private static void initializeServerWideComponents() {

        // Initialize the AccountManager.
        AccountManagerContract accountManagerImpl = new FileStorageAccountManagerImpl(Path.of("accounts.txt"));
        AccountManager.setDelegate(accountManagerImpl);
        logger.info("AccountManager initialized, using {}", accountManagerImpl.getClass().getName());

        // Load all command executors from the executors package.
        CommandExecutorPool.getInstance().loadExecutors("online.awet.system.commands.executors");
        logger.info("CommandExecutorPool initialized, loaded executors from {}", CommandExecutorPool.getInstance().getAll().size());
    }
}
