package online.awet.system.commands.handlers;

import online.awet.system.core.broadcast.ClientConnection;
import online.awet.system.commands.*;
import online.awet.system.commands.handlers.extensions.HelpProvider;
import online.awet.system.core.sessions.Session;

import java.io.IOException;
import java.util.Set;

@RegisterCommandHandler
public class LogoutHandler implements CommandHandler, HelpProvider {

    @Override
    public Set<CommandSignature> getSignatures() {
        return Set.of(CommandSignature.of("LOGOUT"));
    }

    @Override
    public void handle(ClientConnection connection, Command command) {
        Session session = connection.getSession();

        try {
            if (!session.isAuthenticated()) {
                connection.send("You are not logged in");
                return;
            }

            String username = session.getDisplayName();
            connection.logout();
            connection.send("User " + username + " has been logged out.");
        } catch (IOException ignored) {}
    }

    @Override
    public String getHelp() {
        return "Usage: /logout";
    }

    @Override
    public String getDescription() {
        return "/logout: Allows you to logout from your current session.";
    }
}
