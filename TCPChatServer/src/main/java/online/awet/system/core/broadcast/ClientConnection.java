package online.awet.system.core.broadcast;

import online.awet.system.core.sessions.Session;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.Objects;
import java.util.UUID;

public class ClientConnection {

    private final String id;
    private final BufferedWriter writer;
    private final Session session;

    public ClientConnection(BufferedWriter writer) {
        this.id = UUID.randomUUID().toString();
        this.writer = writer;
        this.session = new Session(id);
    }

    public void send(String message) throws IOException {
        writer.write(message);
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

    public String getId() {
        return id;
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
