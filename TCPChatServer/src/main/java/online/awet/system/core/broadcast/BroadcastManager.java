package online.awet.system.core.broadcast;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

public class BroadcastManager {

    private static final Logger logger = LoggerFactory.getLogger(BroadcastManager.class);

    private static final BroadcastManager instance = new BroadcastManager();

    private final ConcurrentHashMap<String, ClientConnection> connections = new ConcurrentHashMap<>();

    private static final String USER_PREFIX = "> ";

    private BroadcastManager() {}

    public static BroadcastManager getInstance() {
        return instance;
    }

    public void addConnection(ClientConnection connection) {
        connections.put(connection.getId(), connection);
    }

    public void removeConnection(ClientConnection connection) {
        connections.remove(connection.getId());
    }

    public void broadcast(String message, ClientConnection sender) {
        for (ClientConnection conn : connections.values()) {
            try {
                String msg = conn.getId().equals(sender.getId()) ? USER_PREFIX + message : message;
                conn.send(msg);
            } catch (IOException e) {
                logger.error("Couldn't send message <<{}>> to client {}", message, conn.getId(), e);
            }
        }
    }

    public void serverBroadcast(String message) {
        for (ClientConnection conn : connections.values()) {
            try {
                conn.send(message);
            } catch (IOException e) {
                logger.error("Couldn't send server broadcast <<{}>> to client {}", message, conn.getId(), e);
            }
        }
    }

    public void serverBroadcastExcludingSender(String message, ClientConnection sender) {
        for (ClientConnection conn : connections.values()) {
            if (conn.getId().equals(sender.getId())) continue;
            try {
                conn.send(message);
            } catch (IOException e) {
                logger.error("Couldn't send server broadcast <<{}>> to client {}", message, conn.getId(), e);
            }
        }
    }

    public boolean sendToUser(String username, String message) {
        for (ClientConnection conn : connections.values()) {
            if (username.equals(conn.getSession().getUsername())) {
                try {
                    conn.send(message);
                    return true;
                } catch (IOException e) {
                    logger.error("Couldn't send message <<{}>> to user {}", message, username, e);
                    return false;
                }
            }
        }
        return false;
    }
}
