package online.awet.system.core.sessions;

import java.util.Objects;

public class Session {

    private final String id;
    private String username;
    private String alias;
    private boolean isAuthenticated;
    private boolean isAnonymous;

    public Session(String id) {
        this.id = id;
        this.isAuthenticated = false;
        this.isAnonymous = true;
    }

    public String getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public boolean isAuthenticated() {
        return isAuthenticated;
    }

    public void setAuthenticated(boolean authenticated) {
        this.isAuthenticated = authenticated;
        this.isAnonymous = !authenticated;
    }

    public boolean isAnonymous() {
        return isAnonymous;
    }

    public void setAnonymous(boolean anonymous) {
        this.isAnonymous = anonymous;
        this.isAuthenticated = !anonymous;
    }

    public String getDisplayName() {
        if (alias != null && !alias.isBlank()) return alias;
        if (username != null && !username.isBlank()) return username;
        return id.substring(0, Math.min(8, id.length()));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Session session = (Session) o;
        return Objects.equals(id, session.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Session{" +
                "id='" + id.substring(0, Math.min(8, id.length())) + "..." + '\'' +
                ", authenticated=" + isAuthenticated +
                ", displayName='" + getDisplayName() + '\'' +
                '}';
    }
}
