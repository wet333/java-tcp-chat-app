package online.awet.tui.states;

public class StatusBarState {

    private String username;
    private boolean authenticated;

    public StatusBarState() {
        this.username = "guest";
        this.authenticated = false;
    }

    public synchronized String getUsername() {
        return username;
    }

    public synchronized boolean isAuthenticated() {
        return authenticated;
    }

    public synchronized void setAuthenticated(String username) {
        this.username = username;
        this.authenticated = true;
    }

    public synchronized void setUnauthenticated() {
        this.username = "guest";
        this.authenticated = false;
    }

    public synchronized void updateFromServerMessage(String message) {
        if (message.startsWith("Server: You are logged in as: ")) {
            String name = message.substring("Server: You are logged in as: ".length()).trim();
            setAuthenticated(name);
        } else if (message.startsWith("Server: User ") && message.endsWith(" has been logged out.")) {
            setUnauthenticated();
        } else if (message.startsWith("Server: Account ") && message.contains(" has been unregistered")) {
            setUnauthenticated();
        }
    }
}
