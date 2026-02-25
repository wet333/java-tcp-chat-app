package online.awet.threads;

import online.awet.system.core.broadcast.BroadcastManager;
import online.awet.system.core.broadcast.ClientConnection;
import online.awet.system.commands.CommandRouter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.Socket;

public class ClientHandlerThread implements Runnable {

    private static final Logger logger = LoggerFactory.getLogger(ClientHandlerThread.class);

    private final Socket socket;
    private final CommandRouter commandRouter;

    public ClientHandlerThread(Socket socket, CommandRouter commandRouter) {
        this.socket = socket;
        this.commandRouter = commandRouter;
    }

    public void run() {
        BroadcastManager broadcastManager = BroadcastManager.getInstance();

        try {
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            ClientConnection connection = new ClientConnection(writer);
            broadcastManager.addConnection(connection);

            logger.info("New client connected {}, connectionId: {}", socket.getRemoteSocketAddress(), connection.getId());
            connection.send("You have successfully connected to the server.");

            String clientMessage;
            while ((clientMessage = reader.readLine()) != null) {
                logger.debug("RAW: {}", clientMessage);
                commandRouter.route(connection, clientMessage);
            }

            broadcastManager.removeConnection(connection);

            String disconnectedMsg = "User <<" + connection.getSession().getDisplayName() + ">> has disconnected.";
            broadcastManager.serverBroadcast(disconnectedMsg);
            logger.info(disconnectedMsg);

            socket.close();

        } catch (IOException e) {
            logger.error("Error while handling connection IO", e);
        }
    }
}
