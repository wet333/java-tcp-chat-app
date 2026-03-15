package online.awet;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.nio.file.Path;
import java.security.KeyStore;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLServerSocket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import online.awet.commons.CommandExecutorPool;
import online.awet.system.Configurations;
import online.awet.system.userManagement.AccountManager;
import online.awet.system.userManagement.AccountManagerContract;
import online.awet.system.userManagement.FileStorageAccountManagerImpl;
import online.awet.threads.ClientHandlerThread;

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

        try (SSLServerSocket serverSocket = createSSLServerSocket(portNumber)) {
            logger.info("Chat server started on port: {} (TLS)", portNumber);

            initializeServerWideComponents();

            while (true) {
                Socket clientConnection = serverSocket.accept();
                ClientHandlerThread clientHandlerThread = new ClientHandlerThread(clientConnection);
                threadPool.execute(clientHandlerThread);
            }

        } catch (IOException e) {
            logger.error("Could not create server on port: {}", portNumber, e);
            logger.info("Terminating server...");
        } catch (Exception e) {
            logger.error("TLS setup failed: {}", e.getMessage(), e);
            logger.info("Terminating server...");
        } finally {
            threadPool.shutdown();
        }
    }

    private static SSLServerSocket createSSLServerSocket(int port) throws Exception {
        // Detect environment from the resource injected at build time by Maven profiles
        Properties tlsProps = new Properties();
        try (InputStream in = ChatServer.class.getResourceAsStream("/server-tls.properties")) {
            tlsProps.load(in);
        }
        String env = tlsProps.getProperty("tls.env", "dev");

        // Password: env var TLS_KS_PASSWORD takes precedence; dev falls back to the default
        String ksPass = System.getenv("TLS_KS_PASSWORD");
        if (ksPass == null) {
            if ("prod".equals(env)) {
                throw new IllegalStateException(
                    "TLS_KS_PASSWORD environment variable is required when running the PROD server.");
            }
            ksPass = "password";
        }

        logger.info("TLS environment: {}", env);

        KeyStore ks = KeyStore.getInstance("PKCS12");
        try (FileInputStream fis = new FileInputStream("server.keystore.jks")) {
            ks.load(fis, ksPass.toCharArray());
        }

        KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
        kmf.init(ks, ksPass.toCharArray());

        SSLContext ctx = SSLContext.getInstance("TLS");
        ctx.init(kmf.getKeyManagers(), null, null);

        return (SSLServerSocket) ctx.getServerSocketFactory().createServerSocket(port);
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
