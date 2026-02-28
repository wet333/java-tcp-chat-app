package online.awet.threads;

import online.awet.commons.Command;
import online.awet.commons.CommandExecutorPool;
import online.awet.commons.CommandSerializer;
import online.awet.commons.CommandTarget;
import online.awet.commons.CommandType;
import online.awet.system.core.ClientConnection;
import online.awet.system.core.ConnectionRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.Socket;
import java.util.Map;

public class ClientHandlerThread implements Runnable {

    private static final Logger logger = LoggerFactory.getLogger(ClientHandlerThread.class);

    private final Socket socket;
    private ClientConnection connection;

    public ClientHandlerThread(Socket socket) {
        this.socket = socket;
    }

    public void run() {
        ConnectionRegistry registry = ConnectionRegistry.getInstance();

        try {
            // Connection process: constructor creates streams and auto-registers.
            connection = new ClientConnection(socket);

            logger.info("New client connected {}, connectionId: {}", socket.getRemoteSocketAddress(), connection.getId());

            // Welcome message and connection announcement.
            sendWelcomeMessage(connection);
            announceConnection(connection);

            // Command processing loop.
            String jsonLine;
            while ((jsonLine = connection.getReader().readLine()) != null) {
                logger.debug("RAW JSON: {}", jsonLine);
                ClientConnection.CURRENT_CONNECTION.set(connection);
                try {
                    CommandExecutorPool.getInstance().select(CommandSerializer.fromJson(jsonLine));
                } catch (IllegalArgumentException e) {
                    Command err = Command.of(
                        CommandType.PRINT_MSG,
                        CommandTarget.CLIENT,
                        Map.of("msg", "Unknown command.")
                    );
                    registry.sendToConnection(connection.getId(), err);
                } catch (Exception e) {
                    logger.warn("Failed to process command from client {}: {}", connection.getId(), e.getMessage());
                } finally {
                    ClientConnection.CURRENT_CONNECTION.remove();
                }
            }

            // Disconnection process.
            registry.unregister(connection.getId());
            announceDisconnection(connection);
            connection.close();

        } catch (IOException e) {
            logger.error("Error while handling connection IO", e);
        }
    }

    private void sendWelcomeMessage(ClientConnection connection) {
        Command welcome = Command.of(
            CommandType.PRINT_MSG,
            CommandTarget.CLIENT,
            Map.of("msg", "You have successfully connected to the server.")
        );
        ConnectionRegistry.getInstance().sendToConnection(connection.getId(), welcome);
    }

    private void announceConnection(ClientConnection connection) {
        String connectedMsg = "User <<" + connection.getSession().getDisplayName() + ">> has connected.";
        Command connectedNotice = Command.of(
            CommandType.PRINT_MSG,
            CommandTarget.CLIENT,
            Map.of("msg", connectedMsg)
        );
        ConnectionRegistry.getInstance().broadcastExcept(connection.getId(), connectedNotice);
        logger.info(connectedMsg);
    }

    private void announceDisconnection(ClientConnection connection) {
        String disconnectedMsg = "User <<" + connection.getSession().getDisplayName() + ">> has disconnected.";
        Command disconnectedNotice = Command.of(
            CommandType.PRINT_MSG,
            CommandTarget.CLIENT,
            Map.of("msg", disconnectedMsg)
        );
        ConnectionRegistry.getInstance().broadcastExcept(connection.getId(), disconnectedNotice);
    }
}
