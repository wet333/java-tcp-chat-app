package online.awet.system.core;

import online.awet.commons.Command;
import online.awet.commons.CommandSerializer;
import online.awet.system.core.sessions.Session;

import java.io.*;
import java.net.Socket;
import java.util.Objects;
import java.util.UUID;

public class ClientConnection {

    /** Thread-local tracking which connection is currently processing a command. */
    public static final ThreadLocal<ClientConnection> CURRENT_CONNECTION = new ThreadLocal<>();

    private final String id;
    private final Socket socket;
    private final BufferedWriter writer;
    private final BufferedReader reader;
    private final Session session;

    public ClientConnection(Socket socket) throws IOException {
        this.id = UUID.randomUUID().toString();
        this.socket = socket;
        this.writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        this.reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        this.session = new Session(id);
        ConnectionRegistry.getInstance().register(this);
    }

    /** Returns the connection currently processing a command on this thread. */
    public static ClientConnection currentConnection() {
        return CURRENT_CONNECTION.get();
    }

    public synchronized void send(String message) throws IOException {
        writer.write(message);
        writer.newLine();
        writer.flush();
    }

    public synchronized void send(Command command) throws IOException {
        writer.write(CommandSerializer.toJson(command));
        writer.newLine();
        writer.flush();
    }

    public void authenticate(String username, String alias) {
        session.setUsername(username);
        session.setAlias(alias);
        session.setAuthenticated(true);
    }

    public void logout() {
        session.setUsername(null);
        session.setAlias(null);
        session.setAuthenticated(false);
    }

    public void close() throws IOException {
        socket.close();
    }

    public String getId() {
        return id;
    }

    public BufferedReader getReader() {
        return reader;
    }

    public Session getSession() {
        return session;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ClientConnection that = (ClientConnection) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
