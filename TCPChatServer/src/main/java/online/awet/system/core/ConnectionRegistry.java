package online.awet.system.core;

import online.awet.commons.Command;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;

/**
 * <p>Global registry for all active client connections.</p>
 * <p>It stores all client connections in a {@link ConcurrentHashMap} and provides methods to register, unregister, get, and send commands to connections.</p>
 * <p>The thread-local tracking of the current connection is handled by {@link ClientConnection#CURRENT_CONNECTION}.</p>
 */
public class ConnectionRegistry {

    private static final Logger logger = LoggerFactory.getLogger(ConnectionRegistry.class);
    private static final ConnectionRegistry instance = new ConnectionRegistry();

    private final ConcurrentHashMap<String, ClientConnection> connections = new ConcurrentHashMap<>();

    private ConnectionRegistry() {}

    public static ConnectionRegistry getInstance() {
        return instance;
    }

    // -------------------------------------------------------------------------
    // Connection lifecycle
    // -------------------------------------------------------------------------

    public void register(ClientConnection connection) {
        connections.put(connection.getId(), connection);
    }

    public void unregister(String id) {
        connections.remove(id);
    }

    public ClientConnection getById(String id) {
        return connections.get(id);
    }

    public ClientConnection getByUsername(String username) {
        for (ClientConnection conn : connections.values()) {
            if (username.equals(conn.getSession().getUsername())) {
                return conn;
            }
        }
        return null;
    }

    public Collection<ClientConnection> getAll() {
        return connections.values();
    }

    // -------------------------------------------------------------------------
    // Routing helpers
    // -------------------------------------------------------------------------

    /** Sends {@code command} to the connection currently executing a command on this thread. */
    public void sendToCurrentConnection(Command command) {
        ClientConnection conn = ClientConnection.currentConnection();
        if (conn == null) return;
        send(conn, command);
    }

    /** Sends {@code command} to every connected client. */
    public void broadcast(Command command) {
        for (ClientConnection conn : connections.values()) {
            send(conn, command);
        }
    }

    /** Sends {@code command} to every connected client except the one with {@code excludeId}. */
    public void broadcastExcept(String excludeId, Command command) {
        for (ClientConnection conn : connections.values()) {
            if (!conn.getId().equals(excludeId)) {
                send(conn, command);
            }
        }
    }

    /**
     * Sends {@code command} to the authenticated client whose username matches.
     *
     * @return {@code true} if the user was found and the command was delivered
     */
    public boolean sendToUser(String username, Command command) {
        ClientConnection target = getByUsername(username);
        if (target == null) return false;
        return send(target, command);
    }

    /**
     * Sends {@code command} to the client with the given connection ID.
     *
     * @return {@code true} if the connection was found and the command was delivered
     */
    public boolean sendToConnection(String id, Command command) {
        ClientConnection target = getById(id);
        if (target == null) return false;
        return send(target, command);
    }

    private boolean send(ClientConnection conn, Command command) {
        try {
            conn.send(command);
            return true;
        } catch (IOException e) {
            logger.error("Failed to deliver {} to connection {}", command.getType(), conn.getId(), e);
            return false;
        }
    }
}
