package online.awet.threads;

import online.awet.system.broadcast.BroadcastManager;
import online.awet.system.commands.CommandRouter;
import online.awet.system.sessions.Session;
import online.awet.system.sessions.holder.InMemorySessionHolder;
import online.awet.system.sessions.holder.SessionHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.Socket;

public class ClientHandlerThread implements Runnable {

    private static final Logger logger = LoggerFactory.getLogger(ClientHandlerThread.class);

    private final Socket socket;
    private final CommandRouter commandRouter;
    private final SessionHolder sessionHolder;

    public ClientHandlerThread(Socket socket, CommandRouter commandRouter) {
        this.socket = socket;
        this.commandRouter = commandRouter;
        this.sessionHolder = new InMemorySessionHolder();
    }

    public void run() {
        BroadcastManager broadcastManager = BroadcastManager.getInstance();
        Session session = sessionHolder.getCurrentSession();

        try {
            InputStream inputStream = socket.getInputStream();
            OutputStream outputStream = socket.getOutputStream();

            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            BufferedWriter socketWriter = new BufferedWriter(new OutputStreamWriter(outputStream));

            broadcastManager.addBroadcastMember(session, socketWriter);

            logger.info("New client connected {}, sessionId: {}", socket.getRemoteSocketAddress(), session.getSessionId());
            broadcastManager.serverDirectMessage("You have successfully connected to the server.", session);

            String clientMessage;
            while ((clientMessage = reader.readLine()) != null) {
                logger.debug("RAW: {}", clientMessage);
                commandRouter.route(sessionHolder, clientMessage);
            }

            String disconnectedMsg = "User <<" + session.getDisplayName() + ">> has disconnected.";
            broadcastManager.serverBroadcast(disconnectedMsg);
            logger.info(disconnectedMsg);

            broadcastManager.removeBroadcastMember(session);
            socket.close();

        } catch (IOException e) {
            logger.error("Error while handling connection IO", e);
        }
    }
}
